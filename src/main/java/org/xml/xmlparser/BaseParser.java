package org.xml.xmlparser;

import java.io.IOException;
import java.util.HashMap;

import org.xml.xmlmodel.XMLDocument;
import org.xml.xmlmodel.XMLElement;
import org.xml.xmlmodel.XMLElement.XMLElementType;
import org.xml.xmlmodel.XMLNode;
import org.xml.xmlmodel.XMLNodeFactory;
import org.xml.xmlmodel.XMLSimpleDeclaration;
import org.xml.xmlmodel.XMLValue;
import org.xml.xmlmodel.DOCTYPE;
import org.xml.xmlmodel.XMLAttribute;
import org.xml.xmlmodel.XMLCData;
import org.xml.xmlmodel.XMLComment;
import org.xml.xmlmodel.XMLDeclaration;

public abstract class BaseParser
{
	protected XMLDocument document;
	private ContinuousSource sourceFile;
	
	protected static final int START_TAG 			= '<';
	protected static final int END_TAG 				= '>';
	protected static final int END_PREFIX 			= '/';
	protected static final int EQUAL 				= '=';
	protected static final int QUOTATION_MARK 		= '"';
	protected static final int QUESTION_MARK		= '?';
	protected static final int DASH					= '-';
	protected static final int APOSTROPHE 			= 39; 	// " ' "
	protected static final int EXCLAMATION_MARK 	= 33; 	// "!";
	/**
     * ASCII code for line feed.
     */
    protected static final byte ASCII_LF 			= 10;
    /**
     * ASCII code for carriage return.
     */
    protected static final byte ASCII_CR 			= 13;
    protected static final int ASCII_SPACE			= 32;
    
    protected static final int x					= 'x';
    protected static final int m					= 'm';
    protected static final int l					= 'l';
    
    protected static final int e					= 'e';
    protected static final int n					= 'n';
    protected static final int c					= 'c';
    protected static final int o					= 'o';
    protected static final int d					= 'd';
    protected static final int i					= 'i';
    protected static final int g					= 'g';
    
    protected static final int s					= 's';
    protected static final int t					= 't';
    protected static final int a					= 'a';
    
    protected static final int OPEN_SQUARE_BRACKET	= '[';
    protected static final int CLOSE_SQUARE_BRACKET	= ']';
    protected static final int C					= 'C';
    protected static final int D					= 'D';
    protected static final int A					= 'A';
    protected static final int T					= 'T';
    
    protected static final int O					= 'O';
    protected static final int Y					= 'Y';
    protected static final int P					= 'P';
    protected static final int E					= 'E';
    
    
	protected boolean isNodeNameFound = false;
	protected boolean isAttributeValueInProgress = false;
	protected boolean shouldExpectNodeName = false;
	protected boolean shouldExpectAttributeKey = false;
	protected boolean shouldExpectAttributeValue = false;
	protected boolean shouldExpectTagEnd = false;
	protected boolean shouldExpectedStartTag = false;
	protected boolean isCommentInProgress = false;
	
	protected boolean shouldExpectXMLDeclaration = true;
	protected boolean shouldExpectRootNode = true;
	
	private static String XML_VERSION = "1.0";
	private static HashMap<String, Integer> ENCODING_VALUE_MAP;
	private static HashMap<String, Integer> STANDALONE_VALUE_MAP;
	
	
	/**
	 * This static block will be initialized when object is created...
	 */
	static
	{
		ENCODING_VALUE_MAP = new HashMap<String, Integer>();
		STANDALONE_VALUE_MAP = new HashMap<String, Integer>();
		
		/**
		 * These are the encoding values that are permitted in xml declaration...
		 */
		ENCODING_VALUE_MAP.put("UTF-8", 0);
		ENCODING_VALUE_MAP.put("UTF-16", 0);
		ENCODING_VALUE_MAP.put("ISO-10646-UCS-2", 0);
		ENCODING_VALUE_MAP.put("ISO-10646-UCS-4", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-1", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-2", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-3", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-4", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-5", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-6", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-7", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-8", 0);
		ENCODING_VALUE_MAP.put("ISO-8859-9", 0);
		ENCODING_VALUE_MAP.put("ISO-2022-JP", 0);
		ENCODING_VALUE_MAP.put("Shift_JIS", 0);
		ENCODING_VALUE_MAP.put("EUC-JP", 0);
		
		/**
		 * These are the standalone values that are permitted in xml declaration...
		 */
		STANDALONE_VALUE_MAP.put("yes", 0);
		STANDALONE_VALUE_MAP.put("no", 0);
	}
	
