package org.xml.xmlcomparator;

import org.xml.xmlmodel.XMLDocument;

public class XMLDocumentComparator
{
	XMLDocument document1;
	XMLDocument document2;
	
	public XMLDocumentComparator(XMLDocument document1, XMLDocument document2) 
	{
		this.document1 = document1;
		this.document2 = document2;
	}
		
	public void compareXMLDocuments(Comparator comparator)
	{
		comparator.compare(document1, document2);
	}
}
