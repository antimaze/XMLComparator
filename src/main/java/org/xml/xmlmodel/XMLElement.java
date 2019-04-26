package org.xml.xmlmodel;

public abstract class XMLElement
{
	public static enum XMLElementType
	{
		XMLDeclaration,
		XMLSimpleDeclaration,
		XMLNode,
		XMLComment,
		XMLValue,
		XML_CDATA,
		DOCTYPE,
		None
	}

	public abstract XMLElementType getElementType();
}
