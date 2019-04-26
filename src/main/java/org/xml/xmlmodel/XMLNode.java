package org.xml.xmlmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLNode extends XMLElement
{
	private final int uniqueId;
	private XMLNode parentNode;
	
	private boolean isSelfClosingNode;
	private StringBuilder nodeName;
	
	private HashMap<Integer, XMLAttribute> attributes;
	private HashMap<String, Integer> attributesNameToIndex;
	private HashMap<String, List<Integer>> nestedNodeNameToIndex;
	private HashMap<Integer, XMLElement> valueNodeMap;
	private HashMap<String, List<Integer>> valueNameToIndex;
	
	private boolean hasNestedNodes;
	private int nestedNodesCount;
	
	private int nodeSequentialIndex;
	private StringBuilder nodePath = null;
	
	// take care when passing the parameters...
	// Maps must not be null...
	public XMLNode(StringBuilder nodeName, int uniqueId)
	{
		this(nodeName, 
			 new HashMap<Integer, XMLAttribute>(),
			 new HashMap<Integer, XMLElement>(),
			 uniqueId);
	}
	
	public XMLNode(StringBuilder nodeName, 
				   HashMap<Integer, XMLAttribute> attributes, 
				   HashMap<Integer, XMLElement> valueNodeMap, 
				   int uniqueId)
	{
		this(nodeName, attributes, new HashMap<String, Integer>(), valueNodeMap, new HashMap<String, List<Integer>>(),
				new HashMap<String, List<Integer>>(), uniqueId);
	}
	
	public XMLNode(StringBuilder nodeName, 
				   HashMap<Integer, XMLAttribute> attributes, 
				   HashMap<String, Integer> attributesNameToIndex,
				   HashMap<Integer, XMLElement> valueNodeMap, 
				   HashMap<String, List<Integer>> nestedNodeNameToIndex,
				   HashMap<String, List<Integer>> valueNameToIndex,
				   int uniqueId)
	{
		this.nodeName = nodeName;
		this.attributes = attributes;
		this.attributesNameToIndex = attributesNameToIndex;
		this.nestedNodeNameToIndex = nestedNodeNameToIndex;
		this.valueNodeMap = valueNodeMap;
		this.valueNameToIndex = valueNameToIndex;
		this.uniqueId = uniqueId;
		
		this.nestedNodesCount = 0;
		this.hasNestedNodes = false;
		this.parentNode = null;
	}
	
	public int getUniqueId()
	{
		return this.uniqueId;
	}
	
	public StringBuilder getNodeName()
	{
		return this.nodeName;
	}
	
	public int getNestedNodesCount()
	{
		return this.nestedNodesCount;
	}
	
	public int getAttributesCount()
	{
		return this.attributes.size();
	}
	
	public void addValueOrNode(Integer index, XMLElement object)
	{
		valueNodeMap.put(index, object);
		List<Integer> indexList;
		if(object instanceof XMLNode)
		{
			String nodeName = (((XMLNode)object).getNodeName().toString());
			indexList = new ArrayList<Integer>();
			if(nestedNodeNameToIndex.containsKey(nodeName))
			{
				indexList = nestedNodeNameToIndex.get(nodeName);
			}
			indexList.add(index);
			nestedNodeNameToIndex.put(nodeName, indexList);
			
			nestedNodesCount++;
			hasNestedNodes = true;
		}
		else if(object instanceof XMLValue)
		{
			indexList = new ArrayList<Integer>();
			String value = ((XMLValue)object).getValue().toString();
			
			if(valueNameToIndex.containsKey(value))
			{
				indexList = valueNameToIndex.get(value);
			}
			indexList.add(index);
			valueNameToIndex.put(value, indexList);
		}
	}
	
	public void setSelfClosingNodeFlag(boolean selfClosingNodeFlag)
	{
		this.isSelfClosingNode = selfClosingNodeFlag;
	}
	
	public void addAttribute(Integer index, XMLAttribute attribute)
	{
		attributes.put(index, attribute);
		attributesNameToIndex.put(attribute.getKey().toString().trim(), index);
	}
	
	public boolean hasAttributes()
	{
		return !attributes.isEmpty();
	}
	
	public boolean hasNestedNodes()
	{
		return this.hasNestedNodes;
	}
	
	public HashMap<Integer, XMLAttribute> getAttributes()
	{
		return this.attributes;
	}
	
	public boolean hasValue(String value)
	{
		return valueNameToIndex.containsKey(value);
	}
	
	public HashMap<Integer, XMLValue> getValues()
	{
		HashMap<Integer, XMLValue> values = new HashMap<Integer, XMLValue>();
		int valueNodeCount = valueNodeMap.size();
		for(int i=0; i<valueNodeCount; i++)
		{
			XMLElement object = valueNodeMap.get(i);
			if(object instanceof XMLValue)
			{
				values.put(i, (XMLValue)object);
			}
		}
		return values;
	}
	
	public HashMap<Integer, XMLNode> getNestedNodes()
	{
		HashMap<Integer, XMLNode> nestedNodes = new HashMap<Integer, XMLNode>();
		int valueNodeCount = valueNodeMap.size();
		for(int i=0; i<valueNodeCount; i++)
		{
			XMLElement object = valueNodeMap.get(i);
			if(object instanceof XMLNode)
			{
				nestedNodes.put(i, (XMLNode)object);
			}
		}
		return nestedNodes;
	}
	
	public HashMap<String, List<Integer>> getNestedNodesNameToIndexMap()
	{
		return this.nestedNodeNameToIndex;
	}
	
	public StringBuilder getXMLTagText()
	{
		StringBuilder text = new StringBuilder();
		
		// Opening the tag... ex. <xyz>
		text.append("<");
		text.append(nodeName);
		
		// Adding attributes if any...
		// ex. <xyz attr1="1" attr2 ="2" >
		// temporary logic... needs to be improved...
		if(hasAttributes())
		{
			int attributeCount = attributes.size();
			for(int i=0; i<attributeCount; i++)
			{
				text.append(" ");
				text.append(attributes.get(i).getXMLAttributeText());
			}
		}
		
		if(isSelfClosingNode && !hasNestedNodes())
		{
			text.append(" />");
		}
		else
		{
			text.append(">");
			
			int valueNodeCount = valueNodeMap.size();
			for(int i=0; i<valueNodeCount; i++)
			{
				XMLElement object = valueNodeMap.get(i);
				if(object instanceof XMLValue)
				{
					// Adding node values...
					// can appear anywhere in node...
					XMLValue value = (XMLValue)object;
					text.append(value.getValue());
				}
				else if(object instanceof XMLNode)
				{
					// adding node text in sequence...
					text.append("\n");
					XMLNode node = (XMLNode) object;
					text.append(node.getXMLTagText());
				}
			}
			
			// Closing the tag... ex. </xyz>
			text.append("</");
			text.append(nodeName);
			text.append(">");
		}
		return text;
	}
	
	public List<XMLNode> getNodesByName(StringBuilder nodeName)
	{
		if(nestedNodeNameToIndex.containsKey(nodeName.toString().trim()))
		{
			List<Integer> indexes = nestedNodeNameToIndex.get(nodeName.toString().trim());
			List<XMLNode> nodes = new ArrayList<XMLNode>();
			
			for(Integer index : indexes)
			{
				XMLElement node = valueNodeMap.get(index);
				if(node instanceof XMLNode)
				{
					nodes.add((XMLNode)node);
				}
			}
			
			return nodes.isEmpty() ? null : nodes;
		}
		else
		{
			return null;
		}
	}
	
	public XMLNode getNodeByIndex(Integer index)
	{
		XMLElement node = valueNodeMap.get(index);
		if(node instanceof XMLNode)
		{
			return (XMLNode)node;
		}
		return null;
	}
	
	public XMLAttribute getAttributeByName(StringBuilder attributeName)
	{
		if(attributesNameToIndex.containsKey(attributeName.toString().trim()))
		{
			Integer index = attributesNameToIndex.get(attributeName.toString().trim());
			return attributes.get(index);
		}
		else
		{
			return null;
		}
	}
	
	public XMLAttribute getAttributeByIndex(Integer index)
	{
		return attributes.get(index);
	}
	
	public boolean isRootNode()
	{
		return (this.parentNode == null);
	}
	
	public void setParentNode(XMLNode parentNode)
	{
		this.parentNode = parentNode;
	}
	
	public XMLNode getParentNode()
	{
		return this.parentNode;
	}
	
	public void setNodeSequentialIndex(int index)
	{
		this.nodeSequentialIndex = index;
	}
	
	public int getNodeSequentialNode()
	{
		return this.nodeSequentialIndex;
	}
	
	public void setNodePath(StringBuilder nodePath)
	{
		this.nodePath = nodePath;
	}
	
	public StringBuilder getNodePath()
	{
		if(nodePath != null)
		{
			return nodePath;	
		}
		else
		{
			nodePath = new StringBuilder();
			List<StringBuilder> nodeNames = new ArrayList<StringBuilder>();
			nodeNames.add(0, nodeName);
			
			XMLNode parent = parentNode;
			while(parent != null)
			{
				nodeNames.add(0, parent.getNodeName());
				parent = parent.getParentNode();
			}
			
			for(StringBuilder nodeName : nodeNames)
			{
				nodePath = nodePath.append("/").append(nodeName);
			}
			return nodePath;
		}
	}

	@Override
	public XMLElementType getElementType()
	{
		return XMLElementType.XMLNode;
	}
}
