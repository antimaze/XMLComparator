package org.xml.xmlmodel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.xml.xmlparser.XMLParser;

public class XMLDocument
{
	private HashMap<Integer, XMLElement> elementsMap;
	private XMLElement xmlDeclaration = null;
//	private XMLComment xmlComment = null;
	private XMLNode rootNode = null;
	
	
	public XMLDocument() 
	{
		elementsMap = new HashMap<Integer, XMLElement>();
	}
	
	
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
	
	public void setXMLDeclaration(XMLElement xmlDeclaration)
	{
		this.xmlDeclaration = xmlDeclaration;
	}
	
	public XMLElement getXMLDeclaration()
	{
		return this.xmlDeclaration;
	}
	
	public void addElement(Integer index, XMLElement element)
	{
		elementsMap.put(index, element);
	}
	
	public XMLElement getElement(Integer index)
	{
		return elementsMap.get(index);
	}
}
