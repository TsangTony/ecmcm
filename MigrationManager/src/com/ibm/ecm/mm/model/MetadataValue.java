package com.ibm.ecm.mm.model;

public class MetadataValue {
	private String value;
	private String source;
	private MetadataExtractionRule metadataExtractionRule;
	private MetadataProperty metadataProperty;
	

	public boolean setValue(String value) {
		this.value = value;
		if (value != null && value != "")
			return true;
		return false;
	}
	public String getValue() {
		return this.value;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public MetadataExtractionRule getMetadataExtractionRule() {
		return metadataExtractionRule;
	}
	public void setMetadataExtractionRule(MetadataExtractionRule metadataExtractionRule) {
		this.metadataExtractionRule = metadataExtractionRule;
	}
	public MetadataProperty getMetadataProperty() {
		return metadataProperty;
	}
	public void setMetadataProperty(MetadataProperty metadataProperty) {
		this.metadataProperty = metadataProperty;
	}
}
