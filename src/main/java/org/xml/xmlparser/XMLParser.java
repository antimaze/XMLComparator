package org.xml.xmlparser;

import java.io.File;
import java.io.FileNotFoundException;

import org.xml.xmlmodel.XMLDocument;

public class XMLParser extends BaseParser
{
	public XMLParser(File file) throws FileNotFoundException
	{
		super(new RandomAccessSource(new RandomAccessFile(file, "r")));
	}
	
	public XMLDocument getXMLDocumnet()
	{
		return document;
	}
}
