package com.ibm.ecm.mm.model;
import java.util.ArrayList;
import java.util.Collections;

public class MetadataExtractionRules {
	private int metadataId;
	private String metadataName;
	private boolean isDefault;
	private boolean hasDefault;
	private ArrayList<MetadataExtractionRule> rules;
	private ArrayList<MetadataExtractionRule> removedRules;
	private ArrayList<MetadataExtractionRule> customRules;
	private ArrayList<MetadataExtractionRule> defaultRules;
	private ArrayList<Lookup> lookups;

	
	public MetadataExtractionRules() {
		rules = new ArrayList<MetadataExtractionRule>();
		removedRules = new ArrayList<MetadataExtractionRule>();
		customRules = new ArrayList<MetadataExtractionRule>();
		defaultRules = new ArrayList<MetadataExtractionRule>();
		lookups = new ArrayList<Lookup>();
	}
	
	public int getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}
	
	public String getMetadataName() {
		return metadataName;
	}

	public void setMetadataName(String metadataName) {
		this.metadataName = metadataName;
	}

	public ArrayList<MetadataExtractionRule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<MetadataExtractionRule> rules) {
		this.rules = rules;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean getHasDefault() {
		return hasDefault;
	}

	public void setHasDefaultRules(boolean hasDefault) {
		this.hasDefault = hasDefault;
	}
	
	public ArrayList<MetadataExtractionRule> getRemovedRules() {
		return removedRules;
	}

	public void setRemovedRules(ArrayList<MetadataExtractionRule> removedRules) {
		this.removedRules = removedRules;
	}

	public ArrayList<MetadataExtractionRule> getCustomRules() {
		return customRules;
	}

	public void setCustomRules(ArrayList<MetadataExtractionRule> customRules) {
		this.customRules = customRules;
	}

	public ArrayList<MetadataExtractionRule> getDefaultRules() {
		return defaultRules;
	}

	public void setDefaultRules(ArrayList<MetadataExtractionRule> defaultRules) {
		this.defaultRules = defaultRules;
	}

	public ArrayList<Lookup> getLookups() {
		return lookups;
	}

	public void setLookups(ArrayList<Lookup> lookups) {
		this.lookups = lookups;
	}

	public void addNewRule() {
		MetadataExtractionRule metadataExtractionRule = new MetadataExtractionRule();
		metadataExtractionRule.setPriority(getRules().size()+1);
		metadataExtractionRule.setNew(true);
		getRules().add(metadataExtractionRule);
	}
	
	public void removeRule(MetadataExtractionRule metadataExtractionRule) {
		getRules().remove(metadataExtractionRule);
		if (!metadataExtractionRule.isNew())
			getRemovedRules().add(metadataExtractionRule);
	}
	
	public void moveRuleUp(MetadataExtractionRule metadataExtractionRule) {		
		getRules().get(getRules().indexOf(metadataExtractionRule)-1).setPriority(metadataExtractionRule.getPriority());
		metadataExtractionRule.setPriority(metadataExtractionRule.getPriority()-1);
		Collections.swap(getRules(), getRules().indexOf(metadataExtractionRule)-1, getRules().indexOf(metadataExtractionRule));
	}
	
	public void moveRuleDown(MetadataExtractionRule metadataExtractionRule) {		
		getRules().get(getRules().indexOf(metadataExtractionRule)+1).setPriority(metadataExtractionRule.getPriority());
		metadataExtractionRule.setPriority(metadataExtractionRule.getPriority()+1);
		Collections.swap(getRules(), getRules().indexOf(metadataExtractionRule)+1, getRules().indexOf(metadataExtractionRule));
	}
	
	@Override
	public String toString() {
		return getMetadataName();
	}
}
