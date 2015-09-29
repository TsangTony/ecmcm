package com.ibm.ecm.mm.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.Lookup;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataExtractionRules;
import com.ibm.ecm.mm.model.MetadataValue;
import com.ibm.ecm.mm.model.Source;
import com.ibm.ecm.mm.util.MSSQLConnection;
import com.ibm.ecm.mm.util.Util;

public class MetadataExtraction {
	
	private Document document;
	private CommencePath commencePath;
	private boolean useDefaultRule;
	private MetadataExtractionRules metadataExtractionRules;
	//private ArrayList<MetadataExtractionRule> defaultMetadataExtractionRules;
	//private ArrayList<MetadataExtractionRule> customMetadataExtractionRules;
	//private ArrayList<MetadataExtractionRule> removedMetadataExtractionRules;
	private ArrayList<IdentifiedDocInstance> identifiedDocInstances;
	//private ArrayList<Lookup> lookups;
	
	private ArrayList<String> sources;
	
	public MetadataExtraction() {
		document = new Document();
		identifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
		useDefaultRule = false;

		sources = new ArrayList<String>();
		sources.add(Source.FILE_PATH);
		sources.add(Source.CONTENT);
		sources.add(Source.DEFAULT);
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

	public boolean isUseDefaultRule() {
		return useDefaultRule;
	}

	public void setUseDefaultRule(boolean useDefaultRule) {
		this.useDefaultRule = useDefaultRule;
	}

	public ArrayList<IdentifiedDocInstance> getIdentifiedDocInstances() {
		return identifiedDocInstances;
	}

	public void setIdentifiedDocInstances(ArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		this.identifiedDocInstances = identifiedDocInstances;
	}
	
	public MetadataExtractionRules getMetadataExtractionRules() {
		return this.metadataExtractionRules;
	}

	public void setMetadataExtractionRules(MetadataExtractionRules metadataExtractionRules) {
		this.metadataExtractionRules = metadataExtractionRules;
	}	

	public ArrayList<String> getSources() {
		return sources;
	}

	public void setSources(ArrayList<String> sources) {
		this.sources = sources;
	}

	public int getMetadataExtractionRulesLastIndex() {
		return metadataExtractionRules.getRules().size()-1;
	}
	
	public boolean getRuleEditable() {
		return (!isUseDefaultRule()) && (getMetadataExtractionRules() != null);
	}


	
	public void setRules() {		
		try {
			Connection conn = MSSQLConnection.getConnection();
			PreparedStatement selectLookupStmt = conn.prepareStatement("SELECT lookup_value, returned_value FROM Lookup WHERE metadata_id = ?");
			selectLookupStmt.setInt(1, getMetadataExtractionRules().getMetadataId());				
			ResultSet lookupRs = selectLookupStmt.executeQuery();						
			while (lookupRs.next()) {
				Lookup lookup = new Lookup();
				lookup.setLookupValue(lookupRs.getString(1));
				lookup.setReturnedValue(lookupRs.getString(2));
				getMetadataExtractionRules().getLookups().add(lookup);
			}			
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			MSSQLConnection.close();
		}

		if (getMetadataExtractionRules().getLookups().size() == 0) {
			getMetadataExtractionRules().setHasDefaultRules(false);
		}
		else {
			getMetadataExtractionRules().setHasDefaultRules(true);
		}
		
		if (getMetadataExtractionRules().isDefault()) {
			setUseDefaultRule(true);
			getMetadataExtractionRules().setDefaultRules(getMetadataExtractionRules().getRules());
		}
		else {
			setUseDefaultRule(false);
			getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules()); 
		}
	}
	
