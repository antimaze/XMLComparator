package org.xml.xmlcomparator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.xmlgenerator.XMLGenerator;
import org.xml.xmlmodel.XMLDocument;

public class Run
{
	public static void main(String[] args) 
	{	
		String sourceXMLFile = "C:\\Users\\Savan\\eclipse-workspace\\xmlcomparator\\sample.xml";
		String targetXMLFile = "C:\\Users\\Savan\\eclipse-workspace\\xmlcomparator\\sample1.xml";
		
		File sourceFile = new File(sourceXMLFile);
		File targetFile = new File(targetXMLFile);
		XMLDocument sourceDoc;
		XMLDocument targetDoc;
		try 
		{
			sourceDoc = XMLDocument.load(sourceFile);
			targetDoc = XMLDocument.load(targetFile);
			
			XMLGenerator generateSourceXML = new XMLGenerator(sourceDoc, null);
			generateSourceXML.generateXML();
			
			XMLGenerator generateTargetXML = new XMLGenerator(targetDoc, null);
			generateTargetXML.generateXML();
			
			Comparator comparator = ComparatorFactory.getComparator(false);
			comparator.compare(sourceDoc, targetDoc);
			
			System.out.println("\n\nIs 2 XML is Equal : ");
			System.out.print(comparator.isEqual());
			System.out.println("");
			
			XMLDiff xmlDiff = comparator.getDiffs();
			HashMap<Integer, List<IDiff>> xmlDiffsDetail = xmlDiff.getXMLDiffs();
			IDiffGenerator stringDiffGenerator = new StringDiffGenerator();
			
			for(Map.Entry<Integer, List<IDiff>> entry : xmlDiffsDetail.entrySet())
			{
				List<IDiff> diffs = entry.getValue();
				for(int i=0; i<diffs.size(); i++)
				{
					IDiff diff = diffs.get(i);
					diff.generateDiff(stringDiffGenerator);
					StringBuilder diffString = ((StringDiffGenerator)stringDiffGenerator).getDiff();
					System.out.println(diffString);
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
