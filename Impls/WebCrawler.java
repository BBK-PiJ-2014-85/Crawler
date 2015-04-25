package Impls;

import Interfaces.WebCrawlerInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



/**
 * This class implements the WebCrawlerInterface which will crawl a website defined by the user following all links until a limit is reached. The following
 * user points should be noted on the functionality of this crawler:
 *  <ul>
 * 		<li>Doesn't obey Robot.txt: This is mainly for academic purposes and does not obey any robot.txt files. You should only use this therefore on websites where you are certain ignoring this will be appropriate.</li>
 * 		<li>Default values: By default the maximum links to find are 20 and only files 5 degrees from the start pages (inclusive of start page) will be searched. There can be altered or removed by the user by using the appropriate constructor</li>
 * 		<li>Search Method: By default, all links found are counted as a match. User defined search criteria can be set by inputing a "SearchCriteria" interface (via Lamba) into an appropriate constructor.</li>
 * 		<li>No HTML validation: This crawler does not validate that the HTML code is valid. It searches for an open tag of either a or base. Once found, it will find the first href link and return it. It is sufficient for a link to be added if the href quotations marks close, or, if no quotation marks used, it doesnt go directly into an
 * 			end of file. It therefore doesn't matter if the tag is closed. A base tag is read in if it occurs before any other base tag or a tag.
 * 		<li>Protocols: The crawler only follows links using the http: protocol. However, all links are stored and are recorded and will be provided in output regardless of protocol specified (unless specified otherwise by the user) 
 * 		<li>URL Exceptions: Should the URL not exist it will not be searched but is still eligible for the match criteria. A URL malfunction will not be added nor eligible. Both print output to the console to report these have occured, and both are included in any max link limit set by the user.
 * 		<li>HTML Standards: Obey HTML as outlined by the worldwide web consortium (http://www.w3.org/TR/html-markup/syntax.html#syntax-elements)
 * 		<li>Output: When crawled one table will be output, containing those finally matched. All temporary output is deleted once complete. The output is a simple text file.
 * 		<li>Duplicates: Duplicates are determined by using Java URL sameFile() to determine whether links were duplicate (once resolved), so host is not deemed case sensitive but the path is.
 * 		<li>Dependencies: Outside of standard libraries, the crawler is dependent on StreamHolder, HTMLread, HTMLStream and StoredTempUrl classes, as well as the SearchCriteria interface.
 * 		<li>Pathnames: If a URL is provided, either by the user on while crawling, consisting of only a host and protocol component, a backslash will be added at the end if there isn't one already. This ensures duplicates of this nature match.
 * 		<li>Fragments: In forming a link, fragments, defined by the symbols ?, :, ; and # are ignored as if everything after these symbols. These reference the same page or provide query information, and have therefore been ignored.
 * </ul>
 *  
 * @author Paul Day
 *
 */

/*
 * 		-Why search criteria chosen
 * 		-Why two tables
 * 		-didnt realise URL functionality until too late
 * 		-why http?
 * 
 * Improvements: 
 * 		- host not exist exception
 * 		- mutlithreaded
 * 		
 * 		
 * 
 */


public class WebCrawler implements WebCrawlerInterface {

	//Set default values
	SearchCriteria matchCondition = (url) -> true;
	int maxLinks = 20; 
	int maxDepth = 5;

	final Set<String> OPENABLE = new HashSet<String>(Arrays.asList("http"));
	
	InputStream currentStream;
	File currentDatabase;
	int linksAdded,currentDepth;
	boolean firstFoundOnHTML;
	URL currentURL;
	int n;
	String domainURL;
	String baseURL;
	boolean firstLinkFromPageFound;

	/* Comparator below defines whether two URLs are deemed duplicate for matches. This is defined as not case sensitive, and if one ends in a forwards slash
		and the other doesn't, but otherwise they are the same, these are determined to be the same*/
	Comparator<URL> urlMatch = (URL s, URL t) -> { return (s.sameFile(t) ? 0 : 1); }; //TODO: This is a little over the top now if i stick with this.
										

	/**
	 * This creates a webcrawler using the default parameter. All links match, the maximum depth is set to 5 and the maximum links found is 20.
	 */
	public WebCrawler(){}
	
	/**
	 * This creates a webcrawler using the default search limits (depth of 5 and maximum total links found of 20), but uses a user defined search function. This should be input as a
	 * Lambda using the SearchCriteria interface, defining a method which has a URL as input and returns the boolean true if it matches the search criteria, and false if it doesn't.  
	 * 
	 * @param match the method defining whether a URL matches the search criteria or not.
	 */
	public WebCrawler(SearchCriteria match)
	{
		matchCondition = match;
	}
	
