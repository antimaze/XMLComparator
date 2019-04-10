package org.xml.xmlcomparator;

import org.xml.xmlmodel.XMLNode;
import org.xml.xmlmodel.XMLValue;

enum VALUE_DIFF_TYPE
{
	Value_Missing,
	Value_Extra
}

public class XMLValueDifference implements IDiff
{
	private XMLValue value;
	private XMLNode node;
	private VALUE_DIFF_TYPE diffType;
	public XMLValueDifference(XMLValue value, XMLNode node, VALUE_DIFF_TYPE diffType) 
	{
		this.value = value;
		this.diffType = diffType;
		this.node = node;
	}
	
	public XMLNode getNode()
	{
		return this.node;
	}
	
	public VALUE_DIFF_TYPE getDiffType()
	{
		return this.diffType;
	}
	
	public XMLValue getValue()
	{
		return this.value;
	}
	
	public void generateDiff(IDiffGenerator diffGenerator) 
	{
		diffGenerator.generateDiff(this);
	}
}
