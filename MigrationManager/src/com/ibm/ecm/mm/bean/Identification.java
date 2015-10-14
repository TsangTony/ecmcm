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
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.ExtractionManager;
import com.ibm.ecm.mm.util.ConnectionManager;

public class Identification {
	
	private ArrayList<Document> documents;
	private Document document;
	private ArrayList<IdentifiedDocInstance> identifiedDocInstances;
	private HashSet<Long> identifiedDocInstanceIds;
	private HashSet<Long> removedIdentifiedDocInstanceIds;

	public Identification() {
		setDocuments(DataManager.getDocuments());
		setIdentifiedDocInstances(new ArrayList<IdentifiedDocInstance>());
		setIdentifiedDocInstanceIds(new HashSet<Long>());
		setRemovedIdentifiedDocInstanceIds(new HashSet<Long>());;
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

	public HashSet<Long> getIdentifiedDocInstanceIds() {
		return identifiedDocInstanceIds;
	}

	public void setIdentifiedDocInstanceIds(HashSet<Long> identifiedDocInstanceIds) {
		this.identifiedDocInstanceIds = identifiedDocInstanceIds;
	}
	
	public HashSet<Long> getRemovedIdentifiedDocInstanceIds() {
		return removedIdentifiedDocInstanceIds;
	}

	public void setRemovedIdentifiedDocInstanceIds(HashSet<Long> removedIdentifiedDocInstanceIds) {
		this.removedIdentifiedDocInstanceIds = removedIdentifiedDocInstanceIds;
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
		setIdentifiedDocInstances(DataManager.getIdentifiedDocInstances(getDocument()));
		getIdentifiedDocInstanceIds().clear();
		for (IdentifiedDocInstance identifiedDocInstance : getIdentifiedDocInstances()) {
			getIdentifiedDocInstanceIds().add(identifiedDocInstance.getId());
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
	
	public boolean preview() {
		
		boolean hasError = false;
		
		String query = "SELECT id, name, path, volume, digest, extension, server FROM All_Document_Instance WHERE snapshot_deleted IS NULL AND (";
		for (CommencePath commencePath : getDocument().getCommencePaths()) {
			query += "volume + '/' + ISNULL(path,'') like '" + commencePath.getActualPath() + "/%' OR ";
			query += "volume + '/' + ISNULL(path,'') = '" + commencePath.getActualPath() + "' OR ";
		}		
		query += "1=0) ";
				
		if (getDocument().getIdentificationRules().size() != 0) {
			query += " AND (";
		
			for (IdentificationRule identificationRule : getDocument().getIdentificationRules()) {
				
				if (identificationRule.getPriority() != 1 && identificationRule.getLogicalOperator() != null)
					query += identificationRule.getLogicalOperator() + " ";
	
				query += identificationRule.getLeftParen();
				
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
				
				query += identificationRule.getRightParen() + " ";
			}		
				
			query += ") ";
		}
		
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
			
			System.out.println(query);
			
			getIdentifiedDocInstances().clear();
			
			ArrayList<IdentifiedDocInstance> identifiedDocInstances = new ArrayList<IdentifiedDocInstance>();
			
			HashSet<String> digests = new HashSet<String>();
			
			while (rs.next()) {
				String digest = rs.getString(5);
				if (!digests.contains(digest)) {				
					IdentifiedDocInstance identifiedDocInstance = new IdentifiedDocInstance();
					identifiedDocInstance.setId(rs.getLong(1));	
					identifiedDocInstance.setName(rs.getNString(2));	
					identifiedDocInstance.setPath(rs.getNString(3));
					identifiedDocInstance.setVolume(rs.getString(4));
					identifiedDocInstance.setDigest(digest);
					identifiedDocInstance.setExtension(rs.getString(6));
					identifiedDocInstance.setServer(rs.getString(7));
					
					for (CommencePath commencePath : getDocument().getCommencePaths()) {
						String fullPath = identifiedDocInstance.getPath() == null ? identifiedDocInstance.getVolume() : identifiedDocInstance.getVolume() + "/" + identifiedDocInstance.getPath();
						if (fullPath.equals(commencePath.getActualPath()) || fullPath.startsWith(commencePath.getActualPath() + "/")) {
							identifiedDocInstance.setCommencePath(commencePath);
						}
					}
					
					identifiedDocInstances.add(identifiedDocInstance);				
					digests.add(digest);
					identifiedDocInstanceCount++;					
				}			
				
			}	
			
			setIdentifiedDocInstances(identifiedDocInstances);
			
		}
		catch (SQLException e) {
			System.err.println(e.getMessage() + " - " + query);
			hasError = true;
		}
		finally {
			ConnectionManager.close();
		}
		
		
		if (hasError) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Identification Rules have error."));
		}
		else {
			if (identifiedDocInstanceCount == 0)
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No document instance is identified."));
			else if (identifiedDocInstanceCount == 1)
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "1 document instance is identified."));
			else
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", identifiedDocInstanceCount + " document instances are identified."));
		}
		return hasError;
	}
	
	public void save() {
		
		boolean hasError = preview();
		
		if (!hasError) {
		
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
				ConnectionManager.close();
			}	
						
			String message = "Source Locations and Identification Rules are saved.";
			
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful",  message) );
		}
		else {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  "Identification Rules have error. Nothing is saved.") );
		}
	}
	

	public void run() {
		
		boolean hasError = preview();
		
		if (!hasError) {
		
			try {
				Connection conn = ConnectionManager.getConnection();
				
				/*
				 *  Identified Doc Instance
				 */
				
				
				PreparedStatement insertIdentifiedDocInstancStmt = conn.prepareStatement("INSERT INTO Identified_Document_Instance (id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,document_id) SELECT id,name,extension,path,server,volume,owner,size,ctime,mtime,atime,digest,snapshot,snapshot_deleted,? FROM All_Document_Instance WHERE id = ?");
				insertIdentifiedDocInstancStmt.setInt(1, getDocument().getId());
				HashSet<Long> newIds = new HashSet<Long>();
				for (IdentifiedDocInstance identifiedDocInstance : getIdentifiedDocInstances()) {
					if (!getIdentifiedDocInstanceIds().contains(identifiedDocInstance.getId())) {
						insertIdentifiedDocInstancStmt.setLong(2, identifiedDocInstance.getId());
						insertIdentifiedDocInstancStmt.addBatch();
					}
					newIds.add(identifiedDocInstance.getId());
				}
				
				getRemovedIdentifiedDocInstanceIds().clear();
				getRemovedIdentifiedDocInstanceIds().addAll(getIdentifiedDocInstanceIds());
				getRemovedIdentifiedDocInstanceIds().removeAll(newIds);		
				
				
				PreparedStatement deleteIdentifiedDocInstanceStmt = conn.prepareStatement("DELETE FROM Identified_Document_Instance WHERE document_id = ? AND id = ?");
				deleteIdentifiedDocInstanceStmt.setInt(1, getDocument().getId());
				PreparedStatement deleteMetadataValueStmt = conn.prepareStatement("DELETE FROM Metadata_Value WHERE document_id = ? AND identified_document_instance_id = ?");
				deleteMetadataValueStmt.setInt(1, getDocument().getId());
				for (Long removedIdentifiedDocInstanceId : getRemovedIdentifiedDocInstanceIds()) {
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
						
			/*
			 *  Metadata Extraction
			 */
			
			ArrayList<MetadataProperty> extractedMetadataProperties = ExtractionManager.extractMetadata(getIdentifiedDocInstances(), getDocument());
			String message = "Identified Document Instances are saved.";
			if (extractedMetadataProperties.size() == 0) {
				message += " There is no default Metadata Property extracted.";
			}
			else {
				message += "The following Metadata Properties are extracted and saved. <ol>";
			for (MetadataProperty metadataProperty : extractedMetadataProperties) {
				message += "<li>" + metadataProperty.getName() + "</li>";
			}
			message += "</ol>";
			}
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful",  message) );
		}
		else {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  "Identification Rules have error. Nothing is saved.") );
		}
	}
}
