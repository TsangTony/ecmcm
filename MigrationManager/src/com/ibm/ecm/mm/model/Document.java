package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;


public class Document {
	private int id;
	private String name;
	private List<MetadataProperty> metadataProperties;
	private List<CommencePath> commencePaths;
	
	public Document() {
		this.commencePaths = new ArrayList<CommencePath>();
		this.metadataProperties = new ArrayList<MetadataProperty>();
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
		return commencePaths;
	}
	public void setCommencePaths(List<CommencePath> commencePaths) {
		this.commencePaths = commencePaths;
	}	
	public List<MetadataProperty> getMetadataProperties() {
		return metadataProperties;
	}
	public void setMetadataProperties(List<MetadataProperty> metadataProperties) {
		this.metadataProperties = metadataProperties;
	}

	public void addCommencePath(CommencePath commencePathToAdd) {
		for (CommencePath commencePath : getCommencePaths()) {
			if (commencePath.getDocumentId() == commencePathToAdd.getDocumentId())
				return;
		}
		getCommencePaths().add(commencePathToAdd);
	}
	
	@Override
	public String toString() {
		return "DOC-" + String.valueOf(getId()) + " " + getName();
	}
}
