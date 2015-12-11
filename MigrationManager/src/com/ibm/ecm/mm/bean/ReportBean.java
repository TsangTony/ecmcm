package com.ibm.ecm.mm.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.DocumentInstancePair;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.ReportManager;


public class ReportBean {
	private ArrayList<Document> documents;
	private ArrayList<Document> filteredDocument;
	private ArrayList<IdentifiedDocInstance> filteredInstance;
	private ArrayList<DocumentInstancePair> filteredInstancePair;
	private ArrayList<String> reports;
	private String report;
	private ArrayList<ColumnModel> columns;

	public ReportBean() {
		setDocuments(DataManager.getDocumentStatusReport());
		setReports(ReportManager.getReports());
		setReport("");
		
		columns = new ArrayList<ColumnModel>();         
		for (int i = 1; i <= DataManager.LATEST_SNAPSHOT; i++)        
			columns.add(new ColumnModel("Snapshot " + i, i));
        
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	public ArrayList<ColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnModel> columns) {
		this.columns = columns;
	}

	public ArrayList<Document> getMetadataDocuments() {
		ArrayList<Document> documents = new ArrayList<Document>();
//		for (Document document : getDocuments()) {
//			for (int i = 0; i<document.getMetadataProperties().size(); i++) {
//				Document newDocument = new Document();
//				newDocument.setId(document.getId());
//				newDocument.setName(document.getName());
//				newDocument.setTeam(document.getTeam());
//				newDocument.setS1(document.getS1());
//				newDocument.setS1Deleted(document.getS1Deleted());
//				newDocument.setS2New(document.getS2New());
//				newDocument.setMetadataProperties(document.getMetadataProperties());
//				newDocument.setPriority(i);
//				documents.add(newDocument);
//			}
//		}
		return documents;
	}
	
	public ArrayList<IdentifiedDocInstance> getIntraTeamDuplicates() {
		return DataManager.getIntraTeamDuplicates();
	}
	
	public ArrayList<DocumentInstancePair> getInterTeamDuplicates() {
		return DataManager.getInterTeamDuplicates();
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}

	public ArrayList<Document> getFilteredDocument() {
		return filteredDocument;
	}

	public void setFilteredDocument(ArrayList<Document> filteredDocument) {
		this.filteredDocument = filteredDocument;
	}

	public ArrayList<IdentifiedDocInstance> getFilteredInstance() {
		return filteredInstance;
	}

	public void setFilteredInstance(ArrayList<IdentifiedDocInstance> filteredInstance) {
		this.filteredInstance = filteredInstance;
	}

	public ArrayList<DocumentInstancePair> getFilteredInstancePair() {
		return filteredInstancePair;
	}

	public void setFilteredInstancePair(ArrayList<DocumentInstancePair> filteredInstancePair) {
		this.filteredInstancePair = filteredInstancePair;
	}

	public boolean isDocumentStatusReport() {
		return getReport().equals(ReportManager.DOCUMENT_STATUS_REPORT);
	}

	public boolean isMetadataStatusReport() {
		return getReport().equals(ReportManager.METADATA_STATUS_REPORT);
	}

	public boolean isIntraTeamDuplicateReport() {
		return getReport().equals(ReportManager.INTRA_TEAM_DUPLICATE_REPORT);
	}

	public boolean isInterTeamDuplicateReport() {
		return getReport().equals(ReportManager.INTER_TEAM_DUPLICATE_REPORT);
	}

	public ArrayList<String> getReports() {
		return reports;
	}

	public void setReports(ArrayList<String> reports) {
		this.reports = reports;
	}
	
	public String getReport() {
		return report;
	}
	
	public void setReport(String report) {
		this.report = report;
	}
	
	public void reportSelected() {
		
		
	}
	
	static public class ColumnModel implements Serializable {
		 
        private String header;
        private int property;
 
        public ColumnModel(String header, int property) {
            this.header = header;
            this.property = property;
        }
 
        public String getHeader() {
            return header;
        }
 
        public int getProperty() {
            return property;
        }
    }
	
}
