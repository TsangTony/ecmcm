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
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.ConnectionManager;

public class Identification {
	
	private ArrayList<Document> documents;
	private Document document;
	private ArrayList<IdentifiedDocInstance> identifiedDocInstances;

	public Identification() {
		setDocuments(DataManager.getDocuments());
		setIdentifiedDocInstances(new ArrayList<IdentifiedDocInstance>());
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
	public ArrayList<IdentifiedDocInstance> getIdentifiedDocInstances() {
		return identifiedDocInstances;
	}
	public void setIdentifiedDocInstances(ArrayList<IdentifiedDocInstance> identifiedDocInstances) {
		this.identifiedDocInstances = identifiedDocInstances;
	}

	public int getCommencePathsLastIndex() {
		return getDocument().getCommencePaths().size()-1;
	}
	
	public int getIdentificationRulesLastIndex() {
		return getDocument().getIdentificationRules().size()-1;
	}	
	
	public void documentSelected() {
		getDocument().setCommencePaths(DataManager.getCommencePaths(getDocument().getId()));
		getDocument().setIdentificationRules(DataManager.getIdentificationRules(getDocument().getId()));	
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
	
	public void preview() {
		
		String query = "SELECT id, name, path, digest FROM All_Document_Instance WHERE (";
		for (CommencePath commencePath : getDocument().getCommencePaths()) {
			query += "volume + '/' + ISNULL(path,'') like '" + commencePath.getActualPath() + "%' OR ";
		}		
		query += "1=0) AND (";
		
		for (IdentificationRule identificationRule : getDocument().getIdentificationRules()) {			
			if (identificationRule.getLogicalOperator() != null)
				query += identificationRule.getLogicalOperator() + " ";
			
			if (identificationRule.getAttribute().equals("Filename"))
				query += "name ";
			else if (identificationRule.getAttribute().equals("File Path"))
				query += "ISNULL(path,'') ";
			else if (identificationRule.getAttribute().equals("Extension"))
				query += "extension ";
			
			if (identificationRule.getRelationalOperator().equals("Equals"))
				query += "= '" + identificationRule.getValue() + "'";
			else if (identificationRule.getRelationalOperator().equals("Not equals"))				
				query += "!= '" + identificationRule.getValue() + "'";
			else if (identificationRule.getRelationalOperator().equals("Contains"))
				query += "LIKE '%" + identificationRule.getValue() + "%'";
			else if (identificationRule.getRelationalOperator().equals("Not contains"))
				query += "NOT LIKE '%" + identificationRule.getValue() + "%'";			
		}		
			
		query += ") ";
		
		//TODO: IG rules from DB
		
		//extension		
		query += "AND extension in ('doc','docx','dot','docm','dotx','dotm','docb',"
			  +  "'xls','xlt','xlm','xlsx','xlsm','xltx','xltm','xlsb','xla','xll',"
			  +  "'xlw','ppt','pot','pps','pptx','pptm','potx','potm','ppam','ppsx',"
			  +  "'ppsm','sldx','sldm','VSD','VST','VSW','VDX','VSX','VTX','VSDX',"
			  +  "'VSDM','VSSX','VSSM','VSTX','VSTM','VSL','pdf','csv'";
		
		if (getDocument().getTeam().contains("Training")) {
			query += ",'afc','wav','mp3','aif','rm','mid','aob','3gp','aiff','aac',"
				  +  "'ape','au','flac','m4a','m4p','ra','raw','wma','mkv','flv','vob',"
				  +  "'avi','mov','qt','wmv','rmvb','asf','mpg','mpeg','mp4',',mpe',"
				  +  "'mpv','m2v','m4v','3g2','mxf','jpg','jpeg','tif','tiff','gif',"
				  +  "'bmp','png','img','psd','cpt','ai','svg','ico'";
		}
		
		query += ") ";
		
		//retention
		if (getDocument().getIgDocClass().equals("General Document")) {
			query += "AND mtime > DATEADD(YEAR,-7,GETDATE())";
		}
		
		int identifiedDocInstanceCount = 0;
		
		try {		
			Connection conn = ConnectionManager.getConnection();						
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);		
			
			getIdentifiedDocInstances().clear();
			HashSet<String> digests = new HashSet<String>();
			
			while (rs.next()) {
				String digest = rs.getString(4);
				if (!digests.contains(digest)) {				
					IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
					identifiedDocInstance.setId(rs.getLong(1));	
					identifiedDocInstance.setName(rs.getString(2));	
					identifiedDocInstance.setPath(rs.getString(3));
					identifiedDocInstance.setDigest(digest);
					getIdentifiedDocInstances().add(identifiedDocInstance);				
					digests.add(digest);
					identifiedDocInstanceCount++;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}
		
		
		
		if (identifiedDocInstanceCount == 0)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No document instance is identified."));
		else if (identifiedDocInstanceCount == 1)
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "1 document instance is identified."));
		else
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", identifiedDocInstanceCount + " document instances are identified."));
		
	}
	
	public void runAndSave() {
		preview();
		
		try {
			Connection conn = ConnectionManager.getConnection();	
			
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
			
			PreparedStatement updateIdentificationRuleStmt = conn.prepareStatement("UPDATE Identification_Rule SET operator_1=?, attribute=?, operator_2=?, value=? WHERE ID = ?");
			PreparedStatement insertIdentificationRuleStmt = conn.prepareStatement("INSERT INTO Identification_Rule (operator_1,attribute,operator_2,value,document_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (IdentificationRule identificationRule : getDocument().getIdentificationRules()) {
				if (identificationRule.isNew()) {
					insertIdentificationRuleStmt.setString(1, identificationRule.getLogicalOperator());
					insertIdentificationRuleStmt.setString(2, identificationRule.getAttribute());
					insertIdentificationRuleStmt.setString(3, identificationRule.getRelationalOperator());
					insertIdentificationRuleStmt.setString(4, identificationRule.getValue());
					insertIdentificationRuleStmt.setInt(5, getDocument().getId());
					insertIdentificationRuleStmt.execute();
					
					ResultSet identificationRuleIdRs = insertIdentificationRuleStmt.getGeneratedKeys();
					if (identificationRuleIdRs != null && identificationRuleIdRs.next()) {
						identificationRule.setId(identificationRuleIdRs.getInt(1));
					}
					
					identificationRule.setNew(false);
				}
				else {
					updateIdentificationRuleStmt.setString(1, identificationRule.getLogicalOperator());
					updateIdentificationRuleStmt.setString(2, identificationRule.getAttribute());
					updateIdentificationRuleStmt.setString(3, identificationRule.getRelationalOperator());
					updateIdentificationRuleStmt.setString(4, identificationRule.getValue());
					updateIdentificationRuleStmt.setInt(5, identificationRule.getId());
					updateIdentificationRuleStmt.addBatch();
				}
			}
			
			updateIdentificationRuleStmt.executeBatch();
			
			/*
			 *  Identified Doc Instance
			 */
			
			PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Identified_Document_Instance WHERE document_id = ?");
			deleteIdentifiedDocInstanceStmt.setInt(1, getDocument().getId());
			deleteIdentifiedDocInstanceStmt.execute();
			
			PreparedStatement insertIdentifiedDocInstancStmt = conn.prepareStatement("INSERT INTO Identified_Document_Instance (id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,document_id) SELECT id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,? FROM All_Document_Instance WHERE id = ?");
			
			for (IdentifiedDocInstance identifiedDocInstance : getIdentifiedDocInstances()) {
				insertIdentifiedDocInstancStmt.setInt(1, getDocument().getId());
				insertIdentifiedDocInstancStmt.setLong(2, identifiedDocInstance.getId());
				insertIdentifiedDocInstancStmt.addBatch();
			}
			
			insertIdentifiedDocInstancStmt.executeBatch();
			
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Successful",  "Source Locations, Identification Rules and Identified Document Instances are saved. ") );
		}
		catch (SQLException e) {				
			e.printStackTrace();
		}
		finally {
			ConnectionManager.close();
		}	
		
		//TODO: run metadata extraction
	}
	
}
