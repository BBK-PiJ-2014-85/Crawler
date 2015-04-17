

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestWebCrawler {

	WebCrawler wc;
	
	File file = new File("testDatabase.txt");
	static File fileLittleA = new File("littleA");
	static File fileLittleAFound = new File("littleAFound");
	
	static Map<URL,File> testPages = new HashMap<URL,File>();
	static URL littleA;
	static URL littleAFound;
	
	
	@BeforeClass
	public static void setUp() throws IOException
	{
		littleA = new URL("http://littleA.com/");
		setBody(fileLittleA,"<a href=http://littleAFound.com/>");
		
		littleAFound = new URL("http://littleAFound.com/");
		setBody(fileLittleAFound,"not much here");
		testPages.put(littleA, fileLittleA);
		testPages.put(littleAFound, fileLittleAFound);
	}
	
	private static void setBody(File file, String body) throws IOException
	{
		
		FileWriter fw = new FileWriter(file);
		fw.write(body);
		fw.close();
	}
	
	
	@Before 
	public void cleanStart() throws MalformedURLException
	{
		HTMLStream.reset();
		HTMLStream.addTestURLs(testPages);
		
		if (file.exists()) file.delete();
	
		wc = new WebCrawler(); 
	}
	
	// DETERMINE TAG READ IN PROPERLY
	
	@Test
	public void testTagFoundLittleA() {
		wc.crawl(littleA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(littleAFound));
		
	}
	
	/*
	 * Create mock class which can set webpages for it to see. I.e., have it return the stream to the WebCrawler. In live, it will use default which will access the site,
	 * for testing, you can feed it the files. Could be a static class/method, with reset and set functions. Will be left in in live, but wont be set in practice.
	 * 
	 * Need method in test to get number of entries, and check if an entry is contained, as a way of determining if output is as expected or not.
	 * 
	 * FUNCTIONALITY
	 * 
	 * Loops through page properly
	 * Works in appropriate breadth first priority order
	 * 
	 * HTML READING
	 * 
	 * 	DETERMINE TAG READ IN PROPERLY
	 	* a read in 
	 	* base read in
	 	* base not added as link
	 	* A read in
	 	* bAsE read in
	 	* BaSe read in
	 	* <ah not read in
	 	* <baseref not read in
	 	* <b, <bas not read in
	 	* space between < and a not read in
	 	* graceful if EOF
	 	* read in within another tag
	 	* tag with no gap until >
	 	* tag with gap before >
	 	* test within body statement
	 	* test within text statement
	 * 
	 	* DETERMINE HREF FOUND PROPERLY
	 	* Only read in for A and Base
	 	* only lower case found
	 	* href found if after a null element
	 	* href found if after an = elemnt
	 	* href not read in if after >
	 	* graceful if EOF directly after
	 	* graceful if EOF during middle of base
	 * 
	 	* HREF LINK READ PROPERLY
		* not added if contains >
		* reads encapsulated by "" properly
		* reads encapsualted by '' properly
		* returns nothing if null (i.e without =)
		* return nothing if empty ""
		* returns properly if just a word added
		* doesnt add link if URL is invalid, and acts graceful
		* only first href added
		* graceful if EOF interupt for all 3 types - none should be added as no close
	 *  
		* LINK FORMED PROPERLY
		* # removed properly
		* test all examples of formulating a link as shown on standards site, including wierd cases
	 * 
		* BASE
		* Base used properly when before
		* Base ignored when not before
		* not added as link
	 *  
	 * INVALID URL
	 * acts graceful when URL provided is invalid
	 * acts graceful when link is not found (and still added to file)	
	 * 
	 * DUPLICATES
	 * only adds once
	 * doesn't add count when found duplicate
	 * not case sensitive
	 * only works each entry once
	 * assigns duplicate those with an / on the end
	 * assigns duplicate after relative link made up
	 * 
	 * FILE EXISTS
	 * returns appropriate error if exists 
	 * 
	 * DEPTH AND BREADTH
	 * 
	 * applies properly to not go over depth.
	 * 0 doesnt limit.
	 * negativenumber either or both errors error 
	 * cant have both zeros 
	 * 
	 * FILE READING
	 * only http read
	 * https: not read without error
	 * file: not read without error
	 * ftp: not read without error
	 * 
	 * SEARCH FUNCTION
	 * changing this properly limits results
	 * is set to true by default
	 * 
	 * CONSTRUCTORS
	 * build with each constructor to test settings properly
	 * 
	 * INVALID HTML FORMAT
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	/*
	 * Things to test:
	 * 	-URL isnt valid
	 * 	-File already exists
	 * 	-if neither defined then don't run
	 *  - test all constructors
	 *  - test limits are set properly
	 *  - test search criteria is set properly
	 *  -each part of a different tag
	 *  	-many different ways of putting in the elemtnes, including case sensitivity, and erroneous quotation marks
	 * 	- have a > in a literals value poart of an arrticute. Is allowed for ' and " methods
	 *       
	 *       Check these
	 *       
	 *       g:h        = <URL:g:h>
      g          = <URL:http://a/b/c/g>
      ./g        = <URL:http://a/b/c/g>
      g/         = <URL:http://a/b/c/g/>
      /g         = <URL:http://a/g>
      //g        = <URL:http://g>
      ?y         = <URL:http://a/b/c/d;p?y>
      g?y        = <URL:http://a/b/c/g?y>
      g?y/./x    = <URL:http://a/b/c/g?y/./x>
      #s         = <URL:http://a/b/c/d;p?q#s>
      g#s        = <URL:http://a/b/c/g#s>
      g#s/./x    = <URL:http://a/b/c/g#s/./x>
      g?y#s      = <URL:http://a/b/c/g?y#s>
      ;x         = <URL:http://a/b/c/d;x>
      g;x        = <URL:http://a/b/c/g;x>
      g;x?y#s    = <URL:http://a/b/c/g;x?y#s>
      .          = <URL:http://a/b/c/>
      ./         = <URL:http://a/b/c/>
      ..         = <URL:http://a/b/>
      ../        = <URL:http://a/b/>
      ../g       = <URL:http://a/b/g>
      ../..      = <URL:http://a/>
      ../../     = <URL:http://a/>
      ../../g    = <URL:http://a/g>
	 * 
	 * 
	 */
	

	


}
