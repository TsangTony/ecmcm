package com.ibm.ecm.mm.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentificationRule;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.IdentificationManager;
import com.ibm.ecm.mm.util.ConnectionManager;

public class IdentificationBean {
	
	private ArrayList<Document> documents;
	private Document document;
	private IdentifiedDocInstances identifiedDocInstances;
	private String status;

	public IdentificationBean() {
		setDocuments(new ArrayList<Document>());
		Document allDocuments = new Document();
		allDocuments.setId(0);
		getDocuments().add(allDocuments);
		getDocuments().addAll(DataManager.getDocuments());		
		setIdentifiedDocInstances(new IdentifiedDocInstances());
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
	public IdentifiedDocInstances getIdentifiedDocInstances() {
		return identifiedDocInstances;
	}
	public void setIdentifiedDocInstances(IdentifiedDocInstances identifiedDocInstances) {
		this.identifiedDocInstances = identifiedDocInstances;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isDocumentSelected() {
		return getDocument() != null && getDocument().getId() != 0;
	}
	
	public boolean isAllDocumentsSelected() {
		return getDocument() != null && getDocument().getId() == 0;
	}

	public ArrayList<IdentifiedDocInstance> getLatestSnapshotInstances() {
		return getIdentifiedDocInstances().getLatestSnapshotInstances();
	}

	public int getCommencePathsLastIndex() {
		return getDocument().getCommencePaths().size()-1;
	}
	
	public int getIdentificationRulesLastIndex() {
		return getDocument().getIdentificationRules().size()-1;
	}	
	
	public void documentSelected() {
		try {
			getDocument().setCommencePaths(DataManager.getCommencePaths(getDocument().getId()));
			getDocument().setIdentificationRules(DataManager.getIdentificationRules(getDocument().getId()));	
		}
		catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getLogicalOperators() {
		return DataManager.getLogicalOperators();
	}
	
	public ArrayList<String> getAttributes() {
		return DataManager.getAttributes();
	}
	
	public ArrayList<String> getRelationalOperators() {
		return DataManager.getRelationalOperators();
	}
	
	public ArrayList<String> getLeftParens() {
		return DataManager.getLeftParens();
	}
	
	public ArrayList<String> getRightParens() {
		return DataManager.getRightParens();
	}
	
	public void ignoreObsolete() {
		getDocument().getIdentificationRules().addAll(IdentificationManager.createObsoleteRules(getDocument().getIdentificationRules().size()+1));
	}
		
	public void preview() {
		try {
			setIdentifiedDocInstances(IdentificationManager.identify(getDocument()));
		}
		catch (Exception e) {
			String message = e.getClass().getName();
			if (e.getMessage() != null)
				message += ": " + e.getMessage();
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			Connection conn = ConnectionManager.getConnection("save");
					
			/*
			 *  BL Identification Rule, is no pdf, is office doc
			 */
			
			PreparedStatement updatDocumentStmt = conn.prepareStatement("UPDATE Document SET bl_identification_rule=?, is_no_pdf=?, is_office_doc=? WHERE id = ?");
			updatDocumentStmt.setString(1, getDocument().getBlIdentificationRule());
			updatDocumentStmt.setBoolean(2, getDocument().isNoPdf());
			updatDocumentStmt.setBoolean(3, getDocument().isOfficeDoc());
			updatDocumentStmt.setInt(4, getDocument().getId());
			updatDocumentStmt.execute();
			
			
			/*
			 *  Commence Path
			 */
			
			// Delete metadata value, metadata extraction rule and commence path
			PreparedStatement deleteMetadataValueStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE metadata_extraction_rule_id IN (SELECT id FROM Metadata_Extraction_Rule WHERE commence_path_id=?)");
			PreparedStatement deleteMetadataExtractionRuleStmt = conn.prepareStatement("DELETE FROM Metadata_Extraction_Rule WHERE commence_path_id =?");
			PreparedStatement deleteCommencePathStmt = conn.prepareStatement("DELETE FROM Commence_Path WHERE id = ?");	
			
			for (CommencePath commencePath : getDocument().getCommencePaths().getRemovedList()) {
				deleteMetadataValueStmt.setInt(1, commencePath.getId());
				deleteMetadataExtractionRuleStmt.setInt(1, commencePath.getId());
				deleteCommencePathStmt.setInt(1, commencePath.getId());
				deleteMetadataValueStmt.addBatch();
				deleteMetadataExtractionRuleStmt.addBatch();
				deleteCommencePathStmt.addBatch();
			}
			deleteMetadataValueStmt.executeBatch();			
			deleteMetadataExtractionRuleStmt.executeBatch();
			deleteCommencePathStmt.executeBatch();

			getDocument().getCommencePaths().getRemovedList().clear();			
			
			//insert and update commence path
			PreparedStatement updateCommencePathStmt = conn.prepareStatement("UPDATE Commence_Path SET business_path=?, actual_path=? WHERE ID = ?");
			PreparedStatement insertCommencePathStmt = conn.prepareStatement("INSERT INTO Commence_Path (business_path,actual_path,document_id) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (CommencePath commencePath : getDocument().getCommencePaths()) {
				if (commencePath.isNew()) {
					insertCommencePathStmt.setString(1, commencePath.getBusinessPath());
					insertCommencePathStmt.setString(2, commencePath.getActualPath());
					insertCommencePathStmt.setInt(3, getDocument().getId());
					insertCommencePathStmt.execute();					
					ResultSet commencePathIdRs = insertCommencePathStmt.getGeneratedKeys();
					if (commencePathIdRs != null && commencePathIdRs.next()) {
						commencePath.setId(commencePathIdRs.getInt(1));
					}					
					commencePath.setNew(false);
				}
				else {
					updateCommencePathStmt.setString(1, commencePath.getBusinessPath());
					updateCommencePathStmt.setString(2, commencePath.getActualPath());
					updateCommencePathStmt.setInt(3, commencePath.getId());
					updateCommencePathStmt.addBatch();
				}
			}			
			updateCommencePathStmt.executeBatch();
			
			/*
			 *  Identification Rule
			 */
			
			PreparedStatement deleteIdentificationRuleStmt = conn.prepareStatement("DELETE FROM Identification_Rule WHERE id = ?");
			for (IdentificationRule identificationRule : getDocument().getIdentificationRules().getRemovedList()) {
				deleteIdentificationRuleStmt.setInt(1, identificationRule.getId());
				deleteIdentificationRuleStmt.addBatch();
			}
			deleteIdentificationRuleStmt.executeBatch();
			
			getDocument().getIdentificationRules().getRemovedList().clear();
			
			PreparedStatement updateIdentificationRuleStmt = conn.prepareStatement("UPDATE Identification_Rule SET operator_1=?, attribute=?, operator_2=?, value=?, priority=?, right_paren=?, left_paren=? WHERE ID = ?");
			PreparedStatement insertIdentificationRuleStmt = conn.prepareStatement("INSERT INTO Identification_Rule (operator_1,attribute,operator_2,value,document_id,priority,right_paren,left_paren) VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (IdentificationRule identificationRule : getDocument().getIdentificationRules()) {
				if (identificationRule.isNew()) {
					insertIdentificationRuleStmt.setString(1, identificationRule.getPriority() == 1 ? null : identificationRule.getLogicalOperator());
					insertIdentificationRuleStmt.setString(2, identificationRule.getAttribute());
					insertIdentificationRuleStmt.setString(3, identificationRule.getRelationalOperator());
					insertIdentificationRuleStmt.setString(4, identificationRule.getValue());
					insertIdentificationRuleStmt.setInt(5, getDocument().getId());
					insertIdentificationRuleStmt.setInt(6, identificationRule.getPriority());
					insertIdentificationRuleStmt.setString(7, identificationRule.getRightParen());
					insertIdentificationRuleStmt.setString(8, identificationRule.getLeftParen());
					insertIdentificationRuleStmt.execute();
					
					ResultSet identificationRuleIdRs = insertIdentificationRuleStmt.getGeneratedKeys();
					if (identificationRuleIdRs != null && identificationRuleIdRs.next()) {
						identificationRule.setId(identificationRuleIdRs.getInt(1));
					}
					
					identificationRule.setNew(false);
				}
				else {
					updateIdentificationRuleStmt.setString(1, identificationRule.getPriority() == 1 ? null : identificationRule.getLogicalOperator());
					updateIdentificationRuleStmt.setString(2, identificationRule.getAttribute());
					updateIdentificationRuleStmt.setString(3, identificationRule.getRelationalOperator());
					updateIdentificationRuleStmt.setString(4, identificationRule.getValue());
					updateIdentificationRuleStmt.setInt(5, identificationRule.getPriority());
					updateIdentificationRuleStmt.setString(6, identificationRule.getRightParen());
					updateIdentificationRuleStmt.setString(7, identificationRule.getLeftParen());
					updateIdentificationRuleStmt.setInt(8, identificationRule.getId());
					updateIdentificationRuleStmt.addBatch();
				}
			}			
			updateIdentificationRuleStmt.executeBatch();
			
		}
		catch (SQLException e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  e.getMessage()));
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("save");
		}
		
		String message = "Source Locations and Identification Rules are saved.";
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful",  message) );

	}

	public void run() {
		Severity severity = null;
		String summary = null;
		String message = "";
		try {
			setIdentifiedDocInstances(IdentificationManager.identify(getDocument()));
			IdentificationManager.saveIdentifiedDocInstances(getDocument(), getIdentifiedDocInstances());
			severity = FacesMessage.SEVERITY_INFO;
			summary = "Successful";
			message = getDocument().toString() + " is identified.";
		}
		catch (Exception e) {
			severity = FacesMessage.SEVERITY_ERROR;
			summary = e.getClass().getName();
			if (e.getMessage() != null)
				message += e.getMessage() + ".";
			message += getDocument().toString() + " is not identified.";
			e.printStackTrace();
		}
		finally {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(severity, summary, message));
		}
	}
	
	public void runAll() {
		String successDoc = "";
		Severity severity = null;
		String summary = null;
		String message = "";
		try {
			for (Document document : getDocuments()) {
				if (document.getId() != 0) {
					document.setCommencePaths(DataManager.getCommencePaths(document.getId()));
					document.setIdentificationRules(DataManager.getIdentificationRules(document.getId()));	
					IdentificationManager.saveIdentifiedDocInstances(document, IdentificationManager.identify(document));
					successDoc += document.getId() + ",";
				}
			}
			successDoc = successDoc.substring(0,successDoc.length()-2);
			severity = FacesMessage.SEVERITY_INFO;
			summary = "Successful";
			message = "The following documents are identified: " + successDoc;
			
		}
		catch (Exception e) {
			severity = FacesMessage.SEVERITY_ERROR;
			summary = e.getClass().getName();
			if (e.getMessage() != null)
				message += e.getMessage() + ".";
			if (successDoc.equals(""))
				message += "No document is identified.";
			else	
				message += "Only the following documents are identified: " + successDoc;
			e.printStackTrace();
		}
		finally {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(severity, summary, message));
		}
	}
}
