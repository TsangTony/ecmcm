package com.ibm.ecm.mm.model;

public class Document {
	private int id;
	private String name;
	private String blIdentificationRule;
	private String igDocClass;
	private String team;
	private DataTableArrayList<CommencePath> commencePaths;
	private DataTableArrayList<IdentificationRule> identificationRules;
	private int s1;
	private int s1Deleted;
	private int s2New;
	
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

	public int getS1() {
		return s1;
	}

	public void setS1(int s1) {
		this.s1 = s1;
	}


	public int getS1Deleted() {
		return s1Deleted;
	}

	public void setS1Deleted(int s1Deleted) {
		this.s1Deleted = s1Deleted;
	}

	public int getS2New() {
		return s2New;
	}

	public void setS2New(int s2New) {
		this.s2New = s2New;
	}
	
	public String getS2() {
		int s2 = getS1() - getS1Deleted() + getS2New();
		return s2 + " (- " + getS1Deleted() + ", + " + getS2New() + ") ";
	}

	@Override
	public String toString() {
		if (getId() > 99)		
			return "DOC-" + String.valueOf(getId()) + " " + getName();
		return "DOC-0" + String.valueOf(getId()) + " " + getName();
		
	}
}
