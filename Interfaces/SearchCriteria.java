package Interfaces;

import java.net.URL;

/**
 * Interface for specifying the conditions that are being searched for. This defines what the search match criteria
 * is for a WebCrawler.
 * 
 * @author Paul Day
 *
 */

public interface SearchCriteria {

	boolean match(URL url);
	
}
