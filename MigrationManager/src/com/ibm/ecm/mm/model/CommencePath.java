package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;

public class CommencePath {
	private int documentId;
	private String commencePath;
	private String businessPath;
	private List<MetadataExtractionRules> metadataExtractionRulesList;

	public CommencePath() {
		setMetadataExtractionRulesList(new ArrayList<MetadataExtractionRules>());
	}
	
	public int getDocumentId() {
		return documentId;
	}
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	public String getCommencePath() {
		return commencePath;
	}
	public void setCommencePath(String commencePath) {
		this.commencePath = commencePath;
	}
	public String getBusinessPath() {
		return businessPath;
	}
	public void setBusinessPath(String businessPath) {
		this.businessPath = businessPath;
	}
	public List<MetadataExtractionRules> getMetadataExtractionRulesList() {
		return metadataExtractionRulesList;
	}
	public void setMetadataExtractionRulesList(List<MetadataExtractionRules> metadataExtractionRulesList) {
		this.metadataExtractionRulesList = metadataExtractionRulesList;
	}
	
	@Override
	public String toString() {
		return getBusinessPath();
	}
}
