package com.ibm.ecm.mm.model;

import java.util.ArrayList;

public class IdentifiedDocInstance {
	private String name;
	private String path;
	private String metadataValue;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMetadataValue() {
		return metadataValue;
	}
	public void setMetadataValue(String metadataValue) {
		this.metadataValue = metadataValue;
	}
}
