package com.ibm.ecm.mm.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentificationRule;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.MetadataProperty;

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
	
	public static DataTableArrayList<IdentifiedDocInstance> identify(Document document, boolean noPdf) {
		DataTableArrayList<IdentifiedDocInstance> identifiedDocInstances = null;
		ArrayList<IdentificationRule> contentRules = new ArrayList<IdentificationRule>();		
		
		if (document.getIdentificationRules().size() != 0)
			for (IdentificationRule identificationRule : document.getIdentificationRules())
				if (identificationRule.getAttribute().equals("Content"))
					contentRules.add(identificationRule);
		
		try {
			if (contentRules.size() == 0) {
				identifiedDocInstances = DataManager.getDocInstances(document, noPdf, false, false);
			}
			else {
				
				/*
				 * If there is content rule, read the content and write to dbo.Identified_Doc_Instance.snippet first. After that,
				 * apply the rules and identify from dbo.Identified_Doc_Instance;
				 */
				
				identifiedDocInstances = DataManager.getDocInstances(document, noPdf, false, true);
				for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
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
				}
				DataManager.addSnippet(document.getId(), identifiedDocInstances);
				identifiedDocInstances = DataManager.getDocInstances(document, noPdf, true, false);
				DataManager.removeSnippet(document.getId());
			}
			
			if (identifiedDocInstances.size() == 0)
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No document instance is identified."));
			else if (identifiedDocInstances.size() == 1)
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "1 document instance is identified."));
			else
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", identifiedDocInstances.size() + " document instances are identified."));
			
		}
		catch (SQLException e) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Identification Rules have error."));
		}
		
		return identifiedDocInstances;
	}
	
	public static void saveIdentifiedDocInstances(Document document, HashSet<Long> existingIdentifiedDocInstaneIds, DataTableArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		
		try {
			
			existingIdentifiedDocInstaneIds = DataManager.addIdentifiedDocInstances(document.getId(), existingIdentifiedDocInstaneIds, identifiedDocInstances);
			
			/*
			 *  Metadata Extraction
			 */
			
			ArrayList<MetadataProperty> extractedMetadataProperties = ExtractionManager.extractMetadata(identifiedDocInstances, document);
			String message = "Identified Document Instances are saved.";
			if (extractedMetadataProperties.size() == 0) {
				message += " There is no default Metadata Property extracted.";
			}
			else {
				message += "The following Metadata Properties are extracted and saved. <ol>";
			for (MetadataProperty metadataProperty : extractedMetadataProperties) {
				message += "<li>" + metadataProperty.getName() + "</li>";
			}
			message += "</ol>";
			}
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful",  message) );
		

		}
		catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  e.getMessage() + " Nothing is saved.") );
		}
	}
	
}
