import java.io.ByteArrayInputStream;
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
	 * Returns true of the first provided parameter is encountered and false if it is the second.
	 * 
	 * TODO: Determine if the character itself is consumed as well (should become clear later). Assumed yes for now.
	 * TODO: What to return if an end file is found at the end? Currently return false when find -1 (eof)
	 * TODO: What id paramters the same? Currently return ch1 
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
	 * TODO: What to do if the end of file is found? Currently return lowest as well. May not be most practical.
	 * TODO: What if the char is actually whitespace? Currently it still returns lowest.
	 * TODO: Case sensitive? Assumed that it is. 
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
	
	public static String readString(InputStream stream, char c1, char c2)
	{
		return null;
	}
	
	public static void main(String[] args) throws IOException
	{
		//Proper tests will be written later once scopes fully understood. Below experiments to make sure id the right approach for my needs
		
		String test = "This is a string to test.";
		InputStream in = new ByteArrayInputStream(test.getBytes());
		System.out.println("Should be true: " + HTMLread.readUntil(in,'H','i'));
		System.out.println("Should be false: " + HTMLread.readUntil(in,'H','i'));
		System.out.println("Should be s: " + (char)in.read());
		System.out.println("Should be false: " + HTMLread.readUntil(in,'z','p'));
		
		String test2 = "  A        b             c";
		InputStream in2 = new ByteArrayInputStream(test2.getBytes());
		System.out.println("Should be null: " + HTMLread.skipSpace(in2, 'A'));
		System.out.println("Should be b: " + HTMLread.skipSpace(in2, 'A'));
		System.out.println("Should be c: " + HTMLread.skipSpace(in2, 'A'));
		System.out.println("Should be null: " + HTMLread.skipSpace(in2, 'A'));
		
	}
	
}
