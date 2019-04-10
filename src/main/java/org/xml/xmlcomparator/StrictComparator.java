package org.xml.xmlcomparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.xmlmodel.XMLAttribute;
import org.xml.xmlmodel.XMLDocument;
import org.xml.xmlmodel.XMLNode;
import org.xml.xmlmodel.XMLValue;
import org.xml.xmlcomparator.XMLDiff;

public class StrictComparator implements Comparator
{
	// Holds the xml diffs in XMLDiff data structure... 
	private XMLDiff xmlDiff;
	public StrictComparator() 
	{
		xmlDiff = new XMLDiff();
	}
	
	public boolean compare(XMLDocument sourceDoc, XMLDocument targetDoc)
	{
		XMLNode sourceNode = sourceDoc.getRootNode();
		XMLNode targetNode = targetDoc.getRootNode();
		
		// Compare names...
		// if mismatch then diff whole node...
		boolean isNodeNameMatch = compareNames(sourceNode, targetNode);
		if(!isNodeNameMatch)
		{
			IDiff missingNodeDiff = new XMLNodeDifference(sourceNode, null, NODE_DIFF_TYPE.Node_Missing);
			IDiff extraNodeDiff = new XMLNodeDifference(targetNode, null, NODE_DIFF_TYPE.Node_Extra);
			xmlDiff.addXMLDiff(sourceNode.getUniqueId(), missingNodeDiff);
			xmlDiff.addXMLDiff(targetNode.getUniqueId(), extraNodeDiff);
			return false;
		}
		
		return compare(sourceNode, targetNode);
	}
	
	public boolean compare(HashMap<Integer, XMLNode> sourceNodes, HashMap<String, List<Integer>> sourceNodesNameToIndexMap,
						HashMap<Integer, XMLNode> targetNodes, HashMap<String, List<Integer>> targetNodesNameToIndexMap,
						XMLNode parentNode)
	{
		boolean hasDifferences = false;
		
		// Cases...
		// 1. Node is missing in targetNode...
		for(Map.Entry<String, List<Integer>> entry : sourceNodesNameToIndexMap.entrySet())
		{
			String sNodeName = entry.getKey();
			if(!targetNodesNameToIndexMap.containsKey(sNodeName))
			{
				List<Integer> sNodeIndexes = entry.getValue();
				for(Integer sNodeIndex : sNodeIndexes)
				{
					XMLNode missingNode = sourceNodes.get(sNodeIndex);
					// Missing node from target...
					IDiff nodeDiff = new XMLNodeDifference(missingNode, parentNode, NODE_DIFF_TYPE.Node_Missing);
					xmlDiff.addXMLDiff(missingNode.getUniqueId(), nodeDiff);
					
					hasDifferences = true;
				}
			}
		}
		
		// 2. Node is extra in targetNode...
		for(Map.Entry<String, List<Integer>> entry : targetNodesNameToIndexMap.entrySet())
		{
			String tNodeName = entry.getKey();
			if(!sourceNodesNameToIndexMap.containsKey(tNodeName))
			{
				// extra node in target...
				List<Integer> tNodeIndexes = entry.getValue();
				
				for(Integer tNodeIndex : tNodeIndexes)
				{
					XMLNode extraNode = targetNodes.get(tNodeIndex);
					// Missing node from target...
					IDiff nodeDiff = new XMLNodeDifference(extraNode, parentNode, NODE_DIFF_TYPE.Node_Extra);
					xmlDiff.addXMLDiff(extraNode.getUniqueId(), nodeDiff);
					
					hasDifferences = true;
				}
			}
		}
		
		// 3. Send the node for comparison...
		for(Map.Entry<String, List<Integer>> entry : sourceNodesNameToIndexMap.entrySet())
		{
			String sNodeName = entry.getKey();
			if(targetNodesNameToIndexMap.containsKey(sNodeName))
			{
				// compare both the nodes...
				List<Integer> sNodeIndexes = entry.getValue();
				List<Integer> tNodeIndexes = targetNodesNameToIndexMap.get(sNodeName);
				
				
				// Simple logic...
				int loopCount = (sNodeIndexes.size() < tNodeIndexes.size()) ? sNodeIndexes.size() : tNodeIndexes.size();
				for(int i=0; i<loopCount; i++)
				{
					Integer sNodeIndex = sNodeIndexes.get(i);
					Integer tNodeIndex = tNodeIndexes.get(i);
					
					XMLNode sourceNode = sourceNodes.get(sNodeIndex);
					XMLNode targetNode = targetNodes.get(tNodeIndex);
					
					boolean isNodesSame = compare(sourceNode, targetNode);
					hasDifferences = hasDifferences || isNodesSame;
				}
				
				if(sNodeIndexes.size() < tNodeIndexes.size())
				{
					for(int i=sNodeIndexes.size(); i<tNodeIndexes.size(); i++)
					{
						Integer tNodeIndex = tNodeIndexes.get(i);
						XMLNode extraNode = targetNodes.get(tNodeIndex);
						// Missing node from target...
						IDiff nodeDiff = new XMLNodeDifference(extraNode, parentNode, NODE_DIFF_TYPE.Node_Extra);
						xmlDiff.addXMLDiff(extraNode.getUniqueId(), nodeDiff);
						
						hasDifferences = true;
					}
				}
				else if(tNodeIndexes.size() < sNodeIndexes.size())
				{
					for(int i=tNodeIndexes.size(); i<sNodeIndexes.size(); i++)
					{
						Integer sNodeIndex = sNodeIndexes.get(i);
						XMLNode missingNode = sourceNodes.get(sNodeIndex);
						// Missing node from target...
						IDiff nodeDiff = new XMLNodeDifference(missingNode, parentNode, NODE_DIFF_TYPE.Node_Missing);
						xmlDiff.addXMLDiff(missingNode.getUniqueId(), nodeDiff);
						
						hasDifferences = true;
					}
				}
				
				// Advance logic in progress...
//				Iterator<Integer> sItr = sNodeIndexes.iterator();
//				Iterator<Integer> tItr = tNodeIndexes.iterator();
//				while(sItr.hasNext())
//				{
//					Integer sNodeIndex = sItr.next();
//					while(tItr.hasNext())
//					{
//						Integer tNodeIndex = tItr.next();
//						XMLNode sourceNode = sourceNodes.get(sNodeIndex);
//						XMLNode targetNode = targetNodes.get(tNodeIndex);
//						
//						boolean isNodesSame = compare(sourceNode, targetNode);
//						if(isNodesSame)
//						{
//							tItr.remove();
//						}
//						hasDifferences = hasDifferences || isNodesSame;
//					}
//				}
			}
		}
		
		return hasDifferences;
	}
	
