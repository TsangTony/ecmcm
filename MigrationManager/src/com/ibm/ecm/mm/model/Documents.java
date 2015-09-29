package com.ibm.ecm.mm.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.ibm.ecm.mm.util.MSSQLConnection;

public class Documents {
	private ArrayList<Document> documents;
	
	public Documents() {
		this.documents = new ArrayList<Document>();		
			
		try {			
			
			Connection conn = MSSQLConnection.getConnection();	
			
			String query = "    SELECT Document.id, Document.name,"
					     + "           Commence_Path.id, Commence_Path.commence_path, Commence_Path.business_path,"
					     + "           Metadata_Property.id, Metadata_Property.name,"
					     + "           Metadata_Extraction_Rule.id, Metadata_Extraction_Rule.source,"
					     + "  	       Metadata_Extraction_Rule.hr_rule, Metadata_Extraction_Rule.regex,"
  					     + "  	       Metadata_Extraction_Rule.capturing_group, Metadata_Extraction_Rule.default_value,"
  					     + "  	       Metadata_Extraction_Rule.priority, Metadata_Extraction_Rule.[default]"
					     + "      FROM Document"
					     + " LEFT JOIN Commence_Path"
					     + "	    ON Document.id = Commence_Path.document_id"
					     + " LEFT JOIN Document_Class"
					     + "        ON Document.document_class_id = Document_Class.id"
					     + " LEFT JOIN [DC-MP]"
					     + "        ON Document_Class.id = [DC-MP].document_class_id"
					     + " LEFT JOIN Metadata_Property"
					     + "        ON [DC-MP].metadata_property_id = Metadata_Property.id"
					     + " LEFT JOIN Metadata_Extraction_Rule"
					     + "        ON Metadata_Extraction_Rule.metadata_id = Metadata_Property.id"
					     + "       AND Metadata_Extraction_Rule.commence_path_id = Commence_Path.id"
					     + "  ORDER BY Document.id, Commence_Path.id, Metadata_Property.id, Metadata_Extraction_Rule.priority";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			int preDocumentId = 0;
			int preCommencePathId = 0;
			int preMetadataPropertyId = 0;
			
			Document document = null;
			CommencePath commencePath = null;
			MetadataExtractionRules metadataExtractionRules = null;
			
			while (rs.next()) {	
				
				int documentId = rs.getInt(1);
				int commencePathId = rs.getInt(3);
				int metadataPropertyId = rs.getInt(6);
				
				if (preDocumentId == 0 || documentId != preDocumentId) {
					document = new Document();
					document.setId(documentId);
					document.setName(rs.getString(2));	
					getDocuments().add(document);
				}
				
				if (preCommencePathId == 0 || commencePathId != preCommencePathId) {
					commencePath = new CommencePath();
					commencePath.setId(rs.getInt(3));
					commencePath.setCommencePath(rs.getString(4));	
					commencePath.setBusinessPath(rs.getString(5));					
					document.getCommencePaths().add(commencePath);
				}
				
				if (preMetadataPropertyId == 0 || metadataPropertyId != preMetadataPropertyId) {					
					metadataExtractionRules = new MetadataExtractionRules();
					metadataExtractionRules.setMetadataId(metadataPropertyId);
					metadataExtractionRules.setMetadataName(rs.getString(7));
					metadataExtractionRules.setDefault(rs.getBoolean(15));
					commencePath.getMetadataExtractionRulesList().add(metadataExtractionRules);
				}				

				if (rs.getInt(8) != 0) {
					MetadataExtractionRule metadataExtractionRule = new MetadataExtractionRule();
					metadataExtractionRule.setId(rs.getInt(8));
					metadataExtractionRule.setSource(rs.getString(9));
					metadataExtractionRule.setHrRule(rs.getString(10));
					metadataExtractionRule.setRule(rs.getString(11));
					metadataExtractionRule.setCapGroup(rs.getString(12));
					metadataExtractionRule.setDefaultValue(rs.getString(13));	
					metadataExtractionRule.setPriority(rs.getInt(14));
					metadataExtractionRule.setNew(false);
					metadataExtractionRules.getRules().add(metadataExtractionRule);
				}
				
				preDocumentId = documentId;
				preCommencePathId = commencePathId;
				preMetadataPropertyId = metadataPropertyId; 
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			MSSQLConnection.close();
		}
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}
	
	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}
	
}
