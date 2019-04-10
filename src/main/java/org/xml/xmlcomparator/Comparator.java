/**
 * 
 */
package org.xml.xmlcomparator;

import java.util.HashMap;
import java.util.List;

import org.xml.xmlmodel.XMLDocument;
import org.xml.xmlmodel.XMLNode;

/**
 * @author Savan
 *
 */
public interface Comparator
{
	/**
	 * This method will compare targetDoc with the sourceDoc...
	 * 
	 * @param document1
	 * @param document2
	 * @return return true if has no differences otherwise false...
	 */
	public boolean compare(XMLDocument sourceDoc, XMLDocument targetDoc);
	
	/**
	 * This method will compare targetNode with the sourceNode...
	 * 
	 * @param sourceNode
	 * @param targetNode
	 * @return return true if has no differences otherwise false...
	 */
	public boolean compare(XMLNode sourceNode, XMLNode targetNode);
	
	/**
	 * This method will compare the list of targetNode's nested nodes with the sourceNode's nestedNodes...
	 * 
	 * @param sourceNestedNodes
	 * @param sourceNestedNodesNameToIndexMap
	 * @param targetNestedNodes
	 * @param targetNestedNodesNameToIndexMap
	 * @param parentNode
	 * @return return true if has no differences otherwise false...
	 */
	public boolean compare(HashMap<Integer, XMLNode> sourceNestedNodes, HashMap<String, List<Integer>> sourceNestedNodesNameToIndexMap,
						HashMap<Integer, XMLNode> targetNestedNodes, HashMap<String, List<Integer>> targetNestedNodesNameToIndexMap,
						XMLNode parentNode);
	
	/**
	 * This method says whether the targetDoc is equal to the sourceDoc or not...
	 * @return boolean
	 */
	public boolean isEqual();
	
	/**
	 * This method returns the diff structure in which all the differences presents...
	 * @return
	 */
	public XMLDiff getDiffs();
}
