package Impls;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that provides methods to interpret an InputStream, particularly useful for interpreting a stream 
 * of html. 
 * 
 * @author Paul Day
 */

public class HTMLread {

	/**
	 * Consumes the stream until a character provided as a parameter is encountered. This is not case sensitive.
	 * 
	 * Returns true if the first provided parameter is encountered and false if it is the second. If end of file is encountered
	 * false shall be returned.
	 * 
	 *  If the input parameters are the same, true is returned if it is encountered.
	 * 
	 * @param stream the input stream
	 * @param ch1 the first character to match
	 * @param ch2 the second character to match
	 * @return whether the first or second character parameter is encountered first

	 */
	public static boolean readUntil(InputStream stream, char ch1, char ch2)
	{
		int n;
		try {
			while ((n = stream.read()) != -1)
			{
				if (Character.toUpperCase((char) n) == Character.toUpperCase(ch1)) return true;
				else if (Character.toUpperCase((char) n) == Character.toUpperCase(ch2)) return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return false;
	}
	
	/**
	 * Consumes an input stream until a non whitespace character is consumed. If this character is not the same as the input character,
	 * the character is returned, else the lowest character value is provided.
	 * 
	 * @param stream the input stream
	 * @param ch the character which returns will lowest character value
	 * @return the non whitespace character encountered or the lowest character value should it match the input parameter
	 */
	public static char skipSpace(InputStream stream, char ch)
	{
		int n;
		
		try {
			while ((n = stream.read()) != -1)
			{
				if ((char) n == ch) return Character.MIN_VALUE;
				else if (!Character.isWhitespace(n)) return (char) n;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Character.MIN_VALUE;
	}
	
	/**
	 * Consumes the input stream until either one of the parameters are encountered. Encounters are case sensitive. If the first parameter is 
	 * encountered, a String containing the characters read is returned. Otherwise the null string is returned.The parameter found
	 * is not returned at the end of the String.
	 * 
	 * If end of file is encountered the null string is returned.
	 * 
	 * @param stream the input stream
	 * @param c1 the character that, once encountered, returns the String of chars up to this point, including the last parameter
	 * @param c2 the character that, once encountered, returns the null string.
	 * @return the String of chars leading up to the first parameter, or null if the second parameter was encountered.
	 */
	
	public static String readString(InputStream stream, char c1, char c2)
	{
		String output = "";
		int n;
		try {
			while ((n = stream.read()) != -1)
			{
				if ((char) n == c1) return output;
				else if ((char) n  == c2) return null;
				else output += (char) n;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Consumes the input stream building a string of all values until either whitespace or the input parameter is found.
	 * 
	 * The final parameter is not returned.
	 * 
	 * Returns null if the end of file was encountered before the parameters.
	 * 
	 * @param stream the input stream
	 * @param ch the character which stops the stream being consumed
	 * @return the String of all chars encountered until the whitespace or parameter was encountered
	 */
	
	public static String readStringUntilWhitespace(InputStream stream, char ch)
	{
		String output = "";
		int n;
		try {
			while ((n = stream.read()) != -1)
			{
				if (Character.isWhitespace((char) n) || (char) n == ch) return output;
				else output += (char) n;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
