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
		this.setDocuments(new ArrayList<Document>());		
			
		try {			
			
			Connection conn = MSSQLConnection.getConnection();	
			String query = "SELECT Document.id, Document.name, Commence_Path.id, Commence_Path.path"
					     + "  FROM Document, Commence_Path "
					     + " WHERE Document.id = Commence_Path.document_id"
					     + " ORDER By Document.id ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			int preDocumentId = 0;
			
			Document document = null;
			ArrayList<CommencePath> commencePaths = null;
			
			while (rs.next()) {	
				
				int documentId = rs.getInt(1);
				
				if (preDocumentId == 0 || documentId != preDocumentId) {
					document = new Document();
					document.setId(rs.getInt(1));
					document.setName(rs.getString(2));
					
					commencePaths = new ArrayList<CommencePath>();
					document.setCommencePaths(commencePaths);
				}
				
				CommencePath commencePath = new CommencePath();
				commencePath.setId(rs.getInt(3));
				commencePath.setPath(rs.getString(4));
				
				commencePaths.add(commencePath);				
				
				getDocuments().add(document);
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
