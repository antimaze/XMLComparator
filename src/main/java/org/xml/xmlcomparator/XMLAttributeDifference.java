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
	 * This is the attribute of sourceNode with target attribute is compared...
	 */
	private XMLAttribute sourceAttribute;
	
	/**
	 * This is the attribute of target in which difference is found...
	 */
	private XMLAttribute targetAttribute;
	
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
	
	public XMLAttributeDifference(XMLNode sourceNode, XMLNode targetNode, XMLAttribute sourceAttribute,
								  XMLAttribute targetAttribute, ATTRIBUTE_DIFF_TYPE diffType) 
	{
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.sourceAttribute = sourceAttribute;
		this.targetAttribute = targetAttribute;
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
	
	public XMLAttribute getSourceAttribute()
	{
		return this.sourceAttribute;
	}
	
	public XMLAttribute getTargetAttribute()
	{
		return this.targetAttribute;
	}

	/**
	 * generate the diff according to the passed IDiffGenerator(a logic i say (in which format you want a diff))...
	 */
	public void generateDiff(IDiffGenerator diffGenerator) 
	{
		diffGenerator.generateDiff(this);
	}
}
