# Crawler
A web crawler as specified in coursework 5.

The method chosen for search function was by defining a search function via a Lambda of the created interface
"SearchCriteria".

Details on the approach taken and usage notes can be found at the top of the WebCrawler.java file within the
folder Impls.

Details for the bulk of the testing can be found at the top of the TestWebCrawler.java file within the folder Tests.

Although standard has been so far to call interfaces a descriptive name and have implementation have Impl after thier name, the spec says that the class should be called WebCrawler, and so interfaces have Interface after thier name instead.

The following implementations are required, all found within the Impls folder:
    -WebCrawler: the main webcrawler method
    -HTMLread: the metods to read HTML, as outlined in the spec. An additional method was added (readStringUntilWhitespace) beyond that in the spec as it was useful.
    -HTMLStream: A class which provides the stream to the webcrawler, and can have test pages stored in it instead providing a mock stream of strings. Mainly used for testing, when running outside of testing the class provides a stream normally.
    -StoredTempUrl: A wrapper class which just holds a URL and its priority to be worked.
    -StreamHolder: A wrapper class holding the connection status for a connection and its stream.
    
The following interfaces are required, stored within the Interfaces folder:
    -SearchCriteria: Interface providing a means for a user to define thier own search conditions
    -WebCrawlerInterface: The interface the webcrawler implements
    -Note: due to the static nature of the HTMLread and HTMLStream classes, these do not have interfaces
    
The test files can be found in the folder Tests.
    
JavaDoc can be found within the doc folder.

