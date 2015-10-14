package com.ibm.ecm.mm.bean;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataExtractionRules;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.ExtractionManager;

public class MetadataExtraction {
	
	private ArrayList<Document> documents;
	private Document document;
	private CommencePath commencePath;
	private MetadataExtractionRules metadataExtractionRules;
	private boolean useDefaultRule;
	private ArrayList<IdentifiedDocInstance> identifiedDocInstances;
	
	public MetadataExtraction() {
		setDocuments(DataManager.getDocuments());
		identifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
		useDefaultRule = false;
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}
	
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public CommencePath getCommencePath() {
		return commencePath;
	}
	public void setCommencePath(CommencePath commencePath) {
		this.commencePath = commencePath;
	}

	public boolean isUseDefaultRule() {
		return useDefaultRule;
	}

	public void setUseDefaultRule(boolean useDefaultRule) {
		this.useDefaultRule = useDefaultRule;
	}

	public ArrayList<IdentifiedDocInstance> getIdentifiedDocInstances() {
		return identifiedDocInstances;
	}

	public void setIdentifiedDocInstances(ArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		this.identifiedDocInstances = identifiedDocInstances;
	}
	
	public MetadataExtractionRules getMetadataExtractionRules() {
		return this.metadataExtractionRules;
	}

	public void setMetadataExtractionRules(MetadataExtractionRules metadataExtractionRules) {
		this.metadataExtractionRules = metadataExtractionRules;
	}	

	public int getMetadataExtractionRulesLastIndex() {
		return metadataExtractionRules.getRules().size()-1;
	}
	
	public boolean getRuleEditable() {
		return (!isUseDefaultRule()) && (getMetadataExtractionRules() != null);
	}
	
	public ArrayList<String> getSources() {
		return DataManager.getSources();
	}
	
	public void documentSelected() {
		if (getDocument().getCommencePaths().size() == 0)
			getDocument().setCommencePaths(DataManager.getCommencePaths(getDocument().getId()));
		setCommencePath(null);
		setMetadataExtractionRules(null);
	}
	
	public void commencePathSelected() {
		if (getCommencePath().getMetadataExtractionRulesList().size() == 0)
			for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(getDocument().getId())) {
				MetadataExtractionRules metadataExtractionRules = new MetadataExtractionRules();
				metadataExtractionRules.setMetadataProperty(metadataProperty);
				getCommencePath().getMetadataExtractionRulesList().add(metadataExtractionRules);
			}
		setMetadataExtractionRules(null);
	}

	
	public void metadataPropertySeletced() {
		if (getMetadataExtractionRules().getRules().size() == 0)
			getMetadataExtractionRules().setRules(DataManager.getMetadataExtractionRules(getCommencePath().getId(),getMetadataExtractionRules().getMetadataProperty().getId()));
		
		if (getMetadataExtractionRules().getRules().size() == 0)
			getMetadataExtractionRules().setDefault(false);
		else
			getMetadataExtractionRules().setDefault(getMetadataExtractionRules().getRules().get(0).isDefault());
		
		getMetadataExtractionRules().setLookups(DataManager.getLookups(getMetadataExtractionRules().getMetadataProperty().getId()));
				
		getMetadataExtractionRules().setHasDefaultRules(getMetadataExtractionRules().getLookups().size() > 0);
		
		setUseDefaultRule(getMetadataExtractionRules().isDefault());
		
		if (isUseDefaultRule()) {
			getMetadataExtractionRules().setDefaultRules(getMetadataExtractionRules().getRules());
			getMetadataExtractionRules().setCustomRules(new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class));
		}
		else {
			getMetadataExtractionRules().setDefaultRules(ExtractionManager.createDefaultMetadataExtractionRules(getMetadataExtractionRules().getMetadataProperty(), getCommencePath().getId()).getRules());		
			getMetadataExtractionRules().setDefault(true);
			getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules());
		}
		
	}
	
	public void toggleRules() {
		if (isUseDefaultRule()) {
			getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules());
			getMetadataExtractionRules().setRules(getMetadataExtractionRules().getDefaultRules());
			getMetadataExtractionRules().setDefault(true);
		}
		else {
			getMetadataExtractionRules().setRules(getMetadataExtractionRules().getCustomRules());
			getMetadataExtractionRules().setDefault(false);
		}
		
	}
	
	public void preview() {
		
		//int filePathCount = 0;
		//int contentCount = 0;
		//int defaultCount = 0;		
		
		setIdentifiedDocInstances(ExtractionManager.extractMetadata(DataManager.getIdentifiedDocInstances(getDocument(), getCommencePath()), getMetadataExtractionRules()));

		//if (filePathCount + contentCount < getIdentifiedDocInstances().size() * 0.8f)
		//	FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Success Rate for File Path: " + Math.round(filePathCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Content:" + Math.round(contentCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Default: " +  + Math.round(defaultCount * 100.0f / getIdentifiedDocInstances().size()) + "%"));
		//else
		//	FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Success Rate for File Path: " + Math.round(filePathCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Content:" + Math.round(contentCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Default: " +  + Math.round(defaultCount * 100.0f / getIdentifiedDocInstances().size()) + "%"));
		
	}
	
	public void runAndSave() {
		preview();
		
		if (useDefaultRule) {
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getCustomRules()) {
				getMetadataExtractionRules().getRules().getRemovedList().add(metadataExtractionRule);
				metadataExtractionRule.setNew(true);
			}
		}
		else {
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getDefaultRules()) {
				getMetadataExtractionRules().getRules().getRemovedList().add(metadataExtractionRule);
				metadataExtractionRule.setNew(true);
			}
		}		

			
		DataManager.removeMetadataExtractionRule(getMetadataExtractionRules().getRules().getRemovedList());
		getMetadataExtractionRules().getRules().getRemovedList().clear();
		
		ArrayList<MetadataExtractionRule> insertList = new ArrayList<MetadataExtractionRule>();
		ArrayList<MetadataExtractionRule> updateList = new ArrayList<MetadataExtractionRule>();
		
		
		
		for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules()) {
			if (metadataExtractionRule.isNew())
				insertList.add(metadataExtractionRule);
			else
				updateList.add(metadataExtractionRule);
			metadataExtractionRule.setNew(false);
		}
		
		DataManager.updateMetadataExtractionRule(updateList);

		DataManager.addMetadataExtractionRule(insertList, getCommencePath().getId(), getMetadataExtractionRules().getMetadataProperty().getId(), isUseDefaultRule());
								
		/*
		 * Update Metadata_Value
		 */
		
		DataManager.removeMetadataValues(getDocument().getId(),getCommencePath().getId(),getMetadataExtractionRules().getMetadataProperty().getId());
		
		DataManager.addMetadataValues(getIdentifiedDocInstances(),getDocument().getId());			
		
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Successful",  "Metadata Extraction Rules and Metadata Values are saved. ") );

	}
	
}