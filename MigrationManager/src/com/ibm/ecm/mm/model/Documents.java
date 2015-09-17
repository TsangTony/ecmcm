package com.ibm.ecm.mm.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.ibm.ecm.mm.util.MSSQLConnection;

public class Documents {
	private ArrayList<Document> documents;
	
	@Resource(name="jdbc/migrationmanager")
	private DataSource ds;
	
	public Documents() {
		this.setDocuments(new ArrayList<Document>());		
			
		try {

			Connection conn = ds.getConnection();	
			String query = "SELECT id, name FROM Document order by id ASC";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);			
			while (rs.next()) {	
				Document document = new Document();
				document.setId(rs.getInt(1));
				document.setName(rs.getString(2));
				
				getDocuments().add(document);
			}
			
		} catch (SQLException e) {
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
