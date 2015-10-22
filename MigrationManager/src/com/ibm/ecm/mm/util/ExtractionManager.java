package com.ibm.ecm.mm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
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
				DataTableArrayList<MetadataExtractionRule> existingMetadataExtractionRuleList = DataManager.getMetadataExtractionRules(commencePath.getId(), metadataProperty.getId());
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
			metadataExtractionRuleFP.setBlRule("Look up " + metadataProperty.getName());
			metadataExtractionRuleFP.setSource("File Path");
			metadataExtractionRuleFP.setPriority(1);
			metadataExtractionRuleFP.setNew(true);
			defaultMetadataExtractionRules.getRules().add(metadataExtractionRuleFP);
			MetadataExtractionRule metadataExtractionRuleCT = new MetadataExtractionRule();
			metadataExtractionRuleCT.setBlRule("Look up " + metadataProperty.getName());
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
		MetadataValue metadataValue = new MetadataValue();
		metadataValue.setValue("");
		StringBuilder content = new StringBuilder();
				
		metadataExtractionRuleloop:
		for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules.getRules()) {
			StringBuilder valueBase = new StringBuilder();
			String searchSequence = "FIRST";
			if (metadataExtractionRule.getSource().equals("File Path"))  {
				valueBase.append(identifiedDocInstance.getFullPath());
				searchSequence = "LAST";
			}
			else if (metadataExtractionRule.getSource().equals("Content")) {
				if (content.equals("")) {
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
				lookupLoop:
				for (Lookup lookup : metadataExtractionRules.getLookups()) {
					if (metadataExtractionRules.getMetadataProperty().getName().equals("Date") ||
						metadataExtractionRules.getMetadataProperty().getName().equals("Month")||
						metadataExtractionRules.getMetadataProperty().getName().equals("Year")) {
						SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat inputSdf = new SimpleDateFormat(lookup.getReturnedValue());
						try {
							String dateFound = Util.findRegex(valueBase.toString(), lookup.getLookupValue(), searchSequence);
							if (dateFound != "") {
								Date dateParsed = inputSdf.parse(dateFound);
								//TODO: from DB
								Date upperLimit = outputSdf.parse("2020-12-31");
								Date lowerLimit = outputSdf.parse("1940-01-01");
								if (dateParsed.before(upperLimit) && dateParsed.after(lowerLimit))
									metadataValue.setValue(outputSdf.format(dateParsed));
							}
						} catch (ParseException e) {
							continue lookupLoop;
						}
					}
					else
						metadataValue.setValue(Util.findRegex(valueBase.toString(), lookup, searchSequence));
				}
			}
			else
				metadataValue.setValue(Util.findRegex(valueBase.toString(), metadataExtractionRule.getRegex(), metadataExtractionRule.getCapGroup(), searchSequence));
				
			if (!metadataValue.getValue().equals("")) {
				metadataValue.setMetadataExtractionRule(metadataExtractionRule);
				break metadataExtractionRuleloop;
			}	
		}
		
		return metadataValue;
	}
	

	public static ArrayList<IdentifiedDocInstance> extractMetadata(ArrayList<IdentifiedDocInstance> identifiedDocInstances, MetadataExtractionRules metadataExtractionRules) {
		for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {	
			identifiedDocInstance.getMetadataValues().clear();
			identifiedDocInstance.getMetadataValues().add(extractMetadata(identifiedDocInstance,metadataExtractionRules));		
		}	
		return identifiedDocInstances;
		
	}
		

}
