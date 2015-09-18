package com.ibm.ecm.mm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

import org.primefaces.model.UploadedFile;

import com.ibm.ecm.mm.model.Document;


public class FileUpload {
	private UploadedFile file;
	private Document document;
	
	@Resource(name="jdbc/migrationmanager")
	private DataSource ds;
	
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
    public void upload() {
        if(file != null) {
        	try {
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputstream()));
				String line;
				
				Connection conn = ds.getConnection();
				
				//TODO: validate document instance
				
				
				
				PreparedStatement insertIdentifiedDocInstanceStmt = conn.prepareStatement("INSERT INTO Identified_Doc_Instance ([name],[extension],[full_path],[volume],[owner],[size],[created],[modified],[accessed],[checksum],[document_id],[validity]) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				
				int lineCount = 0;
				while ((line = br.readLine()) != null) {
					if (lineCount > 0) {
						String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
												
						String filename = splitted[0];
						String extension = filename.contains(".") ? filename.substring(filename.lastIndexOf(".")+1, filename.length()) : "";
						String path = splitted[1];
						String volume = splitted[3];
						String owner = splitted[5];
						Long size = Long.valueOf(splitted[6]);
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						
						Timestamp created = new Timestamp(sdf.parse(splitted[7]).getTime());
						Timestamp modified = new Timestamp(sdf.parse(splitted[8]).getTime());
						Timestamp accessed = new Timestamp(sdf.parse(splitted[9]).getTime());
						String checksum = splitted[10];
						
						insertIdentifiedDocInstanceStmt.setString(1,filename);
						insertIdentifiedDocInstanceStmt.setString(2,extension);
						insertIdentifiedDocInstanceStmt.setString(3,path);
						insertIdentifiedDocInstanceStmt.setString(4,volume);
						insertIdentifiedDocInstanceStmt.setString(5,owner);
						insertIdentifiedDocInstanceStmt.setLong(6, size);
						insertIdentifiedDocInstanceStmt.setTimestamp(7,created);
						insertIdentifiedDocInstanceStmt.setTimestamp(8,modified);
						insertIdentifiedDocInstanceStmt.setTimestamp(9,accessed);
						insertIdentifiedDocInstanceStmt.setString(10,checksum);
						insertIdentifiedDocInstanceStmt.setInt(11,this.getDocument().getId());
						insertIdentifiedDocInstanceStmt.setString(12,"Valid");
						
						insertIdentifiedDocInstanceStmt.addBatch();
					}
				    lineCount++;
				}
				insertIdentifiedDocInstanceStmt.executeBatch();
				
				FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
	            FacesContext.getCurrentInstance().addMessage("messages", message);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
    }
}
