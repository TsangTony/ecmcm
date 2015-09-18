package com.ibm.ecm.mm.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.util.MSSQLConnection;
import com.ibm.ecm.mm.util.Util;

public class MetadataExtraction {
	
	private Document document;
	private CommencePath commencePath;
	private MetadataProperty metadataProperty;
	private boolean useDefaultRule;
	private String humanReadableRule;
	private String regex;
	private String capGroup;
	private ArrayList<IdentifiedDocInstance> identifiedDocInstances;
	
	public MetadataExtraction() {
		this.document = new Document();
		this.identifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
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
	

	public ArrayList<IdentifiedDocInstance> getIdentifiedDocInstances() {
		return identifiedDocInstances;
	}

	public void setIdentifiedDocInstances(ArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		this.identifiedDocInstances = identifiedDocInstances;
	}
	
	public void preview() {
		
		Connection conn = MSSQLConnection.getConnection();	
		
		try {
			PreparedStatement selectIdentifiedDocumentInstanceStmt = conn.prepareStatement("SELECT RIGHT(volume, LEN(volume) - CHARINDEX(':', volume)) + '/' + path AS path, name from Identified_Doc_Instance where document_id = ? and RIGHT(volume, LEN(volume) - CHARINDEX(':', volume)) + '/' + path like ?");
			selectIdentifiedDocumentInstanceStmt.setInt(1, getDocument().getId());
			selectIdentifiedDocumentInstanceStmt.setString(2, getCommencePath().getPath() + "%");
			
			ResultSet rs = selectIdentifiedDocumentInstanceStmt.executeQuery();
			
			while (rs.next()) {
				IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
				identifiedDocInstance.setPath(rs.getString(1));
				identifiedDocInstance.setName(rs.getString(2));		
				String metadataValue = Util.findRegex(rs.getString(2), getRegex(), Integer.valueOf(getCapGroup()), "LAST");
				identifiedDocInstance.setMetadataValue(metadataValue);
				
				getIdentifiedDocInstances().add(identifiedDocInstance);
			}
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		
	}

	
}
