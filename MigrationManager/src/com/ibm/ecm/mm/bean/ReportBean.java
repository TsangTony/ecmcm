package com.ibm.ecm.mm.bean;

import java.util.ArrayList;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.ReportManager;


public class ReportBean {
	private ArrayList<Document> documents;
	private ArrayList<Document> filteredDocument;
	private boolean documentStatusReport;
	private boolean metadataStatusReport;
	private ArrayList<String> reports;
	private String report;

	public ReportBean() {
		setDocuments(DataManager.getDocumentStatusReport());
		setDocumentStatusReport(false);
		setMetadataStatusReport(false);
		setReports(ReportManager.getReports());
	}
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	public ArrayList<Document> getMetadataDocuments() {
		ArrayList<Document> documents = new ArrayList<Document>();
		for (Document document : getDocuments()) {
			for (int i = 0; i<document.getMetadataProperties().size(); i++) {
				Document newDocument = new Document();
				newDocument.setId(document.getId());
				newDocument.setName(document.getName());
				newDocument.setTeam(document.getTeam());
				newDocument.setS1(document.getS1());
				newDocument.setS1Deleted(document.getS1Deleted());
				newDocument.setS2New(document.getS2New());
				newDocument.setMetadataProperties(document.getMetadataProperties());
				newDocument.setPriority(i);
				documents.add(newDocument);
			}
		}
		return documents;
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

	public boolean isDocumentStatusReport() {
		return documentStatusReport;
	}

	public void setDocumentStatusReport(boolean documentStatusReport) {
		this.documentStatusReport = documentStatusReport;
	}

	public boolean isMetadataStatusReport() {
		return metadataStatusReport;
	}

	public void setMetadataStatusReport(boolean metadataStatusReport) {
		this.metadataStatusReport = metadataStatusReport;
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
		if (getReport().equals(ReportManager.DOCUMENT_STATUS_REPORT)) {
			setDocumentStatusReport(true);
			setMetadataStatusReport(false);
		}
		else if (getReport().equals(ReportManager.METADATA_STATUS_REPORT)) {
			setDocumentStatusReport(false);
			setMetadataStatusReport(true);
		}
		
	}
	
}
