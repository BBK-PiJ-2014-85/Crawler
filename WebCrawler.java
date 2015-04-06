
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 */

public class WebCrawler {

	//Set default values
	SearchCriteria matchCondition = (url) -> true;
	int maxLinks = 20; 
	int maxDepth = 5;
	
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

		try {
			database.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			InputStream stream = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO: place any tables heading created code here
		
		addToTemporaryDatabase(database, url, 1);
		
		workNextURL(database,1);
		
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
	private boolean addToTemporaryDatabase(File database, URL url, int depth)
	{
		return false;
	}
	
	private void workNextURL(File database, int linksAdded)
	{

		int linksAddedSoFar = linksAdded; //think i can just use links added direct
		int currentDepth = getDepthNextURLToWork(database);
		URL url = getNextURLToWork(database);
		URL urlToAdd;

		if (url != null)
		{
		while (currentDepth < maxDepth && linksAddedSoFar < maxLinks && (urlToAdd=getNextURLFromStream(database)) != null)
		{
			if (addToTemporaryDatabase(database, urlToAdd, currentDepth + 1)) linksAddedSoFar++;
		}
		
		if (search(url)) addURLToResultsDatabase(database,url);
		setPriorityToZero(database, url);
		workNextURL(database, linksAddedSoFar);
		}
	}
	
	private int getDepthNextURLToWork(File database)
	{
		return 0;
	}
	
	//returns null if there are no more URLs to work
	private URL getNextURLToWork(File database)
	{
		return null;
	}
	
	private void addURLToResultsDatabase(File database, URL url)
	{
		
	}
	
	private void setPriorityToZero(File database, URL url)
	{
		
	}
	
	//return null if there are no urls left
	private URL getNextURLFromStream(File database)
	{
		return null;
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

        URL test = new URL("http://www.dcs.bbk.ac.uk/%7Emartin/sewn/ls3/testpage.html");
        URL test2 = new URL("http://www.w3schools.com/html/html_links.asp");
      //  System.out.println(HTMLread.readString(test.openStream(), 'e','z'));
        
        
        BufferedReader in = new BufferedReader(
        new InputStreamReader(test2.openStream()));  
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }
	
}
