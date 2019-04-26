package org.xml.xmlparser;

import java.io.Closeable;
import java.io.IOException;

public interface ContinuousSource extends Closeable
{
	/**
	 * reads a single byte of data...
	 * 
	 * @return byte of data...
	 * @throws IOException
	 */
	int read() throws IOException;
	
	/**
	 * reads the buffer of data...
	 * 
	 * @param b buffer to write the data to...
	 * @return number of bytes that are read...
	 * @throws IOException
	 */
	int read(byte []b) throws IOException;
	
	/**
	 * read the buffer of data...
	 * 
	 * @param b buffer to write the data to...
	 * @param offSet offset into the buffer to start writing...
	 * @param length length of data to be read...
	 * @return number of byte that are read...
	 * @throws IOException
	 */
	int read(byte []b, int offSet, int length) throws IOException;
	
	/**
	 * returns the offset of next byte to be returned by a read method...
	 * @return offset of the next byte...
	 * @throws IOException
	 */
	long getPosition() throws IOException;
	
	/**
	 * this will peek at the next byte...
	 * 
	 * @return next byte on the stream...
	 * @throws IOException
	 */
	int peek() throws IOException;
	
	/**
	 * unreads the single byte of the data...
	 * 
	 * @param b byte to push back...
	 * @throws IOException
	 */
	void unread(int b) throws IOException;
	
	/**
	 * unread array of bytes...
	 * 
	 * @param b bytes to push back...
	 * @throws IOException
	 */
	void unread(byte[] b) throws IOException;
	
	/**
	 * unread the portion of an array of bytes...
	 * 
	 * @param b bytes to be unread...
	 * @param start starting index...
	 * @param length number of bytes to be unread...
	 * @throws IOException
	 */
	void unread(byte []b, int start, int length) throws IOException;
	
	/**
	 * reads a number of bytes in its entirety...
	 * @param length
	 * @return
	 * @throws IOException
	 */
	byte[] readFully(int length) throws IOException;
	
	/**
	 * check if it's end of source file or not...
	 * @return
	 * @throws IOException
	 */
	boolean isEOF() throws IOException;
	
	/**
     * Returns an estimate of the number of bytes that can be read.
     *
     * @return the number of bytes that can be read
     * @throws IOException if this random access has been closed
     */
    int available() throws IOException;
}
