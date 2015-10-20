package com.ibm.ecm.mm.bean;

import java.util.ArrayList;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.util.DataManager;

public class ValidationBean {
	private ArrayList<Document> documents;
	private ArrayList<Document> selectedDocuments;
	private ArrayList<Document> filteredDocuments;
	
	public ValidationBean() {
		setDocuments(DataManager.getDocuments());
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}
	public ArrayList<Document> getSelectedDocuments() {
		return selectedDocuments;
	}
	public void setSelectedDocuments(ArrayList<Document> selectedDocuments) {
		this.selectedDocuments = selectedDocuments;
	}

	public ArrayList<Document> getFilteredDocuments() {
		return filteredDocuments;
	}

	public void setFilteredDocuments(ArrayList<Document> filteredDocuments) {
		this.filteredDocuments = filteredDocuments;
	}
	
	public void generate() {
		
	}
	
}