	public boolean compare(XMLNode sourceNode, XMLNode targetNode)
	{
		boolean hasDifferences = false;
		
		HashMap<Integer, XMLNode> sourceNestedNodes = sourceNode.getNestedNodes();
		HashMap<Integer, XMLNode> targetNestedNodes = targetNode.getNestedNodes();
		int targetNestedNodesCount = targetNestedNodes.size();
		int sourceNestedNodesCount = sourceNestedNodes.size();
		
		// Compare attributes...
		hasDifferences = hasDifferences || compareAttributes(sourceNode, targetNode);
		
		// Comapre values...
		hasDifferences = hasDifferences || comapareValue(sourceNode, targetNode);
		
		// 1. both have no nestedTags...
		if(sourceNestedNodesCount == 0 && targetNestedNodesCount == 0)
		{
			return hasDifferences;
		}
		
		// 2. one tag has nested tags and other one has not...
		if(sourceNestedNodesCount == 0)
		{
			for(int i=0; i<targetNestedNodesCount; i++)
			{
				XMLNode tNode = targetNestedNodes.get(i);
				IDiff nodeDiff = new XMLNodeDifference(tNode, targetNode, NODE_DIFF_TYPE.Node_Extra);
				xmlDiff.addXMLDiff(targetNode.getUniqueId(), nodeDiff);
				
				hasDifferences = true;
			}
			return hasDifferences;
		}
		else if(targetNestedNodesCount == 0)
		{
			for(int i=0; i<sourceNestedNodesCount; i++)
			{
				XMLNode sNode = sourceNestedNodes.get(i);
				IDiff nodeDiff = new XMLNodeDifference(sNode, targetNode, NODE_DIFF_TYPE.Node_Missing);
				xmlDiff.addXMLDiff(targetNode.getUniqueId(), nodeDiff);
				
				hasDifferences = true;
			}
			return hasDifferences;
		}
		
		// 3. both have nested tags...
		HashMap<String, List<Integer>> sourceNestedNodesNameToIndex = sourceNode.getNestedNodesNameToIndexMap();
		HashMap<String, List<Integer>> targetNestedNodesNameToIndex = targetNode.getNestedNodesNameToIndexMap();
		
		return compare(sourceNestedNodes, sourceNestedNodesNameToIndex, targetNestedNodes, targetNestedNodesNameToIndex, targetNode);
	}

