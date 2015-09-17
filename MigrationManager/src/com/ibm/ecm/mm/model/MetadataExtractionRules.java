package com.ibm.ecm.mm.model;
import java.util.ArrayList;
import java.util.List;

public class MetadataExtractionRules {
	private String metadataName;
	private int commencePathId;
	private List<MetadataExtractionRule> metadataExtractionRules;

	public MetadataExtractionRules() {
		setMetadataExtractionRules(new ArrayList<MetadataExtractionRule>());
	}
	
	public String getMetadataName() {
		return metadataName;
	}

	public void setMetadataName(String metadataName) {
		this.metadataName = metadataName;
	}

	public List<MetadataExtractionRule> getMetadataExtractionRules() {
		return metadataExtractionRules;
	}

	public void setMetadataExtractionRules(List<MetadataExtractionRule> metadataExtractionRules) {
		this.metadataExtractionRules = metadataExtractionRules;
	}

	public int getCommencePathId() {
		return commencePathId;
	}

	public void setCommencePathId(int commencePathId) {
		this.commencePathId = commencePathId;
	}


	
}
