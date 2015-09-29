package com.ibm.ecm.mm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class IdentifiedDocInstance {
	private long id;
	private String name;
	private String extension;
	private String server;
	private String volume;
	private String path;
	private MetadataValue metadataValue;
	
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
	public String getPath() {
		return path == null ? "" : path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFullPath() {
		return getPath().equals("") ? getName() : getPath() + "/" + getName();
	}
	public String getFullyQualifiedPath() {
		return "\\\\" + getServer() + "/" + getVolume() + "/" + getFullPath();
	}
	public MetadataValue getMetadataValue() {
		return metadataValue;
	}
	public void setMetadataValue(MetadataValue metadataValue) {
		this.metadataValue = metadataValue;
	}
	public String getContent() {	
		String content = "";		
		PDDocument pdfDocument = null;	
		WordExtractor docExtractor = null;
		XWPFWordExtractor docxExtractor = null;
		PowerPointExtractor pptExtractor = null;	
		XSLFPowerPointExtractor pptxExtractor = null;			
		
		try {
			File file = new File(getFullyQualifiedPath());		
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			
			if (getExtension().toUpperCase().equals("PDF")) {						
				PDFTextStripper stripper = new PDFTextStripper();
				pdfDocument = PDDocument.load(fis);
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
			else {
				//TODO: logging
				System.out.println("Cannot read content from " + getName() + " due unsupported file format.");	
			}	
		}
		catch (FileNotFoundException e) {
			//TODO: logging
			System.out.println("Cannot read content from " + getName() + " because the file is not found.");		
		}
		catch (Exception e) {
			//TODO: logging
			System.out.println("Cannot read content from " + getName() + " due to error.");
			e.printStackTrace();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		

		return content;
	}
}
