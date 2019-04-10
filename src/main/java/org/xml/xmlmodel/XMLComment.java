package org.xml.xmlmodel;

public class XMLComment extends XMLElement
{
	private StringBuilder comment = null;
	public XMLComment(StringBuilder comment) 
	{
		this.comment = comment;
	}

	public StringBuilder getCommentText()
	{
		return new StringBuilder().append("<-- ").append(this.comment).append(" -->");
	}
	
	@Override
	public XMLElementType getElementType() 
	{
		return XMLElementType.XMLComment;
	}
}
