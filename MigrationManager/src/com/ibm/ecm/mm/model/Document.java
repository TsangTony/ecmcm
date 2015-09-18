package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;


public class Document {
	private int id;
	private String name;
	private List<CommencePath> commencePaths;
	private List<MetadataExtractionRules> metadataExtractionRulesList;
	
	public Document() {
		this.commencePaths = new ArrayList<CommencePath>();
		this.metadataExtractionRulesList = new ArrayList<MetadataExtractionRules>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<CommencePath> getCommencePaths() {
		System.out.println("Getting commencePaths");
		return commencePaths;
	}

	public void setCommencePaths(List<CommencePath> commencePaths) {
		this.commencePaths = commencePaths;
	}	

	public List<MetadataExtractionRules> getMetadataExtractionRulesList() {
		return metadataExtractionRulesList;
	}
	public void setMetadataExtractionRulesList(List<MetadataExtractionRules> metadataExtractionRulesList) {
		this.metadataExtractionRulesList = metadataExtractionRulesList;
	}
	
	@Override
	public String toString() {
		return "DOC-" + String.valueOf(getId()) + " " + getName();
	}

}
