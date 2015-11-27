package com.ibm.ecm.mm.bean;

import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
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

	private String mode;
	private ArrayList<Document> documents;
	private ArrayList<Document> filteredDocuments;
	private ArrayList<Document> selectedDocuments;
	private Document selectedDocument;
	private CommencePath commencePath;
	private MetadataProperty metadataProperty;
	private MetadataExtractionRules metadataExtractionRules;
	private ArrayList<MetadataExtractionRules> multipliedMetadataExtractionRules;
	private boolean useDefaultRule;
	private IdentifiedDocInstances identifiedDocInstances;
	private String status;
	private boolean isPreview;
	private boolean onlyNew;
	
	public ExtractionBean() {
		setDocuments(DataManager.getDocuments());
		setIdentifiedDocInstances(new IdentifiedDocInstances());
		setUseDefaultRule(false);
		setFilteredDocuments(new ArrayList<Document>());
		setPreview(false);
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}
	
	public ArrayList<Document> getFilteredDocuments() {
		return filteredDocuments;
	}

	public void setFilteredDocuments(ArrayList<Document> filteredDocuments) {
		this.filteredDocuments = filteredDocuments;
	}

	public ArrayList<Document> getSelectedDocuments() {
		return selectedDocuments;
	}

	public void setSelectedDocuments(ArrayList<Document> selectedDocuments) {
		this.selectedDocuments = selectedDocuments;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}
	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
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

	public boolean isOnlyNew() {
		return onlyNew;
	}

	public void setOnlyNew(boolean onlyNew) {
		this.onlyNew = onlyNew;
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
	
	public boolean isDocumentSelected() {
		return getSelectedDocument() != null;
	}
	
	
	public void documentSelected() {
		//getSelectedDocument().setCommencePaths(new DataTableArrayList<CommencePath>(CommencePath.class));
		//CommencePath commencePath = new CommencePath();
		//commencePath.setId(0);
		//getSelectedDocument().getCommencePaths().add(commencePath);
		//getSelectedDocument().getCommencePaths().addAll(DataManager.getCommencePaths(getSelectedDocument().getId()));
		getSelectedDocument().setCommencePaths(DataManager.getCommencePaths(getSelectedDocument().getId()));
		setCommencePath(null);
		setMetadataProperty(null);
		setMetadataExtractionRules(null);
		getSelectedDocument().getMetadataProperties().clear();
		for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(getSelectedDocument().getId()))			
			getSelectedDocument().getMetadataProperties().add(metadataProperty);
		setPreview(false);
	}
	
	public void commencePathSelected() {
		setPreview(false);
		if (getMetadataProperty() != null)
			optionSet();			
	}

	
	public void metadataPropertySeletced() {
		setPreview(false);
		if (getCommencePath() != null)
			optionSet();		
	}

	public boolean isOptionSet() {
		return getMetadataProperty() != null && getCommencePath() != null;
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
			getMetadataExtractionRules().setRules(DataManager.getMetadataExtractionRules(getSelectedDocument().getId(),getCommencePath().getId(),getMetadataProperty().getId()));
			
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
	
	public boolean isPreview() {
		return isPreview;
	}
	
	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}
	
	public boolean preview() {
		try {
			/*if (getCommencePath().getId() == 0) {
				setMultipliedMetadataExtractionRules(new ArrayList<MetadataExtractionRules>());
				for (CommencePath commencePath : getSelectedDocument().getCommencePaths()) {
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
				setIdentifiedDocInstances(ExtractionManager.extractMetadata(DataManager.getIdentifiedDocInstances(getSelectedDocument(), getCommencePath()), getMultipliedMetadataExtractionRules()));
			}
			else {*/
				setIdentifiedDocInstances(ExtractionManager.extractMetadata(DataManager.getIdentifiedDocInstances(getSelectedDocument(), getCommencePath(), isOnlyNew()), getMetadataExtractionRules()));
			//}
			setPreview(true);
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
	
	public void runExtraction(Document document, CommencePath commencePath, MetadataExtractionRules metadataExtractionRules) {
		
		
		setIdentifiedDocInstances(ExtractionManager.extractMetadata(DataManager.getIdentifiedDocInstances(document, commencePath, isOnlyNew()), metadataExtractionRules));
		
		//if (preview()) {		
			/*
			 * Update Metadata_Value
			 */	
			System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ": Step 3 of 3 Saving metadata");
			
			DataManager.removeMetadataValues(getIdentifiedDocInstances(),document.getId());
			
			DataManager.addMetadataValues(getIdentifiedDocInstances(),document.getId());			
			
		//};
		
		
	}
	
	public void run() {

		System.out.println(Util.getTimeStamp() + "DOC-" + getSelectedDocument().getId() + ": Run Metadata Extraction Started.");
	
		runExtraction(getSelectedDocument(), getCommencePath(), getMetadataExtractionRules());

		setPreview(true);
		
		System.out.println(Util.getTimeStamp() + "DOC-" + getSelectedDocument().getId() + ": Run Metadata Extraction Completed.");

		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Successful",  "Metadata Extraction Rules and Metadata Values are saved. ") );
		

	
	}
	
	public void runBatch() {
		String successDoc = "";
		Severity severity = null;
		String summary = null;
		String message = "";
		try {

			System.out.println(Util.getTimeStamp() + "Run Batch Metadata Extraction Started.");
		
			int docCount = 0;
			
			for (Document document : getSelectedDocuments()) {
				docCount++;
				System.out.println(Util.getTimeStamp() + docCount + "/" + getSelectedDocuments().size() + " DOC-" + document.getId() + ": Running Metadata Extraction");
			
				document.setCommencePaths(DataManager.getCommencePaths(document.getId()));
				for (CommencePath commencePath : document.getCommencePaths()) {
					document.setMetadataProperties(DataManager.getMetadataPropreties(document.getId()));
					for (MetadataProperty metadataProperty : document.getMetadataProperties()) {
						MetadataExtractionRules metadataExtractionRules = new MetadataExtractionRules();
						metadataExtractionRules.setMetadataProperty(metadataProperty);
						metadataExtractionRules.setCommencePathId(commencePath.getId());
						metadataExtractionRules.setRules(DataManager.getMetadataExtractionRules(document.getId(),commencePath.getId(),metadataProperty.getId()));
	
						metadataExtractionRules.setLookups(DataManager.getLookups(metadataExtractionRules.getMetadataProperty().getId()));
						metadataExtractionRules.setHasDefaultRules(metadataExtractionRules.getLookups().size() > 0);
	
						//set isDefault
						metadataExtractionRules.setDefault(false);
						for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules.getRules()) {
							if (!metadataExtractionRule.isDefault()) {
								break;
							}
							metadataExtractionRules.setDefault(true);
						}
	
						runExtraction(document,commencePath,metadataExtractionRules);
	
					}
				}
				successDoc += document.getId() + ",";
			}
			System.out.println(Util.getTimeStamp() + "Run Batch Metadata Extraction Completed.");
			successDoc = successDoc.substring(0,successDoc.length()-1);
			severity = FacesMessage.SEVERITY_INFO;
			summary = "Successful";
			message = "The following documents are extracted with metadata: " + successDoc;
		}
		catch (Exception e) {
			severity = FacesMessage.SEVERITY_ERROR;
			summary = e.getClass().getName();
			if (e.getMessage() != null)
				message += e.getMessage() + ".";
			if (successDoc.equals(""))
				message += "No metadata is extracted.";
			else	
				message += "Only the following documents are extracted with metadata: " + successDoc;
			e.printStackTrace();
		}
		finally {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(severity, summary, message));
		}
	}
	
}