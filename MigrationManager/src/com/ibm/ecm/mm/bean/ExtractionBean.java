package com.ibm.ecm.mm.bean;

import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataExtractionRules;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.ExtractionManager;
import com.ibm.ecm.mm.util.Util;

public class ExtractionBean {
	
	private ArrayList<Document> documents;
	private Document document;
	private CommencePath commencePath;
	private MetadataProperty metadataProperty;
	private MetadataExtractionRules metadataExtractionRules;
	private ArrayList<MetadataExtractionRules> multipliedMetadataExtractionRules;
	private boolean useDefaultRule;
	private IdentifiedDocInstances identifiedDocInstances;
	private String status;
	
	public ExtractionBean() {
		setDocuments(DataManager.getDocuments());
		setIdentifiedDocInstances(new IdentifiedDocInstances());
		setUseDefaultRule(false);
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

	public MetadataProperty getMetadataProperty() {
		return metadataProperty;
	}

	public void setMetadataProperty(MetadataProperty metadataProperty) {
		this.metadataProperty = metadataProperty;
	}

	public boolean isUseDefaultRule() {
		return useDefaultRule;
	}

	public void setUseDefaultRule(boolean useDefaultRule) {
		this.useDefaultRule = useDefaultRule;
	}

	public IdentifiedDocInstances getIdentifiedDocInstances() {
		return identifiedDocInstances;
	}
	
	public ArrayList<IdentifiedDocInstance> getLatestSnapshotInstances() {
		return getIdentifiedDocInstances().getLatestSnapshotInstances();
	}

	public void setIdentifiedDocInstances(IdentifiedDocInstances identifiedDocInstances) {
		this.identifiedDocInstances = identifiedDocInstances;
	}
	
	public MetadataExtractionRules getMetadataExtractionRules() {
		return this.metadataExtractionRules;
	}

	public void setMetadataExtractionRules(MetadataExtractionRules metadataExtractionRules) {
		this.metadataExtractionRules = metadataExtractionRules;
	}	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<MetadataExtractionRules> getMultipliedMetadataExtractionRules() {
		return multipliedMetadataExtractionRules;
	}

	public void setMultipliedMetadataExtractionRules(ArrayList<MetadataExtractionRules> multipliedMetadataExtractionRules) {
		this.multipliedMetadataExtractionRules = multipliedMetadataExtractionRules;
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
		getDocument().setCommencePaths(new DataTableArrayList<CommencePath>(CommencePath.class));
		CommencePath commencePath = new CommencePath();
		commencePath.setId(0);
		getDocument().getCommencePaths().add(commencePath);
		getDocument().getCommencePaths().addAll(DataManager.getCommencePaths(getDocument().getId()));
		setCommencePath(null);
		setMetadataProperty(null);
		setMetadataExtractionRules(null);
		getDocument().getMetadataProperties().clear();
		for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(getDocument().getId()))			
			getDocument().getMetadataProperties().add(metadataProperty);
	}
	
	public void commencePathSelected() {
		if (getMetadataProperty() != null)
			optionSet();			
	}

	
	public void metadataPropertySeletced() {
		if (getCommencePath() != null)
			optionSet();		
	}
	
