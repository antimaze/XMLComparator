package org.xml.xmlgenerator;

import java.io.File;
import org.xml.xmlmodel.XMLDocument;
import org.xml.xmlmodel.XMLNode;

public class XMLGenerator 
{
	private final XMLDocument document;
	private final File output;
	
	public XMLGenerator(XMLDocument document, File output) 
	{
		this.document = document;
		this.output = output;
	}
	
	public void generateXML()
	{
		System.out.println("");
		if(output == null)
		{
			XMLNode rootNode = document.getRootNode();
			if(rootNode != null)
			{
				System.out.println(rootNode.getXMLTagText());
			}
		}
	}
}
