

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestWebCrawler {

	WebCrawler wc;
	
	File file = new File("testDatabase.txt");

	static URL simpleLinkFound;
	static File fileSimpleLinkFound = new File("simpleLinkFound");
	static URL simpleBaseLinkFound;
	static URL tagBasefNotReadIn;
	static URL tagBasNotReadIn;
	
	static URL littleA;
	static File fileLittleA = new File("littleA");

	static URL bigA;
	static File fileBigA = new File("bigA");
	static URL multipleSpaceA;
	static File fileMultipleSpaceA = new File("multipleSpaceA");
	static URL tabA;
	static File fileTabA = new File("tabA");
	static URL multipleTabA;
	static File fileMultipleTabA = new File("multipleTabA");
	static URL linebreakA;
	static File fileLinebreakA = new File("linebreakA");
	static URL multipleLinebreakA;
	static File fileMultipleLinebreakA = new File("multipleLinebreakA");
	static URL whitespaceMixA;
	static File fileWhitespaceMixA = new File("whitespaceMixA");
	static URL tagAh;
	static File filetagAh = new File("tagAh");
	
	static URL baseLower;
	static File fileBaseLower = new File("baseLower");
	static URL baseUpper;
	static File fileBaseUpper = new File("baseUpper");
	static URL baseMixedCase;
	static File fileBaseMixedCase = new File("baseMaxedCase");
	static URL tagBasef;
	static File fileTagBasef = new File("tagBasef");
	static URL tagBas;
	static File fileTagBas = new File("TagBas");
	
	static Map<URL,File> testPages = new HashMap<URL,File>();




	
	@BeforeClass
	public static void setUp() throws IOException
	{
		addPage(simpleLinkFound = new URL("http://simpleLinkFound"),fileSimpleLinkFound,"Link was followed");
		testPages.put(simpleBaseLinkFound = new URL("http://baseLink.com/found"), fileSimpleLinkFound);
		testPages.put(tagBasefNotReadIn = new URL("http://basef.com/found"), fileSimpleLinkFound);
		testPages.put(tagBasNotReadIn = new URL("http://bas.com/found"),fileSimpleLinkFound);
		
		addPage(littleA = new URL("http://littleA.com/"),fileLittleA,"<a href=http://simpleLinkFound.com/>");

		addPage(bigA = new URL("http://bigA.com/"),fileBigA,"<A href=http://simpleLinkFound.com/>");
		addPage(multipleSpaceA = new URL("http://multipleSpaceA.com/"),fileMultipleSpaceA,"<a  href=http://simpleLinkFound.com/>");
		addPage(tabA = new URL("http://tabA.com/"),fileTabA,"<a\thref=http://simpleLinkFound.com/>");
		addPage(multipleTabA = new URL("http://multipleTabA.com/"),fileMultipleTabA,"<a\t\thref=http://simpleLinkFound.com/>");
		addPage(linebreakA = new URL("http://linebreakA.com/"),fileLinebreakA,"<a\nhref=http://simpleLinkFound.com/>");
		addPage(multipleLinebreakA = new URL("http://multipleLinebreakA.com/"),fileMultipleLinebreakA,"<a\n\nhref=http://simpleLinkFound.com/>");
		addPage(whitespaceMixA = new URL("http://whitespaceMixA.com/"),fileWhitespaceMixA,"<a\n \t \n \t  \t \n\n\nhref=http://simpleLinkFound.com/>");
		addPage(tagAh = new URL("http://tagAH.com/"),filetagAh,"<ah href=http://simpleLinkFound.com/>");
	
		addPage(baseLower = new URL("http://baseLower.com/"),fileBaseLower,"<base href=http://baseLink.com/> <a href=found>");
		addPage(baseUpper = new URL("http://baseUpper.com/"),fileBaseUpper,"<BASE href=http://baseLink.com/> <a href=found>");
		addPage(baseMixedCase = new URL("http://baseMixedCase.com/"),fileBaseMixedCase,"<bAsE href=http://baseLink.com/> <a href=found>");
		addPage(tagBasef = new URL("http://tagBasef.com/"),fileTagBasef,"<basef href=http://baseLink.com/> <a href=found>");
		addPage(tagBas = new URL("http://tagBas.com"),fileTagBas,"<bas href=http://baseLink.com/> <a href=found>");
	}
	
	@AfterClass //use this to delete fioes after test has run to avoid cluttering the program folder
	public static void deleteFiles()
	{

	}
	
	private static void addPage(URL pageName, File pageFile, String pageContent) throws IOException
	{
		setBody(pageFile,pageContent);
		testPages.put(pageName, pageFile);
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
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
		
	}
	
	@Test
	public void testTagFoundBigA() {
		wc.crawl(bigA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}

	@Test
	public void testTagFoundMultipleSpace() {
		wc.crawl(multipleSpaceA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	
	@Test
	public void testTagTabA() {
		wc.crawl(tabA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	
	@Test
	public void testTagFoundMultipleTabA() {
		wc.crawl(multipleTabA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	
	@Test
	public void testTagFoundLinebreak() {
		wc.crawl(linebreakA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	
	@Test
	public void testTagFoundMultipleLinebreak() {
		wc.crawl(multipleLinebreakA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	
	@Test
	public void testTagFoundWhitespaceMix() {
		wc.crawl(whitespaceMixA, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}

	@Test
	public void testTagAH() {
		wc.crawl(tagAh, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	
	@Test
	public void testTagBaseLower() {
		wc.crawl(baseLower, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleBaseLinkFound));
	}
	
	@Test
	public void testTagBaseUpper() {
		wc.crawl(baseUpper, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleBaseLinkFound));
	}
	
	@Test
	public void testTagBaseMixedCase() {
		wc.crawl(baseMixedCase, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleBaseLinkFound));
	}

	@Test
	public void testTagBasefNotFollowed() {
		wc.crawl(tagBasef, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagBasefNotReadIn));
	}

	@Test
	public void testTagBasNotFollowed() {
		wc.crawl(tagBas, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagBasNotReadIn));
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
	 	* base read in
	 	* base not added as link
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
