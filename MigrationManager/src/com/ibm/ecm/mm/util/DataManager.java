package com.ibm.ecm.mm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.DataTableArrayList;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.DocumentClass;
import com.ibm.ecm.mm.model.DocumentInstancePair;
import com.ibm.ecm.mm.model.IdentificationRule;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;
import com.ibm.ecm.mm.model.Lookup;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.model.MetadataValue;

public class DataManager {	
	
	public static ArrayList<Document> getDocuments() {		
		ArrayList<Document> documents = new ArrayList<Document>();		
		try {	
			Connection conn = ConnectionManager.getConnection("getDocuments");	
			String query = ""
					+ "SELECT document.id, "
					+ "       document.NAME, "
					+ "       document.bl_identification_rule, "
					+ "       team.NAME, "
					+ "       ig_document_class.NAME, "
					+ "       ig_security_class.NAME, "
					+ "       document.is_no_pdf, "
					+ "       document.is_office_doc, "
					+ "       document.include_linked_file "
					+ "FROM   document "
					+ "left join team "
					+ "on document.team_id = team.id "
					+ "left join ig_document_class "
					+ "on  document.ig_document_class_id = ig_document_class.id "
					+ "left join ig_security_class "
					+ "on  document.ig_security_class_id = ig_security_class.id "
					+ "  where "
					+ "       document.is_active = 1 "
					+ "       AND document.is_cm_qualified = 1 "
					+ "ORDER  BY document.id";

			PreparedStatement selectDocumentStmt = conn.prepareStatement(query);
			ResultSet selectDocumentRS = selectDocumentStmt.executeQuery();
			while (selectDocumentRS.next()) {					
				Document document = new Document();
				document.setId(selectDocumentRS.getInt(1));
				document.setName(selectDocumentRS.getString(2));	
				document.setBlIdentificationRule(selectDocumentRS.getString(3));
				document.setTeam(selectDocumentRS.getString(4));
				document.setIgDocClass(selectDocumentRS.getString(5));
				document.setIgSecClass(selectDocumentRS.getString(6));
				document.setNoPdf(selectDocumentRS.getBoolean(7));
				document.setOfficeDoc(selectDocumentRS.getBoolean(8));
				document.setIncludeLinkedFile(selectDocumentRS.getBoolean(9));
				documents.add(document);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getDocuments");
		}		
		return documents;
	}
	
	public static DataTableArrayList<CommencePath> getCommencePaths(int documentId) {
		DataTableArrayList<CommencePath> commencePaths = new DataTableArrayList<CommencePath>(CommencePath.class);
		try {	
			Connection conn = ConnectionManager.getConnection("getCommencePaths");		
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
			ConnectionManager.close("getCommencePaths");
		}		
		return commencePaths;
	}
	
	public static DataTableArrayList<IdentificationRule> getIdentificationRules(int documentId) {
		DataTableArrayList<IdentificationRule> identificationRules = new DataTableArrayList<IdentificationRule>(IdentificationRule.class);
		try {	
			Connection conn = ConnectionManager.getConnection("getIdentificationRules");		
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
			ConnectionManager.close("getIdentificationRules");
		}		
		return identificationRules;
	}
	
	public static ArrayList<MetadataProperty> getMetadataPropreties(int documentId) {
		ArrayList<MetadataProperty> metadataPropreties = new ArrayList<MetadataProperty>();
		try {	
			Connection conn = ConnectionManager.getConnection("getMetadataPropreties");		
			String query = ""
					+ "SELECT metadata_property.id, "
					+ "       metadata_property.NAME "
					+ "FROM   metadata_property "
					+ "       LEFT JOIN [D_MP] "
					+ "              ON [D_MP].metadata_property_id = metadata_property.id "
					+ "       LEFT JOIN Document "
					+ "              ON Document.id = [D_MP].document_id "
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
			ConnectionManager.close("getMetadataPropreties");
		}
		return metadataPropreties;
	}
	
	public static DataTableArrayList<MetadataExtractionRule> getMetadataExtractionRules(int documentId, int commencePathId, int metadataPropertyId) {
		DataTableArrayList<MetadataExtractionRule> metadataExtractionRules = new DataTableArrayList<MetadataExtractionRule>(MetadataExtractionRule.class);
		try {	
			Connection conn = ConnectionManager.getConnection("getMetadataExtractionRules");	
			String query = ""
					+ "SELECT id, "
					+ "       priority, "
					+ "       source, "
					+ "       bl_rule, "
					+ "       regex, "
					+ "       capturing_group, "
					+ "       default_value, "
					+ "       is_default "
					+ "FROM   metadata_extraction_rule "
					+ "WHERE  ";
			
			if (commencePathId == 0)
				query += "    commence_path_id IN (SELECT id FROM Commence_Path WHERE document_id = ?) ";
			else
				query += "    commence_path_id = ?";
			
			if (metadataPropertyId != 0)
				query  += " AND  metadata_property_id = ? ";
			
			query  += " ORDER  BY metadata_property_id, priority";
			
			PreparedStatement selectMetadataExtractionRulesStmt = conn.prepareStatement(query);
			if (commencePathId == 0)
				selectMetadataExtractionRulesStmt.setInt(1, documentId);
			else
				selectMetadataExtractionRulesStmt.setInt(1, commencePathId);
			
			if (metadataPropertyId != 0)
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
			ConnectionManager.close("getMetadataExtractionRules");
		}		
		return metadataExtractionRules;
	}
	
	public static ArrayList<Lookup> getLookups(int metadataPropertyId) {
		ArrayList<Lookup> lookups = new ArrayList<Lookup>();
		try {
			Connection conn = ConnectionManager.getConnection("getLookups");
			PreparedStatement selectLookupStmt = conn.prepareStatement("SELECT lookup_value, returned_value FROM Lookup WHERE metadata_property_id = ? ORDER BY id");
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
			ConnectionManager.close("getLookups");
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
			Connection conn = ConnectionManager.getConnection("removeMetadataValues");
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
			ConnectionManager.close("removeMetadataValues");
		}
	}
	
	public static void removeMetadataValues(int documentId, int commencePathId, ArrayList<Integer> metadataPropertyIds) {
		try {
			Connection conn = ConnectionManager.getConnection("removeMetadataValues");
			String query = ""
					+ "DELETE FROM metadata_value "
					+ "WHERE  document_id = ? "
					+ "       AND ( metadata_extraction_rule_id IN (SELECT id "
					+ "                                             FROM   metadata_extraction_rule "
					+ "                                            WHERE   metadata_property_id IN ( ? ) ";
			
			if (commencePathId != 0)
				query += "                                           AND   commence_path_id = ?  ";
			
			query += "                                                        ) "
					+ "              OR metadata_extraction_rule_id NOT IN (SELECT id "
					+ "                                                     FROM "
					+ "                 metadata_extraction_rule) )";

			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement(query);
			deleteIdentifiedDocInstanceStmt.setInt(1, documentId);
			
			String metadataPropertyIdList = "" + metadataPropertyIds.get(0);			
			for (int i=1; i < metadataPropertyIds.size(); i++)
				metadataPropertyIdList += "," + metadataPropertyIds.get(i);
			
			deleteIdentifiedDocInstanceStmt.setString(2, metadataPropertyIdList);
			
			if (commencePathId != 0)
				deleteIdentifiedDocInstanceStmt.setInt(3, commencePathId);
			
			deleteIdentifiedDocInstanceStmt.execute();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("removeMetadataValues");
		}
	}
	
	public static void addMetadataValues(ArrayList<IdentifiedDocInstance> identifiedDocInstances, int documentId) {
		try {
			Connection conn = ConnectionManager.getConnection("addMetadataValues");
			PreparedStatement insertMetadataValueStmt = conn.prepareStatement("INSERT INTO Metadata_Value (identified_document_instance_id,value,metadata_extraction_rule_id,document_id) VALUES (?,?,?,?)");
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				
				for (MetadataValue metadataValue : identifiedDocInstance.getMetadataValues()) {
					if (metadataValue.getMetadataExtractionRule() != null) {
						insertMetadataValueStmt.setLong(1, identifiedDocInstance.getId());
						insertMetadataValueStmt.setString(2, metadataValue.getValue());
						insertMetadataValueStmt.setInt(3, metadataValue.getMetadataExtractionRule().getId());
						insertMetadataValueStmt.setInt(4, documentId);	
						try {
							insertMetadataValueStmt.execute();
						}
						catch (SQLException e) {
							System.err.println(e.getClass().getName() + ":" + e.getMessage() + " in addMetadataValues (" + identifiedDocInstance.getId() + "," + metadataValue.getValue() + "," + metadataValue.getMetadataExtractionRule().getId() + "," + documentId);
						}
						//insertMetadataValueStmt.addBatch();
					}
				}
				
				
			}
			//insertMetadataValueStmt.executeBatch();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("addMetadataValues");
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
	
	//public static IdentifiedDocInstances getIdentifiedDocInstances(Document document) {
	//	return getIdentifiedDocInstances(document,null);
	//}	

	public static IdentifiedDocInstances getIdentifiedDocInstances(Document document, CommencePath commencePath) {
		System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ": Step 1 of 3 Getting identified document instances");
		if (commencePath!=null && commencePath.getId()==0)
			commencePath = null;
		
		IdentifiedDocInstances identifiedDocInstances = new IdentifiedDocInstances();
		try {
			Connection conn = ConnectionManager.getConnection("getIdentifiedDocInstances");
			
			String query = "SELECT id, server, volume, path, name, extension, snapshot_deleted from Identified_Document_Instance where document_id = ? AND snapshot_deleted is null";
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
				identifiedDocInstance.setServer(rs.getString(2) == null ? null : rs.getString(2).trim());
				identifiedDocInstance.setVolume(rs.getString(3) == null ? null : rs.getString(3).trim());
				identifiedDocInstance.setPath(rs.getString(4) == null ? null : rs.getString(4).trim());
				identifiedDocInstance.setName(rs.getString(5) == null ? null : rs.getString(5).trim());	
				identifiedDocInstance.setExtension(rs.getString(6) == null ? null : rs.getString(6).trim());
				identifiedDocInstance.setSnapshotDeleted(rs.getInt(7));
				identifiedDocInstance.setNew(false);
				identifiedDocInstance.setDocument(document);
				if (commencePath != null)
					identifiedDocInstance.setCommencePath(commencePath);
				else {
					for (CommencePath docCommencePath : document.getCommencePaths()) {
						if (docCommencePath.getId()==0)
							continue;
						if (identifiedDocInstance.getVolumePath().toUpperCase().startsWith(docCommencePath.getActualPath().toUpperCase())) {
							identifiedDocInstance.setCommencePath(docCommencePath);
							break;
						}
						System.out.println(Util.getTimeStamp() + "DOC-" + document.getId() + ": No matching commence path: " + document.getName() + " " + identifiedDocInstance.getName());
					}
				}
				
				identifiedDocInstances.add(identifiedDocInstance);
				
				if (identifiedDocInstance.getSnapshotDeleted()==0)
					identifiedDocInstances.getLatestSnapshotInstances().add(identifiedDocInstance);
			}
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getIdentifiedDocInstances");
		}
		
		return identifiedDocInstances;
	}
	
