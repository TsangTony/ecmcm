package com.ibm.ecm.mm.model;

public class MetadataExtractionRule extends DataTableElement {
	private int id;
	private int priority;
	private String source;
	private String regex;
	private int capGroup;
	private String blRule;
	private String defaultValue;
	private boolean isDefault;
	private boolean isNew;
	private String example;
	private int successCount;	
	private int commencePathId;

	public MetadataExtractionRule() {
		setSuccessCount(0);
	}
	
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
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
	public String getBlRule() {
		return blRule;
	}
	public void setBlRule(String blRule) {
		this.blRule = blRule;
	}
	public int getCapGroup() {
		return capGroup;
	}
	public String getCapGroupStr() {
		if (capGroup == 0)
			return "";
		return String.valueOf(capGroup);
	}
	public void setCapGroup(int capGroup) {
		this.capGroup = capGroup;
	}
	public void setCapGroup(String capGroupStr) {
		if (capGroupStr!=null) {
			try {
				int capGroup = Integer.valueOf(capGroupStr);
				this.capGroup = capGroup;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public int getCommencePathId() {
		return commencePathId;
	}
	public void setCommencePathId(int commencePathId) {
		this.commencePathId = commencePathId;
	}
	
}
