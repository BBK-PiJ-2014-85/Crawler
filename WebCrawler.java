
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
 * -TODO: it is just http: sites that we are taking, and should we also be looking at those which are relative links as well from base?
 * TODO: Test end of files occuring at each point of the html
 * TODO: think it should actually be case sensitive
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

 * 
 * 
 */

public class WebCrawler {

	//Set default values
	SearchCriteria matchCondition = (url) -> true;
	int maxLinks = 20; 
	int maxDepth = 5;

	//Crawl variables - could be put in a different class and run in threads
	InputStream currentStream;
	File currentDatabase;
	int linksAdded,currentDepth;
	boolean firstFoundOnHTML;
	URL currentURL;
	int n;
	/*
	 * Database just a text file, first line Priority and URL, followed by links to work
	 * 		then a blank line
	 * 	then heading for second table MATCHED URL's, followed by those matched 
	 */
	
	
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

		linksAdded=1;
		currentURL = url;
		
		
		try {
			database.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		currentDatabase = database;
		
		//TODO: place any tables heading created code here
		
		addToTemporaryDatabase(url, 1);
		
		try {
			workNextURL();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clearTemporaryDatabase(database);
		/*
		 * code structure
		 * 
		 * - start of URL - get depth 
		 *  -if depth != maxDepth and linksAdded < maxLinksAdded then getmore links loop below
		 *  	- while (linksadded < maxLinksAdded)
		 *  		get next link
		 *  		check unique
		 *  		if so, 	add to list with depth + 1
		 *  				add 1 to linksAdded
		 *  		if eof, then break
		 *  - check if the original URL is a match, recording it if it is
		 *  - set priority to zero in text file
		 *  - work next link in same way
		 *  
		 *  Once run, stream final database effectively removing temporary database
		 */	
	}
	
	//TODO: could place this within a class by itself to stop having to pass through database every time, and would be simple to follow if threading too.
	
	//returns true if added and therefore unique, false if not
	private boolean addToTemporaryDatabase(URL url, int depth)
	{
		return false;
	}
	
	private void workNextURL() throws IOException
	{

		int currentDepth = getDepthNextURLToWork();
		currentURL = getNextURLToWork();
		URL urlToAdd;

		//Apache URL validity checker may be useful here
		
		if (currentURL != null)
		{
			try {
				currentStream =  currentURL.openStream();
			} catch (IOException e) {
				e.printStackTrace();//TODO: may want to handle these better by taking it as a bad link rather than halting the program 
			}  	
	       	       
			while (currentDepth < maxDepth && linksAdded < maxLinks && (urlToAdd=getNextURLFromCurrentStream()) != null)
			{
				if (addToTemporaryDatabase(urlToAdd, currentDepth + 1)) linksAdded++;
			}
		
			if (search(currentURL)) addURLToResultsDatabase(currentURL);
			setPriorityToZero(currentURL);
			workNextURL();
		}
		
	/*
	 * (Terrible.. ) Pseudocode to get Links, going by W3C standards and assuming html is accurate, otherwise you're taking a link from an erroneous site.
	 * 
	 * TODO: If at any point hit a <, start again (could be hitting another start, and an end tag may not have been added)
	 * 
	 * readUntil(<)
	 * c=stream.read()
	 * if (c.lowercase()='a') (...or (c.lowercase()='b') <= looking for base, but this case ignored in pseudocode)
	 *  	if (c=stream.read is whitespace)
	 * 			c=skipSpace()
	 * 			if (next.lowercase() ='h') 
	 * 				check next are "ref"one by one to make not case senstive href <= could be another empty element (for exmaple hidden) <= do a little loop				
	 * 					if (c.skipSpace() = '=')
	 * 						getPhase() <= if first (after removing whitespace) is a " returns something in "", if it is a ', returns encapsualted by '', if a char then reutrns this. 
	 * 						link acquired and so stop.
	 * 
	 * 					as soon as not matched, if its whitespace at end then start again from this point, if an = or char then get next element
	 * 			if (not h) then return next after whitespace, the close after an =, or a MIN of > is encountered go to next after whitespace or equals (stopping at > as well) (one could be an empty attribute, the other) returns >, if = retrun char, if > return minChar etc.
					Above method is the getNextElement function
	 * 
	 * 		if (not whitespace) go to next < and start again
	 */
		
		
	}
	
	private int getDepthNextURLToWork()
	{

		return 0;
	}
	
	//returns null if there are no more URLs to work
	private URL getNextURLToWork()
	{
		return null;
	}
	
	private void addURLToResultsDatabase(URL url)
	{
		
	}
	
	private void setPriorityToZero(URL url)
	{
		
	}
	
	//return null if there are no urls left
	private URL getNextURLFromCurrentStream() throws IOException
	{
		URL nextURL=null;
		URL baseURL = currentURL;
		boolean firstTagFound = false; //once an a or base tag has been found, then a base tag can no longer exist (one is in the head, the other, the body)
		while(HTMLread.readUntil(currentStream, '<', '<')) //TODO: not sure what this should stop on, it already returns false at end of file
		{
			n = currentStream.read();
			if (n==-1) break;
			char c = (char) n;
			boolean tagIsBase = false;
			boolean tagIsA = false;
			
			if (!firstTagFound && Character.toLowerCase((char) n) == 'b') //there can only be one base tag
			{
				if (matchStringAndMoveN("ase"))
				{
					n = currentStream.read();
					tagIsBase = Character.isWhitespace((char) n); //tag is only base
				}
			}
			else if (Character.toLowerCase((char) n) == 'a')
			{
				tagIsA = Character.isWhitespace((char) n);
			}
			
			if (tagIsA || tagIsBase)
			{
				if (!firstTagFound) firstTagFound = true;
				
				HTMLread.skipSpace(currentStream,'>');
				
			}
			
			
			
			
		}
		
		//make sure break when found
		
		//if first hasnt been found then need to find first of base or <a href = ... or <base href = ...
		
		//this will need to pick up the link, and compile it using absolute, relative (checking base aswell) and root relative paths
			// it will then check that it starts http://
		return null;
	}
	
	/*
	 * Moves the point in the stream to the next attribute within the element. It will be run from a point wither in the name
	 * of the attribute, within the whitespace before the equals or at the equals sign. If it returns true, the current 
	 * point in the stream is left at the start of the next attribute. If it returns false, the input parameter was encountered
	 * and the program stopped, or the end of the file was found.
	 */
	
	private boolean moveToNextAttribute(char ch) throws IOException
	{
		if (n == -1 || (char) n == ch) return false;
		
		while (!Character.isWhitespace((char) n) && (char) n != '=') //if in middle of word, move to end
		{
			n = currentStream.read();
			if (n== -1 || (char) n == ch) return false;
		}
		//TODO: FIRST Check the logic here, and whether the skipSpace should move the counter one or check where it is
		while (!Character.isWhitespace((char) n)) //move the next word
		{
			if (HTMLread.skipSpace(currentStream,ch) == Character.MIN_VALUE) return false; 
		}
		
		if ((char) n == '=') //if it has hit an =, then it is in the equals part of the attribute and needs to skip through this
		{
			
			if (HTMLread.skipSpace(currentStream,ch) == Character.MIN_VALUE) return false;
			
			if (n== -1 || (char) n == ch) return false;
			
			if ( (char) n == '"' || (char) n == '\'') //if it is in quotes, then go to the end quote
			{
				if (!HTMLread.readUntil(currentStream, (char) n, ch)) return false;
			}
			else 
			//if an " then go to next "
			// if an ' then go to next '
			//if neither, then go to next whitespace
		}
		
	}
	
	/* Read from the current input stream, seeing if it matches the input string (not case sensitive), returning whether it matches.
	 * Stops cycling as soon as the match is not found, and sets the global int last read, n, to the char read in which either didnt mathc
	 * or the last char in the matched string.
	 */
	private boolean matchStringAndMoveN(String input) throws IOException
	{
		boolean matched = true;
		String remainingWord = input;
		
		while (matched && remainingWord.length() != 0)
		{
			n = currentStream.read();
			if (Character.toLowerCase((char) n) != Character.toLowerCase(remainingWord.charAt(0))) matched=false;
			
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
		
	}
	

	
	
	
	
	
	// Temporarily seeing how the InputStreamReader works for reading in HTML from websites  
    public static void main(String[] args) throws IOException {

    	String word = "hello";
    	while (word.length() != 0)
    	{
    		System.out.println(word.charAt(0));
    		word = word.substring(1);
    	}
 
    	int intA = 1;
    	int intB = -1;
    	char a = (char) intA;
    	char b = (char) intB;
    	System.out.println(a);
    	System.out.println(b);
    	
    	System.out.println(Character.MIN_VALUE == b);
    	System.out.println(Character.isWhitespace(b));
    	
    	/*
        URL test = new URL("http://www.dcs.bbk.ac.uk/%7Emartin/sewn/ls3/testpage.html");
        URL test2 = new URL("http://www.w3schools.com/html/html_links.asp");
        URL test3 = new URL("http://www.bbc.co.uk");
        URL test4 = new URL("http://www.motive.co.nz/glossary/linking.php?ref");

      //  System.out.println(HTMLread.readString(test.openStream(), 'e','z'));
        
        
        BufferedReader in = new BufferedReader(
        new InputStreamReader(test3.openStream()));  
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
*/        
    }
	
}