	public static IdentifiedDocInstances getIdentifiedDocInstancesWithMetadataValues(int documentId) {
		IdentifiedDocInstances identifiedDocInstances = new IdentifiedDocInstances();
		try {
			Connection conn = ConnectionManager.getConnection("getIdentifiedDocInstances");
			
			String query = ""
					+ "SELECT identified_document_instance.id, "
					+ "       identified_document_instance.NAME, "
					+ "       path, "
					+ "       volume, "
					+ "       metadata_property.id, "
					+ "       metadata_property.NAME, "
					+ "       metadata_extraction_rule.id, "
					+ "       metadata_extraction_rule.source, "
					+ "       metadata_value.value "
					+ "FROM   identified_document_instance "
					+ "       LEFT JOIN metadata_value "
					+ "              ON metadata_value.identified_document_instance_id = "
					+ "                 identified_document_instance.id "
					+ "                 AND metadata_value.document_id = "
					+ "                     identified_document_instance.document_id "
					+ "       LEFT JOIN metadata_extraction_rule "
					+ "              ON metadata_value.metadata_extraction_rule_id = "
					+ "                 metadata_extraction_rule.id "
					+ "       LEFT JOIN metadata_property "
					+ "              ON metadata_extraction_rule.metadata_property_id = "
					+ "                 metadata_property.id "
					+ "WHERE  identified_document_instance.document_id = ? "
					+ "  AND  identified_document_instance.snapshot_deleted IS NULL "
					+ "ORDER  BY identified_document_instance_id, "
					+ "          metadata_property_id";
			
			PreparedStatement selectIdentifiedDocumentInstanceStmt = conn.prepareStatement(query);
			selectIdentifiedDocumentInstanceStmt.setInt(1, documentId);
			
			ResultSet rs = selectIdentifiedDocumentInstanceStmt.executeQuery();				
			
			Long preInstanceId = Long.valueOf(0);
			IdentifiedDocInstance identifiedDocInstance = null;
			while (rs.next()) {
				
				Long instanceId = rs.getLong(1);
				if (!preInstanceId.equals(instanceId)) {				
					identifiedDocInstance = new IdentifiedDocInstance();
					identifiedDocInstance.setId(rs.getLong(1));
					identifiedDocInstance.setName(rs.getString(2) == null ? null : rs.getString(2).trim());	
					identifiedDocInstance.setPath(rs.getString(3) == null ? null : rs.getString(3).trim());
					identifiedDocInstance.setVolume(rs.getString(4) == null ? null : rs.getString(4).trim());
					identifiedDocInstance.setNew(false);
					identifiedDocInstances.add(identifiedDocInstance);
					preInstanceId = instanceId;
				}
				
				MetadataValue metadataValue = new MetadataValue();
				MetadataProperty metadataProperty = new MetadataProperty();
				MetadataExtractionRule metadataExtractionRule = new MetadataExtractionRule();
				metadataProperty.setId(rs.getInt(5));
				metadataProperty.setName(rs.getString(6));
				metadataValue.setMetadataProperty(metadataProperty);
				metadataExtractionRule.setId(rs.getInt(7));
				metadataValue.setMetadataExtractionRule(metadataExtractionRule);
				metadataValue.setSource(rs.getString(8));
				metadataValue.setValue(rs.getString(9));
				identifiedDocInstance.getMetadataValues().add(metadataValue);				
			}
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getIdentifiedDocInstances");
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
		HashSet<Integer> metadataExtractionRuleIds = new HashSet<Integer>();
		for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules)
			metadataExtractionRuleIds.add(metadataExtractionRule.getId());
		removeMetadataExtractionRule(metadataExtractionRuleIds);
	}
	
