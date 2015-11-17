package com.ibm.ecm.mm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.ibm.ecm.mm.util.Util;

public class IdentifiedDocInstance extends DataTableElement {
	private long id;
	private String name;
	private String extension;
	private String server;
	private String volume;
	private String path;
	private String digest;
	private ArrayList<MetadataValue> metadataValues;
	private CommencePath commencePath;
	private String snippet;
	private int snapshotDeleted;
	private Document document;
	private long originInstanceId;
	
	public IdentifiedDocInstance() {
		setMetadataValues(new ArrayList<MetadataValue>());
		setSnippet("");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getPath() {
		return path == null ? "" : path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getNameWithoutExtension() {
		if (getExtension()!=null)
			return getName().substring(0, getName().lastIndexOf("." + getExtension()));
		return getName();
	}
	public String getVolumePath() {
		return getPath().equals("") ? getVolume() : getVolume() + "/" + getPath();
	}
	public String getFullPath() {
		return getPath().equals("") ? getVolume() + "/" + getName() : getVolume() + "/" + getPath() + "/" + getName();
	}
	public String getFullyQualifiedPath() {
		return "\\\\" + getServer() + "/" + getFullPath();
	}
	public String getUnixMountedPath() {
		return getPath().equals("") ? "/mnt/" + getVolume().toUpperCase() + "/" + getName() : "/mnt/" + getVolume().toUpperCase() + "/" + getPath() + "/" + getName();
	}

	public ArrayList<MetadataValue> getMetadataValues() {
		return metadataValues;
	}

	public void setMetadataValues(ArrayList<MetadataValue> metadataValues) {
		this.metadataValues = metadataValues;
	}
	
	public MetadataValue getMetadataValue() {
		if (getMetadataValues().size() > 0) {
			return metadataValues.get(0);			
		}
		return null;		
	}

	public String getContent() {
		String content = "";		
		PDDocument pdfDocument = null;	
		WordExtractor docExtractor = null;
		XWPFWordExtractor docxExtractor = null;
		PowerPointExtractor pptExtractor = null;	
		XSLFPowerPointExtractor pptxExtractor = null;	
		HSSFWorkbook wb = null;
		XSSFWorkbook xwb = null;
		
		File file = null;
		FileInputStream fis = null;
		try {
			//Windows
			file = new File(getFullyQualifiedPath());
			fis = new FileInputStream(file.getAbsolutePath());
		}
		catch (FileNotFoundException e1) {
			//Unix
			try {
				file = new File(getUnixMountedPath());
				fis = new FileInputStream(file.getAbsolutePath());
			}
			catch (FileNotFoundException e) {
				String log = "DOC-" + getDocument().getId() + ": Cannot found " + getFullyQualifiedPath();
				if (getSnapshotDeleted() > 0) {
					log += " as expected";
					//System.out.println(log);
				}
				else {
					log += " unexpectedly";
					System.out.println(Util.getTimeStamp() + log);		
					System.err.println(Util.getTimeStamp() + log);				
				}
				return content;
			}
		}
		
			
		try {
			if (getExtension().toUpperCase().equals("PDF")) {						
				PDFTextStripper stripper = new PDFTextStripper();
				pdfDocument = PDDocument.load(file);
				content = stripper.getText(pdfDocument);
			}			
			else if (getExtension().toUpperCase().equals("DOC")) {
				HWPFDocument document = new HWPFDocument(fis);
				docExtractor = new WordExtractor(document);
				content = docExtractor.getText();
			}
			else if (getExtension().toUpperCase().equals("DOCX")) {
				XWPFDocument document = new XWPFDocument(fis);
				docxExtractor = new XWPFWordExtractor(document);
				content = docxExtractor.getText();
			}
			else if (getExtension().toUpperCase().equals("PPT") ||
					 getExtension().toUpperCase().equals("PPS")) {
				pptExtractor = new PowerPointExtractor(fis);
				content = pptExtractor.getText(true,true,true,true);
			}
			else if (getExtension().toUpperCase().equals("PPTX") ||
					 getExtension().toUpperCase().equals("PPSX")) {
				XMLSlideShow ppt = new XMLSlideShow(fis);
				pptxExtractor = new XSLFPowerPointExtractor(ppt);
				content	= pptxExtractor.getText(true,true,true);
			}
			else if (getExtension().toUpperCase().equals("XLS")) {				
				wb = new HSSFWorkbook(fis);
				ExcelExtractor xlsExtractor = new ExcelExtractor(wb);				
				content = xlsExtractor.getText();
				xlsExtractor.close();
			}
			else if (getExtension().toUpperCase().equals("XLSX")) {
				xwb = new XSSFWorkbook(fis);
				XSSFExcelExtractor xlsxExtractor = new XSSFExcelExtractor(xwb);	
				content = xlsxExtractor.getText();
				xlsxExtractor.close();
			}
			else if (getExtension().toUpperCase().equals("TXT")) {
				int contentByte;
				while ((contentByte = fis.read()) != -1) {
					content += String.valueOf((char) contentByte);
				}
			}
			else {
				//System.err.println("Cannot read content from " + getName() + " ("+ getId() +") due to unsupported file format.");	
			}	
		}
		catch (OutOfMemoryError e) {
			System.err.println(Util.getTimeStamp() + "DOC-" + getDocument().getId() + ":Cannot read content from " + getName() + " ("+ getId() +") because the file is too big.");
		}
		catch (Exception e) {
			System.err.println(Util.getTimeStamp() + "DOC-" + getDocument().getId() + ":Cannot read content from " + getName() + " ("+ getId() +") due to error. " + e.getMessage());
		}
		finally {
			try {
				if (pdfDocument != null)
					pdfDocument.close();
				if (docExtractor != null)
					docExtractor.close();
				if (docxExtractor != null)
					docxExtractor.close();
				if (pptExtractor != null)
					pptExtractor.close();
				if (pptxExtractor != null)
					pptxExtractor.close();
				if (wb != null)
					wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		

		return content;
	}

	public CommencePath getCommencePath() {
		return commencePath;
	}

	public void setCommencePath(CommencePath commencePath) {
		this.commencePath = commencePath;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public int getSnapshotDeleted() {
		return snapshotDeleted;
	}

	public void setSnapshotDeleted(int snapshotDeleted) {
		this.snapshotDeleted = snapshotDeleted;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public long getOriginInstanceId() {
		return originInstanceId;
	}

	public void setOriginInstanceId(long originInstanceId) {
		this.originInstanceId = originInstanceId;
	}

}
