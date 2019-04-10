package org.xml.xmlparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Stack;

import org.xml.xmlmodel.XMLDocument;
import org.xml.xmlmodel.XMLElement.XMLElementType;
import org.xml.xmlmodel.XMLNode;
import org.xml.xmlmodel.XMLNodeFactory;
import org.xml.xmlmodel.XMLValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.xmlmodel.XMLAttribute;
import org.xml.xmlmodel.XMLCData;
import org.xml.xmlmodel.XMLComment;
import org.xml.xmlmodel.XMLDeclaration;

public abstract class BaseParser
{
	/**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(BaseParser.class);
    
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
	
	private Stack<XMLNode> tagStack = new Stack<XMLNode>();
	private Stack<Integer> tagIndexStack = new Stack<Integer>();
	
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
		
		XMLNode rootNode = readRootNode();
		rootNode.setParentNode(null);
		document.setRootNode(rootNode);
		
//		else
//		{
//			throw new Exception("Error : Do you know this file contains more than one root node???");
//		}
	}
	
	private void readBeforeRootNode() throws Exception
	{
		char c = (char)sourceFile.read();
		
		// If xml declaration is present in the xml file then it must come first...
		// Nothing come before the first xml declaration...
		// comment can come after first xml declaration or before any other xml declaration...
		while(!sourceFile.isEOF() && isWhitespace(c))
		{
			shouldExpectXMLDeclaration = false;
			c = (char)sourceFile.read();
		}
		
		if(!sourceFile.isEOF())
		{
			sourceFile.unread(1);
		}
		
		while(!sourceFile.isEOF())
		{
			c = (char)sourceFile.read();
			System.out.print((char)c);
			if(isWhitespace(c))
			{
				continue;
			}
			
			sourceFile.unread(1);
			
			if(hasXMLDeclaration())
			{
				if(shouldExpectXMLDeclaration)
				{
					XMLDeclaration xmlDeclaration = readXMLDeclaration();
					document.setXMLDeclaration(xmlDeclaration);
					shouldExpectXMLDeclaration = false;
				}
				else
				{
					throw new Exception("Error : I guess this is xml declaration came at the wrong place...");
				}
			}
			else if(hasComment())
			{
				XMLComment xmlComment = readComment();
			}
			else if(hasXMLCDATA())
			{
				throw new Exception("Error : No budy,,, this CDATA,,, you can't put it here,,, just grab that CDATA and get out...");
			}
			else
			{
				break;
			}
		}
	}
	
	private boolean hasXMLDeclaration() throws IOException
	{
		char c = (char)sourceFile.read();
		if(c == START_TAG)
		{
			c = (char)sourceFile.read();
			if(c == QUESTION_MARK)
			{
				char x = (char)sourceFile.read();
				char m = (char)sourceFile.read();
				char l = (char)sourceFile.read();
				
				if(x == this.x && m == this.m && l == this.l)
				{
					sourceFile.unread((byte)5);
					return true;
				}
				
				// unreads the other characters than 'xml'...
				sourceFile.unread((byte)3);
			}
			
			// unreads the character that is not '?'...
			sourceFile.unread(1);
		}
		
		// unreads the character that is not '<'...
		sourceFile.unread(1);
		return false;
	}

	/**
	 * Reads the xml declaration.
	 * 
	 * @return XMLDeclaration object that has the data regarding the xml declaration...
	 * @throws Exception 
	 */
	private XMLDeclaration readXMLDeclaration() throws Exception
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

	private XMLNode readRootNode() throws Exception
	{
		return readNode();
	}
	
	private XMLNode readNode() throws Exception
	{
		readExpectedChar('<');
		StringBuilder nodeName = readNodeName();
		XMLNode node = XMLNodeFactory.createXMLNode(nodeName);

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
		
		boolean isSelfClosingNode = checkSelfClosingNode();
		
		readExpectedChar('>');
		
		XMLValue nodeValue = null;
		int valueOrNodeIndex = -1;
		if(!isSelfClosingNode)
		{
			while(true)
			{
				nodeValue = readNodeValue();
				if(nodeValue != null)
				{
					valueOrNodeIndex++;
					node.addValueOrNode(valueOrNodeIndex, nodeValue);
				}
				
				if(hasComment())
				{
					XMLComment xmlComment = readComment();
					valueOrNodeIndex++;
					node.addValueOrNode(valueOrNodeIndex, xmlComment);
				}
				else if(hasXMLCDATA())
				{
					XMLCData xmlCData = readXMLCDATA();
					valueOrNodeIndex++;
					node.addValueOrNode(valueOrNodeIndex, xmlCData);
				}
				else if(hasXMLDeclaration())
				{
					throw new Exception("OMG,,, It looks like an XMLDeclaration came at wrong place,,, right???");
				}
				else if(hasNestedNodes())
				{
					XMLNode nestedNode = readNestedNode();
					nestedNode.setParentNode(node);
					if(nestedNode != null)
					{
						valueOrNodeIndex++;
						node.addValueOrNode(valueOrNodeIndex, nestedNode);
					}
				}
				else
				{
					checkNodeEnd(nodeName);
					break;
				}
			}
		}
		node.setSelfClosingNodeFlag(isSelfClosingNode);
		
		return node;
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
		else if(hasNestedNodes())
		{
			return XMLElementType.XMLNode;
		}
		else
		{
			return XMLElementType.None;
		}
	}

