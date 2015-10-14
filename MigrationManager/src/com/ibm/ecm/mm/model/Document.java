package com.ibm.ecm.mm.model;

public class Document {
	private int id;
	private String name;
	private String blIdentificationRule;
	private String igDocClass;
	private String team;
	private DataTableArrayList<CommencePath> commencePaths;
	private DataTableArrayList<IdentificationRule> identificationRules;
	
	public Document() {
		this.commencePaths = new DataTableArrayList<CommencePath>(CommencePath.class);
		this.identificationRules = new DataTableArrayList<IdentificationRule>(IdentificationRule.class);
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
	public DataTableArrayList<CommencePath> getCommencePaths() {
		return commencePaths;
	}
	public void setCommencePaths(DataTableArrayList<CommencePath> commencePaths) {
		this.commencePaths = commencePaths;
	}

	public DataTableArrayList<IdentificationRule> getIdentificationRules() {
		return identificationRules;
	}

	public void setIdentificationRules(DataTableArrayList<IdentificationRule> identificationRules) {
		this.identificationRules = identificationRules;
	}

	public String getBlIdentificationRule() {
		return blIdentificationRule;
	}

	public void setBlIdentificationRule(String blIdentificationRule) {
		this.blIdentificationRule = blIdentificationRule;
	}
	
	public String getIgDocClass() {
		return igDocClass;
	}

	public void setIgDocClass(String igDocClass) {
		this.igDocClass = igDocClass;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	@Override
	public String toString() {
		if (getId() > 99)		
			return "DOC-" + String.valueOf(getId()) + " " + getName();
		return "DOC-0" + String.valueOf(getId()) + " " + getName();
		
	}
}
