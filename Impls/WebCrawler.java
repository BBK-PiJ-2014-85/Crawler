package Impls;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

// Good file to test on http://www.dcs.bbk.ac.uk/%7Emartin/sewn/ls3/testpage.html

/*
 * Current uncertainties:
 * 		-Should I have one file which stores both tables, or just have the final table printed to the output database (and the temporary one stored elsewhere)?
 * 			- would be more efficient to have two separate tables
 * 			- spec appears to imply both are on the same location in one sentence, although it says we "can" store two tables there
 * 			- would be easiest to store one table with a priority and whether it matched flag for each link, or have tables in separate locations
 * 		-think i will combine the temporary and final result tables to make it efficient, rather than reprint urls.
 * 			- spec say can rather than must use two tables. This code will create the temporary table, and when complete and ran search() will also contain a matched flag, which will be used to determine which are streamed on the final results table
 * 				-once complete then remove all links which are not matched.
 * 				- could jsut dump those matched underneath though, which although i don't think is any more in ethos of the coursework matches the way of the description more. 
 * 
 * 	- what about for pages not found, etc?
 * 	- should i thread it so each crawl is off a different thread?
	-test URL is ok. What if it is not html?
	- is ther going to be an issue opening streams of the same name
 * 
 * 	- if decide to set maxdepth and maxlength then 0 means no limit, otherwise must be a positive integer limit. Cannot set both to zero as this would be limitless. 
 * 

 * TODO: Test end of files occuring at each point of the html
 * TODO: think it should actually be case sensitive
 * TODO: duplicates p[icked up when only diffference is a slash at the end
 * 
 * For html, I have used the standards outlined by the worldwide web consortium:
 * http://www.w3.org/TR/html-markup/syntax.html#syntax-elements
 * 
 * Namely, for elements (copied from the website referenced above):
 * 
    tags are used to delimit the start and end of elements in markup. Elements have a start tag to indicate where they begin. Non-void elements have an end tag to indicate where they end.
    tag names are used within element start tags and end tags to give the element’s name. HTML elements all have names that only use characters in the range 0–9, a–z, and A–Z.
    start tags consist of the following parts, in exactly the following order:
       - A "<" character.
       - The element’s tag name.
       - Optionally, one or more attributes, each of which must be preceded by one or more space characters.
       - Optionally, one or more space characters.
       - Optionally, a "/" character, which may be present only if the element is a void element.
       - A ">" character.
    Void elements only have a start tag; end tags must not be specified for void elements.
    The start and end tags of certain elements can be omitted. The subsection for each element in the HTML elements section of this reference provides information about which tags (if any) can be omitted for that particular element.
    A non-void element must have an end tag, unless the subsection for that element in the HTML elements section of this reference indicates that its end tag can be omitted.
    The contents of an element must be placed between just after its start tag (which might be implied, in certain cases) and just before its end tag (which might be implied in certain cases).

 * Usage notes:
 * - assumed protocol included within URL, i.e. blah://
 * - protocol should be specified in search function i.e. (url) -> url.toString().substring(0,5).equals("http:") to specfiy http: only protocol
 * -URLs dont have whitespace or carriage returns in them
 * - spec says search should be run after, and says that first link should be read via http://. 
 * 		- if the search is run after, we will be attemptig to open lots of pages which will be refused etc. Doesn't seem as intended
 * - possibly good to only open http:// pages, but find all links. 
 * -need to note if     http://foo.com/hello world/ and http://foo.com/hello%20world are the same
 * -Have a protocol set which set which protocols it will attempt to open
 * - What should it do if a link doesn't exist?
 * 		- still return it as may be indicative. program needs not to fall over.
 * - doesn't read and obey robot.txt protocol
 * -assume good html. Will read in if only opening tag containing href. Reads in first href per tag, even though there should be more
 * -invalid url wont be added
 * - when setting breadth and max links, max links includes files of a different protocol that wont be opened.
 * - breadth and max links set to 0 will not limit by these, if setting then at least one needs to be >0
 * - only http: read by default. Have settings within code to easilly add more to it.
 * -doesnt check for close tags, just so that the previous open tag without a close tag is an a
 * TODO: should add a slash if there isnt one at the end, although then need to determine if it is a file or not at the end.jl
 * - only reads in base if it is before the first a, and base can only be read in once.
 * -mention defaults for max links and mex depth
 * doesnt matter if tag isnt closed. Idea is to assume webpage has legitimate html, but rather than validate it, read in the intention
 * - negative values not allowed for max depth or file search limit. zero means it doesnt search by this
 * -TODO: Could add amehtod to set which protocols it will atmept to open
 * -protocol not case sensitive
 * - href case sensitive
 * -doesnt check closed tag closed.
 * -to be added, must nt be an eof right at the end of the word if not in quotes, must close quote if quoted
 * -ignroes semi colons, ? marks and and hashes as tghese refences different aprts ofa  apge
 * -malformed URLS and non-existant exceptions are ignored, with only note printed to console. 
 * -if used http conneciton more would need to be added to stream
 * if url with only 2 slashes added, third is placed at end automatically.
 */

