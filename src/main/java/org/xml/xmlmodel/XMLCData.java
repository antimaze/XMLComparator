package org.xml.xmlmodel;

public class XMLCData extends XMLElement
{
	private StringBuilder CDATA = null;
	public XMLCData(StringBuilder CDATA) 
	{
		this.CDATA = CDATA;
	}
	
	public StringBuilder getCDATAText()
	{
		return new StringBuilder().append("<![CDATA[").append(this.CDATA).append("]]>");
	}

	@Override
	public XMLElementType getElementType() 
	{
		return XMLElementType.XML_CDATA;
	}

}
