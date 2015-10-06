package com.ibm.ecm.mm.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.Lookup;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataExtractionRules;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.model.MetadataValue;
import com.ibm.ecm.mm.util.ConnectionManager;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.Util;

public class MetadataExtraction {
	
	private ArrayList<Document> documents;
	private Document document;
	private CommencePath commencePath;
	private MetadataExtractionRules metadataExtractionRules;
	private boolean useDefaultRule;
	private ArrayList<IdentifiedDocInstance> identifiedDocInstances;
	
	public MetadataExtraction() {
		setDocuments(DataManager.getDocuments());
		identifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
		useDefaultRule = false;
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
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
	public void setCommencePath(CommencePath commencePath) {
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

	public int getMetadataExtractionRulesLastIndex() {
		return metadataExtractionRules.getRules().size()-1;
	}
	
	public boolean getRuleEditable() {
		return (!isUseDefaultRule()) && (getMetadataExtractionRules() != null);
	}
	
	public ArrayList<String> getSources() {
		return DataManager.getSources();
	}
	
	public void documentSelected() {
		if (getDocument().getCommencePaths().size() == 0)
			getDocument().setCommencePaths(DataManager.getCommencePaths(getDocument().getId()));
		setCommencePath(null);
		setMetadataExtractionRules(null);
	}
	
	public void commencePathSelected() {
		if (getCommencePath().getMetadataExtractionRulesList().size() == 0)
			for (MetadataProperty metadataProperty : DataManager.getMetadataPropreties(getDocument().getId())) {
				MetadataExtractionRules metadataExtractionRules = new MetadataExtractionRules();
				metadataExtractionRules.setMetadataProperty(metadataProperty);
				getCommencePath().getMetadataExtractionRulesList().add(metadataExtractionRules);
			}
		setMetadataExtractionRules(null);
	}

	
	public void metadataPropertySeletced() {
		if (getMetadataExtractionRules().getRules().size() == 0)
			getMetadataExtractionRules().setRules(DataManager.getMetadataExtractionRules(getCommencePath().getId(),getMetadataExtractionRules().getMetadataProperty().getId()));
		
		if (getMetadataExtractionRules().getRules().size() == 0)
			getMetadataExtractionRules().setDefault(false);
		else
			getMetadataExtractionRules().setDefault(getMetadataExtractionRules().getRules().get(0).isDefault());
		
		getMetadataExtractionRules().setLookups(DataManager.getLookups(getMetadataExtractionRules().getMetadataProperty().getId()));
				
		getMetadataExtractionRules().setHasDefaultRules(getMetadataExtractionRules().getLookups().size() > 0);
		
		setUseDefaultRule(getMetadataExtractionRules().isDefault());
		
		if (isUseDefaultRule()) {
			getMetadataExtractionRules().setDefaultRules(getMetadataExtractionRules().getRules());
			getMetadataExtractionRules().setCustomRules(new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class));
		}
		else {
			DataTableArrayList<MetadataExtractionRule> metadataExtractionRules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
			MetadataExtractionRule metadataExtractionRuleFP = new MetadataExtractionRule();
			metadataExtractionRuleFP.setBlRule("Look up " + getMetadataExtractionRules().getMetadataProperty().getName());
			metadataExtractionRuleFP.setSource("File Path");
			metadataExtractionRuleFP.setPriority(1);
			metadataExtractionRuleFP.setNew(true);
			metadataExtractionRules.add(metadataExtractionRuleFP);
			MetadataExtractionRule metadataExtractionRuleCT = new MetadataExtractionRule();
			metadataExtractionRuleCT.setBlRule("Look up " + getMetadataExtractionRules().getMetadataProperty().getName());
			metadataExtractionRuleCT.setSource("Content");
			metadataExtractionRuleCT.setPriority(2);
			metadataExtractionRuleCT.setNew(true);
			metadataExtractionRules.add(metadataExtractionRuleCT);			
			getMetadataExtractionRules().setDefaultRules(metadataExtractionRules);			
			getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules());
		}
		
	}
	
	public void toggleRules() {
		if (isUseDefaultRule()) {
			getMetadataExtractionRules().setCustomRules(getMetadataExtractionRules().getRules());
			getMetadataExtractionRules().setRules(getMetadataExtractionRules().getDefaultRules());
			getMetadataExtractionRules().setDefault(true);
		}
		else {
			getMetadataExtractionRules().setRules(getMetadataExtractionRules().getCustomRules());
			getMetadataExtractionRules().setDefault(false);
		}
		
	}
	
