package com.ibm.ecm.mm.bean;

import java.util.ArrayList;
import java.util.List;

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
	private String comment;

	private String originalDocumentTypeName;
	private Team originalTeam;
	private ArrayList<MetadataProperty> originalMetadataProperties;
	
	public DoctypeBean() {
		setDocumentTypes(DataManager.getDocuments());
		setTeams(DataManager.getTeams());
        setMetadataProperties(new DualListModel<MetadataProperty>());
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOriginalDocumentTypeName() {
		return originalDocumentTypeName;
	}

	public void setOriginalDocumentTypeName(String originalDocumentTypeName) {
		this.originalDocumentTypeName = originalDocumentTypeName;
	}

	public Team getOriginalTeam() {
		return originalTeam;
	}

	public void setOriginalTeam(Team originalTeam) {
		this.originalTeam = originalTeam;
	}

	public ArrayList<MetadataProperty> getOriginalMetadataProperties() {
		return originalMetadataProperties;
	}

	public void setOriginalMetadataProperties(ArrayList<MetadataProperty> originalMetadataProperties) {
		this.originalMetadataProperties = originalMetadataProperties;
	}

	public boolean isDocumentTypeSelected() {
		return selectedDocumentType != null;
	}
	
	public void onDocumentTypeSelected() {
		setOriginalDocumentTypeName(getSelectedDocumentType().getName());
		setOriginalTeam(getSelectedDocumentType().getTeam());
		
		List<MetadataProperty> metadataPropertiesSource = new ArrayList<MetadataProperty>();
        List<MetadataProperty> metadataPropertiesTarget = new ArrayList<MetadataProperty>();
        
        getSelectedDocumentType().setMetadataProperties(DataManager.getMetadataPropreties(getSelectedDocumentType().getId()));

        setOriginalMetadataProperties(new ArrayList<MetadataProperty>());
        
        allMetadataPropertiesLoop:
        for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(0)) {
	        for (MetadataProperty docTypeMetadataProperty : getSelectedDocumentType().getMetadataProperties()) {
	        	if (metadataProperty.getId() == docTypeMetadataProperty.getId()) {
	        		metadataPropertiesTarget.add(metadataProperty);
	        		getOriginalMetadataProperties().add(metadataProperty);
	        		continue allMetadataPropertiesLoop;
	        	}	        	
	        }
	        metadataPropertiesSource.add(metadataProperty);
        }        
                
        getMetadataProperties().setSource(metadataPropertiesSource);
        getMetadataProperties().setTarget(metadataPropertiesTarget);
        
        for (Team team : getTeams()) {
        	if (getSelectedDocumentType().getTeam().getId() == team.getId()) {
        		getSelectedDocumentType().setTeam(team);
        		break;
        	}
        }
	}	
	
	public void save() {
		try {
		ArrayList<String> logs = new ArrayList<String>();
		
		System.out.println(getSelectedDocumentType());
		
		if (!getSelectedDocumentType().getName().equals(getOriginalDocumentTypeName())) {
			//DoctypeManager.updateDocumentTypeName(getSelectedDocumentType());
			logs.add("Name changed from '" + getOriginalDocumentTypeName() + "' to '" + getSelectedDocumentType().getName() + "'");
		}
		
		if (!getSelectedDocumentType().getTeam().getName().equals(getOriginalTeam().getName())) {
			//DoctypeManager.updateDocumentTeam(getSelectedDocumentType());
			logs.add("Team changed from '" + getOriginalTeam().getName() + "' to '" + getSelectedDocumentType().getTeam().getName() + "'");
		}
		
		ArrayList<MetadataProperty> addedMetadataProperties = new ArrayList<MetadataProperty>();
		ArrayList<MetadataProperty> removedMetadataProperties = new ArrayList<MetadataProperty>();
		
		addedMetadataProperties.addAll(getMetadataProperties().getTarget());
		addedMetadataProperties.removeAll(getOriginalMetadataProperties());
		
		removedMetadataProperties.addAll(getOriginalMetadataProperties());
		removedMetadataProperties.removeAll(getMetadataProperties().getTarget());		


		System.out.println("OriginalMetadataProperties");
		for (MetadataProperty metadataProperty : getOriginalMetadataProperties())
			System.out.println("-"+metadataProperty.getName());

		System.out.println("Target");
		for (MetadataProperty metadataProperty : getMetadataProperties().getTarget())
			System.out.println("-"+metadataProperty.getName());
		
		System.out.println("addedMetadataProperties");
		for (MetadataProperty metadataProperty : addedMetadataProperties)
			System.out.println("-"+metadataProperty.getName());

		System.out.println("removedMetadataProperties");
		for (MetadataProperty metadataProperty : removedMetadataProperties)
			System.out.println("-"+metadataProperty.getName());

		System.out.println("logs");
		for (String log : logs)
			System.out.println(log);
		//DoctypeManager.addDocumentLogs(getSelectedDocumentType().getId(),logs,getComment());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