	public BaseParser(ContinuousSource sourceFile)
	{
		this.sourceFile = sourceFile;
		document = new XMLDocument();
	}
	
	public void parse() throws Exception
	{
		readBeforeRootNode();
		
		XMLElement rootNode = readRootNode();
		if(rootNode instanceof XMLNode)
		{
			((XMLNode)rootNode).setNodeSequentialIndex(1);
			((XMLNode)rootNode).setParentNode(null);
			document.setRootNode((XMLNode)rootNode);			
		}

		
//		else
//		{
//			throw new Exception("Error : Do you know this file contains more than one root node???");
//		}
	}
	
	private void readBeforeRootNode() throws Exception
	{
		XMLElementType elementType;
		XMLElement element = null;
		int elementIndex = 1;
		while(!sourceFile.isEOF())
		{
			elementType = checkNextElementType();
			if(elementType == XMLElementType.XML_CDATA)
			{
				throw new Exception("Error : No budy,,, this CDATA,,, you can't put it here,,, just grab that CDATA and get out...");
			}
			else if(elementType == XMLElementType.XMLDeclaration)
			{
				if(!shouldExpectXMLDeclaration)
				{
					throw new Exception("Error : I guess this is xml declaration came at the wrong place or this could be second xml declaration that is also wrong...");
				}
				else
				{
					shouldExpectXMLDeclaration = false;
				}
			}
			else if(elementType == XMLElementType.XMLComment)
			{
				shouldExpectXMLDeclaration = false;
			}
			else if(elementType == XMLElementType.None || elementType == XMLElementType.XMLNode)
			{
				break;
			}
			
			element = readElement(elementType);
			document.addElement(elementIndex++, element);
		}
	}
	
	private XMLElementType checkNextElementType() throws IOException, Exception
	{
		if(hasComment())
		{
			return XMLElementType.XMLComment;
		}
		else if(hasXMLCDATA())
		{
			return XMLElementType.XML_CDATA;
		}
		else if(hasXMLDeclaration())
		{
			return XMLElementType.XMLDeclaration;
		}
		else if(hasSimpleDeclaration())
		{
			return XMLElementType.XMLSimpleDeclaration;
		}
		else if(hasNodeValue())
		{
			return XMLElementType.XMLValue;
		}
		else if(hasNestedNodes())
		{
			return XMLElementType.XMLNode;
		}
		else if(hasDOCTYPE())
		{
			return XMLElementType.DOCTYPE;
		}
		else
		{
			return XMLElementType.None;
		}
	}
	