	/**
	 * This creates a webcrawler returning every link found up to a user defined limit. The values must be zero or positive. Zero signifies that there is no limit for this aspect. At least one of the values must be non-zero and therefore
	 * limiting.
	 * 
	 * If either value is negative, or both are zero, an IllegalArguemntException will be thrown.
	 * 
	 * The maximum depth is defined as having the starting page as 1.
	 * 
	 * @param maxLinks the maximum amount of links that can be found. 
	 * @param maxDepth the maximum depth of search from the initial page.
	 */
	
	public WebCrawler(int maxLinks, int maxDepth)
	{
		if (maxLinks < 0 || maxDepth < 0 || (maxDepth==0 && maxLinks==0)) throw new IllegalArgumentException("Either maxDepth or maxLinks are negative or both are set to zero");
		this.maxLinks = maxLinks;
		this.maxDepth = maxDepth;
	}
	
	/**
	 * This creates a webcrawler with user defined limits on number of links and match return conditions. 
	 * 
	 * The search function is defined by a a Lambda using the SearchCriteria interface, defining a method which has a URL as input and returns the boolean true if it matches the search criteria, and false if it doesn't
	 * . 
	 * For the link limit, the values must be zero or positive. Zero signifies that there is no limit for this aspect. At least one of the values must be non-zero and therefore
	 * limiting.
	 * 
	 * If either value is negative, or both are zero, an IllegalArguemntException will be thrown.
	 * 
	 * The maximum depth is defined as having the starting page as 1.	 
	 * 
	 * @param match the method defining whether a URL matches the search criteria or not.
	 * @param maxLinks the maximum amount of links that can be found. 
	 * @param maxDepth the maximum depth of search from the initial page.
	 */
	
