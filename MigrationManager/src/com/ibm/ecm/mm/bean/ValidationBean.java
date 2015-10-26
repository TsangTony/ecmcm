package com.ibm.ecm.mm.bean;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;
import com.ibm.ecm.mm.util.DataManager;

public class ValidationBean {
	private ArrayList<Document> documents;
	private Document document;
	private ArrayList<Document> filteredDocuments;
	
	public ValidationBean() {
		setDocuments(DataManager.getDocuments());
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

	public ArrayList<Document> getFilteredDocuments() {
		return filteredDocuments;
	}

	public void setFilteredDocuments(ArrayList<Document> filteredDocuments) {
		this.filteredDocuments = filteredDocuments;
	}
	
	public void generate() {
		String relativeWebPath = "/resources/cmvt.xlsx";
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
		File file = new File(absoluteDiskPath);
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(file);
			XSSFSheet worksheet = workbook.getSheet("Validation Rules");	 
            
            //Document Name
            Cell cell = worksheet.getRow(0).getCell(2);
            cell.setCellValue(getDocument().getName());
            
            //Total # Located on Shared Drive
            IdentifiedDocInstances identifiedDocInstances = DataManager.getIdentifiedDocInstances(getDocument());
            cell = worksheet.getRow(1).getCell(2);
            cell.setCellValue(identifiedDocInstances.size());

            // Source Location
            ArrayList<CommencePath> commencePaths = DataManager.getCommencePaths(getDocument().getId());
            String commencePathsStr = "";
            for (CommencePath commencePath : commencePaths) {
            	commencePathsStr += commencePath.getBusinessPath() + "\n";
            }
            cell = worksheet.getRow(2).getCell(2);
            cell.setCellValue(commencePathsStr);
            XSSFRow row = worksheet.getRow(2);
            row.setHeight((short)(360+360*(commencePathsStr.length() - commencePathsStr.replace("\n", "").length())));
            	            
            //Identification Rule
            cell = worksheet.getRow(3).getCell(2);
            cell.setCellValue(getDocument().getBlIdentificationRule());            

            //Document Class
            cell = worksheet.getRow(4).getCell(2);
            cell.setCellValue(getDocument().getIgDocClass());                 

            //Security Class
            cell = worksheet.getRow(5).getCell(2);
            cell.setCellValue(getDocument().getIgSecClass());    

            //Reference File List   
            worksheet = workbook.getSheet("Reference File List");            
            
            int rflRow = 0;
            for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
            	rflRow++;
        		worksheet.createRow(rflRow);
        		
        		//Full Path
        		worksheet.getRow(rflRow).createCell(1);
        		cell = worksheet.getRow(rflRow).getCell(1);
        		cell.setCellValue(identifiedDocInstance.getVolumePath());        		

        		//File Name
        		worksheet.getRow(rflRow).createCell(1);
        		cell = worksheet.getRow(rflRow).getCell(1);
        		cell.setCellValue(identifiedDocInstance.getName());
            }

    		FacesContext ctx = FacesContext.getCurrentInstance();
    		ExternalContext extCtx = ctx.getExternalContext();
    		
    		extCtx.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    		extCtx.setResponseHeader("Content-Disposition", "attachment;filename=\"Content Migration Validation - " + getDocument().toString() + ".xlsx\"");
    				
    		workbook.write(extCtx.getResponseOutputStream());
    		ctx.responseComplete();
    		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
