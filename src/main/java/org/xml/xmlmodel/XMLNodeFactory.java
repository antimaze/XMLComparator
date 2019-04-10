package org.xml.xmlmodel;

import java.util.HashMap;
import java.util.List;

public class XMLNodeFactory
{
	private static int uniqueId = 0;
	
	public static XMLNode createXMLNode(StringBuilder nodeName)
	{
		uniqueId++;
		return new XMLNode(nodeName, uniqueId);
	}
	
	public static XMLNode createXMLNode(StringBuilder nodeName, 
										HashMap<Integer, XMLAttribute> attributes, 
										HashMap<Integer, XMLElement> valueNodeMap)
	{
		uniqueId++;
		return new XMLNode(nodeName, attributes, valueNodeMap, uniqueId);
	}
	
	public static XMLNode createXMLNode(StringBuilder nodeName, 
										HashMap<Integer, XMLAttribute> attributes, 
										HashMap<String, Integer> attributesNameToIndex,
										HashMap<Integer, XMLElement> valueNodeMap, 
										HashMap<String, List<Integer>> nestedNodeNameToIndex,
										HashMap<String, List<Integer>> valueNameToIndex)
	{
		uniqueId++;
		return new XMLNode(nodeName, attributes, attributesNameToIndex, valueNodeMap, 
						   nestedNodeNameToIndex, valueNameToIndex, uniqueId);
	}
}
