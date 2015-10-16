package com.ibm.ecm.mm.bean;

import java.util.ArrayList;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.util.DataManager;


public class SnapshotBean {
	private ArrayList<Document> documents;

	public SnapshotBean() {
		setDocuments(DataManager.getSnapshots());
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}
	
	
}
