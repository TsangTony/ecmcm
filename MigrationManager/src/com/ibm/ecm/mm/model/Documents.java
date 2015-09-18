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
			String query = "SELECT Document.id, Document.name, Metadata_Property.id, Metadata_Property.name, Commence_Path.id, Commence_Path.path"
						 + "  FROM Document, Commence_Path, Document_Class, [DC-MP], Metadata_Property"
                         + " WHERE Document.id = Commence_Path.document_id"
        				 + "   AND Document.document_class_id = Document_Class.id"
        				 + "   AND Document_Class.id = [DC-MP].document_class_id"
        				 + "   AND [DC-MP].metadata_property_id = Metadata_Property.id"
        				 + " ORDER BY Document.id, Metadata_Property.id";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			int preDocumentId = 0;
			int preMetadataPropertyId = 0;
			
			Document document = null;
			
			while (rs.next()) {	
				
				int documentId = rs.getInt(1);
				int metadataPropertyId = rs.getInt(3);
				
				if (preDocumentId == 0 || documentId != preDocumentId) {
					document = new Document();
					document.setId(documentId);
					document.setName(rs.getString(2));				
					
					getDocuments().add(document);
				}
				
				if (preMetadataPropertyId == 0 || metadataPropertyId != preMetadataPropertyId) {
					MetadataProperty metadataProperty = new MetadataProperty();
					metadataProperty.setId(metadataPropertyId);
					metadataProperty.setName(rs.getString(4));
					document.getMetadataProperties().add(metadataProperty);
				}				
				
				CommencePath commencePath = new CommencePath();
				commencePath.setId(rs.getInt(5));
				commencePath.setPath(rs.getString(6));
				
				document.addCommencePath(commencePath);
				
				preDocumentId = documentId;
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
