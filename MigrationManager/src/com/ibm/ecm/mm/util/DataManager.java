package com.ibm.ecm.mm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentificationRule;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.Lookup;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.model.MetadataValue;

public class DataManager {	
	
	public static ArrayList<Document> getDocuments() {		
		ArrayList<Document> documents = new ArrayList<Document>();		
		try {	
			Connection conn = ConnectionManager.getConnection();		
			PreparedStatement selectDocumentStmt = conn.prepareStatement(
					  "SELECT Document.id, Document.name, Document.bl_identification_rule,"
					+ "       Team.name, IG_Document_Class.name"
					+ "  FROM Document, Team, IG_Document_Class"
					+ " WHERE Document.team_id = Team.id"
					+ "   AND Document.ig_document_class_id = IG_Document_Class.id"
					+ "   AND Document.is_active=1"
					+ "   AND Document.is_cm_qualified=1"
					+ " ORDER BY Document.id");
			ResultSet selectDocumentRS = selectDocumentStmt.executeQuery();
			while (selectDocumentRS.next()) {					
				Document document = new Document();
				document.setId(selectDocumentRS.getInt(1));
				document.setName(selectDocumentRS.getString(2));	
				document.setBlIdentificationRule(selectDocumentRS.getString(3));
				document.setTeam(selectDocumentRS.getString(4));
				document.setIgDocClass(selectDocumentRS.getString(5));
				documents.add(document);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}		
		return documents;
	}
	
	public static DataTableArrayList<CommencePath> getCommencePaths(int documentId) {
		DataTableArrayList<CommencePath> commencePaths = new DataTableArrayList<CommencePath>(CommencePath.class);
		try {	
			Connection conn = ConnectionManager.getConnection();		
			PreparedStatement selectCommencePathStmt = conn.prepareStatement("SELECT id,business_path,actual_path FROM Commence_Path WHERE document_id = ? ORDER BY id");
			selectCommencePathStmt.setInt(1, documentId);
			ResultSet selectCommencePathRS = selectCommencePathStmt.executeQuery();
			while (selectCommencePathRS.next()) {					
				CommencePath commencePath = new CommencePath();
				commencePath.setId(selectCommencePathRS.getInt(1));
				commencePath.setBusinessPath(selectCommencePathRS.getString(2));
				commencePath.setActualPath(selectCommencePathRS.getString(3));
				commencePaths.add(commencePath);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}		
		return commencePaths;
	}
	
	public static DataTableArrayList<IdentificationRule> getIdentificationRules(int documentId) {
		DataTableArrayList<IdentificationRule> identificationRules = new DataTableArrayList<IdentificationRule>(IdentificationRule.class);
		try {	
			Connection conn = ConnectionManager.getConnection();		
			PreparedStatement selectIdentificationRuleStmt = conn.prepareStatement("SELECT id, operator_1, attribute, operator_2, value, left_paren, right_paren, priority FROM Identification_Rule WHERE document_id = ? ORDER BY id");
			selectIdentificationRuleStmt.setInt(1, documentId);
			ResultSet selectIdentificationRuleRS = selectIdentificationRuleStmt.executeQuery();
			while (selectIdentificationRuleRS.next()) {					
				IdentificationRule identificationRule = new IdentificationRule();
				identificationRule.setId(selectIdentificationRuleRS.getInt(1));
				identificationRule.setLogicalOperator(selectIdentificationRuleRS.getString(2));
				identificationRule.setAttribute(selectIdentificationRuleRS.getString(3));
				identificationRule.setRelationalOperator(selectIdentificationRuleRS.getString(4));
				identificationRule.setValue(selectIdentificationRuleRS.getString(5));
				identificationRule.setLeftParen(selectIdentificationRuleRS.getString(6));
				identificationRule.setRightParen(selectIdentificationRuleRS.getString(7));
				identificationRule.setPriority(selectIdentificationRuleRS.getInt(8));
				identificationRules.add(identificationRule);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}		
		return identificationRules;
	}
	
	public static ArrayList<MetadataProperty> getMetadataPropreties(int documentId) {
		ArrayList<MetadataProperty> metadataPropreties = new ArrayList<MetadataProperty>();
		try {	
			Connection conn = ConnectionManager.getConnection();		
			PreparedStatement selectMetadataPropertyStmt = conn.prepareStatement("SELECT Metadata_Property.id, Metadata_Property.name FROM Document LEFT JOIN Document_Class ON Document.document_class_id = Document_Class.id LEFT JOIN [DC-MP] ON Document_Class.id = [DC-MP].document_class_id LEFT JOIN Metadata_Property ON [DC-MP].metadata_property_id = Metadata_Property.id WHERE document.id = ? ORDER BY Metadata_Property.id");
			selectMetadataPropertyStmt.setInt(1, documentId);
			ResultSet selectMetadataPropertyRS = selectMetadataPropertyStmt.executeQuery();
			while (selectMetadataPropertyRS.next()) {					
				MetadataProperty metadataProperty = new MetadataProperty();
				metadataProperty.setId(selectMetadataPropertyRS.getInt(1));
				metadataProperty.setName(selectMetadataPropertyRS.getString(2));
				metadataPropreties.add(metadataProperty);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
		return metadataPropreties;
	}
	
	public static DataTableArrayList<MetadataExtractionRule> getMetadataExtractionRules(int commencePathId, int metadataPropertyId) {
		DataTableArrayList<MetadataExtractionRule> metadataExtractionRules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
		try {	
			Connection conn = ConnectionManager.getConnection();		
			PreparedStatement selectMetadataExtractionRulesStmt = conn.prepareStatement("SELECT id, priority, source, bl_rule, regex, capturing_group, default_value, is_default FROM Metadata_Extraction_Rule WHERE commence_path_id = ? AND metadata_property_id = ? ORDER BY priority");
			selectMetadataExtractionRulesStmt.setInt(1, commencePathId);
			selectMetadataExtractionRulesStmt.setInt(2, metadataPropertyId);
			ResultSet selectMetadataExtractionRulesRS = selectMetadataExtractionRulesStmt.executeQuery();
			while (selectMetadataExtractionRulesRS.next()) {					
				MetadataExtractionRule metadataExtractionRule = new MetadataExtractionRule();
				metadataExtractionRule.setId(selectMetadataExtractionRulesRS.getInt(1));
				metadataExtractionRule.setPriority(selectMetadataExtractionRulesRS.getInt(2));
				metadataExtractionRule.setSource(selectMetadataExtractionRulesRS.getString(3));
				metadataExtractionRule.setBlRule(selectMetadataExtractionRulesRS.getString(4));
				metadataExtractionRule.setRegex(selectMetadataExtractionRulesRS.getString(5));
				metadataExtractionRule.setCapGroup(selectMetadataExtractionRulesRS.getInt(6));
				metadataExtractionRule.setDefaultValue(selectMetadataExtractionRulesRS.getString(7));
				metadataExtractionRule.setDefault(selectMetadataExtractionRulesRS.getBoolean(8));
				metadataExtractionRules.add(metadataExtractionRule);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}		
		return metadataExtractionRules;
	}
	
	public static ArrayList<Lookup> getLookups(int metadataPropertyId) {
		ArrayList<Lookup> lookups = new ArrayList<Lookup>();
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement selectLookupStmt = conn.prepareStatement("SELECT lookup_value, returned_value FROM Lookup WHERE metadata_property_id = ?");
			selectLookupStmt.setInt(1, metadataPropertyId);				
			ResultSet lookupRs = selectLookupStmt.executeQuery();						
			while (lookupRs.next()) {
				Lookup lookup = new Lookup();
				lookup.setLookupValue(lookupRs.getString(1));
				lookup.setReturnedValue(lookupRs.getString(2));
				lookups.add(lookup);
			}			
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
		return lookups;
	}
	
	public static void removeMetadataValues(int documentId, int metadataPropertyId) {
		ArrayList<Integer> metadataPropertyIds = new ArrayList<Integer>();
		metadataPropertyIds.add(Integer.valueOf(metadataPropertyId));
		removeMetadataValues(documentId, metadataPropertyIds);
	}
	
	public static void removeMetadataValues(int documentId, int commencePathId, int metadataPropertyId) {
		ArrayList<Integer> metadataPropertyIds = new ArrayList<Integer>();
		metadataPropertyIds.add(Integer.valueOf(metadataPropertyId));
		removeMetadataValues(documentId, commencePathId, metadataPropertyIds);
	}
	
	public static void removeMetadataValues(int documentId, ArrayList<Integer> metadataPropertyIds) {
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE document_id = ? AND (metadata_extraction_rule_id IN (SELECT id FROM Metadata_Extraction_Rule WHERE metadata_property_id IN (?)) OR metadata_extraction_rule_id NOT IN (SELECT id FROM Metadata_Extraction_Rule))");
			deleteIdentifiedDocInstanceStmt.setInt(1, documentId);	
			
			String metadataPropertyIdList = "" + metadataPropertyIds.get(0);			
			for (int i=1; i < metadataPropertyIds.size(); i++)
				metadataPropertyIdList += "," + metadataPropertyIds.get(i);
			
			deleteIdentifiedDocInstanceStmt.setString(2, metadataPropertyIdList);
			deleteIdentifiedDocInstanceStmt.execute();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
	}
	
	public static void removeMetadataValues(int documentId, int commencePathId, ArrayList<Integer> metadataPropertyIds) {
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE document_id = ? AND (metadata_extraction_rule_id IN (SELECT id FROM Metadata_Extraction_Rule WHERE commence_path_id = ? AND metadata_property_id IN (?)) OR metadata_extraction_rule_id NOT IN (SELECT id FROM Metadata_Extraction_Rule))");
			deleteIdentifiedDocInstanceStmt.setInt(1, documentId);
			deleteIdentifiedDocInstanceStmt.setInt(2, commencePathId);
			
			String metadataPropertyIdList = "" + metadataPropertyIds.get(0);			
			for (int i=1; i < metadataPropertyIds.size(); i++)
				metadataPropertyIdList += "," + metadataPropertyIds.get(i);
			
			deleteIdentifiedDocInstanceStmt.setString(3, metadataPropertyIdList);
			deleteIdentifiedDocInstanceStmt.execute();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
	}
	
	public static void addMetadataValues(ArrayList<IdentifiedDocInstance> identifiedDocInstances, int documentId) {
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement insertMetadataValueStmt = conn.prepareStatement("INSERT INTO Metadata_Value (identified_document_instance_id,value,metadata_extraction_rule_id,document_id) VALUES (?,?,?,?)");
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				
				for (MetadataValue metadataValue : identifiedDocInstance.getMetadataValues()) {
					if (metadataValue.getMetadataExtractionRule() != null) {
						insertMetadataValueStmt.setLong(1, identifiedDocInstance.getId());
						insertMetadataValueStmt.setString(2, metadataValue.getValue());
						insertMetadataValueStmt.setInt(3, metadataValue.getMetadataExtractionRule().getId());
						insertMetadataValueStmt.setInt(4, documentId);					
						insertMetadataValueStmt.addBatch();
					}
				}
				
				
			}
			insertMetadataValueStmt.executeBatch();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
	}

	
	public static ArrayList<String> getLogicalOperators() {
		ArrayList<String> logicalOperators = new ArrayList<String>();
		logicalOperators.add("And");
		logicalOperators.add("Or");
		return logicalOperators;
	}
	
	public static ArrayList<String> getAttributes() {
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("Filename");
		attributes.add("File Path");
		attributes.add("Extension");
		return attributes;
	}

	public static ArrayList<String> getRelationalOperators() {
		ArrayList<String> relationalOperators = new ArrayList<String>();
		relationalOperators.add("Contains");
		relationalOperators.add("Not contains");
		relationalOperators.add("Equals");
		relationalOperators.add("Not equals");
		return relationalOperators;
	}

	public static ArrayList<String> getSources() {
		ArrayList<String> sources = new ArrayList<String>();
		sources.add("File Path");
		sources.add("Content");
		sources.add("Default");
		return sources;
	}
	
	public static ArrayList<IdentifiedDocInstance> getIdentifiedDocInstances(Document document) {
		return getIdentifiedDocInstances(document,null);
	}
	

	public static ArrayList<IdentifiedDocInstance> getIdentifiedDocInstances(Document document, CommencePath commencePath) {
		ArrayList<IdentifiedDocInstance> identifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
		try {					
			Connection conn = ConnectionManager.getConnection();
			
			String query = "SELECT id, server, volume, path, name, extension from Identified_Document_Instance where document_id = ?";
			
			
			
			PreparedStatement selectIdentifiedDocumentInstanceStmt = conn.prepareStatement(query);
			selectIdentifiedDocumentInstanceStmt.setInt(1, document.getId());
			if (commencePath != null) {
				query += " AND volume + '/' + ISNULL(path,'') LIKE ?";
				selectIdentifiedDocumentInstanceStmt.setString(2, commencePath.getActualPath() + "%");
			}
			ResultSet rs = selectIdentifiedDocumentInstanceStmt.executeQuery();				
			
			while (rs.next()) {
				IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
				identifiedDocInstance.setId(rs.getLong(1));
				identifiedDocInstance.setServer(rs.getString(2));
				identifiedDocInstance.setVolume(rs.getString(3));
				identifiedDocInstance.setPath(rs.getString(4));
				identifiedDocInstance.setName(rs.getString(5));	
				identifiedDocInstance.setExtension(rs.getString(6));
				identifiedDocInstance.setCommencePath(commencePath);
				identifiedDocInstance.setNew(false);
				identifiedDocInstances.add(identifiedDocInstance);
			}
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
		return identifiedDocInstances;
	}

	public static ArrayList<String> getLeftParens() {
		ArrayList<String> leftParens = new ArrayList<String>();
		leftParens.add("(");
		leftParens.add("((");
		leftParens.add("(((");
		return leftParens;
	}

	public static ArrayList<String> getRightParens() {
		ArrayList<String> rightParens = new ArrayList<String>();
		rightParens.add(")");
		rightParens.add("))");
		rightParens.add(")))");
		return rightParens;
	}

	public static void removeMetadataExtractionRule(ArrayList<MetadataExtractionRule> metadataExtractionRules) {
		try {					
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement deleteMetadataExtractionRuleStmt = conn.prepareStatement("DELETE FROM Metadata_Extraction_Rule WHERE id = ?");
			for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules) {
				deleteMetadataExtractionRuleStmt.setInt(1, metadataExtractionRule.getId());
				deleteMetadataExtractionRuleStmt.addBatch();
			}
			deleteMetadataExtractionRuleStmt.executeBatch();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
	}
	
	public static void updateMetadataExtractionRule(ArrayList<MetadataExtractionRule> metadataExtractionRules) {
		try {					
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement updateMetadataExtractionRuleStmt = conn.prepareStatement("UPDATE Metadata_Extraction_Rule SET source=?, bl_rule=?, regex=?, capturing_group=?, default_value=?, priority=? WHERE ID = ?");
			for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules) {
				updateMetadataExtractionRuleStmt.setString(1, metadataExtractionRule.getSource());
				updateMetadataExtractionRuleStmt.setString(2, metadataExtractionRule.getBlRule());
				updateMetadataExtractionRuleStmt.setString(3, metadataExtractionRule.getRegex());
				updateMetadataExtractionRuleStmt.setInt(4, metadataExtractionRule.getCapGroup());
				updateMetadataExtractionRuleStmt.setString(5, metadataExtractionRule.getDefaultValue());
				updateMetadataExtractionRuleStmt.setInt(6, metadataExtractionRule.getPriority());
				updateMetadataExtractionRuleStmt.setInt(7, metadataExtractionRule.getId());
				updateMetadataExtractionRuleStmt.addBatch();
			}
			updateMetadataExtractionRuleStmt.executeBatch();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
	}
	

	
	public static void addMetadataExtractionRule(ArrayList<MetadataExtractionRule> metadataExtractionRules, int commencePathId, int metadataPropertyId, boolean isDefault) {
		try {					
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement insertMetadataExtractionRuleStmt = conn.prepareStatement("INSERT INTO Metadata_Extraction_Rule (commence_path_id,metadata_property_id,source,bl_rule,regex,capturing_group,default_value,priority,is_default) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules) {
				insertMetadataExtractionRuleStmt.setInt(1, commencePathId);
				insertMetadataExtractionRuleStmt.setInt(2, metadataPropertyId);
				insertMetadataExtractionRuleStmt.setString(3, metadataExtractionRule.getSource());
				insertMetadataExtractionRuleStmt.setString(4, metadataExtractionRule.getBlRule());
				insertMetadataExtractionRuleStmt.setString(5, metadataExtractionRule.getRegex());
				insertMetadataExtractionRuleStmt.setInt(6, metadataExtractionRule.getCapGroup());
				insertMetadataExtractionRuleStmt.setString(7, metadataExtractionRule.getDefaultValue());
				insertMetadataExtractionRuleStmt.setInt(8, metadataExtractionRule.getPriority());
				insertMetadataExtractionRuleStmt.setBoolean(9, isDefault);
				insertMetadataExtractionRuleStmt.execute();
				
				ResultSet metadataExtractionRuleIdRs = insertMetadataExtractionRuleStmt.getGeneratedKeys();
				if (metadataExtractionRuleIdRs != null && metadataExtractionRuleIdRs.next()) {
					metadataExtractionRule.setId(metadataExtractionRuleIdRs.getInt(1));
				}
			}
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
		
	}
	
}
