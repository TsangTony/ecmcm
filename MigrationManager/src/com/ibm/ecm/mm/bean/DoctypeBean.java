package com.ibm.ecm.mm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.model.Team;
import com.ibm.ecm.mm.util.DataManager;

public class DoctypeBean {
	private ArrayList<Document> documentTypes;
	private Document selectedDocumentType;
	private DualListModel<MetadataProperty> metadataProperties;
	private ArrayList<Team> teams;
	private ArrayList<HashMap<String, String>> logs;
	
	public DoctypeBean() {
		setDocumentTypes(DataManager.getDocuments());
		setTeams(DataManager.getTeams());
        setMetadataProperties(new DualListModel<MetadataProperty>());
        setLogs(new ArrayList<HashMap<String, String>>());
	}

	public ArrayList<Document> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(ArrayList<Document> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public Document getSelectedDocumentType() {
		return selectedDocumentType;
	}

	public void setSelectedDocumentType(Document selectedDocumentType) {
		this.selectedDocumentType = selectedDocumentType;
	}
	
	public DualListModel<MetadataProperty> getMetadataProperties() {
		return metadataProperties;
	}

	public void setMetadataProperties(DualListModel<MetadataProperty> metadataProperties) {
		this.metadataProperties = metadataProperties;
	}

	public ArrayList<Team> getTeams() {
		return teams;
	}

	public void setTeams(ArrayList<Team> teams) {
		this.teams = teams;
	}

	public ArrayList<HashMap<String, String>> getLogs() {
		return logs;
	}

	public void setLogs(ArrayList<HashMap<String, String>> logs) {
		this.logs = logs;
	}

	public boolean isDocumentTypeSelected() {
		return selectedDocumentType != null;
	}
	
	public void onDocumentTypeSelected() {
		List<MetadataProperty> metadataPropertiesSource = new ArrayList<MetadataProperty>();
        List<MetadataProperty> metadataPropertiesTarget = new ArrayList<MetadataProperty>();
        
        getSelectedDocumentType().setMetadataProperties(DataManager.getMetadataPropreties(getSelectedDocumentType().getId()));
        
        allMetadataPropertiesLoop:
        for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(0)) {
	        for (MetadataProperty docTypeMetadataProperty : getSelectedDocumentType().getMetadataProperties()) {
	        	if (metadataProperty.getId() == docTypeMetadataProperty.getId()) {
	        		continue allMetadataPropertiesLoop;
	        	}	        	
	        }
	        metadataPropertiesSource.add(metadataProperty);
        }
        
        metadataPropertiesTarget.addAll(getSelectedDocumentType().getMetadataProperties());        
                
        getMetadataProperties().setSource(metadataPropertiesSource);
        getMetadataProperties().setTarget(metadataPropertiesTarget);
        
        for (Team team : getTeams()) {
        	if (getSelectedDocumentType().getTeam().getId() == team.getId()) {
        		getSelectedDocumentType().setTeam(team);
        		break;
        	}
        }
	}
	
	public void onValueChanged(ValueChangeEvent event) {
	    System.out.println(event.getComponent().getId() + " New: "+ event.getNewValue()+", Old: "+ event.getOldValue());
	}
	
    public void onPickMetadataProperty(TransferEvent event) {
    	try {
    		
	        for (Object item : event.getItems()) {
	        	System.out.println(event.getComponent().getId() + ((MetadataProperty) item).getName());
	        }    
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
	public void save() {
		
	}
}
