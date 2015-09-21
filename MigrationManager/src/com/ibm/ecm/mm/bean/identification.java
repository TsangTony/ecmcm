package com.ibm.ecm.mm.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.util.MSSQLConnection;

public class identification {
	
	private Document selectedDocument;
	private CommencePath selectedCommencePath;
	private String attribute;
	private String operator;
	private String value;
	
	public Document getSelectedDocument() {
		System.out.println("Getting selectedDocument");
		return selectedDocument;
	}
	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}
	public CommencePath getSelectedCommencePath() {
		return selectedCommencePath;
	}
	public void setSelectedCommencePath(CommencePath selectedCommencePath) {
		this.selectedCommencePath = selectedCommencePath;
	}
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void testIdentificationRule (){
		
		//System.out.println("This is the rule: attribute: " + attribute + ", operator: " + operator + ", value: " + value);	

		//Convert the values entered by the user to SQL terms
		String sqlAttribute = "";
		String sqlOperator = "";
		String sqlValue = value;
		
		if (attribute.equals("Filename")){
			sqlAttribute = "name";
		}else if (attribute.equals("Filepath")){
			sqlAttribute = "path";
		}else if (attribute.equals("Extension")){
			sqlAttribute = "extension";
		}
		
		if (operator.equals("Equals")){
			sqlOperator = "=";
		}else if (operator.equals("Not Equals")){
			sqlOperator = "!=";
		}else if (operator.equals("Contains")){
			sqlOperator = "like";
			sqlValue = "%" + value + "%";
		}else if (operator.equals("Not Contains")){
			sqlOperator = "not like";
			sqlValue = "%" + value + "%";
		}
		
		
		try {
		
			Connection conn = MSSQLConnection.getConnection();	
			String query = "SELECT name FROM all_document_instance " 
						+ "WHERE path like 'CLKDEPT14_FOP%' "
						+ "AND " +  sqlAttribute + " " + sqlOperator + " '" + sqlValue + "'";
					
			System.out.println(query);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {	
				
				System.out.println(rs.getString(1));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
}