public class WebCrawler {

	//Set default values
	SearchCriteria matchCondition = (url) -> true;
	int maxLinks = 20; 
	int maxDepth = 5;

	final Set<String> OPENABLE = new HashSet<String>(Arrays.asList("http"));
	
	//Crawl variables - could be put in a different class and run in threads
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
		and the other doesnt, but otherwise they are the same, these are determined to be the same*/
	//TODO: Do URLs have to end in a /?
	Comparator<URL> urlMatch = (URL s, URL t) -> { return (s.sameFile(t) ? 0 : 1); }; //TODO: This is a little over the top now if i stick with this.
											
		/*									
	Comparator<String> urlMatch = (s,t) -> {
		if (s.length() == t.length() + 1 && s.charAt(s.length() - 1) == '/') s = s.substring(0,s.length() - 1);
		else if (t.length() == s.length() + 1 && t.charAt(t.length() - 1) == '/') t = t.substring(0,t.length() - 1);
		if (s.equalsIgnoreCase(t)) return 0; else return 1;
	};*/
	
	/*
	 * Database just a text file, first line Priority and URL, followed by links to work
	 * 		then a blank line
	 * 	then heading for second table MATCHED URL's, followed by those matched 
	 */

	public WebCrawler()
	{
		
	}
	
	public WebCrawler(SearchCriteria match)
	{
		matchCondition = match;
	}
	
	public WebCrawler(int maxLinks, int maxDepth)
	{
		if (maxLinks < 0 || maxDepth < 0 || (maxDepth==0 && maxLinks==0)) throw new IllegalArgumentException("Either maxDepth or maxLinks are negative or both are set to zero");
		this.maxLinks = maxLinks;
		this.maxDepth = maxDepth;
	}
	
	public WebCrawler(SearchCriteria match, int maxLinks, int maxDepth)
	{
		if (maxLinks < 0 || maxDepth < 0 || (maxDepth==0 && maxLinks==0)) throw new IllegalArgumentException("Either maxDepth or maxLinks are negative or both are set to zero");
		this.maxLinks = maxLinks;
		this.maxDepth = maxDepth;
		matchCondition = match;
	}
	
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
	
	//TODO: could place this within a class by itself to stop having to pass through database every time, and would be simple to follow if threading too.
	
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
		//String s1 = url.toString();
		String newOutput="";
		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			boolean matched = false;

