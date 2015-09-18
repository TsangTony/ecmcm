package com.ibm.ecm.mm.bean;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.MetadataProperty;

public class MetadataExtraction {
	
	private Document document;
	private CommencePath commencePath;
	private MetadataProperty metadataProperty;
	private boolean useDefaultRule;
	private String humanReadableRule;
	private String regex;
	private String capGroup;
	
	public MetadataExtraction() {
		setDocument(new Document());
	}
	
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public CommencePath getCommencePath() {
		return commencePath;
	}
	public void setcommencePath(CommencePath commencePath) {
		this.commencePath = commencePath;
	}

	public MetadataProperty getMetadataProperty() {
		return metadataProperty;
	}

	public void setMetadataProperty(MetadataProperty metadataProperty) {
		this.metadataProperty = metadataProperty;
	}

	public boolean isUseDefaultRule() {
		return useDefaultRule;
	}

	public void setUseDefaultRule(boolean useDefaultRule) {
		this.useDefaultRule = useDefaultRule;
	}

	public String getHumanReadableRule() {
		return humanReadableRule;
	}

	public void setHumanReadableRule(String humanReadableRule) {
		this.humanReadableRule = humanReadableRule;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getCapGroup() {
		return capGroup;
	}

	public void setCapGroup(String capGroup) {
		this.capGroup = capGroup;
	}
	
	public void submit() {
		
	}
	
}
