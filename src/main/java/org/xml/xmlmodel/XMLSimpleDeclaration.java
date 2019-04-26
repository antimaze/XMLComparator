package org.xml.xmlmodel;

public class XMLSimpleDeclaration extends XMLElement
{
	private StringBuilder simpleDeclarationData;
	public XMLSimpleDeclaration(StringBuilder simpleDeclarationData) 
	{
		this.simpleDeclarationData = simpleDeclarationData;
	}
	
	public StringBuilder getSimpleDeclarationData() 
	{
		return simpleDeclarationData;
	}
	
	public void setSimpleDeclarationData(StringBuilder simpleDeclarationData) 
	{
		this.simpleDeclarationData = simpleDeclarationData;
	}

	@Override
	public XMLElementType getElementType()
	{
		return XMLElementType.XMLSimpleDeclaration;
	}	
}
