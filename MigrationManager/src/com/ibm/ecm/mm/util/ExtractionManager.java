package com.ibm.ecm.mm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;
import com.ibm.ecm.mm.model.Lookup;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataExtractionRules;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.model.MetadataValue;

public class ExtractionManager {
	
	public static ArrayList<MetadataExtractionRules> getMetadataExtractionRules(Document document) {
		ArrayList<MetadataProperty> metadataPropreties = DataManager.getMetadataPropreties(document.getId());
		ArrayList<MetadataExtractionRules> metadataExtractionRulesList = new ArrayList<MetadataExtractionRules>();
		
		for (CommencePath commencePath : document.getCommencePaths()) {
			for (MetadataProperty metadataProperty : metadataPropreties) {				
				DataTableArrayList<MetadataExtractionRule> existingMetadataExtractionRuleList = DataManager.getMetadataExtractionRules(document.getId(),commencePath.getId(),metadataProperty.getId());
				if (existingMetadataExtractionRuleList.size() > 0) {
					MetadataExtractionRules existingMetadataExtractionRules = new MetadataExtractionRules();
					existingMetadataExtractionRules.setCommencePathId(commencePath.getId());
					existingMetadataExtractionRules.setMetadataProperty(metadataProperty);
					existingMetadataExtractionRules.setRules(existingMetadataExtractionRuleList);
					existingMetadataExtractionRules.setDefault(existingMetadataExtractionRuleList.get(0).isDefault());
					if (existingMetadataExtractionRules.isDefault())
						existingMetadataExtractionRules.setLookups(DataManager.getLookups(metadataProperty.getId()));	
					
					metadataExtractionRulesList.add(existingMetadataExtractionRules);
				}
				else {
					MetadataExtractionRules metadataExtractionRules = createDefaultMetadataExtractionRules(metadataProperty, commencePath.getId());
					if (metadataExtractionRules != null) {
						metadataExtractionRulesList.add(metadataExtractionRules);
					}
				}
			}
		}
		
		return metadataExtractionRulesList;
	}
	
	public static MetadataExtractionRules createDefaultMetadataExtractionRules(MetadataProperty metadataProperty, int commencePathId) {
		MetadataExtractionRules defaultMetadataExtractionRules = new MetadataExtractionRules();
		defaultMetadataExtractionRules.setLookups(DataManager.getLookups(metadataProperty.getId()));			
		if (defaultMetadataExtractionRules.getLookups().size() > 0) {
			defaultMetadataExtractionRules.setCommencePathId(commencePathId);
			defaultMetadataExtractionRules.setMetadataProperty(metadataProperty);
			defaultMetadataExtractionRules.setDefault(true);
			
			
			MetadataExtractionRule metadataExtractionRuleFP = new MetadataExtractionRule();
			metadataExtractionRuleFP.setBlRule("Retrieve the " + metadataProperty.getName() + " value");
			metadataExtractionRuleFP.setSource("File Path");
			metadataExtractionRuleFP.setPriority(1);
			metadataExtractionRuleFP.setNew(true);
			defaultMetadataExtractionRules.getRules().add(metadataExtractionRuleFP);
			MetadataExtractionRule metadataExtractionRuleCT = new MetadataExtractionRule();
			metadataExtractionRuleCT.setBlRule("Retrieve the " + metadataProperty.getName() + " value");
			metadataExtractionRuleCT.setSource("Content");
			metadataExtractionRuleCT.setPriority(2);
			metadataExtractionRuleCT.setNew(true);
			defaultMetadataExtractionRules.getRules().add(metadataExtractionRuleCT);
			
			DataManager.addMetadataExtractionRule(defaultMetadataExtractionRules.getRules(), commencePathId, metadataProperty.getId(), true);
			
			return defaultMetadataExtractionRules;
		}
		return null;
	}
	
	public static ArrayList<MetadataProperty> extractMetadata(ArrayList<IdentifiedDocInstance> identifiedDocInstances, Document document) {		
		ArrayList<MetadataExtractionRules> metadataExtractionRulesList = getMetadataExtractionRules(document);
		extractMetadata(identifiedDocInstances, document, metadataExtractionRulesList);
		
		ArrayList<MetadataProperty> extractedMetadataProperties = new ArrayList<MetadataProperty>();
		for (MetadataExtractionRules metadataExtractionRules : metadataExtractionRulesList) {
			if (extractedMetadataProperties.contains(metadataExtractionRules.getMetadataProperty()))
				continue;
			extractedMetadataProperties.add(metadataExtractionRules.getMetadataProperty());
		}
		
		return extractedMetadataProperties;
	}
	
