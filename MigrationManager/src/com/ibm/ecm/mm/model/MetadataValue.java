package com.ibm.ecm.mm.model;

public class MetadataValue {
	private String value;
	private MetadataExtractionRule metadataExtractionRule;

	public boolean setValue(String value) {
		this.value = value;
		if (value != null && value != "")
			return true;
		return false;
	}
	public String getValue() {
		return this.value;
	}
	public MetadataExtractionRule getMetadataExtractionRule() {
		return metadataExtractionRule;
	}
	public void setMetadataExtractionRule(MetadataExtractionRule metadataExtractionRule) {
		this.metadataExtractionRule = metadataExtractionRule;
	}
}
