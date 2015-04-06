
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
 * 				- could jsut dump those matched underneath though, which although i don't think is any more in ethos of the coursework mathces the way of the description more. 
 * 
 * 	- maxDepth and maxLinks set to 0 or less than one then throw exception. Otherwise, can indicate both and will stop when either is hit.
 * 
 */

public class WebCrawler {

	SearchCriteria matchCondition;
	int maxLinks, maxDepth;
	URL baseURL;
	
	/*
	 * Database just a text file, first line Priority and URL, followed by links to work
	 * 		then a blank line
	 * 	then heading for second table MATCHED URL's, followed by those matched 
	 */
	
	public WebCrawler(URL baseURL, int maxLinks, int maxDepth, File database, SearchCriteria match) throws IOException
	{
		if (match == null) matchCondition = (url) -> true;
		else matchCondition = match;
		
		if (maxLinks <= 0 && maxDepth <=0) throw new IllegalArgumentException("Both maxDepth and maxLinks are less than or equal to zero");
		else
		{
			this.maxLinks = (maxLinks < 1 ? -1 : maxLinks);
			this.maxDepth = (maxDepth < 1 ? -1 : maxDepth);
		}
		
		if (database.exists()) throw new IOException("File already exists.");
		
		this.baseURL = baseURL;
	}
	
	/*
	 * This create a separate interface called URLmatch with one method called match, returning true. This can then be set by the programmer using Lambda's at construction. Will have a default method returning true.
	 */
	public boolean search(URL url)
	{
		return matchCondition.match(url);
	}
	
	
	
	
	
	// Temporarily seeing how the InputStreamReader works for reading in HTML from websites  
    public static void main(String[] args) throws IOException {

        URL test = new URL("http://www.dcs.bbk.ac.uk/%7Emartin/sewn/ls3/testpage.html");
        System.out.println(HTMLread.readString(test.openStream(), 'e','z'));
        
        /*
        BufferedReader in = new BufferedReader(
        new InputStreamReader(test.openStream()));  
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();*/
    }
	
}
