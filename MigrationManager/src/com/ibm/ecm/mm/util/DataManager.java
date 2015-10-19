package com.ibm.ecm.mm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

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
			String query = ""
					+ "SELECT metadata_property.id, "
					+ "       metadata_property.NAME "
					+ "FROM   document "
					+ "       LEFT JOIN document_class DC1 "
					+ "              ON document.document_class_id = DC1.id "
					+ "       LEFT JOIN document_class DC2 "
					+ "			  ON DC1.parent_id = DC2.id "
					+ "       LEFT JOIN document_class DC3 "
					+ "			  ON DC2.parent_id = DC3.id "
					+ "       LEFT JOIN [dc-mp] "
					+ "              ON DC1.id = [dc-mp].document_class_id "
					+ "              OR DC2.id = [dc-mp].document_class_id "
					+ "              OR DC3.id = [dc-mp].document_class_id "
					+ "       LEFT JOIN metadata_property "
					+ "              ON [dc-mp].metadata_property_id = metadata_property.id "
					+ "WHERE  document.id = ? "
					+ "ORDER  BY metadata_property.id";
			PreparedStatement selectMetadataPropertyStmt = conn.prepareStatement(query);
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
		attributes.add("Content");
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
	
	public static DataTableArrayList<IdentifiedDocInstance> getIdentifiedDocInstances(Document document) {
		return getIdentifiedDocInstances(document,null);
	}	

	public static DataTableArrayList<IdentifiedDocInstance> getIdentifiedDocInstances(Document document, CommencePath commencePath) {
		DataTableArrayList<IdentifiedDocInstance> identifiedDocInstances = new DataTableArrayList<IdentifiedDocInstance>(IdentifiedDocInstance.class);
		try {					
			Connection conn = ConnectionManager.getConnection();
			
			String query = "SELECT id, server, volume, path, name, extension from Identified_Document_Instance where document_id = ?";
			if (commencePath != null)
				query += " AND volume + '/' + ISNULL(path,'') LIKE ?";
			
			PreparedStatement selectIdentifiedDocumentInstanceStmt = conn.prepareStatement(query);
			selectIdentifiedDocumentInstanceStmt.setInt(1, document.getId());
			if (commencePath != null)
				selectIdentifiedDocumentInstanceStmt.setString(2, commencePath.getActualPath() + "%");
			
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

	public static ArrayList<Document> getDocumentStatusReport() {
		ArrayList<Document> documents = new ArrayList<Document>();
		try {	
			Connection conn = ConnectionManager.getConnection();	
			
			String query = ""
					+ "SELECT document.id, "
					+ "       document.NAME, "
					+ "       team.NAME, "
					+ "       Isnull(S1.count, 0)                   AS S1, "
					+ "       Isnull(S1Del.count, 0)                AS S1Del, "
					+ "       Isnull(S2New.count, 0)                AS S2New, "
					+ "       metadata_property.id                  AS MPID, "
					+ "       metadata_property.NAME                AS MP, "
					+ "       Isnull(S1XMP.metadata_property_id, 0) AS S1XMP, "
					+ "       Isnull(S2XMP.metadata_property_id, 0) AS S2XMP "
					+ "FROM   document "
					+ "       LEFT JOIN team "
					+ "              ON document.team_id = team.id "
					+ "       LEFT JOIN (SELECT document_id, "
					+ "                         Count(*) AS count "
					+ "                  FROM   identified_document_instance "
					+ "                  WHERE  snapshot = 1 "
					+ "                  GROUP  BY document_id) S1 "
					+ "              ON document.id = S1.document_id "
					+ "       LEFT JOIN (SELECT document_id, "
					+ "                         Count(*) AS count "
					+ "                  FROM   identified_document_instance "
					+ "                  WHERE  snapshot = 1 "
					+ "                         AND snapshot_deleted = 2 "
					+ "                  GROUP  BY document_id) S1Del "
					+ "              ON document.id = S1Del.document_id "
					+ "       LEFT JOIN (SELECT document_id, "
					+ "                         Count(*) AS count "
					+ "                  FROM   identified_document_instance "
					+ "                  WHERE  snapshot = 2 "
					+ "                  GROUP  BY document_id) S2New "
					+ "              ON document.id = S2New.document_id "
					+ "       LEFT JOIN document_class DC1 "
					+ "              ON document.document_class_id = DC1.id "
					+ "       LEFT JOIN document_class DC2 "
					+ "			  ON DC1.parent_id = DC2.id "
					+ "       LEFT JOIN document_class DC3 "
					+ "			  ON DC2.parent_id = DC3.id "
					+ "       LEFT JOIN [dc-mp] "
					+ "              ON DC1.id = [dc-mp].document_class_id "
					+ "              OR DC2.id = [dc-mp].document_class_id "
					+ "              OR DC3.id = [dc-mp].document_class_id "
					+ "       LEFT JOIN metadata_property "
					+ "              ON [dc-mp].metadata_property_id = metadata_property.id "
					+ "       LEFT JOIN (SELECT metadata_value.document_id, "
					+ "                         metadata_extraction_rule.metadata_property_id "
					+ "                  FROM   metadata_value, "
					+ "                         metadata_extraction_rule, "
					+ "                         identified_document_instance "
					+ "                  WHERE  metadata_value.metadata_extraction_rule_id = "
					+ "                         metadata_extraction_rule.id "
					+ "                         AND metadata_value.identified_document_instance_id = "
					+ "                             identified_document_instance.id "
					+ "                         AND identified_document_instance.snapshot = 1 "
					+ "                  GROUP  BY metadata_value.document_id, "
					+ "                            metadata_extraction_rule.metadata_property_id) S1XMP "
					+ "              ON document.id = S1XMP.document_id "
					+ "                 AND metadata_property.id = S1XMP.metadata_property_id "
					+ "       LEFT JOIN (SELECT metadata_value.document_id, "
					+ "                         metadata_extraction_rule.metadata_property_id "
					+ "                  FROM   metadata_value, "
					+ "                         metadata_extraction_rule, "
					+ "                         identified_document_instance "
					+ "                  WHERE  metadata_value.metadata_extraction_rule_id = "
					+ "                         metadata_extraction_rule.id "
					+ "                         AND metadata_value.identified_document_instance_id = "
					+ "                             identified_document_instance.id "
					+ "                         AND identified_document_instance.snapshot = 2 "
					+ "                  GROUP  BY metadata_value.document_id, "
					+ "                            metadata_extraction_rule.metadata_property_id) S2XMP "
					+ "              ON document.id = S2XMP.document_id "
					+ "                 AND metadata_property.id = S2XMP.metadata_property_id "
					+ "ORDER  BY document.id ";

			PreparedStatement stmt = conn.prepareStatement(query);
			System.out.println(query);
			ResultSet rs = stmt.executeQuery();
			Document preDoc = null;
			while (rs.next()) {
				Document doc = null;
				if (preDoc == null || rs.getInt(1) != preDoc.getId()) {
					doc = new Document();
					doc.setId(rs.getInt(1));
					doc.setName(rs.getString(2));
					doc.setTeam(rs.getString(3));
					doc.setS1(rs.getInt(4));
					doc.setS1Deleted(rs.getInt(5));
					doc.setS2New(rs.getInt(6));
					documents.add(doc);
					preDoc=doc;
				}
				else 
					doc = preDoc;
				
				MetadataProperty metadataProperty = new MetadataProperty();
				metadataProperty.setId(rs.getInt(7));
				metadataProperty.setName(rs.getString(8));
				metadataProperty.getExtracted().add(rs.getInt(9) != 0);
				metadataProperty.getExtracted().add(rs.getInt(10) != 0);
				doc.getMetadataProperties().add(metadataProperty);				
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
	
	public static void addSnippet(int documentId, ArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		try {
			Connection conn = ConnectionManager.getConnection();
			
			PreparedStatement insertIdentifiedDocInstancStmt = conn.prepareStatement("INSERT INTO Identified_Document_Instance_Snippet (id,name,path,volume,extension,server,document_id,snippet,snapshot_deleted) SELECT id,name,path,volume,extension,server,?,?,snapshot_deleted FROM All_Document_Instance WHERE id = ?");
			
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				insertIdentifiedDocInstancStmt.setInt(1, documentId);
				insertIdentifiedDocInstancStmt.setString(2, identifiedDocInstance.getSnippet());
				insertIdentifiedDocInstancStmt.setLong(3, identifiedDocInstance.getId());
				insertIdentifiedDocInstancStmt.addBatch();
			}
			
			insertIdentifiedDocInstancStmt.executeBatch();		
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}	
	}

	public static HashSet<Long> addIdentifiedDocInstances(int documentId, HashSet<Long> existingIdentifiedDocInstanceIds, DataTableArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		HashSet<Long> newIds = new HashSet<Long>();
		
		try {
			Connection conn = ConnectionManager.getConnection();			
			PreparedStatement insertIdentifiedDocInstancStmt = conn.prepareStatement("INSERT INTO Identified_Document_Instance (id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,document_id) SELECT id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,? FROM All_Document_Instance WHERE id = ?");
			
			ArrayList<IdentifiedDocInstance> fullIdentifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
			fullIdentifiedDocInstances.addAll(identifiedDocInstances);
			fullIdentifiedDocInstances.addAll(identifiedDocInstances.getRemovedList());
						
			for (IdentifiedDocInstance identifiedDocInstance : fullIdentifiedDocInstances) {
				if (!existingIdentifiedDocInstanceIds.contains(identifiedDocInstance.getId())) {
					insertIdentifiedDocInstancStmt.setInt(1, documentId);
					insertIdentifiedDocInstancStmt.setLong(2, identifiedDocInstance.getId());
					insertIdentifiedDocInstancStmt.addBatch();
				}
				newIds.add(identifiedDocInstance.getId());
			}
			
			HashSet<Long> removedIdentifiedDocInstanceIds = new HashSet<Long>();
			removedIdentifiedDocInstanceIds.addAll(existingIdentifiedDocInstanceIds);
			removedIdentifiedDocInstanceIds.removeAll(newIds);		
			
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Identified_Document_Instance WHERE document_id = ? AND id = ?");
			deleteIdentifiedDocInstanceStmt.setInt(1, documentId);
			PreparedStatement deleteMetadataValueStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE document_id = ? AND identified_document_instance_id = ?");
			deleteMetadataValueStmt.setInt(1, documentId);
			for (Long removedIdentifiedDocInstanceId : removedIdentifiedDocInstanceIds) {
				deleteIdentifiedDocInstanceStmt.setLong(2, removedIdentifiedDocInstanceId);
				deleteIdentifiedDocInstanceStmt.addBatch();
				deleteMetadataValueStmt.setLong(2, removedIdentifiedDocInstanceId);
				deleteMetadataValueStmt.addBatch();
			}
			
			insertIdentifiedDocInstancStmt.executeBatch();		
			deleteMetadataValueStmt.executeBatch();		
			deleteIdentifiedDocInstanceStmt.executeBatch();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
		
		return newIds;		
	}
	
	public static DataTableArrayList<IdentifiedDocInstance> getDocInstances(Document document, boolean noPdf, boolean fromSnippet, boolean skipIdentificationRules) throws SQLException {
		DataTableArrayList<IdentifiedDocInstance> identifiedDocInstances = new DataTableArrayList<IdentifiedDocInstance>(IdentifiedDocInstance.class);		
		String query = "SELECT ";		
		
		if (fromSnippet)
			query += "id, name, path, volume, digest, extension, server, snapshot_deleted FROM Identified_Document_Instance_Snippet WHERE document_id=" + document.getId() + " ";
		else {		
			query += "id, name, path, volume, digest, extension, server, snapshot_deleted FROM All_Document_Instance WHERE (";
			for (CommencePath commencePath : document.getCommencePaths()) {
				query += "volume + '/' + ISNULL(path,'') like '" + commencePath.getActualPath() + "/%' OR ";
				query += "volume + '/' + ISNULL(path,'') = '" + commencePath.getActualPath() + "' OR ";
			}		
			query += "1=0) ";
			
			//TODO: IG rules from DB
			
			//extension		
			query += "AND extension in ('doc','docx','dot','docm','dotx','dotm','docb',"
				  +  "'xls','xlt','xlm','xlsx','xlsm','xltx','xltm','xlsb','xla','xll',"
				  +  "'xlw','ppt','pot','pps','pptx','pptm','potx','potm','ppam','ppsx',"
				  +  "'ppsm','sldx','sldm','VSD','VST','VSW','VDX','VSX','VTX','VSDX',"
				  +  "'VSDM','VSSX','VSSM','VSTX','VSTM','VSL','pdf','csv'";
			
			if (document.getTeam().contains("Training")) {
				query += ",'afc','wav','mp3','aif','rm','mid','aob','3gp','aiff','aac',"
					  +  "'ape','au','flac','m4a','m4p','ra','raw','wma','mkv','flv','vob',"
					  +  "'avi','mov','qt','wmv','rmvb','asf','mpg','mpeg','mp4',',mpe',"
					  +  "'mpv','m2v','m4v','3g2','mxf','jpg','jpeg','tif','tiff','gif',"
					  +  "'bmp','png','img','psd','cpt','ai','svg','ico'";
			}
			query += ") ";

			//retention
			if (document.getIgDocClass().equals("General Document"))
				query += "AND mtime > DATEADD(YEAR,-7,GETDATE())";
			
			if (noPdf)
				query += " ORDER BY server, volume, path, name";
		}

		
		if (!skipIdentificationRules && document.getIdentificationRules().size() != 0) {
			query += " AND (";
			for (IdentificationRule identificationRule : document.getIdentificationRules()) {
				if (identificationRule.getPriority() != 1 && identificationRule.getLogicalOperator() != null)
					query += identificationRule.getLogicalOperator() + " ";
				
				query += identificationRule.getLeftParen();
				
				if (identificationRule.getAttribute().equals("Filename"))
					query += "REPLACE([name],'.'+extension,'') ";
				else if (identificationRule.getAttribute().equals("File Path"))
					query += "ISNULL(path,'') ";
				else if (identificationRule.getAttribute().equals("Extension"))
					query += "extension ";
				else if (identificationRule.getAttribute().equals("Content"))
					query += "snippet ";
								
				if (identificationRule.getRelationalOperator().equals("Equals"))
					query += "= '" + identificationRule.getValue() + "'";
				else if (identificationRule.getRelationalOperator().equals("Not equals"))				
					query += "!= '" + identificationRule.getValue() + "'";
				else if (identificationRule.getRelationalOperator().equals("Contains"))
					query += "LIKE '%" + identificationRule.getValue() + "%'";
				else if (identificationRule.getRelationalOperator().equals("Not contains"))
					query += "NOT LIKE '%" + identificationRule.getValue() + "%'";	
				
				query += identificationRule.getRightParen() + " ";
			}

			query += ") ";
		}		
		
		try {		
			Connection conn = ConnectionManager.getConnection();	
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);	
			
			HashSet<String> digests = new HashSet<String>();
			boolean firstRecord = false;
			
			while (rs.next()) {
				String digest = rs.getString(5);
				if (fromSnippet || !digests.contains(digest)) {				
					IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
					identifiedDocInstance.setId(rs.getLong(1));	
					identifiedDocInstance.setName(rs.getNString(2));	
					identifiedDocInstance.setPath(rs.getNString(3));
					identifiedDocInstance.setVolume(rs.getString(4));
					identifiedDocInstance.setDigest(digest);
					identifiedDocInstance.setExtension(rs.getString(6));
					identifiedDocInstance.setServer(rs.getString(7));					
					
					//Ignore PDF
					if (!fromSnippet && noPdf) {
						if (!firstRecord && identifiedDocInstance.getExtension().toUpperCase().equals("PDF")) {
							IdentifiedDocInstance preInstance = identifiedDocInstances.get(identifiedDocInstances.size()-1);
							if (preInstance.getServer().equals(identifiedDocInstance.getServer()) &&
							    preInstance.getVolume().equals(identifiedDocInstance.getVolume()) &&
							    preInstance.getPath().equals(identifiedDocInstance.getPath()) &&
							    preInstance.getNameWithoutExtension().equals(identifiedDocInstance.getNameWithoutExtension())) {
								   continue;
							}
						}
					}
					
					for (CommencePath commencePath : document.getCommencePaths()) {
						String fullPath = identifiedDocInstance.getPath() == null ? identifiedDocInstance.getVolume() : identifiedDocInstance.getVolume() + "/" + identifiedDocInstance.getPath();
						if (fullPath.equals(commencePath.getActualPath()) || fullPath.startsWith(commencePath.getActualPath() + "/")) {
							identifiedDocInstance.setCommencePath(commencePath);
						}
					}
					
					/* Only doc instances without snapshot deleted will be shown in preview
					 * but all will be identified and moved to Identified_Doc Instance
					 */
					
					if (rs.getInt(8) == 0) 
						identifiedDocInstances.add(identifiedDocInstance);
					else
						identifiedDocInstances.getRemovedList().add(identifiedDocInstance);
					
					digests.add(digest);
				}			
			}
			firstRecord = true;
		}
		catch (SQLException e) {
			System.err.println(e.getMessage() + " - " + query);
			throw e;
		}
		finally {
			ConnectionManager.close();
		}
		
		return identifiedDocInstances;
	}

	public static void removeSnippet(int documentId) {
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement deleteSnippetStmt = conn.prepareStatement("DELETE FROM Identified_Document_Instance_Snippet WHERE document_id = ?");
			deleteSnippetStmt.setInt(1, documentId);	
			deleteSnippetStmt.execute();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
	}

	
}
