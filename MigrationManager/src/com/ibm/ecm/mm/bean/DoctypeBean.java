package com.ibm.ecm.mm.bean;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.DualListModel;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.util.DataManager;

public class DoctypeBean {
	private ArrayList<Document> documentTypes;
	private Document selectedDocumentType;
	private DualListModel<MetadataProperty> metadataProperties;
	
	public DoctypeBean() {
		setDocumentTypes(DataManager.getDocuments());
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

	public boolean isDocumentTypeSelected() {
		return selectedDocumentType != null;
	}
	
	public void onDocumentTypeSelected() {
		List<MetadataProperty> metadataPropertiesSource = new ArrayList<MetadataProperty>();
        List<MetadataProperty> metadataPropertiesTarget = new ArrayList<MetadataProperty>();
        
        for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(0))
        	metadataPropertiesSource.add(metadataProperty);
         
        setMetadataProperties(new DualListModel<MetadataProperty>(metadataPropertiesSource, metadataPropertiesTarget));
	}
}
