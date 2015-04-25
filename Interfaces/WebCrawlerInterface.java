package Interfaces;

import java.io.File;
import java.net.URL;

/**
 * This is the interface for a Web Crawler.
 * 
 * There is only one public method, which is crawl(). The user provides this with a URL to crawl and the crawler provides a file specified by the user
 * containing all the links obtained by crawling the pages. This interface does not include a search method, as instead this is definable by the user within
 * the constructors by use of setting a Lamba.
 * 
 * @author Paul Day
 */

/*
 * Any class implementing this interface should use constructors to let the user define certain parameters, should they desire:
 * 		- Max search depth
 * 		- Max files searched
 * 		- Input a Lamba search function to define which files match the user defined serach criteria.
 * 
 */


public interface WebCrawlerInterface {
	
	/**
	 * This method crawls all the links found by following links found within the URL provided by the user. Those which match criteria are stored in the
	 * file as defined by the user.
	 *  
	 * 
	 * @param url the url to start the search from
	 * @param file the file where the results are stored
	 */
	
	public void crawl(URL url, File file);
	
}
