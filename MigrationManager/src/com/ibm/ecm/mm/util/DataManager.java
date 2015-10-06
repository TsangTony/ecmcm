package com.ibm.ecm.mm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentificationRule;
import com.ibm.ecm.mm.model.Lookup;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataProperty;

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
			PreparedStatement selectIdentificationRuleStmt = conn.prepareStatement("SELECT id, operator_1, attribute, operator_2, value FROM Identification_Rule WHERE document_id = ? ORDER BY id");
			selectIdentificationRuleStmt.setInt(1, documentId);
			ResultSet selectIdentificationRuleRS = selectIdentificationRuleStmt.executeQuery();
			while (selectIdentificationRuleRS.next()) {					
				IdentificationRule identificationRule = new IdentificationRule();
				identificationRule.setId(selectIdentificationRuleRS.getInt(1));
				identificationRule.setLogicalOperator(selectIdentificationRuleRS.getString(2));
				identificationRule.setAttribute(selectIdentificationRuleRS.getString(3));
				identificationRule.setRelationalOperator(selectIdentificationRuleRS.getString(4));
				identificationRule.setValue(selectIdentificationRuleRS.getString(5));
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


	
}
