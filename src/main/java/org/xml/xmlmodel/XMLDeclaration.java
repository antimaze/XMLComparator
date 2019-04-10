package org.xml.xmlmodel;

public class XMLDeclaration extends XMLElement
{
	private StringBuilder version;
	private StringBuilder encoding;
	private StringBuilder standalone;
	
	public XMLDeclaration(StringBuilder version) 
	{
		this(version, new StringBuilder(), new StringBuilder());
	}
	
	public XMLDeclaration(StringBuilder version, StringBuilder encoding) 
	{
		this(version, encoding, new StringBuilder());
	}
	
	public XMLDeclaration(StringBuilder version, StringBuilder encoding, StringBuilder standalone) 
	{
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}
	
	public StringBuilder getVersion()
	{
		return this.version;
	}
	
	public StringBuilder getEncoding()
	{
		return this.encoding;
	}
	
	public void setEncoding(StringBuilder encoding)
	{
		this.encoding = encoding;
	}
	
	public StringBuilder getStandalone()
	{
		return this.standalone;
	}
	
	public void setStandalone(StringBuilder standalone)
	{
		this.standalone = standalone;
	}

	@Override
	public XMLElementType getElementType() 
	{
		return XMLElementType.XMLDeclaration;
	}
}
