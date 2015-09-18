package com.ibm.ecm.mm.bean;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.MetadataProperty;

public class MetadataExtraction {
	
	private Document document;
	private CommencePath commencePath;
	private MetadataProperty metadataProperty;
	
	public MetadataExtraction() {
		setDocument(new Document());
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
	public void setcommencePath(CommencePath commencePath) {
		this.commencePath = commencePath;
	}

	public MetadataProperty getMetadataProperty() {
		return metadataProperty;
	}

	public void setMetadataProperty(MetadataProperty metadataProperty) {
		this.metadataProperty = metadataProperty;
	}
}
