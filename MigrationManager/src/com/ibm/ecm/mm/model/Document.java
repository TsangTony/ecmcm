package com.ibm.ecm.mm.model;

import java.util.ArrayList;

public class Document extends DataTableElement {
	private int id;
	private String name;
	private String blIdentificationRule;
	private String igDocClass;
	private String team;
	private DataTableArrayList<CommencePath> commencePaths;
	private DataTableArrayList<IdentificationRule> identificationRules;
	
	private ArrayList<MetadataProperty> metadataProperties;
	private int s1;
	private int s2New;
	private int s1Deleted;
	
	public Document() {
		this.commencePaths = new DataTableArrayList<CommencePath>(CommencePath.class);
		this.identificationRules = new DataTableArrayList<IdentificationRule>(IdentificationRule.class);
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
	

	public ArrayList<MetadataProperty> getMetadataProperties() {
		return metadataProperties;
	}

	public void setMetadataProperties(ArrayList<MetadataProperty> metadataProperties) {
		this.metadataProperties = metadataProperties;
	}
	
	/*
	 * For Document Status Report
	 */
	
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
	
	
	public String getS1Extracted() {		
		int extracted = 0;
		for (MetadataProperty metadataProperty : getMetadataProperties()) {
			if (metadataProperty.getExtracted().get(0)>0)
				extracted++;
		}
		return extracted + " out of " + getMetadataProperties().size();
	}
	
	public String getS2Extracted() {		
		int extracted = 0;
		for (MetadataProperty metadataProperty : getMetadataProperties()) {
			if (metadataProperty.getExtracted().get(1)>0)
				extracted++;
		}
		return extracted + " out of " + getMetadataProperties().size();
	}
	
	public String getS2() {
		int s2 = getS1() - getS1Deleted() + getS2New();
		return s2 + " (- " + getS1Deleted() + ", + " + getS2New() + ") ";
	}
	
	public String getS1SuccessRate(int metadataIndex) {
		if (getS1()<=0)
			return String.valueOf(0);
		return String.valueOf(Math.round((float) getMetadataProperties().get(metadataIndex).getExtracted().get(0) / (float) getS1() * 100));
	}
	
	public String getS2SuccessRate(int metadataIndex) {
		if (getS1()-getS1Deleted()+getS2New()<=0)
			return String.valueOf(0);
		return String.valueOf(Math.round((float) getMetadataProperties().get(metadataIndex).getExtracted().get(1) / (float) (getS1()-getS1Deleted()+getS2New()) * 100));
	}


	@Override
	public String toString() {
		if (getId() > 99)		
			return "DOC-" + String.valueOf(getId()) + " " + getName();
		return "DOC-0" + String.valueOf(getId()) + " " + getName();
		
	}
}
