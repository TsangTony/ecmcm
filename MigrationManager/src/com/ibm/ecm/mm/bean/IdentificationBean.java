package com.ibm.ecm.mm.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import javax.faces.application.FacesMessage;
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
	private HashSet<Long> existingIdentifiedDocInstanceIds;
	private boolean noPdf;

	public IdentificationBean() {
		setDocuments(DataManager.getDocuments());
		setIdentifiedDocInstances(new IdentifiedDocInstances());
		setExistingIdentifiedDocInstanceIds(new HashSet<Long>());
		setNoPdf(false);
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
	public ArrayList<IdentifiedDocInstance> getLatestSnapshotInstances() {
		return getIdentifiedDocInstances().getLatestSnapshotInstances();
	}

	public HashSet<Long> getExistingIdentifiedDocInstanceIds() {
		return existingIdentifiedDocInstanceIds;
	}

	public void setExistingIdentifiedDocInstanceIds(HashSet<Long> existingIdentifiedDocInstanceIds) {
		this.existingIdentifiedDocInstanceIds = existingIdentifiedDocInstanceIds;
	}

	public boolean isNoPdf() {
		return noPdf;
	}

	public void setNoPdf(boolean noPdf) {
		this.noPdf = noPdf;
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
			getExistingIdentifiedDocInstanceIds().clear();
			for (IdentifiedDocInstance identifiedDocInstance : DataManager.getIdentifiedDocInstances(getDocument())) {
				getExistingIdentifiedDocInstanceIds().add(identifiedDocInstance.getId());
			}
		}
		catch (Exception e) {
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
			setIdentifiedDocInstances(IdentificationManager.identify(getDocument(),isNoPdf()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		
		
		try {
			Connection conn = ConnectionManager.getConnection("save");	
			
			/*
			 *  BL Identification Rule
			 */
			
			PreparedStatement updatDocumentStmt = conn.prepareStatement("UPDATE Document SET bl_identification_rule=? WHERE ID = ?");
			updatDocumentStmt.setString(1, getDocument().getBlIdentificationRule());
			updatDocumentStmt.setInt(2, getDocument().getId());
			updatDocumentStmt.execute();
			
			
			/*
			 *  Commence Path
			 */
			
			PreparedStatement deleteCommencePathStmt = conn.prepareStatement("DELETE FROM Commence_Path WHERE id = ?");
			for (CommencePath commencePath : getDocument().getCommencePaths().getRemovedList()) {
				deleteCommencePathStmt.setInt(1, commencePath.getId());
				deleteCommencePathStmt.addBatch();
			}
			deleteCommencePathStmt.executeBatch();

			getDocument().getCommencePaths().getRemovedList().clear();
			
			//TODO: Delete Metadata_Extraction_Rule, Metadata_Value
			

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
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close("save");
		}	
					
		String message = "Source Locations and Identification Rules are saved.";
		
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful",  message) );

	}
	

	public void run() {
		preview();
		IdentificationManager.saveIdentifiedDocInstances(getDocument(), getExistingIdentifiedDocInstanceIds(), getIdentifiedDocInstances());
	}
}