	public void toggleRules() {
		if (useDefaultRule) {
			getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules());
			if (getMetadataExtractionRules().getDefaultRules().size() != 0) {
				getMetadataExtractionRules().setRules(getMetadataExtractionRules().getDefaultRules());
			}
			else {
				ArrayList<MetadataExtractionRule> metadataExtractionRules = new ArrayList<MetadataExtractionRule>();
				
				
				MetadataExtractionRule metadataExtractionRuleFP = new MetadataExtractionRule();
				metadataExtractionRuleFP.setHrRule("Look up " + getMetadataExtractionRules().getMetadataName());
				metadataExtractionRuleFP.setSource(Source.FILE_PATH);
				metadataExtractionRuleFP.setPriority(1);
				metadataExtractionRuleFP.setNew(true);
				
				metadataExtractionRules.add(metadataExtractionRuleFP);

				MetadataExtractionRule metadataExtractionRuleCT = new MetadataExtractionRule();
				metadataExtractionRuleCT.setHrRule("Look up " + getMetadataExtractionRules().getMetadataName());
				metadataExtractionRuleCT.setSource(Source.CONTENT);
				metadataExtractionRuleCT.setPriority(2);
				metadataExtractionRuleCT.setNew(true);

				metadataExtractionRules.add(metadataExtractionRuleCT);
				
				getMetadataExtractionRules().setRules(metadataExtractionRules);
				getMetadataExtractionRules().setDefaultRules(metadataExtractionRules);
			}
			getMetadataExtractionRules().setDefault(true);
		}
		else {
			if (getMetadataExtractionRules().getCustomRules().size() != 0) {
				getMetadataExtractionRules().setRules(getMetadataExtractionRules().getCustomRules());
			}
			else {
				getMetadataExtractionRules().setRules(new ArrayList<MetadataExtractionRule>());
				getMetadataExtractionRules().setCustomRules(new ArrayList<MetadataExtractionRule>());
			}
			getMetadataExtractionRules().setDefault(false);
		}
		
	}
	
	public void preview() {
		
		//TODO: put this into IdentifiedDocInstances.getIdentifiedDocInstances(documentId, commencePath)
		if (identifiedDocInstances.size() == 0) {	
			try {					
				Connection conn = MSSQLConnection.getConnection();	
				PreparedStatement selectIdentifiedDocumentInstanceStmt = conn.prepareStatement("SELECT id, server, volume, path, name, extension from Identified_Doc_Instance where document_id = ? and volume + '/' + ISNULL(path,'') like ?");
				selectIdentifiedDocumentInstanceStmt.setInt(1, getDocument().getId());
				selectIdentifiedDocumentInstanceStmt.setString(2, getCommencePath().getCommencePath() + "%");
				ResultSet rs = selectIdentifiedDocumentInstanceStmt.executeQuery();				
				
				while (rs.next()) {
					IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
					identifiedDocInstance.setId(rs.getLong(1));
					identifiedDocInstance.setServer(rs.getString(2));
					identifiedDocInstance.setVolume(rs.getString(3));
					identifiedDocInstance.setPath(rs.getString(4));
					identifiedDocInstance.setName(rs.getString(5));	
					identifiedDocInstance.setExtension(rs.getString(6));	
					getIdentifiedDocInstances().add(identifiedDocInstance);
				}
				
			}
			catch (SQLException e) {				
				e.printStackTrace();
			}
			finally {
				MSSQLConnection.close();
			}
		}
		
		for (IdentifiedDocInstance identifiedDocInstance : getIdentifiedDocInstances()) {
			
			MetadataValue metadataValue = new MetadataValue();
			metadataValue.setValue("");
			identifiedDocInstance.setMetadataValue(metadataValue);
			if (useDefaultRule) {
				metadataExtractionRuleloop:
				for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules()) {
					
					for (Lookup lookup : getMetadataExtractionRules().getLookups()) {
						if (metadataExtractionRule.getSource().equals(Source.FILE_PATH)) {
							identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getFullPath(), lookup, "LAST"));
						}
						else if (metadataExtractionRule.getSource().equals(Source.CONTENT)) {
							identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getContent(), lookup, "FIRST"));
						}

						if (!identifiedDocInstance.getMetadataValue().getValue().equals("")) {
							identifiedDocInstance.getMetadataValue().setMetadataExtractionRule(metadataExtractionRule);
							break metadataExtractionRuleloop;
						}
					}
				}
			}
			else {
				for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules()) {
					if (metadataExtractionRule.getSource().equals(Source.FILE_PATH)) {
						identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getFullPath(), metadataExtractionRule.getRule(), metadataExtractionRule.getCapGroup(), "LAST"));
					}
					else if (metadataExtractionRule.getSource().equals(Source.CONTENT)) {
						identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getContent(), metadataExtractionRule.getRule(), metadataExtractionRule.getCapGroup(), "FIRST"));
					}
					else if (metadataExtractionRule.getSource().equals(Source.DEFAULT)) {
						identifiedDocInstance.getMetadataValue().setValue(metadataExtractionRule.getDefaultValue());
					}
					
					if (!identifiedDocInstance.getMetadataValue().getValue().equals("")) {
						identifiedDocInstance.getMetadataValue().setMetadataExtractionRule(metadataExtractionRule);
						break;
					}					
				}
			}
		}		
	}
	
	public void runAndSave() {
		preview();
		//TODO: put this into getIdentifiedDocInstance.save()
		
		if (useDefaultRule) {
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getCustomRules()) {
				getMetadataExtractionRules().getRemovedRules().add(metadataExtractionRule);
				metadataExtractionRule.setNew(true);
			}
		}
		else {
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getDefaultRules()) {
				getMetadataExtractionRules().getRemovedRules().add(metadataExtractionRule);
				metadataExtractionRule.setNew(true);
			}
		}
		
		try {
			Connection conn = MSSQLConnection.getConnection();	
			
			PreparedStatement deleteMetadataExtractionRuleStmt = conn.prepareStatement("DELETE FROM Metadata_Extraction_Rule WHERE id = ?");
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRemovedRules()) {
				deleteMetadataExtractionRuleStmt.setInt(1, metadataExtractionRule.getId());
				deleteMetadataExtractionRuleStmt.addBatch();
			}
			deleteMetadataExtractionRuleStmt.executeBatch();
			
			getMetadataExtractionRules().getRemovedRules().clear();
			
			PreparedStatement updateMetadataExtractionRuleStmt = conn.prepareStatement("UPDATE Metadata_Extraction_Rule SET source=?, hr_rule=?, regex=?, capturing_group=?, default_value=?, priority=? WHERE ID = ?");
			PreparedStatement insertMetadataExtractionRuleStmt = conn.prepareStatement("INSERT INTO Metadata_Extraction_Rule (commence_path_id,metadata_id,source,hr_rule,regex,capturing_group,default_value,priority,[default]) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules()) {
				if (metadataExtractionRule.isNew()) {
					insertMetadataExtractionRuleStmt.setInt(1, getMetadataExtractionRules().getMetadataId());
					insertMetadataExtractionRuleStmt.setInt(2, getCommencePath().getId());
					insertMetadataExtractionRuleStmt.setString(3, metadataExtractionRule.getSource());
					insertMetadataExtractionRuleStmt.setString(4, metadataExtractionRule.getHrRule());
					insertMetadataExtractionRuleStmt.setString(5, metadataExtractionRule.getRule());
					insertMetadataExtractionRuleStmt.setInt(6, metadataExtractionRule.getCapGroup());
					insertMetadataExtractionRuleStmt.setString(7, metadataExtractionRule.getDefaultValue());
					insertMetadataExtractionRuleStmt.setInt(8, metadataExtractionRule.getPriority());
					insertMetadataExtractionRuleStmt.setBoolean(9, getMetadataExtractionRules().isDefault());
					insertMetadataExtractionRuleStmt.execute();
					
					ResultSet metadataExtractionRuleIdRs = insertMetadataExtractionRuleStmt.getGeneratedKeys();
					if (metadataExtractionRuleIdRs != null && metadataExtractionRuleIdRs.next()) {
						metadataExtractionRule.setId(metadataExtractionRuleIdRs.getInt(1));
					}
					
				}
				else {
					updateMetadataExtractionRuleStmt.setString(1, metadataExtractionRule.getSource());
					updateMetadataExtractionRuleStmt.setString(2, metadataExtractionRule.getHrRule());
					updateMetadataExtractionRuleStmt.setString(3, metadataExtractionRule.getRule());
					updateMetadataExtractionRuleStmt.setInt(4, metadataExtractionRule.getCapGroup());
					updateMetadataExtractionRuleStmt.setString(5, metadataExtractionRule.getDefaultValue());
					updateMetadataExtractionRuleStmt.setInt(6, metadataExtractionRule.getPriority());
					updateMetadataExtractionRuleStmt.setInt(7, metadataExtractionRule.getId());
					updateMetadataExtractionRuleStmt.addBatch();
				}
				metadataExtractionRule.setNew(false);
			}
			updateMetadataExtractionRuleStmt.executeBatch();
			
			/*
			 * Update Metadata_Value
			 */
			
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE identified_doc_instance_id IN (SELECT id FROM Identified_Doc_Instance WHERE document_id = ?) AND (metadata_extraction_rule_id IN (SELECT id FROM Metadata_Extraction_Rule WHERE commence_path_id = ? AND metadata_id = ?) OR metadata_extraction_rule_id NOT IN (SELECT id FROM Metadata_Extraction_Rule))");
			deleteIdentifiedDocInstanceStmt.setInt(1, getDocument().getId());
			deleteIdentifiedDocInstanceStmt.setInt(2, getCommencePath().getId());
			deleteIdentifiedDocInstanceStmt.setInt(3, getMetadataExtractionRules().getMetadataId());
			deleteIdentifiedDocInstanceStmt.execute();
			
			PreparedStatement insertMetadataValueStmt = conn.prepareStatement("INSERT INTO Metadata_Value (identified_doc_instance_id,value,metadata_extraction_rule_id) VALUES (?,?,?)");
			
			for (IdentifiedDocInstance identifiedDocInstance : getIdentifiedDocInstances()) {
				if (identifiedDocInstance.getMetadataValue().getMetadataExtractionRule() != null) {
					insertMetadataValueStmt.setLong(1, identifiedDocInstance.getId());
					insertMetadataValueStmt.setString(2, identifiedDocInstance.getMetadataValue().getValue());
					insertMetadataValueStmt.setInt(3, identifiedDocInstance.getMetadataValue().getMetadataExtractionRule().getId());
					insertMetadataValueStmt.addBatch();
				}
			}
			insertMetadataValueStmt.executeBatch();			
			
			
			FacesContext context = FacesContext.getCurrentInstance();
	         
	        context.addMessage(null, new FacesMessage("Successful",  "Metadata Extraction Rules and Metadata Values are saved. ") );
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			MSSQLConnection.close();
		}	
	}
	
}
