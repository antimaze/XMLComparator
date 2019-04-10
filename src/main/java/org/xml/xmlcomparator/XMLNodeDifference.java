package org.xml.xmlcomparator;

import org.xml.xmlmodel.XMLNode;


enum NODE_DIFF_TYPE
{
	Node_Missing,
	Node_Extra
}

public class XMLNodeDifference implements IDiff
{
	private XMLNode node;
	private XMLNode parentNode;
	private NODE_DIFF_TYPE diffType;
	public XMLNodeDifference(XMLNode node, XMLNode parentNode, NODE_DIFF_TYPE diffType)
	{
		this.node = node;
		this.parentNode = parentNode;
		this.diffType = diffType;
	}
	
	public NODE_DIFF_TYPE getDiffType()
	{
		return this.diffType;
	}

	public XMLNode getNode()
	{
		return this.node;
	}
	
	public XMLNode getParentNode()
	{
		return this.parentNode;
	}
	
	public void generateDiff(IDiffGenerator diffGenerator) 
	{
		diffGenerator.generateDiff(this);
	}
}
