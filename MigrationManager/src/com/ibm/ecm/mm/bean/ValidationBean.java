package com.ibm.ecm.mm.bean;

import java.io.File;
import java.util.ArrayList;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.ecm.mm.model.CommencePath;
import com.ibm.ecm.mm.model.Document;
import com.ibm.ecm.mm.model.IdentifiedDocInstance;
import com.ibm.ecm.mm.model.IdentifiedDocInstances;
import com.ibm.ecm.mm.model.MetadataExtractionRule;
import com.ibm.ecm.mm.model.MetadataProperty;
import com.ibm.ecm.mm.model.MetadataValue;
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
            IdentifiedDocInstances identifiedDocInstances = DataManager.getIdentifiedDocInstancesWithMetadataValues(getDocument().getId());
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

            //Metadata Extraction Rule
            ArrayList<ArrayList<MetadataExtractionRule>> metadataExtractionRulesList = new ArrayList<ArrayList<MetadataExtractionRule>>();
            ArrayList<MetadataProperty> metadataProperties = DataManager.getMetadataPropreties(getDocument().getId());

    		for (MetadataProperty metadataProperty : metadataProperties) {            	
            	ArrayList<MetadataExtractionRule> metadataExtractionRules = DataManager.getMetadataExtractionRules(getDocument().getId(),0,metadataProperty.getId());
            	metadataExtractionRulesList.add(metadataExtractionRules);
    		}            
    		
            //Reference File List   
            worksheet = workbook.getSheet("Reference File List");            
            
            
            //clear extra column
            for (int j=metadataProperties.size()+2; j<18; j++) {
            	worksheet.getRow(0).removeCell(worksheet.getRow(0).getCell(j));
            }	  
            
            int metadataCount = 0;
            for (MetadataProperty metadataProperty : metadataProperties) {
            	cell = worksheet.getRow(0).createCell(metadataCount+2);
        		cell.setCellValue(metadataProperty.getName());    
        		metadataCount++;
            }
            
            int rflRow = 0;
            for (IdentifiedDocInstance identifiedDocInstance : identifiedDocInstances) {
            	rflRow++;
        		worksheet.createRow(rflRow);
        		
        		//Full Path
        		worksheet.getRow(rflRow).createCell(0).setCellValue(identifiedDocInstance.getVolumePath());    		

        		//File Name
        		worksheet.getRow(rflRow).createCell(1).setCellValue(identifiedDocInstance.getName());

        		metadataCount = 0;
        		for (MetadataProperty metadataProperty : metadataProperties) {
	        		for (MetadataValue metadataValue : identifiedDocInstance.getMetadataValues()) {  			
		        		if (metadataProperty.getId() == metadataValue.getMetadataProperty().getId()) {
		                    worksheet.getRow(rflRow).createCell(metadataCount+2).setCellValue(metadataValue.getValue());
		                    
		            		if (metadataValue.getValue() != null) {
		            			for (ArrayList<MetadataExtractionRule> metadataExtractionRules : metadataExtractionRulesList) {
			            			for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules) {
			            				if (metadataExtractionRule.getId() == metadataValue.getMetadataExtractionRule().getId()) {
			            					metadataExtractionRule.setSuccessCount(metadataExtractionRule.getSuccessCount()+1);
			            					if (metadataExtractionRule.getExample() == null)
			            						metadataExtractionRule.setExample(metadataValue.getValue());
			            				}
			            			}
		            			}
		            		}
		        			break;
	        			}
	        		}
	        		metadataCount++;
        		}
            }            
            
            //autofilter
            
            CellRangeAddress range = new CellRangeAddress(0, identifiedDocInstances.size(), 0, metadataProperties.size()+1);
			worksheet.setAutoFilter(range);
            
            //Metadata Extraction Rule
			worksheet = workbook.getSheet("Validation Rules");	

            int latestRow = 8;
            boolean noFilePathRule = true;
            boolean noContentRule = true;
            boolean noDefaultRule = true;

            metadataCount = 0;
    		for (MetadataProperty metadataProperty : metadataProperties) {
            	cell = worksheet.getRow(latestRow).getCell(0);
            	cell.setCellValue(metadataProperty.getName());
            	
            	int totalSccessCount = 0;
            	
            	ArrayList<MetadataExtractionRule> metadataExtractionRules = metadataExtractionRulesList.get(metadataCount);
            	
            	for (MetadataExtractionRule metadataExtractionRule : metadataExtractionRules) {
            		cell = worksheet.getRow(latestRow).getCell(1);
            		cell.setCellValue(metadataExtractionRule.getSource());
            		if ((metadataExtractionRule.getSource().equals("File Path")))
            			noFilePathRule = false;
            		else if ((metadataExtractionRule.getSource().equals("Content")))
            			noContentRule = false;
            		else if ((metadataExtractionRule.getSource().equals("Default"))) {
            			noDefaultRule = false;
                		cell = worksheet.getRow(latestRow).getCell(3);
                		cell.setCellValue(metadataExtractionRule.getDefaultValue());  
            		}
            		cell = worksheet.getRow(latestRow).getCell(2);
            		cell.setCellValue(metadataExtractionRule.getBlRule());
            		

                    //example
        			cell = worksheet.getRow(latestRow).getCell(4);
            		cell.setCellValue(metadataExtractionRule.getExample());
            		
                    //success rate
        			cell = worksheet.getRow(latestRow).getCell(5);
            		cell.setCellFormula(metadataExtractionRule.getSuccessCount() + "/C2");
            		
            		totalSccessCount += metadataExtractionRule.getSuccessCount();
            		
            		latestRow++;
    			}
            	
            	if (noFilePathRule) {
            		cell = worksheet.getRow(latestRow).getCell(1);
            		cell.setCellValue("File Path");   
            		cell = worksheet.getRow(latestRow).getCell(2);
            		cell.setCellValue("-");   
            		cell = worksheet.getRow(latestRow).getCell(5);
            		cell.setCellValue(0);   
            		latestRow++;   
            	}
            	if (noContentRule) {
            		cell = worksheet.getRow(latestRow).getCell(1);
            		cell.setCellValue("Content");    
            		cell = worksheet.getRow(latestRow).getCell(2);
            		cell.setCellValue("-");   
            		cell = worksheet.getRow(latestRow).getCell(5);
            		cell.setCellValue(0);   
            		latestRow++;
            	}
            	if (noDefaultRule) {
            		cell = worksheet.getRow(latestRow).getCell(1);
            		cell.setCellValue("Default");
            		cell = worksheet.getRow(latestRow).getCell(2);
            		cell.setCellValue("Leave Blank");   
            		cell = worksheet.getRow(latestRow).getCell(5);
            		cell.setCellFormula("(C2-" + totalSccessCount + ")/C2");
            		latestRow++;    
            	}
            	
            	
            	for (int i=0; i<7; i++) {
            		cell = worksheet.getRow(latestRow-1).getCell(i);
            		CellStyle style = workbook.createCellStyle();
            		style.cloneStyleFrom(cell.getCellStyle());
                    style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
            		cell.setCellStyle(style);
            	}
            	
            	
            	metadataCount++;
    		}
    		
    		if (metadataProperties.size() == 0)
    			latestRow = 6;
    		
    		
            //clear extra rows
            for (int j=latestRow; j<88; j++)
            	worksheet.removeRow(worksheet.getRow(j));
            
            //set starting cell
            workbook.setActiveSheet(2);
            worksheet = workbook.getSheetAt(2);
            worksheet.setActiveCell("A1");
            worksheet.showInPane(0, 0);
            
    		FacesContext ctx = FacesContext.getCurrentInstance();
    		ExternalContext extCtx = ctx.getExternalContext();
    		
    		extCtx.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    		extCtx.setResponseHeader("Content-Disposition", "attachment;filename=\"" + getDocument().toString() + ".xlsx\"");
    				
    		workbook.write(extCtx.getResponseOutputStream());
    		ctx.responseComplete();
    		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
