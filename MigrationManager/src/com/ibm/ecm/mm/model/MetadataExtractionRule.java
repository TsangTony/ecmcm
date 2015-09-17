package com.ibm.ecm.mm.model;

public class MetadataExtractionRule {
	private int id;
	private String source;
	private String rule;
	private int capGroup;
	private String hrRule;
	private String defaultValue;
	private String example;
	private int successCount;	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public String getHrRule() {
		return hrRule;
	}
	public void setHrRule(String hrRule) {
		this.hrRule = hrRule;
	}
	public int getCapGroup() {
		return capGroup;
	}
	public void setCapGroup(int capGroup) {
		this.capGroup = capGroup;
	}
	
}
