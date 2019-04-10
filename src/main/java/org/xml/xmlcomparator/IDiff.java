package org.xml.xmlcomparator;

public interface IDiff 
{
	/**
	 * this method will generate the diff according to passed IDiffGenerator logic...
	 * 
	 * @param diffGenerator
	 */
	public void generateDiff(IDiffGenerator diffGenerator);
}
