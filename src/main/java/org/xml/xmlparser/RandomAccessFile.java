package org.xml.xmlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RandomAccessFile implements RandomAccessRead
{
	private final java.io.RandomAccessFile ras;
    private boolean isClosed;

    /**
     * Constructor.
     *
     * @param file The file to write the data to.
     * @param mode The writing mode.
     * @throws FileNotFoundException If the file cannot be created.
     */
    public RandomAccessFile(File file, String mode) throws FileNotFoundException
    {
        ras = new java.io.RandomAccessFile(file, mode);
    }

	public void close() throws IOException
	{
		checkClosed();
        ras.seek(0);
        ras.setLength(0);
	}

	public int read() throws IOException
	{
		checkClosed();
        return ras.read();
	}

	public int read(byte[] b) throws IOException
	{
		checkClosed();
        return ras.read(b);
	}

	public int read(byte[] b, int offset, int length) throws IOException
	{
		checkClosed();
        return ras.read(b, offset, length);
	}

	public long getPosition() throws IOException
	{
		checkClosed();
        return ras.getFilePointer();
	}

	public void seek(long position) throws IOException 
	{
		checkClosed();
        ras.seek(position);
	}

	public long length() throws IOException
	{
		checkClosed();
        return ras.length();
	}

	public boolean isClosed() 
	{
		return isClosed;
	}

	public int peek() throws IOException
	{
		int result = read();
        if (result != -1)
        {
            rewind(1);
        }
        return result;
	}

	public void rewind(int bytes) throws IOException 
	{
		checkClosed();
        ras.seek(ras.getFilePointer() - bytes);
	}

	public byte[] readFully(int length) throws IOException
	{
		checkClosed();
        byte[] b = new byte[length];
        ras.readFully(b);
        return b;
	}

	public boolean isEOF() throws IOException
	{
		return peek() == -1;
	}

	public int available() throws IOException
	{
		checkClosed();
        return (int) Math.min(ras.length() - getPosition(), Integer.MAX_VALUE);
	}
	
	/**
     * Ensure that the RandomAccessFile is not closed
     * 
     * @throws IOException
     */
    private void checkClosed() throws IOException
    {
        if (isClosed)
        {
            throw new IOException("RandomAccessFile already closed");
        }

    }
}
