package org.xml.xmlcomparator;

public interface IDiffGenerator 
{
	/**
	 * generate diff according to the passed IDiff object type...
	 * @param nodeDiff
	 */
	public void generateDiff(XMLNodeDifference nodeDiff);
	public void generateDiff(XMLAttributeDifference attributeDiff);
	public void generateDiff(XMLValueDifference valueDiff);
}
