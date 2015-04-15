import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;


/**
 * This class returns the stream of a URL. It can also have a url database fed into it which it will refer to instead, rather than connecting to http://. This
 * is useful for things such as testing;
 * 
 * @author Paul Day
 */

public class HTMLStream {

	private static Map<URL,String> pages = null;
	
/**
 * Sets the map of URL and corresponding return pages.
 * 
 * @param testPages the map containing each test URL and the corresponding page to return if it is searched
 */
	
	public static void addTestURLs(Map<URL,String> testPages) {pages = testPages;}

	/**
	 * Returns the stream from an input URL, either from an http: connection or of a set page should a testing urls have been defined
	 * 
	 * @param url the url of which to open the stream of
	 * @return the stream of the URL, and the stream of the string page should a testing map have been added
	 * @throws IOException
	 */
	
	public static InputStream getStream(URL url) throws IOException
	{
		return (pages.isEmpty() ? url.openStream() : new ByteArrayInputStream(pages.get(url).getBytes()));
	}
	
	/**
	 * Resets the stream to no longer use a test database but rather return the stream from the http: connection.
	 */
	
	public static void clearSetURLS() 
	{
		pages=null;
	}
	
	
}
