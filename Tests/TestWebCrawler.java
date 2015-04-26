package Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Impls.WebCrawler;
import Impls.HTMLStream;
import Impls.SearchCriteria;

/*
 * This class provides the tests for the Webcrawler implementation of WebCrawler interface.
 * 
 * That testing was done by creating many different files with different conditions and uploading these to HTMLStream static class
 * to store these as webpages to test the webcrawler on. A lot of files are created for this purpose, created in the same location as the
 * test file, but are deleted after the test program has run. Due to the amount of files being created, this test class can take a while to run (c.20 seconds on a 3 year old Macbook air).
 * 
 * Due to the files being created, to avoid conflict the file containing the test class should be clean from files outside of those included within this
 * webcrawler package.
 * 
 * The test should be intuitively named enough for them to be clear what they are testing. They are split up into the following categories:
 * 
 * 		-FUNCTIONALITY: Tests the general functionality, including that it is breadth first, it provides an appropriate error if the crawl file already exists,
 * 			the search function is overwritten and applied properly using both constructors and that the default settings for max links and depth are working as specified
 * 		-DUPLICATES: Tests that links aren't searched twice if they are repeated, they are case sensitive or not as intended, they dont count for the max link limit
 * 			and that parsed links are properly detected as duplicate.
 * 		-OUTPUT: Tests that the output database is as intended, only storing those which match criteria.
 * 		-TAG FINDING: Tests that the a and base tags are found and that other are ignored.
 * 		-PROTOCOL: Tests that only http are searched but that all are found.
 * 		-HREF FINDING: Determines that the href is properly found and other forms are correctly ignored.
 * 		-HREF LINKS: Checks the links following an href are read in correctly and exceptional circumstances are dealt with
 * 		-ABSOLUTE AND RELATIVE PATH PARSING: Checks links are parsed properly and that exceptional cases are dealt with.
 * 		-SEARCH LIMITS: Ensures the constructor settings for max links and max depth are applied correctly, and appropriate errors returned should the user input be invalid
 * 		-MALFORMED AND NOT FOUND URLS: Checks the functionality is as intended when a Malformed URL exists or http connection is unable to be made
 * 
 * Tests begin from row 530.
 */

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
	
	//URLs and file to test href link functionality
	
	static URL linkWithEOFBeforeClosedWithTagWhenQuotes, linkEOFBeforeCloseQuote, linkWithTagClosedBeforeEndQuotes, linkEOFBeforeClosedTag, linkWithWordAfter, linkWhitespaceThenWord;
	static File fileLinkWithEOFBeforeClosedWithTagWhenQuotes = new File("linkWithEOFBeforeClosedWithTagWhenQuotes");
	static File fileLinkEOFBeforeCloseQuote = new File("linkEOFBeforeCloseQuote");
	static File fileLinkTagBeforeCloseQuote = new File("linkTagBeforeCloseQuote");
	static File fileLinkWithWordAfter = new File("linkWithWordAfter");
	static File fileLinkEOFBeforeClosedTag = new File("linkEOFBeforeClosedTag");
	static File fileLinkWhitespaceThenWord = new File("linkWhitespaceThenWord");
	
	static URL linkWhitespaceThenSingleQuotes, linkWhitespaceThenDoubleQuotes, linkWordCloseTagAfter, linkQuoteCloseTagAfter, linkWordAfterEmptyHref;
	static File fileLinkWhitespaceThenSingleQuotes = new File("linkWhitespaceThenSingleQuotes");
	static File fileLinkWhitespaceThenDoubleQuotes = new File("linkWhitespaceThenDoubleQuotes");
	static File fileLinkWordCloseTagAfter = new File("linkWordCloseTagAfter");
	static File fileLinkQuoteCloseTagAfter = new File("linkQuoteCloseTagAfter");
	static File fileLinkWordAfterNullHref = new File("linkWordAfterNullHref");

	static URL linkWordAfterNullHref, linkEmpty, linkNull;
	static File fileLinkWordAfterEmptyHref = new File("linkWordAfterEmptyHref");
	static File fileLinkEmpty = new File("linkEmpty");
	static File fileLinkNull = new File("lileLinkNull");	
	
	//URLs and Files to check parsing links properly
	
	static URL p1, p1a, p2, p3, p3a, p4, p4a, p5, p5a, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p16a, p17, p18,p18a, p19, p20, p20a, p21, p21a, p22, p23, p23a;
	static File fileP1 = new File("p1");
	static File fileP2 = new File("p2");
	static File fileP3 = new File("p3");
	static File fileP4 = new File("p4");
	static File fileP5 = new File("p5");
	static File fileP6 = new File("p6");
	static File fileP7 = new File("p7");
	static File fileP8 = new File("p8");
	static File fileP9 = new File("p9");
	static File fileP10 = new File("p10");
	static File fileP11 = new File("p11");
	static File fileP12 = new File("p12");
	static File fileP13 = new File("p13");
	static File fileP14 = new File("p14");
	static File fileP15 = new File("p15");
	static File fileP16 = new File("p16");
	static File fileP17 = new File("p17");
	static File fileP18 = new File("p18");
	static File fileP19 = new File("p19");
	static File fileP20 = new File("p20");
	static File fileP21 = new File("p21");
	static File fileP22 = new File("p22");
	static File fileP23 = new File("p23");
	
	//URLs and Files for checking default value set correctly
	static URL defaultMaxFile;
	static File fileDefaultMaxFile = new File("defaultMaxFile");
	static URL d1, d2, d3, d4, d5, d6;
	static File fileD1 = new File("d1");
	static File fileD2 = new File("d2");
	static File fileD3 = new File("d3");
	static File fileD4 = new File("d4");
	static File fileD5 = new File("d5");
	static File fileD6 = new File("d6");
	
	//URLs and File for checking duplicates
	
	static URL simpleDup,simpleDupSameBase, simpleDupNotCS, dupDomainSlash, dupNotIncreaseCount, dup1, dup2, dupEndSlashNotDomainNotSame, dupParse1, dupParsed,simpleDupCSPath;
	static File fileSimpleDup = new File("simpleDup");
	static File fileSimpleDupNotCS = new File("simpleDupNotCS");
	static File fileDupDomainSlash = new File("dupDomainSlash");
	static File fileDupNotIncreaseCount = new File("dupNotIncreaseCount");
	static File fileDupEndSlahNotDomainNotSame = new File("dupEndSlahNotDomainNotSame");
	static File fileDupParse = new File("dupParse");
	static File fileSimpleDupSameBase = new File("simpleDupSameBase");
	static File fileSimpleDupCSPath = new File("simpleDupCSPath");

	
	static Map<URL,File> testPages = new HashMap<URL,File>();
	static Map<URL, Integer> testResponses = new HashMap<URL,Integer>();

	//URLs and File for checking URL errors
	
	static URL urlMalfunction, secondLinkNotFound;
	static File fileURLMalfunction = new File("URLMalfunction");
	static File fileSecondLinkNotFound = new File("secondLinkNotFound");
	

	
	@BeforeClass
	public static void setUp() throws IOException
	{
		
		addPage(simpleLinkFound = new URL("http://simpleLinkFound.com/"),fileSimpleLinkFound,"Link was followed");
		testPages.put(simpleBaseLinkFound = new URL("http://baseLink.com/found"), fileSimpleLinkFound);
		testResponses.put(simpleBaseLinkFound,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(tagBasefNotReadIn = new URL("http://tagBasef.com/found"), fileSimpleLinkFound);
		testResponses.put(tagBasefNotReadIn,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(tagBasNotReadIn = new URL("http://tagBas.com/found"),fileSimpleLinkFound);
		testResponses.put(tagBasNotReadIn,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(baseAfterTagAIgnored = new URL("http://baseAfterTagA.com/found"), fileSimpleLinkFound);
		testResponses.put(baseAfterTagAIgnored,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(baseAfterEmptyBaseIgnored = new URL("http://baseAfterEmptyBase.com/found"),fileSimpleLinkFound);
		testResponses.put(baseAfterEmptyBaseIgnored,HttpURLConnection.HTTP_ACCEPTED);
		
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
		addPage(threeLinksC = new URL("http://linkC.com/"),fileThreeLinksC, "<a href=http://linkC1.com/></a><a href=http://linkC2.com/></a><a href=http://linkC3.com/></a>");
		testPages.put(linkA1 = new URL("http://linkA1.com/"), fileSimpleLinkFound);
		testResponses.put(linkA1,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkA2 = new URL("http://linkA2.com/"), fileSimpleLinkFound);
		testResponses.put(linkA2,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkA3 = new URL("http://linkA3.com/"), fileSimpleLinkFound);
		testResponses.put(linkA3,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkB1 = new URL("http://linkB1.com/"), fileSimpleLinkFound);
		testResponses.put(linkB1,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkB2 = new URL("http://linkB2.com/"), fileSimpleLinkFound);
		testResponses.put(linkB2,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkB3 = new URL("http://linkB3.com/"), fileSimpleLinkFound);
		testResponses.put(linkB3,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkC1 = new URL("http://linkC1.com/"), fileSimpleLinkFound);
		testResponses.put(linkC1,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkC2 = new URL("http://linkC2.com/"), fileSimpleLinkFound);
		testResponses.put(linkC2,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(linkC3 = new URL("http://linkC3.com/"), fileSimpleLinkFound);
		testResponses.put(linkC3,HttpURLConnection.HTTP_ACCEPTED);
		addPage(ftpLink = new URL("http://ftpLink.com/"), fileFTPLink, "<a href=http://linkA.com/></a><a href=ftp://linkB.com/>");
		
		//Files to test http only is searched
		
		testPages.put(ftpOnlyLink = new URL("ftp://ftpOnlyLink.com/"), fileSimpleLinkFound);
		testResponses.put(ftpOnlyLink,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(httpUpperCase = new URL("HTTP://httpUpperCase.com/"), fileSimpleLinkFound);
		testResponses.put(httpUpperCase,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(httpLowerCase = new URL("HTTP://httpLowerCase.com/"), fileSimpleLinkFound);
		testResponses.put(httpLowerCase,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(httpMixedCase = new URL("HTTP://httpMixedCase.com/"), fileSimpleLinkFound);
		testResponses.put(httpMixedCase,HttpURLConnection.HTTP_ACCEPTED);

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
		
		//Files to check href link functionality
		addPage(linkWithEOFBeforeClosedWithTagWhenQuotes = new URL("http://linkWithEOFBeforeClosedWithTagWhenQuotes.com/"),fileLinkWithEOFBeforeClosedWithTagWhenQuotes,"<a href='http://simpleLinkFound.com");
		addPage(linkEOFBeforeCloseQuote = new URL("http://linkEOFBeforeCloseQuote.com/"),fileLinkEOFBeforeCloseQuote,"<a href='http://simpleLinkFoun");
		addPage(linkWithTagClosedBeforeEndQuotes = new URL("http://linkWithTagClosedBeforeEndQuotes.com/"),fileLinkTagBeforeCloseQuote,"<a href='http://simpleLink>Found'");
		addPage(linkEOFBeforeClosedTag = new URL("http://linkEOFBeforeCloseQuote.com/"),fileLinkEOFBeforeClosedTag,"<a href=http://simpleLinkFoun");
		addPage(linkWithWordAfter = new URL("http://linkWithWordAfter.com/"),fileLinkWithWordAfter,"<a href='http://simpleLinkFound.com/' hidden>");
		addPage(linkWhitespaceThenWord = new URL("http://linkWhitespaceThenWord.com/"),fileLinkWhitespaceThenWord,"<a href=    http://simpleLinkFound.com/>");
		addPage(linkWhitespaceThenSingleQuotes = new URL("http://linkWhitespaceThenSingleQuotes.com/"),fileLinkWhitespaceThenSingleQuotes,"<a href=    'http://simpleLinkFound.com/'>");
		addPage(linkWhitespaceThenDoubleQuotes = new URL("http://linkWhitespaceThenDoubleQuotes.com/"),fileLinkWhitespaceThenDoubleQuotes,"<a href=    \"http://simpleLinkFound.com/\">");
		addPage(linkWordCloseTagAfter = new URL("http://linkWordCloseTagAfter.com/"),fileLinkWordCloseTagAfter,"<a href=http://simpleLinkFound.com/>");
		addPage(linkQuoteCloseTagAfter = new URL("http://linkQuoteCloseTagAfter.com/"),fileLinkQuoteCloseTagAfter,"<a href='http://simpleLinkFound.com/'>");
		addPage(linkWordAfterEmptyHref = new URL("http://linkWordAfterEmptyHref.com/"),fileLinkWordAfterEmptyHref,"<a href='' 'href=http://simpleLinkFound.com/>'");
		addPage(linkWordAfterNullHref = new URL("http://linkWordAfterNullHref.com/"),fileLinkWordAfterNullHref,"<a href 'href=http://simpleLinkFound.com/>'");
		addPage(linkEmpty = new URL("http://linkEmpty.com/"),fileLinkEmpty,"<a href=''");
		addPage(linkNull = new URL("http://linkNull.com/"),fileLinkNull,"<a href>");
		
		//Files to check link parsing as outlined in w3.org
		
		addPage(p1 = new URL("http://a/b/c/p1"),fileP1,"<a href=g>");
		addPage(p2 = new URL("http://a/b/c/p2"),fileP2,"<a href=./g>");
		addPage(p3 = new URL("http://a/b/c/p3"),fileP3,"<a href=g/>");
		addPage(p4 = new URL("http://a/b/c/p4"),fileP4,"<a href=/g>");
		addPage(p5 = new URL("http://a/b/c/p5"),fileP5,"<a href=//g>");
		addPage(p6 = new URL("http://a/b/c/p6"),fileP6,"<a href=?y>");
		addPage(p7 = new URL("http://a/b/c/p7"),fileP7,"<a href=g?y>");
		addPage(p8 = new URL("http://a/b/c/p8"),fileP8,"<a href=g?y/./x>");
		addPage(p9 = new URL("http://a/b/c/p9"),fileP9,"<a href=#s>");
		addPage(p10 = new URL("http://a/b/c/p10"),fileP10,"<a href=g#s>");
		addPage(p11 = new URL("http://a/b/c/p11"),fileP11,"<a href=g#s/./x >");
		addPage(p12 = new URL("http://a/b/c/p12"),fileP12,"<a href=g?y#s>");
		addPage(p13 = new URL("http://a/b/c/p13"),fileP13,"<a href=;x>");
		addPage(p14 = new URL("http://a/b/c/p14"),fileP14,"<a href=g;x>");
		addPage(p15 = new URL("http://a/b/c/p15"),fileP15,"<a href=g;x?y#s>");
		addPage(p16 = new URL("http://a/b/c/p16"),fileP16,"<a href=.>");
		addPage(p17 = new URL("http://a/b/c/p17"),fileP17,"<a href=./>");
		addPage(p18 = new URL("http://a/b/c/p18"),fileP18,"<a href=..>");
		addPage(p19 = new URL("http://a/b/c/p19"),fileP19,"<a href=../>");
		addPage(p20 = new URL("http://a/b/c/p20"),fileP20,"<a href=../g>");
		addPage(p21 = new URL("http://a/b/c/p21"),fileP21,"<a href=../..>");
		addPage(p22 = new URL("http://a/b/c/p22"),fileP22,"<a href=../../>");
		addPage(p23 = new URL("http://a/b/c/p23"),fileP23,"<a href=../../g>");
		testPages.put(p1a = new URL("http://a/b/c/g"),fileSimpleLinkFound);
		testResponses.put(p1a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p3a = new URL("http://a/b/c/g/"),fileSimpleLinkFound);
		testResponses.put(p3a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p4a = new URL("http://a/g"),fileSimpleLinkFound);
		testResponses.put(p4a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p5a = new URL("http://g/"),fileSimpleLinkFound);
		testResponses.put(p5a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p16a = new URL("http://a/b/c/"),fileSimpleLinkFound);
		testResponses.put(p16a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p18a = new URL("http://a/b/"),fileSimpleLinkFound);
		testResponses.put(p18a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p20a = new URL("http://a/b/g"),fileSimpleLinkFound);
		testResponses.put(p20a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p21a = new URL("http://a/"),fileSimpleLinkFound);
		testResponses.put(p21a,HttpURLConnection.HTTP_ACCEPTED);
		testPages.put(p23a = new URL("http://a/g"),fileSimpleLinkFound);
		testResponses.put(p23a,HttpURLConnection.HTTP_ACCEPTED);
		
		
		// Add check for default breadth and depth of files
		String breadth = "";
		int MAX_NUM = 25;
		for (int i=1; i<=MAX_NUM; i++)
		{
			String http="http://breadth"+ i + ".com/";
			breadth +="<a href="+http+ "></a>";
			testPages.put(new URL(http),fileSimpleLinkFound);
			testResponses.put(new URL(http),HttpURLConnection.HTTP_ACCEPTED);
		}

		addPage(defaultMaxFile = new URL("http://defaultMaxFile.com/"),fileDefaultMaxFile,breadth);
		
		
		addPage(d1 = new URL("http://d1.com/"),fileD1,"<a href=http://d2.com/>");
		addPage(d2 = new URL("http://d2.com/"),fileD2,"<a href=http://d3.com/>");
		addPage(d3 = new URL("http://d3.com/"),fileD3,"<a href=http://d4.com/>");
		addPage(d4 = new URL("http://d4.com/"),fileD4,"<a href=http://d5.com/>");
		addPage(d5 = new URL("http://d5.com/"),fileD5,"<a href=http://d6.com/>");
		addPage(d6 = new URL("http://d6.com/"),fileD6,"<a href=http://simpleLinkFound.com/>");
		
		
		//Files for duplicate testing
		
			addPage(simpleDupSameBase = new URL("http://simpleDupSameBase.com/"),fileSimpleDupSameBase,"<a href=http://simpleDupSameBase.com/></a>");
			addPage(simpleDup = new URL("http://simpleDup.com/"),fileSimpleDup,"<a href=http://simpleLinkFound.com/></a><a href=http://simpleLinkFound.com/></a>");
			addPage(simpleDupNotCS = new URL("http://simpleDupNotCS.com/"),fileSimpleDupNotCS,"<a href=http://simpleLinkFound.com/></a><a href=hTtp://simPleLinkFound.cOm/></a>");
			addPage(dupDomainSlash = new URL("http://dupDomainSlash.com/"),fileDupDomainSlash,"<a href=http://dupDomainSlash.com></a>");
			addPage(dupNotIncreaseCount = new URL("http://dupNotIncreaseCount.com/"),fileDupNotIncreaseCount,"<a href=http://dupNotIncreaseCount.com/></a><a href=http://simpleLinkFound.com/>");
			testPages.put(dup1 = new URL("http://duplicate.com/dup"),fileSimpleLinkFound);
			testResponses.put(dup1,HttpURLConnection.HTTP_ACCEPTED);
			testPages.put(dup2 = new URL("http://duplicate.com/dup/"),fileSimpleLinkFound);
			testResponses.put(dup2,HttpURLConnection.HTTP_ACCEPTED);
			addPage(dupEndSlashNotDomainNotSame= new URL("http://dupSlashNotEndNotSame.com/"),fileDupEndSlahNotDomainNotSame,"<a href=http://duplicate.com/dup/></a><a href=http://duplicate.com/dup>");
			testPages.put(dupParse1 = new URL("http://dupParse.com/dup/first/second"),fileSimpleLinkFound);
			testResponses.put(dupParse1,HttpURLConnection.HTTP_ACCEPTED);
			addPage(dupParsed= new URL("http://dupParse.com/dup/first/word"),fileDupParse,"<a href=http://dupParse.com/dup/first/second></a><a href=second></a>");
			addPage(simpleDupCSPath= new URL("http://simpleDupCSPath.com/"),fileSimpleDupCSPath,"<a href=http://duplicate.com/dup/></a><a href=http://duplicate.com/duP/>");
			testPages.put(new URL("http://duplicate.com/duP/"),fileSimpleLinkFound);
			testResponses.put(new URL("http://duplicate.com/duP/"),HttpURLConnection.HTTP_ACCEPTED);
		
		//Files for URL errors tested
			addPage(urlMalfunction = new URL("http://urlMalfunction.com/"),fileURLMalfunction,"<a href=p:/malfunction></a><a href=http://simpleLinkFound.com>");
			testPages.put(new URL("http://linkNotFound.com/"),null);
			testResponses.put(new URL("http://linkNotFound.com/"),HttpURLConnection.HTTP_NOT_FOUND);
			addPage(secondLinkNotFound = new URL("http://secondLinkNotFound.com/"),fileSecondLinkNotFound,"<a href=http://linkNotFound.com/></a><a href=http://simpleLinkFound.com>");
	}
	
	/*
	 * Adds a page to the test area for the HTMLStream
	 */
	private static void addPage(URL pageName, File pageFile, String pageContent) throws IOException
	{
		setBody(pageFile,pageContent);
		testPages.put(pageName, pageFile);
		testResponses.put(pageName, HttpURLConnection.HTTP_ACCEPTED);
	}
	
	private static void setBody(File file, String body) throws IOException
	{
		FileWriter fw = new FileWriter(file);
		fw.write(body);
		fw.close();
	}
	
	/*
	 * Provides a list of the results the webcrawler output into the file
	 */
	private static List<URL> getMatchedURLs(File file)
	{
		List<URL> rtn = new ArrayList<URL>();		
		
		try {
			Scanner sc = new Scanner(file);
			
			while (sc.hasNextLine())
			{
				String next = sc.nextLine();
				if (next.charAt(0) == '"')
					try {
						rtn.add(new URL(next.substring(1, next.length() - 1)));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
			}	
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		return rtn;
	}
	
	/*
	 * Cleans the stream, removes the database, and creates a fresh webcrawler before each test.
	 */
	@Before 
	public void cleanStart() throws MalformedURLException
	{
		HTMLStream.reset();
		HTMLStream.addTestURLs(testPages, testResponses);
		
		if (file.exists()) file.delete();
	
		wc = new WebCrawler(); 
	}
	
	@After
	public void removeFile()
	{
		if (file.exists()) file.delete();
	}
	
	//*************TESTS BEGIN HERE*******************//
	
	// TEST FUNCTIONALITY
	
	@Test
	public void testBreadthFirst()
	{
		wc = new WebCrawler(6,0);
		wc.crawl(threeLinks, file);
		assertEquals(6,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(linkA2));	
		assertFalse(HTMLStream.getSearchedURLs().contains(linkA3));		
	}
	
	@Test(expected=FileSystemAlreadyExistsException.class) 
	public void testErrorReturnedIfFileAlreadyExists() throws IOException
	{
		file.createNewFile();
		wc.crawl(simpleLinkFound, file);
	}
	
	@Test //Dont need to test default setting of this as covered elsewhere
	public void testOverwriteSearchFunction() throws MalformedURLException
	{
		SearchCriteria ftp = (url) -> (url.getProtocol().equals("ftp") ? true: false);
		wc = new WebCrawler(ftp,5,0);
		wc.crawl(ftpLink,file);
		assertEquals(4,HTMLStream.getSearchedURLs().size()); //expected 4 as one of them is an ftp and so wont be searched.
		List<URL> stored = getMatchedURLs(file);
		assertEquals(1,stored.size());
		assertTrue(stored.contains(new URL("ftp://linkB.com/")));
	}
	
	@Test //Dont need to test default setting of this as covered elsewhere
	public void testOverwriteSearchFunctionSimpleConstructor() throws MalformedURLException
	{
		SearchCriteria ftp = (url) -> (url.getProtocol().equals("ftp") ? true: false);
		wc = new WebCrawler(ftp);
		wc.crawl(ftpLink,file);
		List<URL> stored = getMatchedURLs(file);
		assertEquals(1,stored.size());
		assertTrue(stored.contains(new URL("ftp://linkB.com/")));
	}
	
	@Test
	public void testDefaultBreadthCorrect()
	{
		wc.crawl(defaultMaxFile, file);
		assertEquals(20,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void testDefaultDepthCorrect()
	{
		wc.crawl(d1,file);
		assertEquals(5,HTMLStream.getSearchedURLs().size());
	}

	// TEST DUPLICATES
	
	@Test
	public void testDuplicatesNotSearchedTwice()
	{		
		wc.crawl(simpleDup, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertEquals(2,getMatchedURLs(file).size());
	}
	@Test
	public void testDuplicatesNotSearchedTwiceSameAsBase()
	{
		wc.crawl(simpleDupSameBase, file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertEquals(1,getMatchedURLs(file).size());
	}
	@Test
	public void testDuplicatesNotCaseSensitiveDomain()
	{
		wc.crawl(simpleDupNotCS, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertEquals(2,getMatchedURLs(file).size());
	}
	@Test
	public void testDuplicatesCaseSensitivePath()
	{
		wc.crawl(simpleDupCSPath, file);
		assertEquals(3,HTMLStream.getSearchedURLs().size());
		assertEquals(3,getMatchedURLs(file).size());
	}
	@Test
	public void testSlashPutOnEndOfDomainAndDuplicateDetected()
	{
		wc.crawl(dupDomainSlash, file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
		assertEquals(1,getMatchedURLs(file).size());
	}
	@Test
	public void testDuplicateDoesntIncreaseCount()
	{
		wc= new WebCrawler(2,0);		
		wc.crawl(dupNotIncreaseCount, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
	}
	@Test
	public void testSlashOnEndNotDomainNotSame()
	{
		wc.crawl(dupEndSlashNotDomainNotSame, file);
		assertEquals(3,HTMLStream.getSearchedURLs().size());
		assertEquals(3,getMatchedURLs(file).size());
	}
	@Test
	public void testDuplicateDetectedOnParsedLink()
	{
		wc.crawl(dupParsed,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertEquals(2,getMatchedURLs(file).size());
	}

	
	// TEST STORING OF RESULTS
	
	//Testing that only those matched by search have been added has been added within the change to the default search function
	
	@Test
	public void testOneSiteAdded()
	{
		wc.crawl(simpleLinkFound,file);
		assertEquals(1,getMatchedURLs(file).size());
	}
	
	@Test
	public void testSitesAddedInBreadthAndDepth()
	{
		wc = new WebCrawler(0,3);
		wc.crawl(threeLinks,file);
		assertEquals(13,getMatchedURLs(file).size() );
	}
	
	// TEST TAG READ IN PROPERLY
	
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
		assertTrue(HTMLStream.getSearchedURLs().contains(simpleLinkFound));
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
	
	// TEST PROTOCOL
	
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
	
	// TEST HREF READ IN PROPERLY
	
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

	// TEST HREF LINK READ
	
	@Test
	public void testLinkAddedIfEOFEndNoTagWhenQuotes()
	{
		wc.crawl(linkWithEOFBeforeClosedWithTagWhenQuotes,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkNotAddedIfEOFEndBeforeEndQuotes()
	{
		wc.crawl(linkEOFBeforeCloseQuote,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkNotAddedIfTagClosedBeforeEndQuotes()
	{
		wc.crawl(linkWithTagClosedBeforeEndQuotes,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkNotAddedWhenEOFNoQuotes()
	{
		wc.crawl(linkEOFBeforeClosedTag,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkWhenWordAfter()
	{
		wc.crawl(linkWithWordAfter,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkAddedWhitespaceThenWord()
	{
		wc.crawl(linkWhitespaceThenWord,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkAddedWhitespaceThenSingleQuotes()
	{
		wc.crawl(linkWhitespaceThenSingleQuotes,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkAddedWhitespaceThenDoubleQuotes()
	{
		wc.crawl(linkWhitespaceThenDoubleQuotes,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkAddedWhenCloseDirectlyAfterNoQuotes()
	{
		wc.crawl(linkWordCloseTagAfter,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkAddedWhenCloseDirectlyAfterQuotes()
	{
		wc.crawl(linkQuoteCloseTagAfter,file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testLinkNotAddedAfterEmptyHref()
	{
		wc.crawl(linkWordAfterEmptyHref,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testNullLinkBeforeHref()
	{
		wc.crawl(linkWordAfterNullHref,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testEmptyLink()
	{
		wc.crawl(linkEmpty,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	@Test
	public void testNullLink()
	{
		wc.crawl(linkNull,file);
		assertEquals(1,HTMLStream.getSearchedURLs().size());
	}
	
	// TEST FORMING OF ABSOLUTE PATHS AND PARSING RELATIVE PATHS
	
	@Test 	// 		g = <URL:http://a/b/c/g>
	public void testParse1()
	{
		wc.crawl(p1, file);
		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
	}
     @Test//	./g = <URL:http://a/b/c/g>
 	public void testParse2()
 	{
 		wc.crawl(p2, file);
 		assertEquals(2,HTMLStream.getSearchedURLs().size());
 		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
 	}
     @Test//	g/ = <URL:http://a/b/c/g/>
  	public void testParse3()
  	{
  		wc.crawl(p3, file);
  		assertEquals(2,HTMLStream.getSearchedURLs().size());
  		assertTrue(HTMLStream.getSearchedURLs().contains(p3a));
  	}
     @Test//	/g = <URL:http://a/g>
   	public void testParse4()
   	{
   		wc.crawl(p4, file);
   		assertEquals(2,HTMLStream.getSearchedURLs().size());
   		assertTrue(HTMLStream.getSearchedURLs().contains(p4a));
   	}
     @Test//	 //g = <URL:http://g>
   	public void testParse5()
    	{
    		wc.crawl(p5, file);
    		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p5a));
    	}
     @Test// ?y         = <URL:http://a/b/c/d;p?y>
    public void testParse6()
 	{
 		wc.crawl(p6, file);
 		assertEquals(1,HTMLStream.getSearchedURLs().size());
 	}
     //	g?y        = <URL:http://a/b/c/g?y>
     @Test public void testParse7()
  	{
  		wc.crawl(p7, file);
  		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
  	}
     //	g?y/./x    = <URL:http://a/b/c/g?y/./x>
     @Test public void testParse8()
  	{
  		wc.crawl(p8, file);
  		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
  	}
     //	   #s         = <URL:http://a/b/c/d;p?q#s>
     @Test public void testParse9()
   	{
   		wc.crawl(p9, file);
   		assertEquals(1,HTMLStream.getSearchedURLs().size());
   	}
     //	   g#s        = <URL:http://a/b/c/g#s>
     @Test public void testParse10()
   	{
   		wc.crawl(p10, file);
   		assertEquals(2,HTMLStream.getSearchedURLs().size());
		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
   	}
     //	   g#s/./x    = <URL:http://a/b/c/g#s/./x>
     @Test public void testParse11()
    	{
       		wc.crawl(p11, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
       	}
     //	 g?y#s      = <URL:http://a/b/c/g?y#s>
    @Test public void testParse12()
 	{
    		wc.crawl(p12, file);
    		assertEquals(2,HTMLStream.getSearchedURLs().size());
 		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
    }
    //	;x= <URL:http://a/b/c/d;x>
     @Test public void testParse13()
 	{
    		wc.crawl(p13, file);
    		assertEquals(1,HTMLStream.getSearchedURLs().size());
    	}
     //	g;x        = <URL:http://a/b/c/g;x>
     @Test public void testParse14()
  	{
     		wc.crawl(p14, file);
     		assertEquals(2,HTMLStream.getSearchedURLs().size());
  		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
     }
     //			    g;x?y#s    = <URL:http://a/b/c/g;x?y#s>
     @Test public void testParse15()
  	{
     		wc.crawl(p15, file);
     		assertEquals(2,HTMLStream.getSearchedURLs().size());
  		assertTrue(HTMLStream.getSearchedURLs().contains(p1a));
     }
     //	.  = <URL:http://a/b/c/>
     @Test public void testParse16()
   	{
      		wc.crawl(p16, file);
      		assertEquals(2,HTMLStream.getSearchedURLs().size());
   		assertTrue(HTMLStream.getSearchedURLs().contains(p16a));
      }
     //		./         = <URL:http://a/b/c/>
     @Test public void testParse17()
    	{
       		wc.crawl(p17, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p16a));
       }
     //		.. = <URL:http://a/b/>
     @Test public void testParse18()
    	{
       		wc.crawl(p18, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p18a));
       }
     //		../ = <URL:http://a/b/>
     @Test public void testParse19()
    	{
       		wc.crawl(p19, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p18a));
       }
     //		../g  = <URL:http://a/b/g>
     @Test public void testParse20()
    	{
       		wc.crawl(p20, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p20a));
       }
     // ../..      = <URL:http://a/>
     @Test public void testParse21()
    	{
       		wc.crawl(p21, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p21a));
       }
     // ../../     = <URL:http://a/>
     @Test public void testParse22()
    	{
       		wc.crawl(p22, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p21a));
       }
     // ../../g    = <URL:http://a/g>
     @Test public void testParse23()
    	{
       		wc.crawl(p23, file);
       		assertEquals(2,HTMLStream.getSearchedURLs().size());
    		assertTrue(HTMLStream.getSearchedURLs().contains(p23a));
       }

	
	// TEST DEPTH AND LINK MAX SEARCH LIMITS WORK

	@Test(expected=IllegalArgumentException.class) 
	public void testMaxDepth0MaxFiles0error()
	{
		wc = new WebCrawler(0,0);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testMaxDepth0MaxFiles0errorFullConstructor()
	{
		wc = new WebCrawler((url)->true,0,0);
	}
	
	@Test
	public void maxDepth0DoesntLimit()
	{
		wc = new WebCrawler(5,0);
		wc.crawl(threeLinks,file);
		assertEquals(5,HTMLStream.getSearchedURLs().size());
	}
	
	@Test
	public void maxDepth0DoesntLimitFullConstructor()
	{
		wc = new WebCrawler((url)->true,5,0);
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
	
	@Test
	public void maxFile0DoesntLimitFullConstructor()
	{
		wc = new WebCrawler((url)->true,0,2);
		wc.crawl(threeLinks,file);
		assertEquals(4,HTMLStream.getSearchedURLs().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxDepthNegativeIntError()
	{
		wc = new WebCrawler(-2,4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxDepthNegativeIntErrorFullConstructor()
	{
		wc = new WebCrawler((url)->true,-2,4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxFileNegativeIntError()
	{
		wc = new WebCrawler(4,-2);

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxFileNegativeIntErrorFullConstructor()
	{
		wc = new WebCrawler((url)->true,4,-2);

	}
	
	@Test 
	public void maxDepthLimitAppliedProperly()
	{
		wc = new WebCrawler(20,2);
		wc.crawl(threeLinks,file);
		assertEquals(4,HTMLStream.getSearchedURLs().size());
	}
	
	@Test 
	public void maxDepthLimitAppliedProperlyFullConstructor()
	{
		wc = new WebCrawler((url)-> true,20,2);
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
	public void maxFileSearchedLimitProperlyFullConstructor()
	{
		wc = new WebCrawler((url)->true,3,20);
		wc.crawl(threeLinks,file);
		assertEquals(3,HTMLStream.getSearchedURLs().size());
	}
	
	@Test 
	public void checkNotSearchableProtocolCountedInLinkLimit()
	{
		wc = new WebCrawler(4,20);
		wc.crawl(ftpLink,file);
		assertEquals(3,HTMLStream.getSearchedURLs().size()); //this is 3 rather than 4 as the ftp wont be searched
		assertTrue(!HTMLStream.getSearchedURLs().contains(linkA2));
	}
	
	// TEST ERROR DISPLAYED WHEN URL INVALID
	
	@Test
	public void testURLMalfunctionNotAddedToMatchAndContinues()
	{
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		  System.setOut(new PrintStream(outContent));
		wc.crawl(urlMalfunction, file);
		assertEquals(2,getMatchedURLs(file).size());
		assertEquals("Malformed URL found: p:/malfunction\n", outContent.toString());
		  System.setOut(null);
	}
	@Test
	public void testURLErrorAddedToMatchAndContinues()
	{
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		wc.crawl(secondLinkNotFound, file);
		assertEquals(3,getMatchedURLs(file).size());
		assertEquals("Unsuccessful connection to http://linkNotFound.com/: 404\n", outContent.toString());
		System.setOut(null);
	}
	@Test
	public void testURLErrorFirstAddedToMatch() throws MalformedURLException
	{		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));
			wc.crawl(new URL("http://linkNotFound.com/"), file);
			assertEquals(1,getMatchedURLs(file).size());
			assertEquals("Unsuccessful connection to http://linkNotFound.com/: 404\n", outContent.toString());
			System.setOut(null);
	}
		
	
	@AfterClass //use this to delete files after test has run to avoid cluttering the program folder
	public static void deleteFiles()
	{

		fileSimpleLinkFound.delete();
		fileLittleA.delete();
		fileBigA.delete();
		fileMultipleSpaceA.delete();
		fileTabA.delete();
		fileMultipleTabA.delete();
fileLinebreakA.delete();
fileMultipleLinebreakA.delete();
fileWhitespaceMixA.delete();
filetagAh.delete();
fileTagSpaceBeforeA.delete();
fileWithinTag.delete();
fileMultipleTags.delete();
fileTagSpaceBeforeAll.delete();
fileTagEOF.delete();
fileTagEOFOnceRead.delete();
fileWithinElement.delete();
fileAfterTag.delete();
fileBaseLower.delete();
fileBaseUpper.delete();
fileBaseMixedCase.delete();
fileTagBasef.delete();
fileTagBas.delete();
fileTagEOFBas.delete();
fileBaseAfterTagA.delete();
fileBaseAfterBase.delete();
fileBaseAfterEmptyBase.delete();
fileThreeLinks.delete();
fileThreeLinksA.delete(); 
fileThreeLinksB.delete();
fileThreeLinksC.delete();
fileFTPLink.delete();
fileHrefOutsideTag.delete();
fileHrefAfterTag.delete();
fileHrefInBody.delete();
fileHrefInWrongTag.delete();
fileHrefUpperCase.delete();
fileHrefLowerCase.delete();
fileHrefAfterNullElement.delete();
fileHrefAfterEqualsElement.delete();
fileHrefAfterDoubleElement.delete();
fileHrefAfterSingleElement.delete();
fileHrefNoErrorEOFDuring.delete();
fileHrefNoErrorEOFAfterEnd.delete();
fileHrefNoErrorEOFAfterEquals.delete();
fileHreffNotIncluded.delete();
fileHreNotIncluded.delete();
fileHrefWSInPreviousDouble.delete();
fileHrefWSInPreviousSingle.delete();
fileHrefEndPreviousNotReadIn.delete();
fileHrefTwoInSameElement.delete();	
fileLinkWithEOFBeforeClosedWithTagWhenQuotes.delete();
fileLinkEOFBeforeCloseQuote.delete();
fileLinkTagBeforeCloseQuote.delete();
fileLinkWithWordAfter.delete();
fileLinkEOFBeforeClosedTag.delete();
fileLinkWhitespaceThenWord.delete();
fileLinkWhitespaceThenSingleQuotes.delete();
fileLinkWhitespaceThenDoubleQuotes.delete();
fileLinkWordCloseTagAfter.delete();
fileLinkQuoteCloseTagAfter.delete();
fileLinkWordAfterNullHref.delete();
fileLinkWordAfterEmptyHref.delete();
fileLinkEmpty.delete();
fileLinkNull.delete();
fileP1.delete();
fileP2.delete();
fileP3.delete();
fileP4.delete();
fileP5.delete();
fileP6.delete();
fileP7.delete();
fileP8.delete();
fileP9.delete();
fileP10.delete();
fileP11.delete();
fileP12.delete();
fileP13.delete();
fileP14.delete();
fileP15.delete();
fileP16.delete();
fileP17.delete();
fileP18.delete();
fileP19.delete();
fileP20.delete();
fileP21.delete();
fileP22.delete();
fileP23.delete();
fileDefaultMaxFile.delete();
fileD1.delete();
fileD2.delete();
fileD3.delete();
fileD4.delete();
fileD5.delete();
fileD6.delete();
fileSimpleDup.delete();
fileSimpleDupNotCS.delete();
fileDupDomainSlash.delete();
fileDupNotIncreaseCount.delete();
fileDupEndSlahNotDomainNotSame.delete();
fileDupParse.delete();
fileSimpleDupSameBase.delete();
fileSimpleDupCSPath.delete();
fileURLMalfunction.delete();
fileSecondLinkNotFound.delete();
	}
}
