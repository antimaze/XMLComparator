package org.xml.xmlcomparator;

import org.xml.xmlmodel.XMLAttribute;
import org.xml.xmlmodel.XMLNode;

public class StringDiffGenerator implements IDiffGenerator
{
	// String that will hold the diff detail...
	StringBuilder diffString;
	public StringBuilder getDiff()
	{
		return diffString;
	}

	/**
	 * Generate the diff of node and expressed it in string...
	 */
	public void generateDiff(XMLNodeDifference nodeDiff)
	{
		XMLNode node = nodeDiff.getNode();
		XMLNode parentNode = nodeDiff.getParentNode();
		diffString = new StringBuilder();
		NODE_DIFF_TYPE nodeDiffType = nodeDiff.getDiffType();
		if(nodeDiffType == NODE_DIFF_TYPE.Node_Extra)
		{
			if(parentNode != null)
			{
				// Node "xyz" is an extra node in "parentnode"...
				diffString.append("Node \"").append(node.getNodeName()).append("\" is an extra in \"")
						  .append(parentNode.getNodeName()).append("\" node...");
			}
			else
			{
				// If parent node is null,,, it means root nodes are different in both the files...
				diffString.append("Node \"").append(node.getNodeName()).append("\" is an extra node...");
			}
		}
		else if(nodeDiffType == NODE_DIFF_TYPE.Node_Missing)
		{
			if(parentNode != null)
			{
				diffString.append("Node \"").append(node.getNodeName()).append("\" is missing in \"")
				  .append(parentNode.getNodeName()).append("\" node...");
			}
			else
			{
				diffString.append("Node \"").append(node.getNodeName()).append("\" is missing node...");
			}
		}
	}
	
	/**
	 * Generate the diff of attributes and expressed it in string...
	 */
	public void generateDiff(XMLAttributeDifference attributeDiff) 
	{
		diffString = new StringBuilder();
		XMLNode node = attributeDiff.getTargetNode();
		ATTRIBUTE_DIFF_TYPE attrDiffType = attributeDiff.getDiffType();
		XMLAttribute attr = attributeDiff.getAttribute();
		if(attrDiffType == ATTRIBUTE_DIFF_TYPE.Attribute_Extra)
		{
			diffString.append("Attribute \"").append(attr.getKey()).append("\" is an extra in \"")
					  .append(node.getNodeName()).append("\" node...");
		}
		else if(attrDiffType == ATTRIBUTE_DIFF_TYPE.Attribute_Missing)
		{
			diffString.append("Attribute \"").append(attr.getKey()).append("\" is missing from \"")
			  .append(node.getNodeName()).append("\" node...");
		}
		else if(attrDiffType == ATTRIBUTE_DIFF_TYPE.Attribute_Value_Mismatch)
		{
			diffString.append("Attribute ").append(attr.getKey()).append(" got different value ").append(attr.getValue())
			.append(" from source attribute in \"").append(node.getNodeName()).append("\" node...");;
		}
	}

	/**
	 * Generate the diff of value and expressed it in the string...
	 */
	public void generateDiff(XMLValueDifference valueDiff) 
	{
		diffString = new StringBuilder();
		StringBuilder value = valueDiff.getValue().getValue();
		VALUE_DIFF_TYPE valueDiffType = valueDiff.getDiffType();
		
		if(valueDiffType == VALUE_DIFF_TYPE.Value_Extra)
		{
			diffString.append("Value \"").append(value).append("\" is an extra in \"")
					  .append(valueDiff.getNode().getNodeName()).append("\" node...");
		}
		else if(valueDiffType == VALUE_DIFF_TYPE.Value_Missing)
		{
			diffString.append("Value \"").append(value).append("\" is missing in \"")
					  .append(valueDiff.getNode().getNodeName()).append("\" node...");
		}		
	}
}
