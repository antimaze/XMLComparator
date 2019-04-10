package org.xml.xmlmodel;

import java.io.File;
import java.io.IOException;

import org.xml.xmlparser.XMLParser;

public class XMLDocument
{
	private XMLDeclaration xmlDeclaration = null;
//	private XMLComment xmlComment = null;
	private XMLNode rootNode = null;
	public static XMLDocument load(File file) throws IOException
	{
		XMLParser parser = new XMLParser(file);
		XMLDocument doc = new XMLDocument();
		try 
		{
			parser.parse();
			doc = parser.getXMLDocumnet();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return doc;
	}
	
	public boolean setRootNode(XMLNode node)
	{
		if(rootNode == null)
		{
			rootNode = node;
			return true;
		}
		
		return false;
	}
	
	public XMLNode getRootNode()
	{
		return this.rootNode;
	}
	
	public void setXMLDeclaration(XMLDeclaration xmlDeclaration)
	{
		this.xmlDeclaration = xmlDeclaration;
	}
	
	public XMLDeclaration getXMLDeclaration()
	{
		return this.xmlDeclaration;
	}
}
