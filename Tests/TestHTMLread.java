package Tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import Impls.HTMLread;

/*
 * This class tests the HTMLread functions.
 * 
 */

public class TestHTMLread {

	//Test ReadUntil
	
	@Test
	public void testReadUntilFirstMatch()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertTrue(HTMLread.readUntil(s, 'o', 'r'));
	}
	
	@Test
	public void testReadUntilFirstMatchFirstPosition()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertTrue(HTMLread.readUntil(s, 'w', 'o'));
	}
	
	@Test
	public void testReadUntilSecondMatch()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertFalse(HTMLread.readUntil(s, 'r', 'o'));
	}
	
	@Test
	public void testReadUntilSecondMatchFirstPosition()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertFalse(HTMLread.readUntil(s, 'o', 'w'));
	}
	
	@Test
	public void testReadUntilSameParameters()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertTrue(HTMLread.readUntil(s, 'o', 'o'));
	}
	
	@Test
	public void testReadUntilEOF()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertFalse(HTMLread.readUntil(s, 'x', 'y'));
	}
	
	@Test
	public void testReadUntilNotCaseSensitive()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertTrue(HTMLread.readUntil(s, 'O', 'r'));
	}
	
	@Test
	public void testReadUntilEmptyStream()
	{
		String in = "";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertFalse(HTMLread.readUntil(s, 'O', 'r'));
	}
	
	//Test skipSpace
	
	@Test
	public void testSkipspaceParameterFirst()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(Character.MIN_VALUE, HTMLread.skipSpace(s,'w'));
	}
	
	@Test
	public void testSkipspaceNotParameterFirst()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals('w', HTMLread.skipSpace(s,'o'));
	}
	
	@Test
	public void testSkipspaceWhitespaceThenParameter()
	{
		String in = "  words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(Character.MIN_VALUE, HTMLread.skipSpace(s,'w'));
	}
	
	@Test
	public void testSkipspaceWhitespaceThenNotParameter()
	{
		String in = "  words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals('w', HTMLread.skipSpace(s,'o'));
	}
	
	@Test
	public void testSkipspaceWhitespaceThenEOF()
	{
		String in = "  ";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(Character.MIN_VALUE, HTMLread.skipSpace(s,'o'));
	}
	@Test
	public void testSkipspaceWhitespaceTypesFirst()
	{
		String in = "  \t\nwords";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals('w', HTMLread.skipSpace(s,'o'));
	}
	@Test
	public void testSkipspaceEmptyFile()
	{
		String in = "";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(Character.MIN_VALUE, HTMLread.skipSpace(s,'o'));
	}
	
	//Test ReadString
	
	@Test
	public void testReadStringParameterFoundFirstLocation()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals("", HTMLread.readString(s,'w','o'));
	}
	
	@Test
	public void testReadStringFirstParameterFound()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals("wo", HTMLread.readString(s,'r','d'));
	}
	
	@Test
	public void testReadStringSecondParameterFoundFirstLocation()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readString(s,'o','w'));
	}
	
	@Test
	public void testReadStringSecondParameterFound()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readString(s,'s','d'));
	}
	
	@Test
	public void testReadStringCaseSensitive()
	{
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readString(s,'O','d'));
	}
	
	@Test
	public void testReadStringEmptyStream()
	{
		String in = "";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readString(s,'O','d'));
	}
	
	@Test
	public void testReadStringEOF()
	{
		String in = "wo";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readString(s,'x','y'));
	}
	
	//Test readStringUntilWhitespace()
	
	@Test
	public void testReadStringUntilWhitespaceParameterFoundFirstLocation() {
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals("", HTMLread.readStringUntilWhitespace(s,'w'));
	}
	@Test
	public void testReadStringUntilWhitespaceParameterFound() {
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals("wo", HTMLread.readStringUntilWhitespace(s,'r'));
	}
	@Test
	public void testReadStringUntilWhitespaceWhitespaceFoundFirstLocation() {
		String in = " words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals("", HTMLread.readStringUntilWhitespace(s,'r'));
	}
	@Test
	public void testReadStringUntilWhitespaceWhitespaceFound() {
		String in = "wo rds";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals("wo", HTMLread.readStringUntilWhitespace(s,'s'));
	}
	@Test
	public void testReadStringUntilWhitespaceEmptyStream() {
		String in = "";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readStringUntilWhitespace(s,'s'));
	}
	@Test
	public void testReadStringUntilWhitespaceEOF() {
		String in = "words";
		InputStream s = new ByteArrayInputStream(in.getBytes());
		assertEquals(null, HTMLread.readStringUntilWhitespace(s,'x'));
	}
	
	

}