	public static void removeMetadataExtractionRule(HashSet<Integer> metadataExtractionRuleIds) {
		try {					
			Connection conn = ConnectionManager.getConnection("removeMetadataExtractionRule");
			PreparedStatement deleteMetadataExtractionRuleStmt = conn.prepareStatement("DELETE FROM Metadata_Extraction_Rule WHERE id = ?");
			for (Integer metadataExtractionRuleId : metadataExtractionRuleIds) {
				deleteMetadataExtractionRuleStmt.setInt(1, metadataExtractionRuleId);
				deleteMetadataExtractionRuleStmt.addBatch();
			}
			deleteMetadataExtractionRuleStmt.executeBatch();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("removeMetadataExtractionRule");
		}
	}
	
	public static void updateMetadataExtractionRule(ArrayList<MetadataExtractionRule> metadataExtractionRules) {
		try {					
			Connection conn = ConnectionManager.getConnection("updateMetadataExtractionRule");
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
			ConnectionManager.close("updateMetadataExtractionRule");
		}
	}
	

	
	public static void addMetadataExtractionRule(ArrayList<MetadataExtractionRule> metadataExtractionRules, int commencePathId, int metadataPropertyId, boolean isDefault) {
		try {					
			Connection conn = ConnectionManager.getConnection("addMetadataExtractionRule");
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
			ConnectionManager.close("addMetadataExtractionRule");
		}
		
	}

