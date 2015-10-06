package com.ibm.ecm.mm.model;
import java.util.ArrayList;

public class MetadataExtractionRules {
	private MetadataProperty metadataProperty;
	private boolean isDefault;
	private boolean hasDefault;
	private DataTableArrayList<MetadataExtractionRule> rules;
	private DataTableArrayList<MetadataExtractionRule> customRules;
	private DataTableArrayList<MetadataExtractionRule> defaultRules;
	private ArrayList<Lookup> lookups;

	
	public MetadataExtractionRules() {
		rules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
		customRules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
		defaultRules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
		lookups = new ArrayList<Lookup>();
	}

	public MetadataProperty getMetadataProperty() {
		return metadataProperty;
	}
	public void setMetadataProperty(MetadataProperty metadataProperty) {
		this.metadataProperty = metadataProperty;
	}
	
	public DataTableArrayList<MetadataExtractionRule> getRules() {
		return rules;
	}

	public void setRules(DataTableArrayList<MetadataExtractionRule> rules) {
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

	public DataTableArrayList<MetadataExtractionRule> getCustomRules() {
		return customRules;
	}

	public void setCustomRules(DataTableArrayList<MetadataExtractionRule> customRules) {
		this.customRules = customRules;
	}

	public DataTableArrayList<MetadataExtractionRule> getDefaultRules() {
		return defaultRules;
	}

	public void setDefaultRules(DataTableArrayList<MetadataExtractionRule> defaultRules) {
		this.defaultRules = defaultRules;
	}

	public ArrayList<Lookup> getLookups() {
		return lookups;
	}

	public void setLookups(ArrayList<Lookup> lookups) {
		this.lookups = lookups;
	}
	
	@Override
	public String toString() {
		return getMetadataProperty().getName();
	}
}