	public WebCrawler(SearchCriteria match, int maxLinks, int maxDepth)
	{
		if (maxLinks < 0 || maxDepth < 0 || (maxDepth==0 && maxLinks==0)) throw new IllegalArgumentException("Either maxDepth or maxLinks are negative or both are set to zero");
		this.maxLinks = maxLinks;
		this.maxDepth = maxDepth;
		matchCondition = match;
	}
	
	
	@Override
	public void crawl(URL url, File database)
	{	
		if (database.exists()) throw new FileSystemAlreadyExistsException();
		
		try {
			database.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		currentDatabase = database;
		
		int slashCount = 0;
		String urlString = url.toString();
		for (int i=0; i<urlString.length(); i++) if (urlString.charAt(i) == '/') slashCount++;
		
		try {
			if (addToTemporaryDatabase( (slashCount== 2 ? new URL(urlString + '/') : url), 1)) linksAdded++;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		try {
			workNextURL();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		clearTemporaryDatabase(database);

	}
	
	
	//returns true if added and therefore unique, false if not
	private boolean addToTemporaryDatabase(URL url, int depth)
	{
		String tempDatabase = getAllTemporaryURLs();

		System.out.println("Adding to temporary database: " + url.toString());
		
		tempDatabase += "\n" + depth + "\t\"" + url.toString() + "\"\n\n";

		tempDatabase += getAllMatchedURLs();
		
		try (PrintWriter out = new PrintWriter(currentDatabase)){
			out.write(tempDatabase);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	private void setPriorityToZero(URL url)
	{
		String newOutput="";
		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			boolean matched = false;

			String line;
			while ((line = in.readLine()) != null) 
			{
				if (line.length() == 0) newOutput += "\n";
				else if (line.charAt(0)=='P') newOutput += line;
				else if (!matched && Character.isDigit(line.charAt(0)))
				{
					if (urlMatch.compare(url, getURLFromString(line).url) == 0)
					{
						matched = true;
						newOutput += "\n0\t\"" + url.toString() + "\"";
					}
					else newOutput += "\n"+line;
				}
				else newOutput += "\n"+line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (PrintWriter out = new PrintWriter(currentDatabase)){
			out.write(newOutput);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	private String getAllTemporaryURLs()
	{
		String returnString="PRIORITY \tURL";

		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			String line;
			while ((line = in.readLine()) != null && line.length() > 0 && (Character.isDigit(line.charAt(0)) || line.charAt(0)=='P')) 
				{
				if (Character.isDigit(line.charAt(0))) returnString += "\n"+line;
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnString;
	}
	
	private String getAllMatchedURLs()
	{
		String returnString="MATCHED";
	
		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			String line;
			while ((line = in.readLine()) != null && (line.length()==0 || line.charAt(0) != 'M'));//find either the start of MATCHED of end of file if it doesn't exist
			if (line != null)
			{
				while ((line = in.readLine()) != null && line.length()>0 && line.charAt(0) == '"') 
				{
					returnString += "\n"+line;
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnString;
	}
	
	/*
	 * Returns the next temporarily stored URL which is yet to be worked, or null if it doesn't exist.
	 */
	private StoredTempURL getNextUnworkedURL()
	{
		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			String line;
			while ((line = in.readLine()) != null && line.length() > 0 && (Character.isDigit(line.charAt(0)) || line.charAt(0)=='P')) 
			{
				if (Character.isDigit(line.charAt(0)) && line.charAt(0) != '0') return getURLFromString(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private boolean tempURLAlreadyExist(URL url)
	{
		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			String line;
			while ((line = in.readLine()) != null && line.length() > 0 && (Character.isDigit(line.charAt(0)) || line.charAt(0)=='P')) 
			{
				if (Character.isDigit(line.charAt(0)))
				{
					if (urlMatch.compare(url, getURLFromString(line).url) == 0) return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private StoredTempURL getURLFromString(String line)
	{
		int numPosn = 0;
		while(Character.isDigit(line.charAt(numPosn))) numPosn++;
		int priority = Integer.parseInt(line.substring(0,numPosn));
		
		int urlStartPosn = 0;
		while (line.charAt(urlStartPosn) != '"') urlStartPosn++;
		try {
			URL url = new URL(line.substring(urlStartPosn+1, line.length()-1));
			return new StoredTempURL(priority, url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void workNextURL() throws IOException
	{
		StoredTempURL next = getNextUnworkedURL();
		
		if (next!=null)
		{
			int currentDepth = next.priority;
			currentURL = next.url;

			URL urlToAdd;
		
			System.out.println("Working: " + currentURL.toString() );
		
			Iterator<String> it = OPENABLE.iterator();
			boolean canOpen = false;
			while (it.hasNext() && !canOpen) { if (it.next().equalsIgnoreCase(currentURL.getProtocol())) canOpen = true;}
		
			if (canOpen)
			{
				domainURL = getDomainFromURL(currentURL);
				baseURL = currentURL.toString();
		
				StreamHolder nextStream = HTMLStream.getStream(currentURL);
				
				if (nextStream.response != HttpURLConnection.HTTP_ACCEPTED)
				{
					System.out.println("Not right...");
				}
				else
				{
						currentStream =  nextStream.stream;
						firstLinkFromPageFound = false;
					while ((maxDepth==0 || currentDepth < maxDepth) && (maxLinks == 0 || linksAdded < maxLinks) && (urlToAdd=getNextURLFromCurrentStream()) != null)
					{

						if (!tempURLAlreadyExist(urlToAdd))
						{
							if (addToTemporaryDatabase(urlToAdd, currentDepth + 1)) linksAdded++;
						}
					}
				}
			}
			
			if (search(currentURL)) addURLToMatchedDatabase(currentURL);
			setPriorityToZero(currentURL);
			workNextURL(); 
		}
		
		
	}
	
	private String getDomainFromURL(URL currentURL)
	{
		String urlString = currentURL.toString();
		int slashCount=0;
		int position=0;
		while (position < urlString.length() && (slashCount < 2 || urlString.charAt(position) != '/'))
		{
			if (urlString.charAt(position) == '/') slashCount++;
			position++;
		}
		
		return urlString.substring(0, position);
	}
	
	private void addURLToMatchedDatabase(URL url)
	{

		System.out.println("Adding to matched database: " + url.toString());
		
		try (PrintWriter out = new PrintWriter(new FileWriter(currentDatabase,true))){
			out.write("\n\"" + url.toString() + "\"");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//return null if there are no urls left
	public URL getNextURLFromCurrentStream() throws IOException
	{
//		URL nextURL=null;
		/*URL*/ baseURL = trimURLToLastSlash(currentURL).toString();
//		boolean firstTagFound = false; //once an a or base tag has been found, then a base tag can no longer exist (one is in the head, the other, the body)
		while(HTMLread.readUntil(currentStream, '<', '<')) //TODO: not sure what this should stop on, it already returns false at end of file
		{

				n = currentStream.read();
				if (n==-1) break;
//				char c = (char) n;
				boolean tagIsBase = false;
				boolean tagIsA = false;
//				String link="";
				
				if (!firstLinkFromPageFound && Character.toLowerCase((char) n) == 'b') //there can only be one base tag, and must be beofer an A tag
				{
					if (matchStringAndMoveN(false,"ase"))
					{
						n = currentStream.read();
						tagIsBase = Character.isWhitespace((char) n); //tag is only base
						n = HTMLread.skipSpace(currentStream,'>');
					}
				}
				else if (Character.toLowerCase((char) n) == 'a')
				{
					n = currentStream.read();
					tagIsA = Character.isWhitespace((char) n);
					n = HTMLread.skipSpace(currentStream,'>');
				}
			
				
				
				if (tagIsA || tagIsBase)
				{
					boolean noLinkContained=false;
					boolean baseTagAdded = false;

					if (!firstLinkFromPageFound) firstLinkFromPageFound = true;
				
					
					while (n != -1 && (char) n != Character.MIN_VALUE && (char) n != '>' && !noLinkContained && !baseTagAdded) //should parameterise the >
					{
						if ((char) n == 'h')
						{

							if (matchStringAndMoveN(true,"ref"))
							{
								n = currentStream.read();
								if (Character.isWhitespace((char) n) || ((char) n == '=')) //href match made
								{
									URL returnURL = getLink('>');
									if (returnURL == null) noLinkContained = true;
									else if (tagIsA) return returnURL;
									else {
										baseURL = trimURLToLastSlash(returnURL).toString();
										baseTagAdded=true;
										
									}
								}
							}
						}
					//}
						
					if (n != -1 && (char) n != Character.MIN_VALUE & !baseTagAdded) moveToNextElement('>');
					}
				}
			}			
		return null;
	}
	

	private URL getLink(char ch)
	{
		if (n == -1 || (char) n == ch) return null;
		if ((char) n != '=') n = HTMLread.skipSpace(currentStream, ch); //move it from space to equals

		if ((char) n != '=') return null;	
		n = HTMLread.skipSpace(currentStream, ch);
		
		String urlRaw = ((char) n == '"' || (char) n == '\'' ? "" : "" + (char) n);
		String tempString;
		
		if ((char) n == '\'' || (char) n == '"') tempString = HTMLread.readString(currentStream, (char) n, ch);
		else tempString = HTMLread.readStringUntilWhitespace(currentStream, ch);
		if (tempString == null) return null;
		urlRaw += tempString;
		return convertStringToURL(urlRaw);
	}
	
	/*
	 * This method obtains the value for the current attribute and compiles it into a URL.
	 * 
	 * The point in the stream will either be at the "=" or whitespace, before the value but after the element name.
	 * 
	 * If the current point is whitespace, it will move to the next non whitespace which should be = (if not then null is returned).
	 * 
	 * From the point of equals, it moves forwards to the next no whitespace, where it will encounter an ", ' or character.
	 * 
	 * The URL returned is null if the input parameter is met, am end of file is met, or an '=' is not met 
	 */
	private URL convertStringToURL(String input)
	{
		// Standards written to: http://tools.ietf.org/html/rfc1808#section-5
		String linkString="";
		
		String stringWithoutParameters ="";
		int position=0;
		boolean parameterFound = false;
		while (position < input.length() && !parameterFound)
		{
			if (input.charAt(position) == '#' || input.charAt(position) == '?' ||input.charAt(position) == ';' ) parameterFound = true;
			else position++;
		 
		}
		
		stringWithoutParameters = input.substring(0,position); //removing hash as this is a local reference, and ? and ; as this is for parameters to pass to the other address
		
		if (stringWithoutParameters.length() == 0) linkString = currentURL.toString(); //This shouldnt be base but current site when empty
		else if (stringWithoutParameters.contains(":")) linkString = stringWithoutParameters; //String is full reference as includes protocol
		else if (stringWithoutParameters.length() > 1 && stringWithoutParameters.substring(0,2).equals("./"))
		{
			linkString = baseURL + (stringWithoutParameters.length() > 2? stringWithoutParameters.substring(2) : "");
		}
		else if (stringWithoutParameters.charAt(0) == '/') //Link is relative to domain
		{
			if (stringWithoutParameters.length() > 1 && stringWithoutParameters.charAt(1) == '/')
			{
				int colonLoc = 0;
				String current = currentURL.toString();
				while (current.charAt(colonLoc) != ':') colonLoc++;

				linkString = current.substring(0, colonLoc + 1) + stringWithoutParameters; 
			}
			else 
			{
				linkString = domainURL + stringWithoutParameters;
			}
		}
		else if (stringWithoutParameters.equals(".")) linkString = baseURL;
		else if (stringWithoutParameters.equals("..") || (stringWithoutParameters.length() > 2 && stringWithoutParameters.substring(0, 3).equals("../")))
		{
			String testString="../";
			int count = 0;
			while ( ( (testString.length() == stringWithoutParameters.length() + 1) && stringWithoutParameters.equals(testString.substring(0, testString.length() - 1)))
					|| testString.length() <= stringWithoutParameters.length() && testString.equals(stringWithoutParameters.substring(0,testString.length())))
			{
				count++;
				testString += "../";
			}
			
			int depthOfBase = -3;
			for (int i = 0; i < baseURL.length() ; i++) if(baseURL.charAt(i) == '/') depthOfBase++;
			
			int numToRemove = (count > depthOfBase ? depthOfBase : count);
			int numRemoved = 0;
			int basePosition = baseURL.length();
			while (numToRemove > numRemoved || baseURL.charAt(basePosition - 1) != '/')
			{
				if (baseURL.charAt(basePosition - 1) == '/') numRemoved++;
				basePosition--;
			}
			
			String basePart = baseURL.substring(0, basePosition);

			String linkPart = (stringWithoutParameters.length() < (numToRemove * 3) ? "" : stringWithoutParameters.substring(numToRemove * 3));
			linkString = basePart + linkPart;//specifies at stadards that too many ../ then possible shoudl be added in adress 
			
		}
		else linkString = baseURL + stringWithoutParameters;
		
		int slashCount = 0;
		for (int i=0; i<linkString.length(); i++) if (linkString.charAt(i) == '/') slashCount++;
		
		try {
			return new URL ((slashCount == 2 ? linkString + '/' : linkString));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private void moveToNextElement(char ch) throws IOException
	{
		while (n != -1 && (char) n != '=' && (char) n != '\'' && (char) n != '"' && !Character.isWhitespace((char) n) && (char) n != ch )  n=currentStream.read();
		if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>'); // move on from space to next word

		if ((char) n == '=' || (char) n == '"' || (char) n == '\'') //if have an equals, ' or "" then need to proceed past the next entry
		{
			if ((char) n == '=') n = HTMLread.skipSpace(currentStream, '>'); //move equals onto next word
			
			if ((char) n == '"'|| (char) n == '\'')
			{
				if (HTMLread.readUntil(currentStream,(char) n,'>')) n = HTMLread.skipSpace(currentStream, '>');
				else n = Character.MIN_VALUE;
			}
		}
	}
	
	/* Read from the current input stream, seeing if it matches the input string, returning whether it matches.
	 * Stops cycling as soon as the match is not found, and sets the global int last read, n, to the char read in which either didnt mathc
	 * or the last char in the matched string.
	 */
	private boolean matchStringAndMoveN(boolean caseSensitive, String input) throws IOException
	{
		boolean matched = true;
		String remainingWord = input;
		
		while (matched && remainingWord.length() != 0 && ( (n = currentStream.read()) != -1))
		{
			if (caseSensitive) 
			{
				if ((char) n != remainingWord.charAt(0)) matched=false;
			}
			else
			{
				if (Character.toLowerCase((char) n) != Character.toLowerCase(remainingWord.charAt(0))) matched=false;
			}
			
			remainingWord = remainingWord.substring(1);
		}
		return matched;
	}

	public boolean search(URL url)
	{
		return matchCondition.match(url);
	}
	
	private void clearTemporaryDatabase(File database)
	{
		String matched = getAllMatchedURLs();
	
		try (PrintWriter out = new PrintWriter(currentDatabase)){
			out.write(matched);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Removes from the URL anything after the final /, so long as there are 3 already (i.e. ://.../ must remain)
	 */
	
	private URL trimURLToLastSlash(URL url) throws MalformedURLException
	{
		String temp = url.toString();
		
		int count = 0;
		for (int i = 0; i<temp.length(); i++) if (temp.charAt(i) == '/') count++;

		int loc = temp.length();
		if (count >= 3) 
		{
			while (temp.charAt(loc - 1) != '/') loc--;
		}
		
		temp = temp.substring(0, loc);
		
		URL rtn = new URL(temp);
		
		return rtn;
	}
	
	
	
	
	
	// Temporarily seeing how the InputStreamReader works for reading in HTML from websites  
    public static void main(String[] args) throws IOException {
    	
    	
    	
        URL test = new URL("http://www.bbc.co.uk");
        URL test3 = new URL("http://www.dcs.bbk.ac.uk/%7Emartin/sewn/ls3/testpage.html");
        URL test2 = new URL("http://www.bbc.co.uk/dfdfdfdfd");
        /*
        
        BufferedReader in = new BufferedReader(
        new InputStreamReader(test3.openStream()));  
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    	*/
        
        File file = new File("database.txt");
        if (file.exists()) file.delete();
        
        WebCrawler wc = new WebCrawler((url) -> url.toString().substring(0,5).equals("http:"),5,5);
        wc.crawl(test2,file);
        
        
    }
	
}
