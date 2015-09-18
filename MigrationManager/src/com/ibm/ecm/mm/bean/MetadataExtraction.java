package com.ibm.ecm.mm.bean;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;

public class MetadataExtraction {
	
	private Document selectedDocument;
	private CommencePath selectedCommencePath;
	
	public Document getSelectedDocument() {
		System.out.println("Getting selectedDocument");
		return selectedDocument;
	}
	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}
	public CommencePath getSelectedCommencePath() {
		return selectedCommencePath;
	}
	public void setSelectedCommencePath(CommencePath selectedCommencePath) {
		this.selectedCommencePath = selectedCommencePath;
	}
	
}
