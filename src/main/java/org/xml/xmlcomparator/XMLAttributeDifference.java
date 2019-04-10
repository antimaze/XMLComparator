package org.xml.xmlcomparator;

import org.xml.xmlmodel.XMLAttribute;
import org.xml.xmlmodel.XMLNode;

/**
 * This class holds the attribute difference and generate the diff according to the IDiffGenerator...
 * @author Savan
 *
 */

/**
 * This enum represents the possible attribute differences...
 */
enum ATTRIBUTE_DIFF_TYPE
{
	Attribute_Missing,
	Attribute_Extra,
	Attribute_Value_Mismatch
}

public class XMLAttributeDifference implements IDiff
{
	/**
	 * This is the attribute in which difference is found...
	 */
	private XMLAttribute attribute;
	
	/**
	 * source node contains the ideal attribute...
	 */
	private XMLNode sourceNode;
	
	/**
	 * target node contains the adulterated attribute...
	 */
	private XMLNode targetNode;
	
	/**
	 * diff type tells that what kind of difference is there in the attribute...
	 */
	private ATTRIBUTE_DIFF_TYPE diffType;
	
	public XMLAttributeDifference(XMLNode sourceNode, XMLNode targetNode, XMLAttribute attribute, ATTRIBUTE_DIFF_TYPE diffType) 
	{
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.attribute = attribute;
		this.diffType = diffType;
	}
	
	public XMLNode getSourceNode()
	{
		return this.sourceNode;
	}
	
	public XMLNode getTargetNode()
	{
		return this.targetNode;
	}
	
	public ATTRIBUTE_DIFF_TYPE getDiffType()
	{
		return this.diffType;
	}
	
	public XMLAttribute getAttribute()
	{
		return this.attribute;
	}

	/**
	 * generate the diff according to the passed IDiffGenerator(a logic i say (in which format you want a diff))...
	 */
	public void generateDiff(IDiffGenerator diffGenerator) 
	{
		diffGenerator.generateDiff(this);
	}
}
