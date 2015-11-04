package com.ibm.ecm.mm.model;

public class DocumentClass {
	private int id;
	private String name;
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
	
	@Override
	public String toString() {
		if (getId() == 0)
			return "All Document Classes";
		return "DC-" + getId() + " " + getName();
	}
}