	public void preview() {
		
		int filePathCount = 0;
		int contentCount = 0;
		int defaultCount = 0;
		
		//TODO: put this into IdentifiedDocInstances.getIdentifiedDocInstances(documentId, commencePath)
		if (identifiedDocInstances.size() == 0) {	
			try {					
				Connection conn = ConnectionManager.getConnection();	
				PreparedStatement selectIdentifiedDocumentInstanceStmt = conn.prepareStatement("SELECT id, server, volume, path, name, extension from Identified_Document_Instance where document_id = ? and volume + '/' + ISNULL(path,'') like ?");
				selectIdentifiedDocumentInstanceStmt.setInt(1, getDocument().getId());
				selectIdentifiedDocumentInstanceStmt.setString(2, getCommencePath().getActualPath() + "%");
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
				ConnectionManager.close();
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
						if (metadataExtractionRule.getSource().equals("File Path")) {
							if (identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getFullPath(), lookup, "LAST")))
								filePathCount++;
						}
						else if (metadataExtractionRule.getSource().equals("Content")) {
							if (identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getContent(), lookup, "FIRST")))
								contentCount++;
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
					if (metadataExtractionRule.getSource().equals("File Path")) {
						if (identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getFullPath(), metadataExtractionRule.getRegex(), metadataExtractionRule.getCapGroup(), "LAST")))
							filePathCount++;
					}
					else if (metadataExtractionRule.getSource().equals("Content")) {
						if (identifiedDocInstance.getMetadataValue().setValue(Util.findRegex(identifiedDocInstance.getContent(), metadataExtractionRule.getRegex(), metadataExtractionRule.getCapGroup(), "FIRST")))
							contentCount++;
					}
					else if (metadataExtractionRule.getSource().equals("Default")) {
						if (identifiedDocInstance.getMetadataValue().setValue(metadataExtractionRule.getDefaultValue()))
							defaultCount++;
					}
					
					if (!identifiedDocInstance.getMetadataValue().getValue().equals("")) {
						identifiedDocInstance.getMetadataValue().setMetadataExtractionRule(metadataExtractionRule);
						break;
					}					
				}
			}
		}
		

		if (filePathCount + contentCount < getIdentifiedDocInstances().size() * 0.8f)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Success Rate for File Path: " + Math.round(filePathCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Content:" + Math.round(contentCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Default: " +  + Math.round(defaultCount * 100.0f / getIdentifiedDocInstances().size()) + "%"));
		else
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Success Rate for File Path: " + Math.round(filePathCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Content:" + Math.round(contentCount * 100.0f / getIdentifiedDocInstances().size()) + "%, Default: " +  + Math.round(defaultCount * 100.0f / getIdentifiedDocInstances().size()) + "%"));
		
	}
	
	public void runAndSave() {
		preview();
		//TODO: put this into getIdentifiedDocInstance.save()
		
		if (useDefaultRule) {
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getCustomRules()) {
				getMetadataExtractionRules().getRules().getRemovedList().add(metadataExtractionRule);
				metadataExtractionRule.setNew(true);
			}
		}
		else {
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getDefaultRules()) {
				getMetadataExtractionRules().getRules().getRemovedList().add(metadataExtractionRule);
				metadataExtractionRule.setNew(true);
			}
		}
		
		try {
			Connection conn = ConnectionManager.getConnection();	
			
			PreparedStatement deleteMetadataExtractionRuleStmt = conn.prepareStatement("DELETE FROM Metadata_Extraction_Rule WHERE id = ?");
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules().getRemovedList()) {
				deleteMetadataExtractionRuleStmt.setInt(1, metadataExtractionRule.getId());
				deleteMetadataExtractionRuleStmt.addBatch();
			}
			deleteMetadataExtractionRuleStmt.executeBatch();
			
			getMetadataExtractionRules().getRules().getRemovedList().clear();
			
			PreparedStatement updateMetadataExtractionRuleStmt = conn.prepareStatement("UPDATE Metadata_Extraction_Rule SET source=?, bl_rule=?, regex=?, capturing_group=?, default_value=?, priority=? WHERE ID = ?");
			PreparedStatement insertMetadataExtractionRuleStmt = conn.prepareStatement("INSERT INTO Metadata_Extraction_Rule (commence_path_id,metadata_property_id,source,bl_rule,regex,capturing_group,default_value,priority,is_default) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (MetadataExtractionRule metadataExtractionRule : getMetadataExtractionRules().getRules()) {
				if (metadataExtractionRule.isNew()) {
					insertMetadataExtractionRuleStmt.setInt(1, getCommencePath().getId());
					insertMetadataExtractionRuleStmt.setInt(2, getMetadataExtractionRules().getMetadataProperty().getId());
					insertMetadataExtractionRuleStmt.setString(3, metadataExtractionRule.getSource());
					insertMetadataExtractionRuleStmt.setString(4, metadataExtractionRule.getBlRule());
					insertMetadataExtractionRuleStmt.setString(5, metadataExtractionRule.getRegex());
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
					updateMetadataExtractionRuleStmt.setString(2, metadataExtractionRule.getBlRule());
					updateMetadataExtractionRuleStmt.setString(3, metadataExtractionRule.getRegex());
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
			
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE identified_document_instance_id IN (SELECT id FROM Identified_Document_Instance WHERE document_id = ?) AND (metadata_extraction_rule_id IN (SELECT id FROM Metadata_Extraction_Rule WHERE commence_path_id = ? AND metadata_property_id = ?) OR metadata_extraction_rule_id NOT IN (SELECT id FROM Metadata_Extraction_Rule))");
			deleteIdentifiedDocInstanceStmt.setInt(1, getDocument().getId());
			deleteIdentifiedDocInstanceStmt.setInt(2, getCommencePath().getId());
			deleteIdentifiedDocInstanceStmt.setInt(3, getMetadataExtractionRules().getMetadataProperty().getId());
			deleteIdentifiedDocInstanceStmt.execute();
			
			PreparedStatement insertMetadataValueStmt = conn.prepareStatement("INSERT INTO Metadata_Value (identified_document_instance_id,value,metadata_extraction_rule_id) VALUES (?,?,?)");
			
			for (IdentifiedDocInstance identifiedDocInstance : getIdentifiedDocInstances()) {
				if (identifiedDocInstance.getMetadataValue().getMetadataExtractionRule() != null) {
					insertMetadataValueStmt.setLong(1, identifiedDocInstance.getId());
					insertMetadataValueStmt.setString(2, identifiedDocInstance.getMetadataValue().getValue());
					insertMetadataValueStmt.setInt(3, identifiedDocInstance.getMetadataValue().getMetadataExtractionRule().getId());
					insertMetadataValueStmt.addBatch();
				}
			}
			insertMetadataValueStmt.executeBatch();			
			
			
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Successful",  "Metadata Extraction Rules and Metadata Values are saved. ") );
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}	
	}
	
}
