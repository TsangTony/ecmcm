package com.ibm.ecm.mm.bean;

import java.util.ArrayList;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.util.DataManager;


public class ReportBean {
	private ArrayList<Document> documents;

	public ReportBean() {
		setDocuments(DataManager.getDocumentStatusReport());
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}
	
	
}
