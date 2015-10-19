package com.ibm.ecm.mm.model;

import java.util.ArrayList;

public class MetadataProperty {
	private int id;
	private String name;
	private ArrayList<Boolean> extracted;
	
	public MetadataProperty() {
		setExtracted(new ArrayList<Boolean>());
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
	public ArrayList<Boolean> getExtracted() {
		return extracted;
	}
	public void setExtracted(ArrayList<Boolean> extracted) {
		this.extracted = extracted;
	}
	
	@Override
	public String toString() {
		return getName() == null ? "" : getName() ;
	}
}
