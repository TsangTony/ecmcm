package com.ibm.ecm.mm.bean;

import java.io.IOException;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.GenerationManager;

public class GenerationBean {
	
	private ArrayList<Document> documents;
	private Document document;
	
	public GenerationBean() {		
		setDocuments(new ArrayList<Document>());
		Document document = new Document();
		document.setId(0);
		getDocuments().add(document);
		getDocuments().addAll(DataManager.getDocuments());
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

	public void generate() {
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment;filename=file.xml");  

		ServletOutputStream out;
		
		try {
			out = response.getOutputStream();
			out.write(GenerationManager.generate(getDocument().getId()));
			out.flush(); 
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			ctx.responseComplete();
		}		
	}
}