	public static ArrayList<Document> getDocumentStatusReport() {
		ArrayList<Document> documents = new ArrayList<Document>();
		try {	
			Connection conn = ConnectionManager.getConnection("getDocumentStatusReport");	
			
			String query = ""
						+ "SELECT document.id, "
						+ "       document.NAME, "
						+ "       team.NAME, "
						+ "       Isnull(S1.count, 0)    AS S1, "
						+ "       Isnull(S1Del.count, 0) AS S1Del, "
						+ "       Isnull(S2.count, 0) AS S2, "
						+ "       metadata_property.id   AS MPID, "
						+ "       metadata_property.NAME AS MP, "
						+ "       Isnull(S1XMP.count, 0) AS S1XMP, "
						+ "       Isnull(S1DelXMP.count, 0) AS S1DelXMP, "
						+ "       Isnull(S2XMP.count, 0) AS S2XMP "
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
						+ "                  GROUP  BY document_id) S2 "
						+ "              ON document.id = S2.document_id "						
						+ "       LEFT JOIN [D_MP] "
						+ "              ON [D_MP].document_id = Document.id "
						+ "       LEFT JOIN metadata_property "
						+ "              ON [D_MP].metadata_property_id = metadata_property.id "
						+ "       LEFT JOIN (SELECT metadata_value.document_id, "
						+ "						 metadata_extraction_rule.metadata_property_id, "
						+ "                         Count(*) AS count "
						+ "                  FROM   metadata_value, "
						+ "                         metadata_extraction_rule, "
						+ "                         identified_document_instance "
						+ "                  WHERE  metadata_value.metadata_extraction_rule_id = "
						+ "                         metadata_extraction_rule.id "
						+ "                         AND metadata_value.identified_document_instance_id = "
						+ "                             identified_document_instance.id "
						+ "                         AND identified_document_instance.snapshot = 1 "
						+ "						 AND metadata_value.document_id = identified_document_instance.document_id "
						+ "						 AND rtrim(ltrim(isnull(Metadata_Value.value,'')))!='' "
						+ "                  GROUP  BY metadata_value.document_id, "
						+ "                            metadata_extraction_rule.metadata_property_id) S1XMP "
						+ "              ON document.id = S1XMP.document_id "
						+ "                 AND metadata_property.id = S1XMP.metadata_property_id "
						+ "	          LEFT JOIN (SELECT metadata_value.document_id, "
						+ "						 metadata_extraction_rule.metadata_property_id, "
						+ "                         Count(*) AS count "
						+ "                  FROM   metadata_value, "
						+ "                         metadata_extraction_rule, "
						+ "                         identified_document_instance "
						+ "                  WHERE  metadata_value.metadata_extraction_rule_id = "
						+ "                         metadata_extraction_rule.id "
						+ "                         AND metadata_value.identified_document_instance_id = "
						+ "                             identified_document_instance.id "
						+ "                         AND identified_document_instance.snapshot = 1 "
						+ "						 AND Identified_Document_Instance.snapshot_deleted IS NOT NULL "
						+ "						 AND metadata_value.document_id = identified_document_instance.document_id "
						+ "						 AND rtrim(ltrim(isnull(Metadata_Value.value,'')))!='' "
						+ "                  GROUP  BY metadata_value.document_id, "
						+ "                            metadata_extraction_rule.metadata_property_id) S1DelXMP "
						+ "              ON document.id = S1DelXMP.document_id "
						+ "                 AND metadata_property.id = S1XMP.metadata_property_id "
						+ "       LEFT JOIN (SELECT metadata_value.document_id, "
						+ "						 metadata_extraction_rule.metadata_property_id, "
						+ "                         Count(*) AS count "
						+ "                  FROM   metadata_value, "
						+ "                         metadata_extraction_rule, "
						+ "                         identified_document_instance "
						+ "                  WHERE  metadata_value.metadata_extraction_rule_id = "
						+ "                         metadata_extraction_rule.id "
						+ "                         AND metadata_value.identified_document_instance_id = "
						+ "                             identified_document_instance.id "
						+ "                         AND identified_document_instance.snapshot = 2 "
						+ "						 AND metadata_value.document_id = identified_document_instance.document_id "
						+ "						 AND rtrim(ltrim(isnull(Metadata_Value.value,'')))!='' "
						+ "                  GROUP  BY metadata_value.document_id, "
						+ "                            metadata_extraction_rule.metadata_property_id) S2XMP "
						+ "              ON document.id = S2XMP.document_id "
						+ "                 AND metadata_property.id = S2XMP.metadata_property_id "
						+ "WHERE  Document.is_cm_qualified = 1 "
						+ "ORDER  BY document.id";

			

			PreparedStatement stmt = conn.prepareStatement(query);
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
				metadataProperty.getExtracted().add(rs.getInt(9));
				metadataProperty.getExtracted().add(rs.getInt(11));
				doc.getMetadataProperties().add(metadataProperty);				
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getDocumentStatusReport");
		}		
		return documents;
	}
	
