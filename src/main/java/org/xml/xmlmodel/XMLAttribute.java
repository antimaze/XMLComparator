package org.xml.xmlmodel;

public class XMLAttribute
{
	private StringBuilder key;
	private StringBuilder value;
	
	public XMLAttribute(StringBuilder key, StringBuilder value)
	{
		this.key = key;
		this.value = value;
	}
	
	public StringBuilder getKey()
	{
		return this.key;
	}
	
	public StringBuilder getValue()
	{
		return this.value;
	}
	
	public StringBuilder getXMLAttributeText()
	{
		StringBuilder text = new StringBuilder();
		text.append(key).append("=\"").append(value).append("\"");
		return text;
	}
}