	public static void extractMetadata(ArrayList<IdentifiedDocInstance> identifiedDocInstances, Document document, ArrayList<MetadataExtractionRules> metadataExtractionRulesList) {
		for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
			for (MetadataExtractionRules metadataExtractionRules : metadataExtractionRulesList) {
				if (identifiedDocInstance.getCommencePath() != null) {
					if (identifiedDocInstance.getCommencePath().getId() == metadataExtractionRules.getCommencePathId()) {
						DataManager.removeMetadataValues(document.getId(), metadataExtractionRules.getMetadataProperty().getId());
						identifiedDocInstance.getMetadataValues().add(extractMetadata(identifiedDocInstance, metadataExtractionRules));
					}
				}
			}
		}
		DataManager.addMetadataValues(identifiedDocInstances, document.getId());
		
	}
	
	
	public static MetadataValue extractMetadata(IdentifiedDocInstance identifiedDocInstance, MetadataExtractionRules metadataExtractionRules) {

		//System.out.println("DOC-" + identifiedDocInstance.getDocument().getId() + ":   Start extracting metadata for " + identifiedDocInstance.getName());
		
		MetadataValue metadataValue = new MetadataValue();
		metadataValue.setValue("");
		StringBuilder content = null;
				
		metadataExtractionRuleloop:
		for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules.getRules()) {
			StringBuilder valueBase = new StringBuilder();
			String searchSequence = "FIRST";
			if (metadataExtractionRule.getSource().equals("File Path"))  {
				valueBase.append(identifiedDocInstance.getVolumePath() + "/" + identifiedDocInstance.getNameWithoutExtension());
				searchSequence = "LAST";
			}
			else if (metadataExtractionRule.getSource().equals("Content")) {
				if (content == null) {
					valueBase.append(identifiedDocInstance.getContent());
					content = valueBase;
				}
				else
					valueBase = content;
			}
			else if (metadataExtractionRule.getSource().equals("Default")) {
				metadataValue.setValue(metadataExtractionRule.getDefaultValue());
				metadataValue.setMetadataExtractionRule(metadataExtractionRule);
				break;
			}
			
			if (valueBase.toString().equals(""))
				continue metadataExtractionRuleloop;
			
			
			if (metadataExtractionRules.isDefault()) {
				
				//System.out.println("DOC-" + identifiedDocInstance.getDocument().getId() + ":   Start looking up value from " + metadataExtractionRule.getSource() + " for " + identifiedDocInstance.getName());
				
				lookupLoop:
				for (Lookup lookup : metadataExtractionRules.getLookups()) {
					
					//System.out.println("DOC-" + identifiedDocInstance.getDocument().getId() + ":     Looking up " + lookup.getLookupValue() + " for " + identifiedDocInstance.getName());
										
					if (metadataExtractionRules.getMetadataProperty().getName().equals("Date") ||
						metadataExtractionRules.getMetadataProperty().getName().equals("Month")||
						metadataExtractionRules.getMetadataProperty().getName().equals("Year")) {
						
						SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd");
						
						if (metadataExtractionRules.getMetadataProperty().getName().equals("Month"))
							outputSdf = new SimpleDateFormat("MMMMM");
						else if (metadataExtractionRules.getMetadataProperty().getName().equals("Year"))
							outputSdf = new SimpleDateFormat("yyyy");
						
						SimpleDateFormat inputSdf = new SimpleDateFormat(lookup.getReturnedValue());
						String dateFound = Util.findRegex(valueBase.toString(), lookup.getLookupValue(), searchSequence);
						try {
							if (!dateFound.equals("")) {
								Date dateParsed = inputSdf.parse(dateFound);
								//TODO: from DB
								Date upperLimit = null;
								Date lowerLimit = null;
								if ((metadataExtractionRules.getMetadataProperty().getName().equals("Year"))) {
									upperLimit = outputSdf.parse("2020");
									lowerLimit = outputSdf.parse("1940");							
								}
								else if ((metadataExtractionRules.getMetadataProperty().getName().equals("Date"))) {
									upperLimit = outputSdf.parse("2020-12-31");
									lowerLimit = outputSdf.parse("1940-01-01");
								}
								
								if (metadataExtractionRules.getMetadataProperty().getName().equals("Month") || dateParsed.before(upperLimit) && dateParsed.after(lowerLimit)) {
									metadataValue.setValue(outputSdf.format(dateParsed));									
								}
							}
						} catch (ParseException e) {
							continue lookupLoop;
						}
					}
					else {
						metadataValue.setValue(Util.findRegex(valueBase.toString(), lookup, searchSequence));						
					}
					
					if (!metadataValue.getValue().equals("")) {
						//System.out.println("DOC-" + identifiedDocInstance.getDocument().getId() + ":   Value is set for " + identifiedDocInstance.getName() + " : " + metadataValue.getValue());
						metadataValue.setMetadataExtractionRule(metadataExtractionRule);
						break metadataExtractionRuleloop;
					}	
				}
			}
			else {
				metadataValue.setValue(Util.findRegex(valueBase.toString(), metadataExtractionRule.getRegex(), metadataExtractionRule.getCapGroup(), searchSequence));
			}
			
			if (!metadataValue.getValue().equals("")) {
				metadataValue.setMetadataExtractionRule(metadataExtractionRule);
				break metadataExtractionRuleloop;
			}	
		}
		return metadataValue;
	}
	
	public static IdentifiedDocInstances extractMetadata(IdentifiedDocInstances identifiedDocInstances, MetadataExtractionRules metadataExtractionRules) {
		ArrayList<MetadataExtractionRules> metadataExtractionRulesList = new ArrayList<MetadataExtractionRules>();
		metadataExtractionRulesList.add(metadataExtractionRules);
		return extractMetadata(identifiedDocInstances, metadataExtractionRulesList);
	}

	public static IdentifiedDocInstances extractMetadata(IdentifiedDocInstances identifiedDocInstances, ArrayList<MetadataExtractionRules> metadataExtractionRulesList) {
		int totalCount = 0;
		int filePathCount = 0;
		int contentCount = 0;
		int defaultCount = 0;
		
		int count = 1;
		boolean commencePathMatch = false;
		for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				
			identifiedDocInstance.getMetadataValues().clear();
			for (MetadataExtractionRules metadataExtractionRules : metadataExtractionRulesList) {
				if (identifiedDocInstance.getCommencePath().getId() == metadataExtractionRules.getCommencePathId()) {					
					if (count % 50 == 0)
						System.out.println("DOC-" + identifiedDocInstance.getDocument().getId() + ": Extracting metadata (" + metadataExtractionRules.getMetadataProperty().getName() + ") " + count + "/" + identifiedDocInstances.size());
											
					MetadataValue metadataValue = extractMetadata(identifiedDocInstance,metadataExtractionRules);
					identifiedDocInstance.getMetadataValues().add(metadataValue);					
					if (identifiedDocInstance.getSnapshotDeleted() == 0) {
						totalCount++;	
						if (!metadataValue.getValue().equals("") && metadataValue.getMetadataExtractionRule().getSource().equals("File Path"))
							filePathCount++;
						else if (!metadataValue.getValue().equals("") && metadataValue.getMetadataExtractionRule().getSource().equals("Content"))
							contentCount++;
						else if (!metadataValue.getValue().equals("") && metadataValue.getMetadataExtractionRule().getSource().equals("Default"))
							defaultCount++;
					}	
					commencePathMatch = true;
					break;
				}
			}		
			if (!commencePathMatch) {
				System.out.println("DOC-" + identifiedDocInstance.getDocument().getId() + ": No commence path matching metadata extraction rule for " + identifiedDocInstance.getFullyQualifiedPath());
				
			}
			count++;
			System.gc();
		}
		
		if (filePathCount + contentCount < totalCount * 0.8f)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Success Rate for File Path: " + Math.round(filePathCount * 100.0f / totalCount) + "%, Content:" + Math.round(contentCount * 100.0f / totalCount) + "%, Default: " + Math.round(defaultCount * 100.0f / totalCount) + "%"));
		else
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Success Rate for File Path: " + Math.round(filePathCount * 100.0f / totalCount) + "%, Content:" + Math.round(contentCount * 100.0f / totalCount) + "%, Default: " + Math.round(defaultCount * 100.0f / totalCount) + "%"));
		
		return identifiedDocInstances;
		
	}
		

}
