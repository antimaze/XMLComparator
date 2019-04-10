package org.xml.xmlparser;

import java.io.IOException;

public final class RandomAccessSource implements ContinuousSource
{

	private final RandomAccessRead reader;
	public RandomAccessSource(RandomAccessRead reader) 
	{
		this.reader = reader;
	}
	
	public void close() throws IOException
	{
		reader.close();
	}

	public int read() throws IOException 
	{
		return reader.read();
	}

	public int read(byte[] b) throws IOException 
	{
		return reader.read(b);
	}

	public int read(byte[] b, int offSet, int length) throws IOException
	{
		return reader.read(b, offSet, length);
	}

	public long getPosition() throws IOException
	{
		return reader.getPosition();
	}

	public int peek() throws IOException
	{
		return reader.peek();
	}

	public void unread(int b) throws IOException 
	{
		reader.rewind(b);
	}

	public void unread(byte[] b) throws IOException
	{
		reader.readFully(b.length);
	}

	public void unread(byte[] b, int start, int length) throws IOException
	{
		reader.rewind(length - start);
	}

	public byte[] readFully(int length) throws IOException
	{
		return reader.readFully(length);
	}

	public boolean isEOF() throws IOException 
	{
		return reader.isEOF();
	}

}
