package org.xml.xmlmodel;

public class XMLValue extends XMLElement
{
	private StringBuilder value;
	public XMLValue(StringBuilder value) 
	{
		this.value = value;
	}
	
	public StringBuilder getValue() 
	{
		return value;
	}
	
	public void setValue(StringBuilder value)
	{
		this.value = value;
	}

	@Override
	public XMLElementType getElementType() 
	{
		return XMLElementType.XMLValue;
	}
}
