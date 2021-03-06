package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Document extends DataTableElement {
	private int id;
	private String name;
	private String blIdentificationRule;
	private String igDocClass;
	private String igSecClass;
	private Team team;
	private int release;
	private boolean isNoPdf;
	private boolean isOfficeDoc;
	private boolean includeLinkedFile;
	private DataTableArrayList<CommencePath> commencePaths;
	private DataTableArrayList<IdentificationRule> identificationRules;
	private boolean identifyDeltaOnly;
	
	private ArrayList<MetadataProperty> metadataProperties;
	private HashMap<Integer,Integer> identifiedFilesCounts;
	
	public Document() {
		this.commencePaths = new DataTableArrayList<CommencePath>(CommencePath.class);
		this.identificationRules = new DataTableArrayList<IdentificationRule>(IdentificationRule.class);
		this.metadataProperties = new ArrayList<MetadataProperty>();
		this.identifiedFilesCounts = new HashMap<Integer,Integer>();
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

	public String getIgSecClass() {
		return igSecClass;
	}

	public void setIgSecClass(String igSecClass) {
		this.igSecClass = igSecClass;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	

	public int getRelease() {
		return release;
	}

	public void setRelease(int release) {
		this.release = release;
	}

	public boolean isNoPdf() {
		return isNoPdf;
	}

	public void setNoPdf(boolean isNoPdf) {
		this.isNoPdf = isNoPdf;
	}

	public boolean isOfficeDoc() {
		return isOfficeDoc;
	}

	public void setOfficeDoc(boolean isOfficeDoc) {
		this.isOfficeDoc = isOfficeDoc;
	}

	public boolean isIncludeLinkedFile() {
		return includeLinkedFile;
	}

	public void setIncludeLinkedFile(boolean includeLinkedFile) {
		this.includeLinkedFile = includeLinkedFile;
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
	
	public HashMap<Integer,Integer> getIdentifiedFilesCounts() {
		return identifiedFilesCounts;
	}

	public void setIdentifiedFilesCounts(HashMap<Integer,Integer> identifiedFilesCounts) {
		this.identifiedFilesCounts = identifiedFilesCounts;
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


	@Override
	public String toString() {
		if (getId() > 99)		
			return "DOC-" + String.valueOf(getId()) + " " + getName();
		if (getId() > 9)		
			return "DOC-0" + String.valueOf(getId()) + " " + getName();
		if (getId() == 0)
			return "All Documents";
		return "DOC-00" + String.valueOf(getId()) + " " + getName();		
	}

	public boolean isIdentifyDeltaOnly() {
		return identifyDeltaOnly;
	}

	public void setIdentifyDeltaOnly(boolean identifyDeltaOnly) {
		this.identifyDeltaOnly = identifyDeltaOnly;
	}
}