	/**
	 * This method will compare the targetNode value with the sourceNode value,,,
	 * and put it in the xmlDiff if they mismatch...
	 * 
	 * @param sourceNode
	 * @param targetNode
	 */
	private boolean comapareValue(XMLNode sourceNode, XMLNode targetNode)
	{	
		boolean hasDifferences = false;
		HashMap<Integer, XMLValue> sourceValues = sourceNode.getValues();
		HashMap<Integer, XMLValue> targetValues = targetNode.getValues();
		
		for (Map.Entry<Integer, XMLValue> values : targetValues.entrySet())
		{
			XMLValue value = values.getValue();
			if(!sourceNode.hasValue(value.getValue().toString()))
			{
				// Extra value...
				IDiff nodeValueDiff = new XMLValueDifference(value, targetNode, VALUE_DIFF_TYPE.Value_Extra);
				xmlDiff.addXMLDiff(targetNode.getUniqueId(), nodeValueDiff);
				hasDifferences = true;
			}
		}
		
		for (Map.Entry<Integer, XMLValue> values : sourceValues.entrySet())
		{
			XMLValue value = values.getValue();
			if(!targetNode.hasValue(value.getValue().toString()))
			{
				// Missing value...
				IDiff nodeValueDiff = new XMLValueDifference(value, targetNode, VALUE_DIFF_TYPE.Value_Missing);
				xmlDiff.addXMLDiff(targetNode.getUniqueId(), nodeValueDiff);
				hasDifferences = true;
			}
		}
		
		return hasDifferences;
	}

	/**
	 * This method will compare the targetNode and sourceNode's names...
	 * 
	 * @param sourceNode
	 * @param targetNode
	 * @return true if they match or false...
	 */
	private boolean compareNames(XMLNode sourceNode, XMLNode targetNode)
	{
		// Compare the tag names and add to diff if any mismatch...
		if(sourceNode.getNodeName().toString().equals(targetNode.getNodeName().toString()))
		{
			return true;
		}
		
		return false;
	}

	/**
	 * This method will compare the targetNode's attributes with sourceNode's attributes...
	 * 
	 * @param sourceNode
	 * @param targetNode
	 */
	public boolean compareAttributes(XMLNode sourceNode, XMLNode targetNode)
	{
		boolean hasDifferences = false;
		
		// Comapre the tag attributes and put the difference in diff if any...
		HashMap<Integer, XMLAttribute> sourceNodeAttributes = sourceNode.getAttributes();
		HashMap<Integer, XMLAttribute> targetNodeAttributes = targetNode.getAttributes();
		
		// Following logic will be improved...
		// Cases...
		// 1. attribute is missing in target node...
		int sourceNodeAttrCount = sourceNodeAttributes.size();
		for(int i=0; i<sourceNodeAttrCount; i++)
		{
			if(targetNode.getAttributeByName(sourceNode.getAttributeByIndex(i).getKey()) == null)
			{
				IDiff attrDiff = new XMLAttributeDifference(sourceNode, targetNode, sourceNode.getAttributeByIndex(i), 
															ATTRIBUTE_DIFF_TYPE.Attribute_Missing);
				xmlDiff.addXMLDiff(targetNode.getUniqueId(), attrDiff);
				hasDifferences = true;
			}
		}
		
		// 2. attribute is extra in target node...
		int targetNodeAttrCount = targetNodeAttributes.size();
		for(int i=0; i<targetNodeAttrCount; i++)
		{
			if(sourceNode.getAttributeByName(targetNode.getAttributeByIndex(i).getKey()) == null)
			{
				IDiff attrDiff = new XMLAttributeDifference(sourceNode, targetNode, targetNode.getAttributeByIndex(i), 
															ATTRIBUTE_DIFF_TYPE.Attribute_Extra);
				xmlDiff.addXMLDiff(targetNode.getUniqueId(), attrDiff);
				hasDifferences = true;
			}
		}
		
		// 3. attribute value mismatch in nodes...		
		for(int i=0; i<sourceNodeAttrCount; i++)
		{
			XMLAttribute sourceAttribute = sourceNode.getAttributeByIndex(i);
			XMLAttribute targetAttribute = targetNode.getAttributeByName(sourceAttribute.getKey());
			if(targetAttribute != null)
			{
				StringBuilder value1 = sourceAttribute.getValue();
				StringBuilder value2 = targetAttribute.getValue();
			
				if(!value1.toString().equals(value2.toString()))
				{
					IDiff attrDiff = new XMLAttributeDifference(sourceNode, targetNode, sourceNode.getAttributeByIndex(i), 
																ATTRIBUTE_DIFF_TYPE.Attribute_Value_Mismatch);
					xmlDiff.addXMLDiff(targetNode.getUniqueId(), attrDiff);
					hasDifferences = true;
				}
			}
		}
		
		return hasDifferences;
	}
	
	/**
	 * returns the diff structure...
	 */
	public XMLDiff getDiffs()
	{
		return this.xmlDiff;
	}

	/**
	 * return true if there are any differences otherwise returns false...
	 */
	public boolean isEqual() 
	{
		return !xmlDiff.hasDifference();
	}
}
