package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.List;


public class Document {
	private int id;
	private String name;
	private ArrayList<CommencePath> commencePaths;
	
	public Document() {
		this.commencePaths = new ArrayList<CommencePath>();
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
	public void setCommencePaths(ArrayList<CommencePath> commencePaths) {
		this.commencePaths = commencePaths;
	}		

	@Override
	public String toString() {
		return "DOC-" + String.valueOf(getId()) + " " + getName();
	}
}
