package org.xml.xmlmodel;

public abstract class XMLElement
{
	public static enum XMLElementType
	{
		XMLDeclaration,
		XMLNode,
		XMLComment,
		XMLValue,
		XML_CDATA,
		None
	}

	public abstract XMLElementType getElementType();
}
