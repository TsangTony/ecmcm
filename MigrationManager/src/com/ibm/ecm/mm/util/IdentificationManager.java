package com.ibm.ecm.mm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hslf.model.Hyperlink;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentificationRule;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;

public class IdentificationManager {

	public static ArrayList<IdentificationRule> createObsoleteRules(int startPriority) {
		String[] obsoleteKeywords = {"Obsolete", "Obselete", "Obsolote",  "Obsolate", "Archive", "Archieve", "/Old"};
		boolean isFirstRow = true;
		ArrayList<IdentificationRule> identificationRules = new ArrayList<IdentificationRule>();
		for (String obsoleteKeyword : obsoleteKeywords) {
			IdentificationRule identificationRule = new IdentificationRule();
			identificationRule.setPriority(startPriority);
			if (isFirstRow) {
				if (identificationRule.getPriority() > 1)
					identificationRule.setLogicalOperator("And");
				isFirstRow = false;
			}
			else 
				identificationRule.setLogicalOperator("And");
			identificationRule.setAttribute("File Path");
			identificationRule.setRelationalOperator("Not contains");
			identificationRule.setValue(obsoleteKeyword);
			identificationRule.setNew(true);
			identificationRules.add(identificationRule);
			startPriority++;
		}
		return identificationRules;
	}
	
	
	public static IdentifiedDocInstances identify(Document document) throws SQLException {
		IdentifiedDocInstances identifiedDocInstances = null;
		ArrayList<IdentificationRule> contentRules = new ArrayList<IdentificationRule>();		
		
		if (document.getIdentificationRules().size() != 0)
			for (IdentificationRule identificationRule : document.getIdentificationRules())
				if (identificationRule.getAttribute().equals("Content"))
					contentRules.add(identificationRule);

		if (contentRules.size() == 0) {
			System.out.println("DOC-" + document.getId() + ": Step 1 of 2 Querying the database");
			identifiedDocInstances = DataManager.getDocInstances(document, false, false);
			System.out.println("DOC-" + document.getId() + ": Step 2 of 2 Linked file analysis");
		}
		else {
			
			/*
			 * If there is content rule, read the content and write to dbo.Identified_Doc_Instance.snippet first. After that,
			 * apply the rules and identify from dbo.Identified_Doc_Instance;
			 */
			
			identifiedDocInstances = DataManager.getDocInstances(document, false, true);
			int count = 1;
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				System.out.println("DOC-" + document.getId() + ": Step 1 of 5 Looking into content " + Math.round(count * 100.0f / identifiedDocInstances.size()) + "%");
				String content = identifiedDocInstance.getContent();
				if (!content.equals("")) {
					for (IdentificationRule contentRule : contentRules) {
						if (content.equals(contentRule.getValue())) {
							identifiedDocInstance.setSnippet(contentRule.getValue());
							break;
						}
						else if (content.contains(contentRule.getValue())) {
							identifiedDocInstance.setSnippet(identifiedDocInstance.getSnippet() + contentRule.getValue());
						}
					}
				}
				count++;
			}
			System.out.println("DOC-" + document.getId() + ": Step 2 of 5 Writing into Snippet table");
			DataManager.addSnippet(document.getId(), identifiedDocInstances);
			System.out.println("DOC-" + document.getId() + ": Step 3 of 5 Querying Snippet table");
			identifiedDocInstances = DataManager.getDocInstances(document, true, false);
			System.out.println("DOC-" + document.getId() + ": Step 4 of 5 Clearing Snippet table");
			DataManager.removeSnippet(document.getId());
			System.out.println("DOC-" + document.getId() + ": Step 5 of 5 Linked file analysis");
		}
		
