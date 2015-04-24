

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	static URL baseAfterTagAIgnored;
	static URL baseAfterEmptyBaseIgnored;
	
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
	static URL tagSpaceBeforeA;
	static File fileTagSpaceBeforeA = new File("tagSpaceBeforeA");
	static URL withinTag;
	static File fileWithinTag = new File("withinTag");
	static URL multipleTags;
	static File fileMultipleTags = new File("multipleTags");
	static URL tagSpaceBeforeAll;
	static File fileTagSpaceBeforeAll = new File("tagSpaceBeforeAll");
	static URL tagEOF;
	static File fileTagEOF = new File("tagEOF");
	static URL tagEOFOnceRead;
	static File fileTagEOFOnceRead = new File ("tagEOFOnceRead");
	static URL withinElement;
	static File fileWithinElement = new File("withinElement");
	static URL afterTag;
	static File fileAfterTag = new File("afterTag");
	
	static URL baseLower;
	static File fileBaseLower = new File("baseLower");
	static URL baseUpper;
	static File fileBaseUpper = new File("baseUpper");
	static URL baseMixedCase;
	static File fileBaseMixedCase = new File("baseMixedCase");
	static URL tagBasef;
	static File fileTagBasef = new File("tagBasef");
	static URL tagBas;
	static File fileTagBas = new File("tagBas");
	static URL tagEOFBas;
	static File fileTagEOFBas = new File("tagEOFBas");
	static URL baseAfterTagA;
	static File fileBaseAfterTagA = new File("baseAfterTagA");
	static URL baseAfterBase;
	static File fileBaseAfterBase = new File("baseAfterBase");
	static URL baseAfterEmptyBase;
	static File fileBaseAfterEmptyBase = new File("baseAfterEmptyBase");
	
	static URL threeLinks, threeLinksA, threeLinksB, threeLinksC;
	static File fileThreeLinks = new File("threeLinks");
	static File fileThreeLinksA = new File("threeLinksA");
	static File fileThreeLinksB = new File("threeLinksB");
	static File fileThreeLinksC = new File("threeLinksC");
	static URL linkA1, linkA2, linkA3, linkB1, linkB2, linkB3, linkC1, linkC2, linkC3;
	
	static URL ftpLink;
	static File fileFTPLink = new File("ftpLink");
	static URL ftpOnlyLink, httpUpperCase, httpLowerCase, httpMixedCase;

	//URLs and Files to test href keyword is found properly
	static URL hrefOutsideTagNotReadIn, hrefAfterTagNotReadIn, hrefInBodyNotReadIn, hrefInWrongTagNotReadIn, hrefUpperCaseNotReadIn, hrefMixedCaseNotReadIn;
	static File fileHrefOutsideTag = new File("hrefOutsideTag");
	static File fileHrefAfterTag = new File("hrefAfterTag");
	static File fileHrefInBody = new File("hrefInBody");
	static File fileHrefInWrongTag = new File("hrefInWrongTag");
	static File fileHrefUpperCase = new File("hrefUpperCase");
	static File fileHrefLowerCase = new File("hrefLowerCase");
	
	static URL hrefAfterNullElementReadIn, hrefAfterEqualsElementReadIn, hrefAfterDoubleElementReadIn, hrefAfterSingleElementReadIn;
	static File fileHrefAfterNullElement = new File("hrefAfterNullElement");
	static File fileHrefAfterEqualsElement = new File("hrefAfterEqualsElement");
	static File fileHrefAfterDoubleElement = new File("hrefAfterDoubleElement");
	static File fileHrefAfterSingleElement = new File("hrefAfterSingleElement");
	
	static URL hrefNoErrorEOFDuringReadIn, hrefNoErrorEOFAfterFReadIn, hrefNoErrorEOFAfterEqualsReadIn, hreffNotIncluded, hreNotIncluded;
	static File fileHrefNoErrorEOFDuring = new File("hrefNoErrorEOFDuring");
	static File fileHrefNoErrorEOFAfterEnd = new File("hrefNoErrorEOFAfterEnd");
	static File fileHrefNoErrorEOFAfterEquals = new File("hrefNoErrorEOFAfterEquals");
	static File fileHreffNotIncluded = new File("hreffNotIncluded");
	static File fileHreNotIncluded = new File("hreNotIncluded");
	
	static URL hrefWhitespaceInPreviousDoubleQuote, hrefWhitespaceInPreviousSingleQuote, hrefEndPreviousNotReadIn, hrefTwoInSameElement;
	static File fileHrefWSInPreviousDouble = new File("hrefWSInPreviousDouble");
	static File fileHrefWSInPreviousSingle = new File("hrefWSInPreviousSingle");
	static File fileHrefEndPreviousNotReadIn = new File("hrefEndPreviousNotReadIn");
	static File fileHrefTwoInSameElement = new File("hrefTwoInSameElement");
	
	
	static Map<URL,File> testPages = new HashMap<URL,File>();




	
	@BeforeClass
	public static void setUp() throws IOException
	{
		addPage(simpleLinkFound = new URL("http://simpleLinkFound.com/"),fileSimpleLinkFound,"Link was followed");
		testPages.put(simpleBaseLinkFound = new URL("http://baseLink.com/found"), fileSimpleLinkFound);
		testPages.put(tagBasefNotReadIn = new URL("http://tagBasef.com/found"), fileSimpleLinkFound);
		testPages.put(tagBasNotReadIn = new URL("http://tagBas.com/found"),fileSimpleLinkFound);
		testPages.put(baseAfterTagAIgnored = new URL("http://baseAfterTagA.com/found"), fileSimpleLinkFound);
		testPages.put(baseAfterEmptyBaseIgnored = new URL("http://baseAfterEmptyBase.com/found"),fileSimpleLinkFound);
		
		addPage(littleA = new URL("http://littleA.com/"),fileLittleA,"<a href=http://simpleLinkFound.com/>");
		addPage(bigA = new URL("http://bigA.com/"),fileBigA,"<A href=http://simpleLinkFound.com/>");
		addPage(multipleSpaceA = new URL("http://multipleSpaceA.com/"),fileMultipleSpaceA,"<a  href=http://simpleLinkFound.com/>");
		addPage(tabA = new URL("http://tabA.com/"),fileTabA,"<a\thref=http://simpleLinkFound.com/>");
		addPage(multipleTabA = new URL("http://multipleTabA.com/"),fileMultipleTabA,"<a\t\thref=http://simpleLinkFound.com/>");
		addPage(linebreakA = new URL("http://linebreakA.com/"),fileLinebreakA,"<a\nhref=http://simpleLinkFound.com/>");
		addPage(multipleLinebreakA = new URL("http://multipleLinebreakA.com/"),fileMultipleLinebreakA,"<a\n\nhref=http://simpleLinkFound.com/>");
		addPage(whitespaceMixA = new URL("http://whitespaceMixA.com/"),fileWhitespaceMixA,"<a\n \t \n \t  \t \n\n\nhref=http://simpleLinkFound.com/>");
		addPage(tagAh = new URL("http://tagAH.com/"),filetagAh,"<ah href=http://simpleLinkFound.com/>");
		addPage(tagSpaceBeforeA = new URL("http://tagSpaceBeforeA.com/"),fileTagSpaceBeforeA, "< a href=http://simpleLinkFound.com/>");
		addPage(withinTag = new URL("http://withinTag.com/"),fileWithinTag,"<start href=http://littleA.com/> <a href=http://simpleLinkFound.com/>");
		addPage(withinElement = new URL("http://withinElement.com/"),fileWithinElement,"<start href=http://littleA.com <a href=http://simpleLinkFound.com/>");
		addPage(afterTag = new URL("http://afterTag.com/"),fileAfterTag,"<start href=http://littleA.com <start></start> <a href=http://simpleLinkFound.com/>");
		addPage(multipleTags = new URL("http://multipleTags.com/"),fileMultipleTags,"<a href=http://littleA.com/></a> <a href=http://simpleLinkFound.com/> >");
		addPage(tagSpaceBeforeAll = new URL("http://tagSpaceBeforeAll.com/"),fileTagSpaceBeforeAll," <a href=http://simpleLinkFound.com/>");
		addPage(tagEOF = new URL("http://tagEOF.com/"),fileTagEOF,"<");
		addPage(tagEOFOnceRead = new URL("http://tagEOFOnceRead.com/"),fileTagEOFOnceRead,"<a ");
		
		addPage(baseLower = new URL("http://baseLower.com/"),fileBaseLower,"<base href=http://baseLink.com/> <a href=found>");
		addPage(baseUpper = new URL("http://baseUpper.com/"),fileBaseUpper,"<BASE href=http://baseLink.com/> <a href=found>");
		addPage(baseMixedCase = new URL("http://baseMixedCase.com/"),fileBaseMixedCase,"<bAsE href=http://baseLink.com/> <a href=found>");
		addPage(tagBasef = new URL("http://tagBasef.com/"),fileTagBasef,"<basef href=http://baseLink.com/> <a href=found>");
		addPage(tagBas = new URL("http://tagBas.com/"),fileTagBas,"<bas href=http://baseLink.com/> <a href=found>");
		addPage(tagEOFBas = new URL("http://tagEOFBas.com/"), fileTagEOFBas,"<bas");
		addPage(baseAfterTagA = new URL("http://baseAfterTagA.com/"),fileBaseAfterTagA,"<a href=http://simpleLinkFound.com/> <base href=http://baseLink.com/><a href=found>");
		addPage(baseAfterBase = new URL("http://baseAfterBase.com/"),fileBaseAfterBase,"<base href=http://baseLink.com/><base href=http://baseAfterTagA.com/><a href=found>");
		addPage(baseAfterEmptyBase = new URL("http://baseAfterEmptyBase.com/"),fileBaseAfterEmptyBase,"<base ><base href=http://baseAfterTagA.com/><a href=found>");
		
		//Files to test breadth and depth searches
		
		addPage(threeLinks = new URL("http://threeLinks.com/"),fileThreeLinks, "<a href=http://linkA.com/></a><a href=http://linkB.com/></a><a href=http://linkC.com/></a>");
		addPage(threeLinksA = new URL("http://linkA.com/"),fileThreeLinksA, "<a href=http://linkA1.com/></a><a href=http://linkA2.com/></a><a href=http://linkA3.com/></a>");
		addPage(threeLinksB = new URL("http://linkB.com/"),fileThreeLinksB, "<a href=http://linkB1.com/></a><a href=http://linkB2.com/></a><a href=http://linkB3.com/></a>");
		addPage(threeLinksC = new URL("http://LinkC.com/"),fileThreeLinksC, "<a href=http://linkC1.com/></a><a href=http://linkC2.com/></a><a href=http://linkC3.com/></a>");
		testPages.put(linkA1 = new URL("http://linkA1.com/"), fileSimpleLinkFound);
		testPages.put(linkA2 = new URL("http://linkA2.com/"), fileSimpleLinkFound);
		testPages.put(linkA3 = new URL("http://linkA3.com/"), fileSimpleLinkFound);
		testPages.put(linkB1 = new URL("http://linkB1.com/"), fileSimpleLinkFound);
		testPages.put(linkB2 = new URL("http://linkB2.com/"), fileSimpleLinkFound);
		testPages.put(linkB3 = new URL("http://linkB3.com/"), fileSimpleLinkFound);
		testPages.put(linkC1 = new URL("http://linkC1.com/"), fileSimpleLinkFound);
		testPages.put(linkC2 = new URL("http://linkC2.com/"), fileSimpleLinkFound);
		testPages.put(linkC3 = new URL("http://linkC3.com/"), fileSimpleLinkFound);
		addPage(ftpLink = new URL("http://ftpLink.com/"), fileFTPLink, "<a href=http://linkA.com/></a><a href=ftp://linkB.com/>");
		
		//Files to test http only is searched
		
		testPages.put(ftpOnlyLink = new URL("ftp://ftpOnlyLink.com/"), fileSimpleLinkFound);
		testPages.put(httpUpperCase = new URL("HTTP://httpUpperCase.com/"), fileSimpleLinkFound);
		testPages.put(httpLowerCase = new URL("HTTP://httpLowerCase.com/"), fileSimpleLinkFound);
		testPages.put(httpMixedCase = new URL("HTTP://httpMixedCase.com/"), fileSimpleLinkFound);

		//Files to test href keyword is found properly
		addPage(hrefOutsideTagNotReadIn = new URL("http://hrefOutsideTag.com/"),fileHrefOutsideTag," href=http://simpleLinkFound.com/");
		addPage(hrefAfterTagNotReadIn = new URL("http://hrefAfterTag.com/"),fileHrefAfterTag,"<a></a>  href=http://simpleLinkFound.com/>");
		addPage(hrefInBodyNotReadIn = new URL("http://hrefInBody.com/"),fileHrefInBody,"<a> href=http://simpleLinkFound.com/</a>");
		addPage(hrefInWrongTagNotReadIn = new URL("http://hrefInWrongTag.com/"),fileHrefInWrongTag,"<b href=http://simpleLinkFound.com/></b>");
		addPage(hrefUpperCaseNotReadIn = new URL("http://hrefUpperCase.com/"),fileHrefUpperCase,"<a HREF=http://simpleLinkFound.com/></a>");
		addPage(hrefMixedCaseNotReadIn = new URL("http://hrefMisxedCase.com/"),fileHrefLowerCase,"<a hReF=http://simpleLinkFound.com/></a>");
		addPage(hrefAfterNullElementReadIn = new URL("http://hrefAfterNullElement.com/"),fileHrefAfterNullElement,"<a hidden href=http://simpleLinkFound.com/></a>");
		addPage(hrefAfterEqualsElementReadIn = new URL("http://hrefAfterEqualsElement.com/"),fileHrefAfterEqualsElement,"<a hidden=false href=http://simpleLinkFound.com/></a>");
		addPage(hrefAfterDoubleElementReadIn = new URL("http://hrefAfterDoubleElement.com/"),fileHrefAfterDoubleElement,"<a hidden=\"false\" href=http://simpleLinkFound.com/></a>");
		addPage(hrefAfterSingleElementReadIn = new URL("http://hrefAfterSingleElement.com/"),fileHrefAfterSingleElement,"<a hidden='false' href=http://simpleLinkFound.com/></a>");
		addPage(hrefNoErrorEOFDuringReadIn = new URL("http://hrefNoErrorEOFDuring.com/"),fileHrefNoErrorEOFDuring,"<a hre");
		addPage(hrefNoErrorEOFAfterFReadIn = new URL("http://hrefNoErrorEOFAfterEnd.com/"),fileHrefNoErrorEOFAfterEnd,"<a href");
		addPage(hrefNoErrorEOFAfterEqualsReadIn = new URL("http://hrefNoErrorEOFAfterEquals.com/"),fileHrefNoErrorEOFAfterEquals,"<a href=");
		addPage(hreffNotIncluded = new URL("http://hreffNotIncluded.com/"),fileHreffNotIncluded,"<a hreff=http://simpleLinkFound.com/></a>");
		addPage(hreNotIncluded = new URL("http://hreNotIncluded.com/"),fileHreNotIncluded,"<a hre=http://simpleLinkFound.com/></a>");

		addPage(hrefWhitespaceInPreviousDoubleQuote = new URL("http://hrefWSInPreviousDouble.com/"),fileHrefWSInPreviousDouble,"<a word=\" href=http://linkA.com\" href=http://simpleLinkFound.com/></a>");
		addPage(hrefWhitespaceInPreviousSingleQuote = new URL("http://hrefWSInPreviousSingle.com/"),fileHrefWSInPreviousSingle,"<a word=' href=http://linkA.com' href=http://simpleLinkFound.com/></a>");
		addPage(hrefEndPreviousNotReadIn = new URL("http://hrefEndPreviousNotReadIn.com/"),fileHrefEndPreviousNotReadIn,"<a word='>' href=http://simpleLinkFound.com/></a>");
		addPage(hrefTwoInSameElement = new URL("http://hrefTwoInSameElement.com/"),fileHrefTwoInSameElement,"<a href=http://simpleLinkFound.com/ href=http://linkA.com></a>");
		
		//TODO: End of files 
	
	}
	
	@AfterClass //use this to delete files after test has run to avoid cluttering the program folder
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
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagAh));
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
	
	@Test
	public void testTagWithSpaceBeforeA()
	{
		wc.crawl(tagSpaceBeforeA, file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagSpaceBeforeA));
	}

	@Test
	public void testTagWithinTag()
	{
		wc.crawl(withinTag,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}

	@Test
	public void testTagWithinElement()
	{
		wc.crawl(withinElement,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound)); //TODO: Need to determine if this link should be returned or not, as it may not make sense if it is, but may lose information if it isnt
	}

	@Test
	public void testTagAfterNotLinkTag()
	{
		wc.crawl(afterTag,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}

	@Test
	public void testTagMultipleTags()
	{
		wc.crawl(multipleTags, file);
		assertEquals(3,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
		assertTrue(HTMLStream.getSearchedURLs().contains(littleA));
	}

	@Test
	public void testTagSpaceBeforeAll()
	{
		wc.crawl(tagSpaceBeforeAll,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}

	@Test
	public void testTagEOF()
	{
		wc.crawl(tagEOF,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagEOF));
	}

	@Test
	public void testTagEOFOnceTagRead()
	{
		wc.crawl(tagEOFOnceRead,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagEOFOnceRead));
	}

	@Test
	public void testTagEOFMidTag()
	{
		wc.crawl(tagEOFBas,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(tagEOFBas));
	}
	
	@Test
	public void testBaseAfterTagARead()
	{
		wc.crawl(baseAfterTagA,file);
		assertEquals(3,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
		assertTrue(HTMLStream.getSearchedURLs().contains(baseAfterTagAIgnored));
	}

	@Test
	public void testBaseAfterAnotherBaseTag()
	{
		wc.crawl(baseAfterBase,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleBaseLinkFound));
	}
	
	@Test
	public void testBaseAfterEmptyBaseNotReadIn()
	{
		wc.crawl(baseAfterEmptyBase,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(baseAfterEmptyBaseIgnored));
	}
	
	@Test
	public void testTagWithinTextNotFound()
	{
		fail("Need to add test that link within text not detected");
	}
	
	// TODO: TEST PROTOCOL
	
	@Test
	public void testNonHttpNotSearched()
	{
		wc.crawl(ftpOnlyLink,file);
		assertTrue(HTMLStream.getSearchedURLs().isEmpty());
	}
	
	@Test
	public void testHttpUpperCaseSearched()
	{
		wc.crawl(httpUpperCase,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHttpLowerCaseSearched()
	{
		wc.crawl(httpLowerCase,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHttpMixedCaseSearched()
	{
		wc.crawl(httpMixedCase,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	// TODO: DETERMINE HREF READ IN PROPERLY
	
	@Test
	public void testHrefOutsideTagNotReadIn()
	{
		wc.crawl(hrefOutsideTagNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testHrefInBodyNotReadIn()
	{
		wc.crawl(hrefInBodyNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefInDifferntTagNotReadIn()
	{
		wc.crawl(hrefInWrongTagNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefAfterEndNotReadIn()
	{
		wc.crawl(hrefAfterTagNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefUpperCaseNotReadIn()
	{
		wc.crawl(hrefUpperCaseNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefMixedCaseNotReadIn()
	{
		wc.crawl(hrefMixedCaseNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}

	@Test
	public void testHrefFoundAfterNullElement()
	{
		wc.crawl(hrefAfterNullElementReadIn,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefFoundAfterEqualsElement()
	{
		wc.crawl(hrefAfterEqualsElementReadIn,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefAfterSingleQuoteReadIn()
	{
		wc.crawl(hrefAfterSingleElementReadIn,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefAfterDoubleQuoteReadIn()
	{
		wc.crawl(hrefAfterDoubleElementReadIn,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testHrefNoErrorIfEOFDuring()
	{
		wc.crawl(hrefNoErrorEOFDuringReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testHrefIfEOFAfterF()
	{
		wc.crawl(hrefNoErrorEOFAfterFReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testHrefIfEOFAfterEquals()
	{
		wc.crawl(hrefNoErrorEOFAfterEqualsReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testHreffNotIncluded()
	{
		wc.crawl(hreffNotIncluded,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testHreNotIncluded()
	{
		wc.crawl(hreNotIncluded,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testHrefWhitespaceInPreviousDoubleQuotes()
	{
		wc.crawl(hrefWhitespaceInPreviousDoubleQuote,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	@Test
	public void testHrefWhitespaceInPreviousSingleQuotes()
	{
		wc.crawl(hrefWhitespaceInPreviousSingleQuote,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	@Test
	public void testHrefEndPreviousNotReadIn()
	{
		wc.crawl(hrefEndPreviousNotReadIn,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testOnlyFirstHrefReadIn()
	{
		wc.crawl(hrefTwoInSameElement,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	

	
	// TEST DEPTH AND LINK MAX SEARCH LIMITS WORK

	@Test(expected=IllegalArgumentException.class) 
	public void testMaxDepth0MaxFiles0error()
	{
		wc = new WebCrawler(0,0);
	}
	
	@Test
	public void maxDepth0DoesntLimit()
	{
		wc = new WebCrawler(5,0);
		wc.crawl(threeLinks,file);
		assertEquals(5,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void maxFile0DoesntLimit()
	{
		wc = new WebCrawler(0,2);
		wc.crawl(threeLinks,file);
		assertEquals(4,HTMLStream.getSearchedURLs().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxDepthNegativeIntError()
	{
		wc = new WebCrawler(-2,4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxFileNegativeIntError()
	{
		wc = new WebCrawler(4,-2);

	}
	
	@Test 
	public void maxDepthLimitAppliedProperly()
	{
		wc = new WebCrawler(20,2);
		wc.crawl(threeLinks,file);
		assertEquals(4,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void maxFileSearchedLimitProperly()
	{
		wc = new WebCrawler(3,20);
		wc.crawl(threeLinks,file);
		assertEquals(3,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void checkDefaultMaxDepthAndMaxFilesSetCorrectly()
	{
		fail("Set up a test to check the limit is set properly");
	}
	
	@Test 
	public void checkNotSearchableProtocolCountedInLinkLimit()
	{
		wc = new WebCrawler(4,20);
		wc.crawl(ftpLink,file);
		assertEquals(3,HTMLStream.getSearchedURLs().size()); //this is 3 rather than 4 as the ftp wont be searched
		assertTrue(!HTMLStream.getSearchedURLs().contains(linkA2));
	}
		
	//TODO: End of tests
	
	
	
	
	/*
	 * FUNCTIONALITY
	 * 
	 * Loops through page properly
	 * Works in appropriate breadth first priority order
	 * doesn't search ftp's but adds them to list (i.e. only searches http://)
	 * returns appropriate error if file already exists 
	 * list is printed properly
	 * if doesnt have / at end of domain, add it. If alreadyy 3, then trim to last as its a file.
	 * ok if no links exist
	 * 
	 * 
	 * HTML READING
	 * 
	 * 	DETERMINE TAG READ IN PROPERLY
	 	* test not read in if written within text
	 * 
	 * 

	 	* HREF LINK READ PROPERLY
		* not added if contains > within quotes part
		* ok if word after the link
		* ok if > directly after it
		* ok if whitesapce directly after it
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
	 * TEST WEBSITE
	 * 
	 * -particular cases from the test website checked
	 * 
	 * FILE EXISTS

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
	 * SEARCH
	 * 	sucessfully overwrites
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