	public void optionSet() {
		try {
			
			/*
			 * TODO: if commence_path_id = 0
			 * 			if custom rule exists, show the first commence path's rule
			 * 			else, create new default rules
			 */
			setMetadataExtractionRules(new MetadataExtractionRules());
			getMetadataExtractionRules().setMetadataProperty(getMetadataProperty());
			getMetadataExtractionRules().setCommencePathId(getCommencePath().getId());
			getMetadataExtractionRules().setRules(DataManager.getMetadataExtractionRules(getDocument().getId(),getCommencePath().getId(),getMetadataProperty().getId()));
			
			getMetadataExtractionRules().setLookups(DataManager.getLookups(getMetadataExtractionRules().getMetadataProperty().getId()));
			getMetadataExtractionRules().setHasDefaultRules(getMetadataExtractionRules().getLookups().size() > 0);
						
			//set isDefault
			getMetadataExtractionRules().setDefault(false);
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules()) {
				if (!metadataExtractionRule.isDefault()) {
					break;
				}
				getMetadataExtractionRules().setDefault(true);
			}
			setUseDefaultRule(getMetadataExtractionRules().isDefault());			
			
			if (isUseDefaultRule()) {
				if (getCommencePath().getId() == 0)
					getMetadataExtractionRules().setRules(ExtractionManager.createDefaultMetadataExtractionRules(getMetadataExtractionRules().getMetadataProperty(), getCommencePath().getId()).getRules());				
				getMetadataExtractionRules().setCustomRules(new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class));
				getMetadataExtractionRules().setDefaultRules(getMetadataExtractionRules().getRules());
			}
			else {
				if (getCommencePath().getId() == 0) {					
					DataTableArrayList<MetadataExtractionRule> metadataExtractionRules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
					for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules())
						if (!metadataExtractionRule.isDefault())
							metadataExtractionRules.add(metadataExtractionRule);
					getMetadataExtractionRules().setRules(metadataExtractionRules);
				}
				if (getMetadataExtractionRules().getHasDefault())
					getMetadataExtractionRules().setDefaultRules(ExtractionManager.createDefaultMetadataExtractionRules(getMetadataExtractionRules().getMetadataProperty(), getCommencePath().getId()).getRules());		
				getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
	
	public boolean preview() {
		try {
			if (getCommencePath().getId() == 0) {
				setMultipliedMetadataExtractionRules(new ArrayList<MetadataExtractionRules>());
				for (CommencePath commencePath : getDocument().getCommencePaths()) {
					if (commencePath.getId() != 0) {
						MetadataExtractionRules metadataExtractionRules = new MetadataExtractionRules();
						metadataExtractionRules.setCommencePathId(commencePath.getId());
						metadataExtractionRules.setDefault(getMetadataExtractionRules().isDefault());
						metadataExtractionRules.setLookups(getMetadataExtractionRules().getLookups());
						metadataExtractionRules.setMetadataProperty(getMetadataExtractionRules().getMetadataProperty());
						metadataExtractionRules.setRules(getMetadataExtractionRules().getRules());
						getMultipliedMetadataExtractionRules().add(metadataExtractionRules);
					}
				}
				setIdentifiedDocInstances(ExtractionManager.extractMetadata(DataManager.getIdentifiedDocInstances(getDocument(), getCommencePath()), getMultipliedMetadataExtractionRules()));
			}
			else {
				setIdentifiedDocInstances(ExtractionManager.extractMetadata(DataManager.getIdentifiedDocInstances(getDocument(), getCommencePath()), getMetadataExtractionRules()));
			}
			return true;
		}
		catch (PatternSyntaxException e) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your regex is wrong."));
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void save() {
		if (getCommencePath().getId() == 0) {
			DataManager.removeMetadataExtractionRule(getMetadataExtractionRules().getRules().getRemovedList());
			for (MetadataExtractionRules metadataExtractionRules : getMultipliedMetadataExtractionRules()) {
				DataManager.addMetadataExtractionRule(metadataExtractionRules.getRules(), metadataExtractionRules.getCommencePathId(), getMetadataExtractionRules().getMetadataProperty().getId(), isUseDefaultRule());
			}
		}
		else {
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
		}
	}
	
	public void run() {
		
		System.out.println(Util.getTimeStamp() + "DOC-" + getDocument().getId() + ": Run Metadata Extraction Started.");
	
		if (preview()) {		
			/*
			 * Update Metadata_Value
			 */	
			System.out.println(Util.getTimeStamp() + "DOC-" + getDocument().getId() + ": Step 3 of 3 Saving metadata");
			
			DataManager.removeMetadataValues(getDocument().getId(),getCommencePath().getId(),getMetadataExtractionRules().getMetadataProperty().getId());
			
			DataManager.addMetadataValues(getIdentifiedDocInstances(),getDocument().getId());			
			
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Successful",  "Metadata Extraction Rules and Metadata Values are saved. ") );
		};
		
		System.out.println(Util.getTimeStamp() + "DOC-" + getDocument().getId() + ": Run Metadata Extraction Completed.");
		
	}
	
}