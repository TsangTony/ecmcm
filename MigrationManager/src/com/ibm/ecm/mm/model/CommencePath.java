package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;

public class CommencePath {
	private int id;
	private String path;
	private String buPath;
	private List<MetadataExtractionRules> metadataExtractionRulesList;

	public CommencePath() {
		setMetadataExtractionRulesList(new ArrayList<MetadataExtractionRules>());
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getBuPath() {
		return buPath;
	}
	public void setBuPath(String buPath) {
		this.buPath = buPath;
	}
	public List<MetadataExtractionRules> getMetadataExtractionRulesList() {
		return metadataExtractionRulesList;
	}
	public void setMetadataExtractionRulesList(List<MetadataExtractionRules> metadataExtractionRulesList) {
		this.metadataExtractionRulesList = metadataExtractionRulesList;
	}
	
	@Override
	public String toString() {
		return getPath();
	}
}
