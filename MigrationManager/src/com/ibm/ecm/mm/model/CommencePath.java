package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;

public class CommencePath extends DataTableElement {
	private int id;
	private String actualPath;
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

	public String getActualPath() {
		return actualPath;
	}

	public void setActualPath(String actualPath) {
		if (actualPath != null) {
			actualPath = actualPath.replace("\\", "/");
			actualPath = actualPath.endsWith("/") ? actualPath.substring(0, actualPath.length()-2) : actualPath;
		}
		this.actualPath = actualPath;
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
		return getActualPath() == null ? "" : getActualPath();
	}
}
