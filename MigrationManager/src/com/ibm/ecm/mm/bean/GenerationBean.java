package com.ibm.ecm.mm.bean;

import java.io.IOException;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.mm.model.DocumentClass;
import com.ibm.ecm.mm.util.DataManager;
import com.ibm.ecm.mm.util.GenerationManager;

public class GenerationBean {
	
	private ArrayList<DocumentClass> documentClasses;
	private DocumentClass documentClass;
	
	public GenerationBean() {		
		setDocumentClasses(new ArrayList<DocumentClass>());
		DocumentClass documentClass = new DocumentClass();
		documentClass.setId(0);
		getDocumentClasses().add(documentClass);
		getDocumentClasses().addAll(DataManager.getDocumentClasses());
	}
	

	public ArrayList<DocumentClass> getDocumentClasses() {
		return documentClasses;
	}


	public void setDocumentClasses(ArrayList<DocumentClass> documentClasses) {
		this.documentClasses = documentClasses;
	}


	public DocumentClass getDocumentClass() {
		return documentClass;
	}


	public void setDocumentClass(DocumentClass documentClass) {
		this.documentClass = documentClass;
	}
	
	public void generate() {		
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment;filename=\"" + getDocumentClass().getName() + ".xml\"");  

		ServletOutputStream out;
		
		try {
			out = response.getOutputStream();
			out.write(GenerationManager.generate(getDocumentClass().getId()));
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
