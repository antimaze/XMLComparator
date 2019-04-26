package org.xml.run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.xml.xmlcomparator.Comparator;
import org.xml.xmlcomparator.ComparatorFactory;
import org.xml.xmlcomparator.IDiff;
import org.xml.xmlcomparator.StringDiffGenerator;
import org.xml.xmlcomparator.XMLDiff;
import org.xml.xmlgenerator.XMLGenerator;
import org.xml.xmlmodel.XMLDocument;

public class Run 
{

	private static File sourceFile;
	private static File targetFile;
	private static File differencesFile;
	
	private static XMLDocument sourceDoc;
	private static XMLDocument targetDoc;
	
	public static void main(String[] args) 
	{
		if(args.length < 2 || args.length > 3)
		{
			usage();
		}
		else
		{
			InitializeFiles(args);
			InitializeXMLDocuments(sourceFile, targetFile);
			// generateXMLFromXMLDocuments(sourceDoc, targetDoc);
			StringBuilder diffString = CompareXMLDocuments(sourceDoc, targetDoc);
			try 
			{
				WriteDiffInFile(diffString);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static void generateXMLFromXMLDocuments(XMLDocument sourceDoc, XMLDocument targetDoc)
	{
		XMLGenerator generateSourceXML = new XMLGenerator(sourceDoc, null);
		generateSourceXML.generateXML();
		
		XMLGenerator generateTargetXML = new XMLGenerator(targetDoc, null);
		generateTargetXML.generateXML();
	}

	private static void WriteDiffInFile(StringBuilder diffString) throws IOException
	{	     
	    FileWriter fileWriter = new FileWriter(differencesFile);
	    fileWriter.write(diffString.toString());
	    fileWriter.close();
	}

	private static StringBuilder CompareXMLDocuments(XMLDocument sourceDoc, XMLDocument targetDoc)
	{
		Comparator comparator = ComparatorFactory.getComparator(false);
		comparator.compare(sourceDoc, targetDoc);
		
		StringBuilder diffString = null;
		if(!comparator.isEqual())
		{
			diffString = new StringBuilder();
			
			XMLDiff xmlDiff = comparator.getDiffs();
			HashMap<Integer, List<IDiff>> xmlDiffsDetail = xmlDiff.getXMLDiffs();
			StringDiffGenerator stringDiffGenerator = new StringDiffGenerator();
			
			for(Entry<Integer, List<IDiff>> entry : xmlDiffsDetail.entrySet())
			{
				List<IDiff> diffs = entry.getValue();
				for(int i=0; i<diffs.size(); i++)
				{
					IDiff diff = diffs.get(i);
					diff.generateDiff(stringDiffGenerator);
					diffString.append(stringDiffGenerator.getDiff());
				}
			}
		}
		
//		System.out.println(diffString);
		
		return diffString;
	}

	private static void InitializeXMLDocuments(File sourceFile, File targetFile)
	{
		try 
		{
			sourceDoc = XMLDocument.load(sourceFile);
			targetDoc = XMLDocument.load(targetFile);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private static void InitializeFiles(String[] args)
	{
		sourceFile = new File(args[0].toString());
		targetFile = new File(args[1].toString());
		
		checkFile(sourceFile);
		checkFile(targetFile);
		
		if(args.length == 3)
		{
			differencesFile = new File(args[2].toString());
			checkFile(differencesFile);
		}
		else
		{
			differencesFile = new File(targetFile.getPath() + ".txt");
			if(!differencesFile.exists())
			{
				try 
				{
					differencesFile.createNewFile();
					System.out.println("File created successfully...");
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void checkFile(File file)
	{
		if(!file.exists())
		{
			System.out.println("File " + file.getAbsolutePath() + " is not exist...");
			usage();
			System.exit(0);
		}
	}
	
	private static void usage()
	{
		String usage = "\nAccepted Parameteres : "
				+ "\n1. <source xml-file path> (Required)"
				+ "\n2. <target xml-file path> (Required)"
				+ "\n3. <differences file> (Optional)"
				+ "\n *If <differences file> is given then it must be a txt file. and all the differences will be written into that file."
				+ "\n *If <differences file> is not given then it will create the <target xml file name>.txt file at <target xml-file path>";
		
		System.out.println(usage);
	}
}
