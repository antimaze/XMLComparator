package org.xml.xmlmodel;

public class DOCTYPE extends XMLElement
{
	private StringBuilder docTypeString;
	public DOCTYPE(StringBuilder docTypeString) 
	{
		this.docTypeString = docTypeString;
	}
	
	public StringBuilder getDocTypeString() 
	{
		return docTypeString;
	}
	
	public void setDocTypeString(StringBuilder docTypeString) 
	{
		this.docTypeString = docTypeString;
	}

	@Override
	public XMLElementType getElementType() 
	{
		return XMLElementType.DOCTYPE;
	}
}
