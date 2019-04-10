package org.xml.xmlcomparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLDiff 
{
	private HashMap<Integer, List<IDiff>> xmlDiffs;
	public XMLDiff() 
	{
		xmlDiffs = new HashMap<Integer, List<IDiff>>();
	}
	
	public void addXMLDiff(Integer uniqueId, IDiff diffDetail)
	{
		List<IDiff> existingDiffs = null;
		if(xmlDiffs.containsKey(uniqueId))
		{
			existingDiffs = xmlDiffs.get(uniqueId);
			existingDiffs.add(diffDetail);
		}
		else
		{
			existingDiffs = new ArrayList<IDiff>();
			existingDiffs.add(diffDetail);
		}
		xmlDiffs.put(uniqueId, existingDiffs);
	}
	
	public List<IDiff> getXMLDiff(Integer uniqueId)
	{
		return xmlDiffs.get(uniqueId);
	}
	
	public HashMap<Integer, List<IDiff>> getXMLDiffs()
	{
		return this.xmlDiffs;
	}
	
	public boolean hasDifference()
	{
		return !xmlDiffs.isEmpty();
	}
}