			String line;
			while ((line = in.readLine()) != null) 
			{
				if (line.length() == 0) newOutput += "\n";
				else if (line.charAt(0)=='P') newOutput += line;//TODO: can make this more flexible by no longer relying on P being the first character
				else if (!matched && Character.isDigit(line.charAt(0)))
				{
					//String s2 = getURLFromString(line).url.toString();
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
		} catch (IOException e) {
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
	 * Returns the nth temporarily stored URL, or null if it doesnt exist.
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
		//String s1 = url.toString();
		try (BufferedReader in = new BufferedReader(new FileReader(currentDatabase))) 
		{
			String line;
			while ((line = in.readLine()) != null && line.length() > 0 && (Character.isDigit(line.charAt(0)) || line.charAt(0)=='P')) 
			{
				if (Character.isDigit(line.charAt(0)))
				{
					//String s2 = getURLFromString(line).url.toString();
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
				baseURL = currentURL.toString(); //TODO: think this is acutally redundant now
				//baseURL = trimURLToLastSlash(currentURL).toString();
		
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
		URL nextURL=null;
		/*URL*/ baseURL = trimURLToLastSlash(currentURL).toString();
		boolean firstTagFound = false; //once an a or base tag has been found, then a base tag can no longer exist (one is in the head, the other, the body)
		while(HTMLread.readUntil(currentStream, '<', '<')) //TODO: not sure what this should stop on, it already returns false at end of file
		{

				n = currentStream.read();
				if (n==-1) break;
				char c = (char) n;
				boolean tagIsBase = false;
				boolean tagIsA = false;
				String link="";
				
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
		
		//make sure break when found
		
		//if first hasnt been found then need to find first of base or <a href = ... or <base href = ...
		
		//this will need to pick up the link, and compile it using absolute, relative (checking base aswell) and root relative paths
			// it will then check that it starts http://
		return null;
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
	
	private URL convertStringToURL(String input)
	{
		// Standards to write this to: http://tools.ietf.org/html/rfc1808#section-5
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
			// TODO Auto-generated catch block
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
	
	/*
	
		
		
		
		if (n != -1 && (char) n != '=')
		{		
			if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>');
			else
			{
				while (n != -1 && (char) n != '=' && !Character.isWhitespace((char) n)) n = currentStream.read();

				if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>');
			}
		}

		//current point now is either eof, end of tag (>), the start of the next element (should the previous no have had an equals) of the equals

		if ((char) n == '=')
		{
			n = HTMLread.skipSpace(currentStream, '<');

			if (n!= -1 && (char) n != Character.MIN_VALUE)
			{
				if ((char) n == '"' || (char) n == '\'') 
				{
					if (HTMLread.readUntil(currentStream,(char) n,'>')) n = HTMLread.skipSpace(currentStream, '>');
					else n = Character.MIN_VALUE;
				}
				else
				{

					while (n != -1 && (char) n != '=' && !Character.isWhitespace((char) n)) n = currentStream.read();

					if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>');
				}
			}
		}

	}*/
	
/*	private void moveToNextElement(char ch) throws IOException
	{
		
		if (n != -1 && (char) n != '=')
		{		
			if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>');
			else
			{
				while (n != -1 && (char) n != '=' && !Character.isWhitespace((char) n)) n = currentStream.read();

				if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>');
			}
		}

		//current point now is either eof, end of tag (>), the start of the next element (should the previous no have had an equals) of the equals

		if ((char) n == '=')
		{
			n = HTMLread.skipSpace(currentStream, '<');

			if (n!= -1 && (char) n != Character.MIN_VALUE)
			{
				if ((char) n == '"' || (char) n == '\'') 
				{
					if (HTMLread.readUntil(currentStream,(char) n,'>')) n = HTMLread.skipSpace(currentStream, '>');
					else n = Character.MIN_VALUE;
				}
				else
				{

					while (n != -1 && (char) n != '=' && !Character.isWhitespace((char) n)) n = currentStream.read();

					if (Character.isWhitespace((char) n)) n = HTMLread.skipSpace(currentStream, '>');
				}
			}
		}

	}*/
	
	
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
			//n = currentStream.read();
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
		} catch (IOException e) {
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