	/**
	 * Checks whether next element is comment or not...
	 * @return boolean that indicates whether the next readable element is comment or not...
	 * @throws Exception
	 */
	private boolean hasComment() throws Exception
	{
		int spaces = skipSpaces();
		
		if(sourceFile.isEOF() || sourceFile.available() < 4)
		{
			sourceFile.unread((byte)spaces);
			return false;
		}
		
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		
		sourceFile.unread((byte)(4 + spaces));
		if(c1 == START_TAG && c2 == EXCLAMATION_MARK && c3 == DASH && c4 == DASH)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the next element is CDATA or not...
	 * 
	 * @return boolean indicates whether the next readable element is CDATA or not...
	 * @throws IOException
	 */
	private boolean hasXMLCDATA() throws IOException
	{
		int spaces = skipSpaces();
		
		if(sourceFile.isEOF() || sourceFile.available() < 9)
		{
			sourceFile.unread((byte)spaces);
			return false;
		}
		
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();
		char c7 = (char)sourceFile.read();
		char c8 = (char)sourceFile.read();
		char c9 = (char)sourceFile.read();
		
		sourceFile.unread((byte)(9 + spaces));
		if(c1 == START_TAG && c2 == EXCLAMATION_MARK && c3 == OPEN_SQUARE_BRACKET && c4 == C && c5 == D && 
				c6 == A && c7 == T && c8 == A && c9 == OPEN_SQUARE_BRACKET)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether the next element is XMLDeclaration or not...
	 * 
	 * @return boolean indicates whether the next readable element is XMLDeclaration or not...
	 * @throws IOException
	 */
	private boolean hasXMLDeclaration() throws IOException
	{
		if(sourceFile.isEOF() || sourceFile.available() < 5)
		{
			return false;
		}

		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();

		sourceFile.unread((byte)6);
		if(c1 == START_TAG && c2 == QUESTION_MARK && c3 == x && c4 == m && c5 == l && c6 == ASCII_SPACE)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether the next element is XMLSimpleDeclaration or not...
	 * 
	 * @return boolean indicates whether the next readable element is XMLSimpleDeclaration or not...
	 * @throws IOException
	 */
	private boolean hasSimpleDeclaration() throws IOException
	{
		int spaces = skipSpaces();
		if(sourceFile.isEOF() || sourceFile.available() < 5)
		{
			sourceFile.unread((byte)spaces);
			return false;
		}

		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();

		sourceFile.unread((byte)(6 + spaces));
		if(c1 == START_TAG && c2 == QUESTION_MARK)
		{
			if(c3 == x && c4 == m && c5 == l && c6 == ASCII_SPACE)
			{
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the next element is XMLNode or not...
	 * 
	 * @return boolean indicates whether the next readable element is XMLNode or not...
	 * @throws IOException
	 */
	private boolean hasNestedNodes() throws Exception
	{
		int spaces = skipSpaces();
		
		if(sourceFile.isEOF() || sourceFile.available() < 2)
		{
			sourceFile.unread((byte)spaces);
			return false;
		}
		
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		
		sourceFile.unread((byte)(2+spaces));
		if(c1 == START_TAG)
		{
			if(c2 == END_PREFIX)
			{
				return false;
			}
			else if(c2 == EXCLAMATION_MARK)
			{
				return false;
			}
			return true;
		}
		
		return false;
	}
	
	private boolean hasDOCTYPE() throws IOException
	{
		int spaces = skipSpaces();
		if(sourceFile.isEOF() || sourceFile.available() < 9)
		{
			sourceFile.unread((byte)spaces);
			return false;
		}
		
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();
		char c7 = (char)sourceFile.read();
		char c8 = (char)sourceFile.read();
		char c9 = (char)sourceFile.read();
		
		sourceFile.unread((byte)(9 + spaces));
		if(c1 == START_TAG && c2 == EXCLAMATION_MARK && c3 == D && c4 == O 
				&& c5 == C && c6 == T && c7 == Y && c8 == P && c9 == E)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether the next element is XMLValue or not...
	 * 
	 * @return boolean indicates whether the next readable element is XMLValue or not...
	 * @throws IOException
	 */
	public boolean hasNodeValue() throws IOException
	{
		int spaces = skipSpaces();
		char c = (char)sourceFile.read();
		
		sourceFile.unread((byte)(1 + spaces));
		if(c != START_TAG)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Reads the next element in the file by elementType...
	 * 
	 * @param elementType
	 * @return XMLElement if valid element type is passed...
	 * @throws Exception
	 */
	private XMLElement readElement(XMLElementType elementType) throws Exception
	{
		XMLElement element = null;
		if(elementType == XMLElementType.XMLValue)
		{
			element = readNodeValue();
		}
		else if(elementType == XMLElementType.XMLComment)
		{
			element = readComment();
		}
		else if(elementType == XMLElementType.XML_CDATA)
		{
			element = readXMLCDATA();
		}
		else if(elementType == XMLElementType.XMLDeclaration)
		{
			element = readXMLDeclaration();
			skipSpaces();
		}
		else if(elementType == XMLElementType.XMLSimpleDeclaration)
		{
			element = readSimpleDeclaration();
		}
		else if(elementType == XMLElementType.XMLNode)
		{
			element = readNode();
		}
		else if(elementType == XMLElementType.DOCTYPE)
		{
			element = readDOCTYPE();
		}
		
		return element;
	}
	
	/**
	 * Reads the node value( text comes in the node )...
	 * @return XMLElement of type XMLValue...
	 * @throws Exception
	 */
	private XMLElement readNodeValue() throws Exception
	{
		StringBuilder  nodeValue = new StringBuilder();
		
		int spaces = skipSpaces();
		char c = (char)sourceFile.read();
		if(c == START_TAG)
		{
			return null;
		}
		
		sourceFile.unread((byte)(1 + spaces));
		c = (char)sourceFile.read();
		while(!sourceFile.isEOF() && c != START_TAG)
		{
			nodeValue.append(c);
			c = (char)sourceFile.read();
		}
		
		if(!sourceFile.isEOF())
		{
			sourceFile.unread(1);
		}
		
		return new XMLValue(nodeValue);
	}
	
	/**
	 * Reads the simple declaration.
	 * 
	 * @return XMLDeclaration object that has the data regarding the simple declaration...
	 * @throws Exception 
	 */
	private XMLElement readSimpleDeclaration() throws Exception
	{
		int spaces = skipSpaces();
		
		readExpectedChar('<');
		readExpectedChar('?');
		
		StringBuilder simpleDeclarationData = new StringBuilder();
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		while(!sourceFile.isEOF() && c1 != ASCII_SPACE && c2 != END_TAG)
		{
			simpleDeclarationData.append(c1).append(c2);
			c1 = (char)sourceFile.read();
			c2 = (char)sourceFile.read();
		}
		
		return new XMLSimpleDeclaration(simpleDeclarationData);
	}

	/**
	 * Reads the xml declaration.
	 * 
	 * @return XMLDeclaration object that has the data regarding the xml declaration...
	 * @throws Exception 
	 */
	private XMLElement readXMLDeclaration() throws Exception
	{
		// reading xml declaration prefix...
		readExpectedChar('<');
		readExpectedChar('?');
		readExpectedChar('x');
		readExpectedChar('m');
		readExpectedChar('l');
		
		skipSpaces();
		
		// Version is must if xml declaration present...
		readExpectedChar('v');
		readExpectedChar('e');
		readExpectedChar('r');
		readExpectedChar('s');
		readExpectedChar('i');
		readExpectedChar('o');
		readExpectedChar('n');
		
		skipSpaces();
		readExpectedChar('=');
		skipSpaces();
		
		StringBuilder version = new StringBuilder();
		StringBuilder encoding = new StringBuilder();
		StringBuilder standalone = new StringBuilder();
		char c = (char)sourceFile.read();
		if(c == QUOTATION_MARK || c == APOSTROPHE)
		{
			int prefix = c;
			
			c = (char)sourceFile.read();
			while(!sourceFile.isEOF() && !isWhitespace(c) && c != prefix)
			{
				version.append((char)c);
				c = (char)sourceFile.read();
			}

			if(!version.toString().equals(XML_VERSION))
			{
				throw new Exception("You got little error in xml declaration's version... it should be equal to \"" + XML_VERSION
						+ "\" But you got something like this : \"" + version + "\"");
			}
		}
		else
		{
			throw new Exception("Something weird happens while reading... Expected \" ' \" or \" \" \" character"
					+ "but got this wierdo :  " + c);
		}
		
		skipSpaces();
		
		// read encoding if present,,, this must come before standalone value and after version if present...
		if(hasEncoding())
		{
			readExpectedChar('e');
			readExpectedChar('n');
			readExpectedChar('c');
			readExpectedChar('o');
			readExpectedChar('d');
			readExpectedChar('i');
			readExpectedChar('n');
			readExpectedChar('g');
			
			skipSpaces();
			readExpectedChar('=');
			skipSpaces();
			
			c = (char)sourceFile.read();
			if(c == QUOTATION_MARK || c == APOSTROPHE)
			{
				int prefix = c;
				
				c = (char)sourceFile.read();
				while(!sourceFile.isEOF() && !isWhitespace(c) && c != prefix)
				{
					encoding.append((char)c);
					c = (char)sourceFile.read();
				}

				if(!ENCODING_VALUE_MAP.containsKey(encoding.toString().toUpperCase()))
				{
					throw new Exception("You got unexpected xml declaration's encoding... please check it...");
				}
			}
			else
			{
				throw new Exception("Something weird happens while reading... Expected \" ' \" or \" \" \" character"
						+ "but got this wierdo :  " + c);
			}
		}
		
		skipSpaces();
		
		// read standalone if present,,, this must come last if present...
		if(hasStandAlone())
		{
			readExpectedChar('s');
			readExpectedChar('t');
			readExpectedChar('a');
			readExpectedChar('n');
			readExpectedChar('d');
			readExpectedChar('a');
			readExpectedChar('l');
			readExpectedChar('o');
			readExpectedChar('n');
			readExpectedChar('e');
			
			skipSpaces();
			readExpectedChar('=');
			skipSpaces();
			
			c = (char)sourceFile.read();
			if(c == QUOTATION_MARK || c == APOSTROPHE)
			{
				int prefix = c;
				
				c = (char)sourceFile.read();
				while(!sourceFile.isEOF() && !isWhitespace(c) && c != prefix)
				{
					standalone.append((char)c);
					c = (char)sourceFile.read();
				}

				if(!STANDALONE_VALUE_MAP.containsKey(standalone.toString().toUpperCase()))
				{
					throw new Exception("You got unexpected xml declaration's standalone value... it must be \"yes\" or \"no\" in small case...");
				}
			}
			else
			{
				throw new Exception("Something weird happens while reading... Expected \" ' \" or \" \" \" character"
						+ "but got this wierdo :  " + c);
			}
		}
		
		skipSpaces();
		
		// only 3 attributes can come,,, version is must, encoding and standalone is optional,,, nothing else...
		readExpectedChar('?');
		readExpectedChar('>');
		
		XMLDeclaration xmlDeclaration = new XMLDeclaration(version, encoding, standalone);
		return xmlDeclaration;
	}
	
	private boolean hasStandAlone() throws IOException
	{
		if(sourceFile.isEOF() || sourceFile.available() <= 10)
		{
			return false;
		}
		
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();
		char c7 = (char)sourceFile.read();
		char c8 = (char)sourceFile.read();
		char c9 = (char)sourceFile.read();
		char c10 = (char)sourceFile.read();
		
		sourceFile.unread((byte)10);
		if(c1 == s && c2 == t && c3 == a && c4 == n && c5 == d && c6 == a && c7 == l && c8 == o && c9 == n && c10 == e)
		{
			return true;
		}
		
		return false;
	}

	private boolean hasEncoding() throws IOException
	{
		if(sourceFile.isEOF() || sourceFile.available() <= 8)
		{
			return false;
		}
		
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();
		char c7 = (char)sourceFile.read();
		char c8 = (char)sourceFile.read();
		
		sourceFile.unread((byte)8);
		if(c1 == e && c2 == n && c3 == c && c4 == o && c5 == d && c6 == i && c7 == n && c8 == g)
		{
			return true;
		}
		
		return false;
	}

	private XMLElement readRootNode() throws Exception
	{
		return readNode();
	}
	
	private XMLElement readNode() throws Exception
	{
		skipSpaces();
		readExpectedChar('<');
		
		// Reading node name...
		StringBuilder nodeName = readNodeName();
		
		// Creating node object by NodeFactory class...
		XMLNode node = XMLNodeFactory.createXMLNode(nodeName);

		// Reading node attributes...
		int attributeIndex = -1;
		while(hasAttribute())
		{
			XMLAttribute attribute = readXMLAttribute();
			if(attribute != null)
			{
				attributeIndex++;
				node.addAttribute(attributeIndex, attribute);
			}
		}
		
		// Check if node is self closing node or not...
		boolean isSelfClosingNode = checkSelfClosingNode();
		
		readExpectedChar('>');
		
		int valueOrNodeIndex = -1;
		
		// If node is not self closing node then there must be something inside it,,,
		// (ex text values, nested nodes, comments, or cdata)...
		if(!isSelfClosingNode)
		{
			int nodeSequentialIndex = 1;
			XMLElementType elementType = null;
			XMLElement element = null;
			while(true)
			{
				elementType = checkNextElementType();
				if(elementType == XMLElementType.XMLDeclaration)
				{
					throw new Exception("OMG,,, It looks like an XMLDeclaration came at wrong place,,, right???");
				}
				
				element = readElement(elementType);
				if(element == null)
				{
					readNodeEnd(nodeName);
					break;
				}
				else
				{
					if(elementType == XMLElementType.XMLNode)
					{
						((XMLNode)element).setNodeSequentialIndex(nodeSequentialIndex++);
						((XMLNode)element).setParentNode(node);
					}
					
					valueOrNodeIndex++;
					node.addValueOrNode(valueOrNodeIndex, element);
				}
			}
		}
		node.setSelfClosingNodeFlag(isSelfClosingNode);
		
		return node;
	}

	private XMLElement readXMLCDATA() throws Exception
	{
		skipSpaces();
		readExpectedChar('<');
		readExpectedChar('!');
		readExpectedChar('[');
		readExpectedChar('C');
		readExpectedChar('D');
		readExpectedChar('A');
		readExpectedChar('T');
		readExpectedChar('A');
		readExpectedChar('[');
		
		StringBuilder cData = new StringBuilder();
		char c1 = (char)sourceFile.read();
		char c2;
		char c3;
		while(!sourceFile.isEOF())
		{
			if(c1 == CLOSE_SQUARE_BRACKET)
			{
				c2 = (char)sourceFile.read();
				if(c2 == CLOSE_SQUARE_BRACKET)
				{
					c3 = (char)sourceFile.read();
					if(c3 == END_TAG)
					{
						break;
					}
					sourceFile.unread(1);
				}
				sourceFile.unread(1);
			}
			cData.append(c1);
			c1 = (char)sourceFile.read();
		}
		
		if(sourceFile.isEOF())
		{
			throw  new Exception("Error : While reading CDATA,,, End Of File encountered...");
		}
		
		return new XMLCData(cData);
	}

	private boolean hasAttribute() throws Exception
	{
		skipSpaces();
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		
		if(c == END_PREFIX)
		{
			c = (char)sourceFile.read();
			System.out.print((char)c);
			if(c == END_TAG)
			{
				sourceFile.unread((byte)2);
				return false;
			}
		}
		else if(c == END_TAG)
		{
			sourceFile.unread(1);
			return false;
		}
		
		sourceFile.unread(1);
		return true;
	}
	
	private void readNodeEnd(StringBuilder nodeName) throws Exception
	{
		skipSpaces();
		readExpectedChar('<');
		readExpectedChar('/');
		
		int nodeNameLength = nodeName.length();
		for(int i=0; i<nodeNameLength; i++)
		{
			readExpectedChar(nodeName.charAt(i));
		}
		
		readExpectedChar('>');
	}

	private boolean checkSelfClosingNode() throws Exception
	{
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		if(c == END_PREFIX)
		{
			readExpectedChar('>');
			sourceFile.unread(1);
			return true;
		}
		
		sourceFile.unread(1);
		return false;
	}
	
	private XMLAttribute readXMLAttribute() throws Exception
	{
		StringBuilder attributeName = readAttributeName();
		StringBuilder attributeValue = readAttributeValue();
		
		XMLAttribute attribute = new XMLAttribute(attributeName, attributeValue);
		return attribute;
	}

	private StringBuilder readAttributeValue() throws Exception 
	{
		StringBuilder attributeValue = new StringBuilder();
		int valueStartIdentifier;
		
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		while(true)
		{
			if(c == QUOTATION_MARK || c == APOSTROPHE)
			{
				break;
			}
			c = (char)sourceFile.read();
			System.out.print((char)c);
		}
		
		valueStartIdentifier = c;
		
		c = (char)sourceFile.read();
		System.out.print((char)c);
		while(c != valueStartIdentifier)
		{
			// Series of unexpected characters that are not allowed in attribute value...
			if(c == START_TAG)
			{
				throw new Exception("Error : Unexpected character found : " + c);
			}
			
			attributeValue.append((char)c);
			c = (char)sourceFile.read();
			System.out.print((char)c);
		}
		return attributeValue;
	}

	private StringBuilder readAttributeName() throws Exception 
	{
		StringBuilder attributeName = new StringBuilder();
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		while(c != EQUAL)
		{
			if(c == ASCII_SPACE)
			{
				continue;
			}
			
			if(c == END_PREFIX || c == END_TAG)
			{
				if(attributeName.length() != 0)
				{
					throw new Exception("Error : Unexpected character found : " + (char)c);
				}
				sourceFile.unread(1);
				return null;
			}
			
			attributeName.append((char)c);
			c = (char)sourceFile.read();
			System.out.print((char)c);
		}
		
		if(attributeName.length() == 0)
		{
			throw new Exception("Error : Attribute Name must not empty...");
		}
		return attributeName;
	}

	private StringBuilder readNodeName() throws Exception
	{
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		StringBuilder nodeName = new StringBuilder();
		while(c != ASCII_SPACE && c != END_PREFIX && c != END_TAG)
		{
			nodeName.append((char)c);
			c = (char)sourceFile.read();
			System.out.print((char)c);
		}
		
		if(c == END_PREFIX || c == END_TAG)
		{
			sourceFile.unread(1);
		}
		
		if(nodeName.length() == 0)
		{
			throw new Exception("Error : Expected node name but encountered space...");
		}
		return nodeName;
	}

	private XMLElement readComment() throws Exception
	{
		skipSpaces();
		readExpectedChar('<');
		readExpectedChar('!');
		readExpectedChar('-');
		readExpectedChar('-');
		
		StringBuilder comment = new StringBuilder();
		
		char c = (char) sourceFile.read();
		
		while(!sourceFile.isEOF())
		{
			if(c == DASH)
			{
				if(sourceFile.peek() == DASH)
				{
					readExpectedChar('-');
					readExpectedChar('>');
					break;
				}
			}
			comment.append(c);
			c = (char) sourceFile.read();
		}
		
		return new XMLComment(comment);
	}
	
	private XMLElement readDOCTYPE() throws IOException
	{
		skipSpaces();
		
		readExpectedChar('<');
		readExpectedChar('!');
		readExpectedChar('D');
		readExpectedChar('O');
		readExpectedChar('C');
		readExpectedChar('T');
		readExpectedChar('Y');
		readExpectedChar('P');
		readExpectedChar('E');
		
		StringBuilder docTypeString = new StringBuilder();
		
		char c = (char)sourceFile.read();
		while(!sourceFile.isEOF() && c != END_TAG)
		{
			docTypeString.append(c);
			c = (char)sourceFile.read();
		}
		
		return new DOCTYPE(docTypeString);
	}
	
	/**
     * Read one char and throw an exception if it is not the expected value.
     *
     * @param ec the char value that is expected.
     * @throws IOException if the read char is not the expected value or if an
     * I/O error occurs.
     */
    protected void readExpectedChar(char ec) throws IOException
    {
        char c = (char) sourceFile.read();
        if (c != ec)
        {
            throw new IOException("expected='" + ec + "' actual='" + c + "' at offset " + sourceFile.getPosition());
        }
    }
    
    /**
     * reads the spaces and skip it...
     * @throws IOException 
     */
    protected int skipSpaces() throws IOException
    {
    	int spaces = 0;
    	int c = sourceFile.read();
        while( isWhitespace(c))
        {
        	spaces++;
    		c = sourceFile.read();
        }

        if (c != -1)
        {
//        	if(spaces > 0) 
//        	{ 
//        		spaces--;
//        	}
            sourceFile.unread(1);
        }
        
        return spaces;
    }
    
    /**
     * This will tell if a character is whitespace or not.  These values are
     * specified in table 1 (page 12) of ISO 32000-1:2008.
     * @param c The character to check against whitespace
     * @return true if the character is a whitespace character.
     */
    protected boolean isWhitespace( int c )
    {
        return c == 0 || c == 9 || c == 12  || c == ASCII_LF
        || c == ASCII_CR || c == ASCII_SPACE;
    }
}