	public static void addSnippet(int documentId, IdentifiedDocInstances identifiedDocInstances) {
		try {
			Connection conn = ConnectionManager.getConnection("addSnippet");
			
			PreparedStatement insertIdentifiedDocInstancStmt = conn.prepareStatement("INSERT INTO Identified_Document_Instance_Snippet (id,name,path,volume,extension,server,document_id,snippet,snapshot_deleted,snapshot) SELECT id,name,path,volume,extension,server,?,?,snapshot_deleted,snapshot FROM All_Document_Instance WHERE id = ?");
									
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
			ConnectionManager.close("addSnippet");
		}	
	}

	public static void addIdentifiedDocInstances(Document document, IdentifiedDocInstances identifiedDocInstances) {
		try {
			Connection conn = ConnectionManager.getConnection("addIdentifiedDocInstances");
			
			/*
			 * Delete existing metadata value
			 */
			PreparedStatement deleteMetadataValueStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE document_id = ?");
			deleteMetadataValueStmt.setInt(1, document.getId());
			deleteMetadataValueStmt.execute();	
			
			/*
			 * Delete existing identified doc instance
			 */
			
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Identified_Document_Instance WHERE document_id = ?");
			deleteIdentifiedDocInstanceStmt.setInt(1, document.getId());			
			deleteIdentifiedDocInstanceStmt.execute();
			
			/*
			 * Insert identified doc instance and metadata value
			 */		
			
			PreparedStatement insertIdentifiedDocInstancStmt = conn.prepareStatement("INSERT INTO Identified_Document_Instance (id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,document_id,origin_instance_id) SELECT id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,?,? FROM All_Document_Instance WHERE id = ?");
			PreparedStatement insertMetadataValueStmt = conn.prepareStatement("INSERT INTO Metadata_Value (identified_document_instance_id,value,document_id) VALUES (?,?,?)");
							
			for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
				insertIdentifiedDocInstancStmt.setInt(1, document.getId());
				insertIdentifiedDocInstancStmt.setLong(2, identifiedDocInstance.getOriginInstanceId());
				insertIdentifiedDocInstancStmt.setLong(3, identifiedDocInstance.getId());
				insertIdentifiedDocInstancStmt.addBatch();
				
				if (document.isIncludeLinkedFile() && identifiedDocInstance.getMetadataValues().size() > 0) {
					for (MetadataValue metadataValue : identifiedDocInstance.getMetadataValues()) {
						insertMetadataValueStmt.setLong(1, identifiedDocInstance.getId());
						insertMetadataValueStmt.setString(2, metadataValue.getValue());
						insertMetadataValueStmt.setInt(3, document.getId());	
						insertMetadataValueStmt.addBatch();
					}
				}
			}
			
			insertIdentifiedDocInstancStmt.executeBatch();
			insertMetadataValueStmt.executeBatch();
			
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("addIdentifiedDocInstances");
		}
	}
	
	
	
	public static IdentifiedDocInstances getDocInstances(Document document, boolean fromSnippet, boolean skipIdentificationRules) throws SQLException {
		
		IdentifiedDocInstances identifiedDocInstances = new IdentifiedDocInstances();		
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
			
			if (document.isOfficeDoc()) {
			
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
			
			}

			//tilde
			query += " AND name not like '~$%' ";
			
			//retention
			if (document.getIgDocClass().equals("General Document"))
				query += "AND mtime > DATEADD(YEAR,-7,GETDATE())";
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

			query += ")";
		}		
		
		query += " ORDER BY snapshot, snapshot_deleted";
				
		try {
			Connection conn = ConnectionManager.getConnection("getDocInstances");	
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);	
			
			HashMap<String,Integer> digests = new HashMap<String,Integer>();
			
			ArrayList<IdentifiedDocInstance> pdfInstances = new ArrayList<IdentifiedDocInstance>();
			
			while (rs.next()) {
				IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
				identifiedDocInstance.setId(rs.getLong(1));	
				identifiedDocInstance.setName(rs.getNString(2) == null ? null : rs.getNString(2).trim());	
				identifiedDocInstance.setPath(rs.getNString(3) == null ? null : rs.getNString(3).trim());
				identifiedDocInstance.setVolume(rs.getString(4) == null ? null : rs.getString(4).trim());
				identifiedDocInstance.setDigest(rs.getString(5) == null ? null : rs.getString(5).trim());
				identifiedDocInstance.setExtension(rs.getString(6) == null ? null : rs.getString(6).trim());
				identifiedDocInstance.setServer(rs.getString(7) == null ? null : rs.getString(7).trim());
				identifiedDocInstance.setSnapshotDeleted(rs.getInt(8));
				identifiedDocInstance.setDocument(document);
				
				
				if (fromSnippet ||
					!digests.containsKey(identifiedDocInstance.getDigest()) || 
					 digests.get(identifiedDocInstance.getDigest()) > 0) {
					
					
					//Ignore PDF
					if (!fromSnippet && document.isNoPdf()) {
						if (identifiedDocInstance.getExtension().toUpperCase().equals("PDF"))	{	
							pdfInstances.add(identifiedDocInstance);
						}
					}

					boolean hasCommencePath = false;
					
					for (CommencePath commencePath : document.getCommencePaths()) {						
						if (identifiedDocInstance.getVolumePath().toUpperCase().equals(commencePath.getActualPath().toUpperCase()) || identifiedDocInstance.getVolumePath().toUpperCase().startsWith(commencePath.getActualPath().toUpperCase() + "/")) {
							identifiedDocInstance.setCommencePath(commencePath);
							hasCommencePath = true;
							break;
						}
					}
					
					if (!hasCommencePath) {
						String log = identifiedDocInstance.getFullyQualifiedPath() + " has no matching commence path :";
						for (CommencePath commencePath : document.getCommencePaths())
							log += commencePath.getActualPath() + ";";
						System.out.println(Util.getTimeStamp() + log);
					}		
					
					if ((digests.containsKey(identifiedDocInstance.getDigest()) &&
						identifiedDocInstance.getSnapshotDeleted() == 0) ||
						!digests.containsKey(identifiedDocInstance.getDigest())) {
						digests.put(identifiedDocInstance.getDigest(),Integer.valueOf(identifiedDocInstance.getSnapshotDeleted()));
					}
					
					identifiedDocInstances.add(identifiedDocInstance);	
					
					if (identifiedDocInstance.getSnapshotDeleted() == 0)
						identifiedDocInstances.getLatestSnapshotInstances().add(identifiedDocInstance);
					
				}

			}
				
			ArrayList<IdentifiedDocInstance> tobeRemovedPdf = new ArrayList<IdentifiedDocInstance>();
			
			//noPdf
			if (!fromSnippet && document.isNoPdf()) {
				if (pdfInstances.size() > 0) {
					for (IdentifiedDocInstance pdfInstance : pdfInstances) {
						for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
							if (pdfInstance.getServer().equals(identifiedDocInstance.getServer()) &&
								pdfInstance.getVolume().equals(identifiedDocInstance.getVolume()) &&
								pdfInstance.getPath().equals(identifiedDocInstance.getPath()) &&
								pdfInstance.getNameWithoutExtension().equals(identifiedDocInstance.getNameWithoutExtension()) &&
								!identifiedDocInstance.getExtension().toUpperCase().equals("PDF") &&
								identifiedDocInstance.getSnapshotDeleted() == 0) {
									tobeRemovedPdf.add(pdfInstance);
							}
						}
					}
				}
			}
			
			identifiedDocInstances.removeAll(tobeRemovedPdf);
			identifiedDocInstances.getLatestSnapshotInstances().removeAll(tobeRemovedPdf);
			identifiedDocInstances.setDigests(digests);
		}
		catch (SQLException e) {
			System.err.println(Util.getTimeStamp() + e.getMessage() + " - " + query);
		}
		finally {
			ConnectionManager.close("getDocInstances");
		}
		
		return identifiedDocInstances;
	}

	public static void removeSnippet(int documentId) {
		try {
			Connection conn = ConnectionManager.getConnection("removeSnippet");
			PreparedStatement deleteSnippetStmt = conn.prepareStatement("DELETE FROM Identified_Document_Instance_Snippet WHERE document_id = ?");
			deleteSnippetStmt.setInt(1, documentId);	
			deleteSnippetStmt.execute();
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("removeSnippet");
		}
	}

	public static ArrayList<DocumentClass> getDocumentClasses() {		
		ArrayList<DocumentClass> documentClasses = new ArrayList<DocumentClass>();		
		try {	
			Connection conn = ConnectionManager.getConnection("getDocumentClasses");	
			String query = ""
					+ "SELECT id, "
					+ "       name "
					+ "FROM   document_class "
					+ "WHERE  parent_id != 124 AND parent_id != 0"
					+ "ORDER  BY id";

			PreparedStatement selectDocumentClassStmt = conn.prepareStatement(query);
			ResultSet selectDocumentClassRS = selectDocumentClassStmt.executeQuery();
			while (selectDocumentClassRS.next()) {					
				DocumentClass documentClass = new DocumentClass();
				documentClass.setId(selectDocumentClassRS.getInt(1));
				documentClass.setName(selectDocumentClassRS.getString(2));	
				documentClasses.add(documentClass);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getDocumentClasses");
		}		
		return documentClasses;
	}

	public static ArrayList<IdentifiedDocInstance> getIntraTeamDuplicates() {
		ArrayList<IdentifiedDocInstance> duplicates = new ArrayList<IdentifiedDocInstance>();		
		try {	
			Connection conn = ConnectionManager.getConnection("getIntraTeamDuplicates");	
			String query = ""
						+ "SELECT Team.name, "
						+ "       Document.id, "
						+ "       Document.name, "
						+ "       Duplicates.digest, "
						+ "       Identified_document_instance.id, "
						+ "       Identified_document_instance.NAME, "
						+ "       path, "
						+ "       volume "
						+ "FROM   Identified_document_instance "
						+ "       LEFT JOIN Document "
						+ "              ON Identified_document_instance.document_id = Document.id "
						+ "	      LEFT JOIN Team "
						+ "	             ON Document.team_id = Team.id "
						+ "       INNER JOIN (SELECT team_id, "
						+ "                          digest "
						+ "                   FROM   Identified_document_instance "
						+ "                          LEFT JOIN Document "
						+ "                                 ON Identified_document_instance.document_id = "
						+ "                                    Document.id "
						+ "                   GROUP  BY team_id, "
						+ "                             digest "
						+ "                   HAVING Count(*) > 1) Duplicates "
						+ "               ON Document.team_id = Duplicates.team_id "
						+ "                  AND Identified_document_instance.digest = Duplicates.digest "
						+ "ORDER  BY Duplicates.team_id, "
						+ "          Duplicates.digest";


			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {					
				IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
				Document document = new Document();
				document.setTeam(rs.getString(1));
				document.setId(rs.getInt(2));
				document.setName(rs.getString(3));
				identifiedDocInstance.setDocument(document);
				identifiedDocInstance.setDigest(rs.getString(4));
				identifiedDocInstance.setId(rs.getLong(5));
				identifiedDocInstance.setName(rs.getString(6));
				identifiedDocInstance.setPath(rs.getString(7));
				identifiedDocInstance.setVolume(rs.getString(8));
				duplicates.add(identifiedDocInstance);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getIntraTeamDuplicates");
		}		
		return duplicates;
	}
	

	public static ArrayList<DocumentInstancePair> getInterTeamDuplicates() {
		ArrayList<DocumentInstancePair> duplicates = new ArrayList<DocumentInstancePair>();		
		try {	
			Connection conn = ConnectionManager.getConnection("getInterTeamDuplicates");	
			String query = ""
						+ "SELECT IDI1.digest, "
						+ "       IDI1.team_name, "
						+ "       IDI1.document_id, "
						+ "       IDI1.document_name, "
						+ "       IDI1.id, "
						+ "       IDI1.NAME, "
						+ "       IDI1.path, "
						+ "       IDI1.volume, "
						+ "       IDI2.team_name, "
						+ "       IDI2.document_id, "
						+ "       IDI2.document_name, "
						+ "       IDI2.id, "
						+ "       IDI2.NAME, "
						+ "       IDI2.path, "
						+ "       IDI2.volume "
						+ "FROM   (SELECT Identified_document_instance.id, "
						+ "               Identified_document_instance.document_id, "
						+ "               Identified_document_instance.NAME, "
						+ "               Identified_document_instance.path, "
						+ "               Identified_document_instance.volume, "
						+ "               Identified_document_instance.server, "
						+ "               Identified_document_instance.digest, "
						+ "               Document.team_id, "
						+ "               Document.NAME AS document_name, "
						+ "               Team.NAME     AS team_name "
						+ "        FROM   Identified_document_instance "
						+ "               LEFT JOIN Document "
						+ "                      ON Identified_document_instance.document_id = Document.id "
						+ "               LEFT JOIN Team "
						+ "                      ON Document.team_id = Team.id "
						+ "        WHERE  Identified_document_instance.snapshot_deleted IS NULL) AS IDI1, "
						+ "       (SELECT Identified_document_instance.id, "
						+ "               Identified_document_instance.document_id, "
						+ "               Identified_document_instance.NAME, "
						+ "               Identified_document_instance.path, "
						+ "               Identified_document_instance.volume, "
						+ "               Identified_document_instance.server, "
						+ "               Identified_document_instance.digest, "
						+ "               Document.team_id, "
						+ "               Document.NAME AS document_name, "
						+ "               Team.NAME     AS team_name "
						+ "        FROM   Identified_document_instance "
						+ "               LEFT JOIN Document "
						+ "                      ON Identified_document_instance.document_id = Document.id "
						+ "               LEFT JOIN Team "
						+ "                      ON Document.team_id = Team.id "
						+ "        WHERE  Identified_document_instance.snapshot_deleted IS NULL) AS IDI2 "
						+ "WHERE  IDI1.digest = IDI2.digest "
						+ "       AND IDI1.team_id != IDI2.team_id "
						+ "ORDER  BY IDI1.digest, "
						+ "          IDI1.team_id, "
						+ "          IDI1.document_id, "
						+ "          IDI1.id, "
						+ "          IDI2.team_id, "
						+ "          IDI2.document_id, "
						+ "          IDI2.id";

			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {					
				DocumentInstancePair documentInstancePair = new DocumentInstancePair();
				documentInstancePair.getDocumentInstance1().setDigest(rs.getString(1));
				documentInstancePair.getDocumentInstance2().setDigest(rs.getString(1));				
				
				Document document1 = new Document();
				document1.setTeam(rs.getString(2));
				document1.setId(rs.getInt(3));
				document1.setName(rs.getString(4));				
				documentInstancePair.getDocumentInstance1().setDocument(document1);				
				documentInstancePair.getDocumentInstance1().setId(rs.getLong(5));
				documentInstancePair.getDocumentInstance1().setName(rs.getString(6));
				documentInstancePair.getDocumentInstance1().setPath(rs.getString(7));
				documentInstancePair.getDocumentInstance1().setVolume(rs.getString(8));
				
				Document document2 = new Document();
				document2.setTeam(rs.getString(9));
				document2.setId(rs.getInt(10));
				document2.setName(rs.getString(11));				
				documentInstancePair.getDocumentInstance2().setDocument(document2);				
				documentInstancePair.getDocumentInstance2().setId(rs.getLong(12));
				documentInstancePair.getDocumentInstance2().setName(rs.getString(13));
				documentInstancePair.getDocumentInstance2().setPath(rs.getString(14));
				documentInstancePair.getDocumentInstance2().setVolume(rs.getString(15));
				
				duplicates.add(documentInstancePair);
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getInterTeamDuplicates");
		}		
		return duplicates;
	}

	public static IdentifiedDocInstance getDocInstance(Document document, String address) {
		IdentifiedDocInstance identifiedDocInstance = null;
		try {
			Connection conn = ConnectionManager.getConnection("getDocInstance");
			
			String query = "SELECT id, server, volume, path, name, extension, snapshot_deleted, digest from All_Document_Instance WHERE '\\\\' + server + '/' + volume + '/' + CASE WHEN path is null THEN '' ELSE path + '/' END + name =? AND snapshot_deleted IS NULL";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, address);
			
			ResultSet rs = stmt.executeQuery();				
			
			if (rs.next()) {
				identifiedDocInstance = new IdentifiedDocInstance();
				identifiedDocInstance.setId(rs.getLong(1));
				identifiedDocInstance.setServer(rs.getString(2) == null ? null : rs.getString(2).trim());
				identifiedDocInstance.setVolume(rs.getString(3) == null ? null : rs.getString(3).trim());
				identifiedDocInstance.setPath(rs.getString(4) == null ? null : rs.getString(4).trim());
				identifiedDocInstance.setName(rs.getString(5) == null ? null : rs.getString(5).trim());	
				identifiedDocInstance.setExtension(rs.getString(6) == null ? null : rs.getString(6).trim());
				identifiedDocInstance.setSnapshotDeleted(rs.getInt(7));
				identifiedDocInstance.setDigest(rs.getString(8));
				identifiedDocInstance.setNew(false);
				identifiedDocInstance.setDocument(document);

				for (CommencePath commencePath : document.getCommencePaths()) {
					if (identifiedDocInstance.getVolumePath().startsWith(commencePath.getActualPath())) {
						identifiedDocInstance.setCommencePath(commencePath);
						break;
					}
					System.out.println(Util.getTimeStamp() + "No matching commence path: " + document.getName() + " " + identifiedDocInstance.getName());
				}
			}
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("getDocInstance");
		}
		
		return identifiedDocInstance;
	}
	
	
}
