package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;

public class CommencePath {
	private int id;
	private String commencePath;
	private String businessPath;
	private ArrayList<MetadataExtractionRules> metadataExtractionRulesList;

	public CommencePath() {
		setMetadataExtractionRulesList(new ArrayList<MetadataExtractionRules>());
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public void setMetadataExtractionRulesList(ArrayList<MetadataExtractionRules> metadataExtractionRulesList) {
		this.metadataExtractionRulesList = metadataExtractionRulesList;
	}	

	
	@Override
	public String toString() {
		return getCommencePath() == null ? "" : getCommencePath();
	}
}
