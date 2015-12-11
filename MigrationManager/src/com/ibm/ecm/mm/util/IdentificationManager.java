package com.ibm.ecm.mm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.ibm.ecm.mm.model.MetadataValue;

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
		System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ": Run Identification 1/2 identifying... ");
		IdentifiedDocInstances identifiedDocInstances = null;
		ArrayList<IdentificationRule> contentRules = new ArrayList<IdentificationRule>();		
		
		if (document.getIdentificationRules().size() != 0)
			for (IdentificationRule identificationRule : document.getIdentificationRules())
				if (identificationRule.getAttribute().equals("Content"))
					contentRules.add(identificationRule);

		if (contentRules.size() == 0) {
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 1 of 2 Querying the database");
			identifiedDocInstances = DataManager.getDocInstances(document, false, false);
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 2 of 2 Linked file analysis");
		}
		else {
			
			/*
			 * If there is content rule, read the content and write to dbo.Identified_Doc_Instance.snippet first. After that,
			 * apply the rules and identify from dbo.Identified_Doc_Instance;
			 */

			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 1 of 6 Querying the database");
			identifiedDocInstances = DataManager.getDocInstances(document, false, true);
			int count = 1;
			int progress = 0;
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				if (Math.round(count * 100.0f / identifiedDocInstances.size()) > progress) {
					progress = Math.round(count * 100.0f / identifiedDocInstances.size());
					System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 2 of 6 Looking into content " + progress + "%");
				}
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
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 3 of 6 Writing into Snippet table");
			DataManager.addSnippet(document.getId(), identifiedDocInstances);
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 4 of 6 Querying Snippet table");
			identifiedDocInstances = DataManager.getDocInstances(document, true, false);
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 5 of 6 Clearing Snippet table");
			DataManager.removeSnippet(document.getId());
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":   Step 6 of 6 Linked file analysis");
		}
		
		if (document.isIncludeLinkedFile()) {
			IdentifiedDocInstances linkedDocumentInstances = new IdentifiedDocInstances();
			HashMap<String, IdentifiedDocInstance> digests = new HashMap<String, IdentifiedDocInstance>();
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				digests.put(identifiedDocInstance.getDigest(), identifiedDocInstance);
			}
			
			int count = 1;
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ":     Identifying linked files " + Math.round(count * 100.0f / identifiedDocInstances.size()) + "% - " + identifiedDocInstance.getFullyQualifiedPath());
				IdentifiedDocInstances newLinkedDocumentInstances = getLinkedDocumentInstances(document,identifiedDocInstance,digests);
				linkedDocumentInstances.addAll(newLinkedDocumentInstances);
				linkedDocumentInstances.getLatestSnapshotInstances().addAll(newLinkedDocumentInstances.getLatestSnapshotInstances());
				count++;
			}			
			identifiedDocInstances.addAll(linkedDocumentInstances);
			identifiedDocInstances.getLatestSnapshotInstances().addAll(linkedDocumentInstances.getLatestSnapshotInstances());
		}
		
		int displayCount = identifiedDocInstances.getLatestSnapshotInstances().size();
		
		if (displayCount == 0)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "DOC-" + document.getId() + ": No document instance is identified."));
		else if (displayCount == 1)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "DOC-" + document.getId() + ": 1 document instance is identified."));
		else
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "DOC-" + document.getId() + ": " + displayCount + " document instances are identified."));

		return identifiedDocInstances;			
	}


	private static IdentifiedDocInstances getLinkedDocumentInstances(Document document, IdentifiedDocInstance identifiedDocInstance, HashMap<String, IdentifiedDocInstance> digests) {
		
		//System.out.println("DOC-" + document.getId() + ": Identifying linked files from " + identifiedDocInstance.getFullyQualifiedPath());
				
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
					//System.out.println(log);
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
				//System.out.println("DOC-" + document.getId() + ": reading content from " + identifiedDocInstance.getFullyQualifiedPath());
				SlideShow slideShow = new SlideShow(fis);
				Slide[] slides = slideShow.getSlides();
				for (Slide slide : slides) {
					TextRun[] textRuns = slide.getTextRuns();
					for (TextRun textRun : textRuns) {
						if (textRun != null) {
							Hyperlink[] hyperlinks = textRun.getHyperlinks();
							if (hyperlinks != null) {
								for (Hyperlink hyperlink : hyperlinks)
									if (hyperlink != null && hyperlink.getAddress() != null)
										linksFound.add(hyperlink.getAddress());
							}
						}
					}
				}	
			}
			else if (identifiedDocInstance.getExtension().toUpperCase().equals("PPTX") ||
					identifiedDocInstance.getExtension().toUpperCase().equals("PPSX")) {	
				//System.out.println("DOC-" + document.getId() + ": reading content from " + identifiedDocInstance.getFullyQualifiedPath());
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
				//System.out.println(link);
				link = link.replace("file://///cpadm001.corp.cathaypacific.com/clk/APPFOLDER", "\\\\10.210.225.24");
				link = link.replace("file://///clkcbt01", "\\\\10.210.225.24");
				
				link = link.replace("\\", "/");
				if (link.startsWith("//"))
					link.replaceFirst("//","\\\\");

				link = link.replace("%20", " ");
				
				if (link.startsWith("../")) {
					String prefix = "\\\\" + identifiedDocInstance.getServer() + "/" + identifiedDocInstance.getVolumePath();
					while (link.startsWith("../")) {
						prefix = prefix.substring(0, prefix.lastIndexOf("/"));
						link = link.replaceFirst("../", "");
					}
					link = prefix + "/" + link;
				}
				
				if (!link.startsWith("\\\\")) {
					link = "\\\\" + identifiedDocInstance.getServer() + "/" + identifiedDocInstance.getVolumePath() + "/" + link;
				}
				
					
				IdentifiedDocInstance newIdentifiedDocInstance = DataManager.getDocInstance(document,link);		
				if (newIdentifiedDocInstance == null) {
					System.out.println("DOC-" + document.getId() + ":  Invalid link: " + link);
				}
				else {
				    MetadataValue metadataValue = new MetadataValue();
				    metadataValue.setValue(String.valueOf(identifiedDocInstance.getId()));
				    
					if (digests.containsKey(newIdentifiedDocInstance.getDigest()) &&
						digests.get(newIdentifiedDocInstance.getDigest()).getSnapshotDeleted() > 0 ||
						!digests.containsKey(newIdentifiedDocInstance.getDigest())) {						
					    System.out.println("DOC-" + document.getId() + ":  Qualified link: " + link);

					    newIdentifiedDocInstance.getMetadataValues().add(metadataValue);
					    digests.put(newIdentifiedDocInstance.getDigest(), newIdentifiedDocInstance);
					    
						//newIdentifiedDocInstance.setOriginInstanceId(identifiedDocInstance.getId());
						newLinkedDocumentInstances.add(newIdentifiedDocInstance);
						if (newIdentifiedDocInstance.getSnapshotDeleted()==0)
							newLinkedDocumentInstances.getLatestSnapshotInstances().add(newIdentifiedDocInstance);
						
						IdentifiedDocInstances instancesLinkedToNewIdentifiedDocInstance = getLinkedDocumentInstances(document,newIdentifiedDocInstance,digests); 
						newLinkedDocumentInstances.addAll(instancesLinkedToNewIdentifiedDocInstance);
						newLinkedDocumentInstances.getLatestSnapshotInstances().addAll(instancesLinkedToNewIdentifiedDocInstance);
					}
					else {
						System.out.println("DOC-" + document.getId() + ":  Duplicated link: " + link);		
						ArrayList<MetadataValue> metadataValues = digests.get(newIdentifiedDocInstance.getDigest()).getMetadataValues();
						if (metadataValues.size() > 0)
							metadataValues.get(0).setValue(metadataValues.get(0).getValue() + "," + metadataValue.getValue());					
						else
							metadataValues.add(metadataValue);
					}
					
				}
			}
		}
		catch (Exception e) {
			System.out.println("DOC-" + document.getId() + ": Cannot read content from " + identifiedDocInstance.getFullyQualifiedPath() + " due to error: " + e.getClass().getName() + " " + e.getMessage());
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

		System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ": Run Identification 2/2 saving... ");
		DataManager.addIdentifiedDocInstances(document, identifiedDocInstances);
		
		
		/*
		 * Update Document_snapshot_count
		 */
		
		
		DataManager.saveSnapshotCount(document.getId());
		
		
			
		/*
		 *  Metadata Extraction
		 */
		
		//if (extractMetadata)
			//ExtractionManager.extractMetadata(identifiedDocInstances, document);
		
	}
	
}