		if (document.isIncludeLinkedFile()) {
			IdentifiedDocInstances linkedDocumentInstances = new IdentifiedDocInstances();
			int count = 1;
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				System.out.println("DOC-" + document.getId() + ": Identifying linked files " + Math.round(count * 100.0f / identifiedDocInstances.size()) + "%");
				IdentifiedDocInstances newLinkedDocumentInstances = getLinkedDocumentInstances(document,identifiedDocInstance,identifiedDocInstances.getDigests());
				linkedDocumentInstances.addAll(newLinkedDocumentInstances);
				linkedDocumentInstances.getLatestSnapshotInstances().addAll(newLinkedDocumentInstances.getLatestSnapshotInstances());
				count++;
			}			
			identifiedDocInstances.addAll(linkedDocumentInstances);
			identifiedDocInstances.getLatestSnapshotInstances().addAll(linkedDocumentInstances.getLatestSnapshotInstances());
		}
		
		int displayCount = identifiedDocInstances.getLatestSnapshotInstances().size();
		
		if (displayCount == 0)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No document instance is identified."));
		else if (displayCount == 1)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "1 document instance is identified."));
		else
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", displayCount + " document instances are identified."));

		return identifiedDocInstances;			
	}


	private static IdentifiedDocInstances getLinkedDocumentInstances(Document document, IdentifiedDocInstance identifiedDocInstance, HashMap<String, Integer> digests) {
		
		System.out.println("DOC-" + document.getId() + ": Identifying linked files from " + identifiedDocInstance.getFullyQualifiedPath());
				
		IdentifiedDocInstances newLinkedDocumentInstances = new IdentifiedDocInstances();
		ArrayList<String> linksFound = new ArrayList<String>();
		
		PowerPointExtractor pptExtractor = null;
		XSLFPowerPointExtractor pptxExtractor = null;		
		File file = null;
		FileInputStream fis = null;
		try {
			//Windows
			file = new File(identifiedDocInstance.getFullyQualifiedPath());
			fis = new FileInputStream(file.getAbsolutePath());
		}
		catch (FileNotFoundException e1) {
			//Unix
			try {
				file = new File(identifiedDocInstance.getUnixMountedPath());
				fis = new FileInputStream(file.getAbsolutePath());
			}
			catch (FileNotFoundException e) {
				String log = "DOC-" + document.getId() + ": Cannot found " + identifiedDocInstance.getFullyQualifiedPath();
				if (identifiedDocInstance.getSnapshotDeleted() > 0) {
					log += " as expected";
					System.out.println(log);
				}
				else {
					log += " unexpectedly";
					System.out.println(log);		
					System.err.println(log);				
				}
				return newLinkedDocumentInstances;
			}
		}			
			
		try {
			if (identifiedDocInstance.getExtension().toUpperCase().equals("PPT") ||
				identifiedDocInstance.getExtension().toUpperCase().equals("PPS")) {
				System.out.println("DOC-" + document.getId() + ": reading content from " + identifiedDocInstance.getFullyQualifiedPath());
				SlideShow slideShow = new SlideShow(fis);
				Slide[] slides = slideShow.getSlides();
				for (Slide slide : slides) {
					TextRun[] textRuns = slide.getTextRuns();
					for (TextRun textRun : textRuns) {
						Hyperlink[] hyperlinks = textRun.getHyperlinks();
						for (Hyperlink hyperlink : hyperlinks)
							if (hyperlink.getAddress() != null)
								linksFound.add(hyperlink.getAddress());		
					}
				}	
			}
			else if (identifiedDocInstance.getExtension().toUpperCase().equals("PPTX") ||
					identifiedDocInstance.getExtension().toUpperCase().equals("PPSX")) {	
				System.out.println("DOC-" + document.getId() + ": reading content from " + identifiedDocInstance.getFullyQualifiedPath());
				XMLSlideShow slideShow = new XMLSlideShow(fis);
				XSLFSlide[] slides = slideShow.getSlides();
				for (XSLFSlide slide : slides) {
					XSLFShape[] shapes = slide.getShapes();
					for (XSLFShape shape : shapes) {			
						try {
							List<XSLFTextParagraph> textParagraphs = ((XSLFAutoShape)shape).getTextParagraphs();
							for (XSLFTextParagraph textParagraph : textParagraphs) {
								List<XSLFTextRun> textRuns = textParagraph.getTextRuns();
								for (XSLFTextRun textRun : textRuns) {
									XSLFHyperlink hyperlink =  textRun.getHyperlink();
									if (hyperlink != null)
										if (hyperlink.getTargetURI().toString() != null)
											linksFound.add(hyperlink.getTargetURI().toString());		
								}
							}
						} catch (ClassCastException e) {							
						}
					}
				}								
			}	
			else {
				//System.out.println("DOC-" + document.getId() + ": Cannot read content from " + identifiedDocInstance.getFullyQualifiedPath() + " due to unsupported file format.");	
			}
			
			for (String link : linksFound) {
				link = link.replace("\\", "/");
				if (link.startsWith("//"))
					link.replaceFirst("//","\\\\");

				link = link.replace("%20", " ");
				
				if (!link.contains("/"))
					link = "\\\\" + identifiedDocInstance.getServer() + "/" + identifiedDocInstance.getVolumePath() + "/" + link;

				System.out.println(" Link found " + link);	
				
				
				IdentifiedDocInstance newIdentifiedDocInstance = DataManager.getDocInstance(document,link);		
				if (newIdentifiedDocInstance != null) {
					if ((digests.containsKey(newIdentifiedDocInstance.getDigest()) &&
							digests.get(newIdentifiedDocInstance.getDigest()) > 0) ||
						 !digests.containsKey(newIdentifiedDocInstance.getDigest())) {
						
					    digests.put(identifiedDocInstance.getDigest(),Integer.valueOf(identifiedDocInstance.getSnapshotDeleted()));
						System.out.println(" Link is qualified " + link);
						
						newIdentifiedDocInstance.setOriginInstanceId(identifiedDocInstance.getId());
						newLinkedDocumentInstances.add(newIdentifiedDocInstance);
						if (newIdentifiedDocInstance.getSnapshotDeleted()==0)
							newLinkedDocumentInstances.getLatestSnapshotInstances().add(newIdentifiedDocInstance);
						
						IdentifiedDocInstances instancesLinkedToNewIdentifiedDocInstance = getLinkedDocumentInstances(document,newIdentifiedDocInstance,digests); 
						newLinkedDocumentInstances.addAll(instancesLinkedToNewIdentifiedDocInstance);
						newLinkedDocumentInstances.getLatestSnapshotInstances().addAll(instancesLinkedToNewIdentifiedDocInstance);
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("Cannot read content from " + identifiedDocInstance.getFullyQualifiedPath() + " due to error: " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				if (pptExtractor != null)
					pptExtractor.close();
				if (pptxExtractor != null)
					pptxExtractor.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return newLinkedDocumentInstances;				
	}


	public static void saveIdentifiedDocInstances(Document document, IdentifiedDocInstances identifiedDocInstances, boolean extractMetadata) {
		DataManager.addIdentifiedDocInstances(document.getId(), identifiedDocInstances);
			
		/*
		 *  Metadata Extraction
		 */
		
		if (extractMetadata)
			ExtractionManager.extractMetadata(identifiedDocInstances, document);
		
	}
	
}
