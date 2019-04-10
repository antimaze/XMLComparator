package org.xml.xmlcomparator;

public class ComparatorFactory
{	
	/**
	 * this method returns the object of StrictComparator...
	 * 
	 * @param isStrictCompare is not used in this application...
	 * @return Comparator...
	 */
	public static Comparator getComparator(boolean isStrictCompare)
	{
		return new StrictComparator();
	}
}