	private boolean hasXMLCDATA() throws IOException
	{
		skipSpaces();
		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		char c4 = (char)sourceFile.read();
		char c5 = (char)sourceFile.read();
		char c6 = (char)sourceFile.read();
		char c7 = (char)sourceFile.read();
		char c8 = (char)sourceFile.read();
		char c9 = (char)sourceFile.read();
		
		sourceFile.unread((byte)9);
		if(c1 == START_TAG && c2 == EXCLAMATION_MARK && c3 == OPEN_SQUARE_BRACKET && c4 == C && c5 == D && 
				c6 == A && c7 == T && c8 == A && c9 == OPEN_SQUARE_BRACKET)
		{
			return true;
		}
		
		return false;
	}

	private XMLCData readXMLCDATA() throws IOException
	{
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
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		while(!sourceFile.isEOF() && c1 != CLOSE_SQUARE_BRACKET && c2 != CLOSE_SQUARE_BRACKET && c3 != END_TAG)
		{
			cData.append((char)c1).append(c2).append(c3);
			c1 = (char)sourceFile.read();
			c2 = (char)sourceFile.read();
			c3 = (char)sourceFile.read();
		}
		
		XMLCData CDATA = new XMLCData(cData);
		return CDATA;
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

	private XMLNode readNestedNode() throws Exception 
	{
		return readNode();
	}
	
	private boolean hasComment() throws Exception
	{
		skipSpaces();
		readExpectedChar('<');

		char c1 = (char)sourceFile.read();
		char c2 = (char)sourceFile.read();
		char c3 = (char)sourceFile.read();
		if(c1 == EXCLAMATION_MARK && c2 == DASH && c3 == DASH)
		{
			return true;
		}

		sourceFile.unread((byte)4);
		return false;
	}
	
	private void checkNodeEnd(StringBuilder nodeName) throws Exception
	{
		readExpectedChar('<');
		readExpectedChar('/');
		
		StringBuilder nodeEndName = new StringBuilder();
		
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		
		while(!sourceFile.isEOF() && c != END_TAG)
		{
			nodeEndName.append((char)c);
			c = (char)sourceFile.read();
		}
		
		if(c != END_TAG)
		{
			throw new Exception("Node is not ended properly...");
		}
		
		if(!(nodeEndName.toString()).equals(nodeName.toString()))
		{
			throw new Exception("Node should be ended with " + nodeName + " but actually ended with " + nodeEndName);
		}
	}

	private boolean hasNestedNodes() throws Exception
	{
		skipSpaces();
		readExpectedChar('<');
		
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		
		if(c == END_PREFIX)
		{
			sourceFile.unread((byte)2);
			return false;
		}
		else
		{
			sourceFile.unread((byte)2);
			return true;
		}
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

	private XMLValue readNodeValue() throws Exception
	{
		StringBuilder  nodeValue = new StringBuilder();
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		
		boolean countValue = false;
		while(!sourceFile.isEOF() && c != START_TAG)
		{
			if(!isWhitespace(c))
			{
				countValue = true;
			}
			nodeValue.append((char)c);
			c = (char)sourceFile.read();
			System.out.print((char)c);
		}
		
		sourceFile.unread(1);
		
		if(nodeValue.length() != 0 && countValue)
		{
			return new XMLValue(nodeValue);
		}
		return null;
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

	private XMLComment readComment() throws Exception
	{
		char c = (char)sourceFile.read();
		System.out.print((char)c);
		
		StringBuilder comment = new StringBuilder();
		while(!sourceFile.isEOF())
		{
			if(c == DASH)
			{
				c = (char)sourceFile.read();
				System.out.print((char)c);
				if(c == DASH)
				{
					c = (char)sourceFile.read();
					System.out.print((char)c);
					if(c == END_TAG)
					{
						// Comment reading over...
						break;
					}
					else
					{
						// Got -- ... and it's error...
						throw new Exception("This comment contains illegel series of characters and that is this \"--\"...");
					}
				}
				else
				{
					comment.append(c);
				}
			}
			else
			{
				comment.append(c);
			}
			
			c = (char)sourceFile.read();
			System.out.print((char)c);
		}
		
		XMLComment xmlComment = new XMLComment(comment);
		return xmlComment;
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
    protected void skipSpaces() throws IOException
    {
    	int c = sourceFile.read();
        while( isWhitespace(c))
        {
    		c = sourceFile.read();
        }

        if (c != -1)
        {
            sourceFile.unread(1);
        }
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

	/*
	 * This was basic parsing...
	 * Not used anymore...
	 */
//	public void parse() throws Exception
//	{
//		StringBuilder text = new StringBuilder();
//		
//		StringBuilder nodeName = new StringBuilder();
//		StringBuilder nodeValue = new StringBuilder();
//		StringBuilder attributeKey = new StringBuilder();
//		StringBuilder attributeValue = new StringBuilder();
//		
//		int attributeIndex = 0;
//		int tagIndex = 0;
//		int keyValueStartIndicator = 0;
//		
//		int c;
//		while ((c = fileReader.read()) != -1)
//		{
//			if(c == START_TAG)
//			{
//				if(!isAttributeValueInProgress)
//				{
//					shouldExpectedStartTag = false;
//					shouldExpectNodeName = true;
//					if(text.length() > 0)
//					{
//						nodeValue = text;
//						text = new StringBuilder();
//						tagStack.peek().setNodeValue(nodeValue);
//					}
//				}
//				else
//				{
//					/*
//					 * <name id= " dasdas < dsadsa "> "<" will be invalid character in attribute value...
//					 */
//					throw new Exception("Error : Invalid character "+ c +" present in attribtue value...");
//				}
//				continue;
//			}
//			
//			if(c == QUOTATION_MARK || c == APOSTROPHE)
//			{
//				/*
//				 * Check for the apostrophe or quotation mark character.
//				 * <name id = " ">, <name id = ' '> correct...
//				 * <name id = " '>, <name id = ' "> incorrect...
//				 */
//				if(keyValueStartIndicator == 0)
//				{
//					keyValueStartIndicator = c;
//				}
//				else if(keyValueStartIndicator != c)
//				{
//					throw new Exception("Error : Invalid character... Expected \" " + keyValueStartIndicator + " \" but got \" " + c + " \"");
//				}
//				
//				if(shouldExpectAttributeValue)
//				{
//					shouldExpectAttributeKey = true;
//					if(text.length() > 0)
//					{
//						attributeValue = text;
//						tagStack.peek().addAttribute(attributeIndex++, new XMLAttribute(attributeKey, attributeValue));
//					}
//				}
//				text = new StringBuilder();
//				isAttributeValueInProgress = !isAttributeValueInProgress;
//				shouldExpectAttributeValue = !shouldExpectAttributeValue;
//				continue;
//			}
//			
//			if(c == EQUAL && !isAttributeValueInProgress)
//			{
//				if(shouldExpectAttributeKey)
//				{
//					shouldExpectAttributeKey = false;
//					if(text.length() > 0)
//					{
//						attributeKey = new StringBuilder(text.toString().trim());
//						text = new StringBuilder();
//					}
//				}
//				continue;
//			}
//			
//			if(c == SPACE && !isAttributeValueInProgress)
//			{
//				if(shouldExpectNodeName)
//				{
//					shouldExpectNodeName = false;
//					shouldExpectAttributeKey = true;
//					if(text.length() > 0)
//					{
//						nodeName = text;
//						text = new StringBuilder();
//						XMLNode tag = XMLNodeFactory.createXMLNode(nodeName);
//						tagStack.push(tag);
//						tagIndexStack.push(tagIndex);
//						
//						attributeIndex = 0;
//					}
//					continue;
//				}
//			}
//			
//			if(c == END_PREFIX && !isAttributeValueInProgress)
//			{
//				shouldExpectTagEnd = true;
//				shouldExpectNodeName = false;
//				continue;
//			}
//			
//			if(c == END_TAG)
//			{
//				if(shouldExpectTagEnd)
//				{
//					shouldExpectTagEnd = false;
//					shouldExpectedStartTag = true;
//					if(text.length() == 0)
//					{
//						tagStack.peek().setSelfClosingNodeFlag(true);
//					}
//					else if(text.toString().endsWith((tagStack.peek().getNodeName().toString())))
//					{
//					}
//					else
//					{
//						System.out.println("Error Occured while parsing...");
//						text = new StringBuilder();
//						return;
//					}
//					
//					XMLNode popedTag = tagStack.pop();
//					tagIndexStack.pop();
//					if(tagStack.empty())
//					{
//						boolean isMoreThanOneRootNode = !document.setRootNode(popedTag);
//						if(isMoreThanOneRootNode)
//						{
//							throw new Exception("Error : xml file contains more than one root node...");
//						}
//					}
//					else
//					{
//						Integer tIndex = tagIndexStack.pop();
//						tagStack.peek().addNestedNode(tIndex++, popedTag);
//
//						tagIndexStack.push(tIndex);
//					}
//					text = new StringBuilder();
//				}
//				else
//				{
//					if(shouldExpectNodeName)
//					{
//						shouldExpectNodeName = false;
//						if(text.length() > 0)
//						{
//							nodeName = text;
//							text = new StringBuilder();
//							XMLNode tag = XMLNodeFactory.createXMLNode(nodeName);
//							tagStack.push(tag);
//							tagIndexStack.push(tagIndex);
//						}
//					}
//				}
//				continue;
//			}
//			
//			if(!shouldExpectedStartTag)
//			{
//				text.append((char)c);
//			}
//		}
//	}
}
