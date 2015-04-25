package Impls;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class returns the stream of a URL. It can also have a url database fed into it which it will refer to instead, rather than connecting to http://. Further, should
 * a test database be set, it will also store the list of URLs which have been searched. This functionality makes testing the crawler easier.
 * 
 * @author Paul Day
 */

public class HTMLStream {

	private static Map<URL,File> pages = null;
	private static Map<URL,Integer> responses = null;
	private static List<URL> searchedURLs = new ArrayList<URL>();
	
/**
 * Sets the map of URL and corresponding return pages.
 * 
 * @param testPages the map containing each test URL and the corresponding page to return if it is searched
 */
	
	public static void addTestURLs(Map<URL,File> testPages, Map<URL,Integer> testResponses) 
	{
		pages = testPages;
		responses=testResponses;
	}

	/**
	 * Returns the stream from an input URL, either from an http: connection or of a set page should a testing urls have been defined. Should a test database be defined,
	 * the search will be recorded in the database.
	 * 
	 * @param url the url of which to open the stream of
	 * @return the stream of the URL, and the stream of the string page should a testing map have been added
	 * @throws IOException
	 */
	
	public static StreamHolder getStream(URL url) throws IOException
	{
		StreamHolder rtnObject = new StreamHolder();
		if (pages == null) {
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			if ((rtnObject.response = c.getResponseCode()) == HttpURLConnection.HTTP_ACCEPTED) rtnObject.stream = url.openStream();
		}
		else 
		{
			searchedURLs.add(url);
			if ((rtnObject.response = responses.get(url)) == HttpURLConnection.HTTP_ACCEPTED) rtnObject.stream = new FileInputStream(pages.get(url));
		}
		
		return rtnObject;
	}
	
	/**
	 * Resets the stream to no longer use a test database but rather return the stream from the http: connection. It also resets the search history of the crawler.
	 */
	
	public static void reset() 
	{
		pages=null;
		responses=null;
		searchedURLs = new ArrayList<URL>();
	}
	
	/**
	 * Returns the list of recorded URLs that have been searched. Useful for testing functionality of the WebCrawler.
	 * 
	 * @return a list of URLs that were searched, in the order in which they were searched.
	 */
	
	public static List<URL> getSearchedURLs() {return searchedURLs;}
	

}
