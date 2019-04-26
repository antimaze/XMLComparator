package org.xml.xmlcomparator;

import org.xml.util.DiffStrings;
import org.xml.xmlmodel.XMLAttribute;
import org.xml.xmlmodel.XMLNode;
import org.xml.xmlmodel.XMLValue;

public class StringDiffGenerator implements IDiffGenerator
{
	private int diffNumber = 1;
	
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
		diffString = new StringBuilder();
		NODE_DIFF_TYPE nodeDiffType = nodeDiff.getDiffType();
		if(nodeDiffType == NODE_DIFF_TYPE.Node_Extra)
		{
			diffString.append(getExtraNodeText(nodeDiff));
		}
		else if(nodeDiffType == NODE_DIFF_TYPE.Node_Missing)
		{
			diffString.append(getMissingNodeText(nodeDiff));
		}
	}
	
	/**
	 * Make the missing node string...
	 * @param nodeDiff
	 * @return missing node diff string...
	 */
	private StringBuilder getMissingNodeText(XMLNodeDifference nodeDiff)
	{
		XMLNode node = nodeDiff.getNode();
		XMLNode parentNode = nodeDiff.getParentNode();
		
		StringBuilder missingNodeDiff = new StringBuilder();

		missingNodeDiff.append("\n");
		missingNodeDiff.append(diffNumber++);
		missingNodeDiff.append(". ");

		if(parentNode != null)
		{
			missingNodeDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n");
			missingNodeDiff.append(DiffStrings.NODE_MISSING);
		}
		else
		{
			missingNodeDiff.append("");
			missingNodeDiff.append("Missing RootNode : ");
		}
		
		missingNodeDiff.append("\"").append(node.getNodeName()).append("\"\n");
		return missingNodeDiff;
	}
	
	/**
	 * Make the extra node string...
	 * @param nodeDiff
	 * @return extra node diff string...
	 */
	private StringBuilder getExtraNodeText(XMLNodeDifference nodeDiff)
	{
		XMLNode node = nodeDiff.getNode();
		XMLNode parentNode = nodeDiff.getParentNode();
		
		StringBuilder extraNodeDiff = new StringBuilder();
		
		extraNodeDiff.append("\n");
		extraNodeDiff.append(diffNumber++);
		extraNodeDiff.append(". ");

		if(parentNode != null)
		{
			extraNodeDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n");
			extraNodeDiff.append(DiffStrings.NODE_EXTRA);
		}
		else
		{
			extraNodeDiff.append("");
			extraNodeDiff.append("Missing RootNode : ");
		}
		
		extraNodeDiff.append("\"").append(node.getNodeName()).append("\"\n");
		
		return extraNodeDiff;
	}
	
	/**
	 * Generate the diff of attributes and expressed it in string...
	 */
	public void generateDiff(XMLAttributeDifference attributeDiff) 
	{
		diffString = new StringBuilder();
		ATTRIBUTE_DIFF_TYPE attrDiffType = attributeDiff.getDiffType();
		if(attrDiffType == ATTRIBUTE_DIFF_TYPE.Attribute_Extra)
		{
			diffString.append(getExtraAttributeText(attributeDiff));
		}
		else if(attrDiffType == ATTRIBUTE_DIFF_TYPE.Attribute_Missing)
		{
			diffString.append(getMissingAttributeText(attributeDiff));
		}
		else if(attrDiffType == ATTRIBUTE_DIFF_TYPE.Attribute_Value_Mismatch)
		{
			diffString.append(getMismatchAttributeText(attributeDiff));
		}
	}
	
	/**
	 * Make the missing attribute string...
	 * @param attributeDiff
	 * @return missing attribute diff string...
	 */
	private StringBuilder getMissingAttributeText(XMLAttributeDifference attributeDiff)
	{
		XMLNode node = attributeDiff.getTargetNode();
		XMLAttribute attr = attributeDiff.getTargetAttribute();
		
		StringBuilder missingAttributeDiff = new StringBuilder();
		
		missingAttributeDiff.append("\n");
		missingAttributeDiff.append(diffNumber++);
		missingAttributeDiff.append(". ");
		missingAttributeDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n\n");
		missingAttributeDiff.append("\t");
		missingAttributeDiff.append(DiffStrings.ATTRIBUTE_MISSING);
		missingAttributeDiff.append("\"").append(attr.getKey()).append("\"\n");
		
		return missingAttributeDiff;
	}
	
	/**
	 * Make the extra attribute string...
	 * @param attributeDiff
	 * @return extra attribute diff string...
	 */
	private StringBuilder getExtraAttributeText(XMLAttributeDifference attributeDiff)
	{
		XMLNode node = attributeDiff.getTargetNode();
		XMLAttribute attr = attributeDiff.getTargetAttribute();
		
		StringBuilder extraAttributeDiff = new StringBuilder();
		
		extraAttributeDiff.append("\n");
		extraAttributeDiff.append(diffNumber++);
		extraAttributeDiff.append(". ");
		extraAttributeDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n\n");
		extraAttributeDiff.append("\t");
		extraAttributeDiff.append(DiffStrings.ATTRIBUTE_EXTRA);
		extraAttributeDiff.append("\"").append(attr.getKey()).append("\"\n");
		
		return extraAttributeDiff;
	}
	
	/**
	 * Generate the attribute mismatch diff string...
	 * @param attributeDiff
	 * @return attribute mismatch diff string...
	 */
	private StringBuilder getMismatchAttributeText(XMLAttributeDifference attributeDiff)
	{
		XMLNode node = attributeDiff.getTargetNode();
		XMLAttribute targetAttr = attributeDiff.getTargetAttribute();
		XMLAttribute srcAttr = attributeDiff.getSourceAttribute();
		
		StringBuilder mismatchAttributeDiff = new StringBuilder();
		
		mismatchAttributeDiff.append("\n");
		mismatchAttributeDiff.append(diffNumber++);
		mismatchAttributeDiff.append(". ");
		mismatchAttributeDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n\n");
		mismatchAttributeDiff.append("\t");
		mismatchAttributeDiff.append(DiffStrings.ATTRIBUTE_MISMATCH);
		mismatchAttributeDiff.append("\n\t\t");
		mismatchAttributeDiff.append("Expected Attribute was : ").append(srcAttr.getKey())
							 .append(" = \"").append(srcAttr.getValue()).append("\"");
		mismatchAttributeDiff.append("\n\t\t");
		mismatchAttributeDiff.append("But got : ").append(targetAttr.getKey())
							 .append(" = \"").append(targetAttr.getValue()).append("\"\n");

		return mismatchAttributeDiff;
	}

	/**
	 * Generate the diff of value and expressed it in the string...
	 */
	public void generateDiff(XMLValueDifference valueDiff) 
	{
		diffString = new StringBuilder();
		VALUE_DIFF_TYPE valueDiffType = valueDiff.getDiffType();
		
		if(valueDiffType == VALUE_DIFF_TYPE.Value_Extra)
		{
			diffString.append(getExtraValueText(valueDiff));
		}
		else if(valueDiffType == VALUE_DIFF_TYPE.Value_Missing)
		{
			diffString.append(getMissingValueText(valueDiff));
		}		
	}
	
	/**
	 * Generate the missing value of node string...
	 * @param valueDiff
	 * @return missing value diff string...
	 */
	private StringBuilder getMissingValueText(XMLValueDifference valueDiff)
	{
		XMLNode node = valueDiff.getNode();
		XMLValue value = valueDiff.getValue();
		StringBuilder missingValueDiff = new StringBuilder();
		
		missingValueDiff.append("\n");
		missingValueDiff.append(diffNumber++);
		missingValueDiff.append(". ");
		missingValueDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n\n");
		missingValueDiff.append("\t\t");
		missingValueDiff.append(DiffStrings.VALUE_MISSING);
		missingValueDiff.append("\"").append(value.getValue()).append("\"\n");
		
		return missingValueDiff;
	}
	
	/**
	 * Generate the extra value string...
	 * @param valueDiff
	 * @return extra value diff string...
	 */
	private StringBuilder getExtraValueText(XMLValueDifference valueDiff)
	{
		XMLNode node = valueDiff.getNode();
		XMLValue value = valueDiff.getValue();
		StringBuilder extraValueDiff = new StringBuilder();
		
		extraValueDiff.append("\n");
		extraValueDiff.append(diffNumber++);
		extraValueDiff.append(". ");
		extraValueDiff.append(node.getNodePath()).append("[").append(node.getNodeSequentialNode()).append("]\n\n");
		extraValueDiff.append("\t\t");
		extraValueDiff.append(DiffStrings.VALUE_EXTRA);
		extraValueDiff.append("\"").append(value.getValue()).append("\"\n");
		
		return extraValueDiff;
	}
}
