package com.ibm.ecm.mm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.ibm.ecm.mm.model.IdentifiedDocInstance;

public class FileExtractor {
	public static String getContent(IdentifiedDocInstance documentInstance) {
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
			// Windows
			file = new File(documentInstance.getFullyQualifiedPath());
			fis = new FileInputStream(file.getAbsolutePath());
		} catch (FileNotFoundException e1) {
			// Unix
			try {
				file = new File(documentInstance.getUnixMountedPath());
				fis = new FileInputStream(file.getAbsolutePath());
			} catch (FileNotFoundException e) {
				String log = "DOC-" + documentInstance.getDocument().getId() + ": Cannot found " + documentInstance.getFullyQualifiedPath();
				if (documentInstance.getSnapshotDeleted() > 0) {
					log += " as expected";
					// System.out.println(log);
				} else {
					log += " unexpectedly";
					System.out.println(Util.getTimeStamp() + log);
					System.err.println(Util.getTimeStamp() + log);
				}
				return content;
			}
		}

		try {
			if (documentInstance.getExtension().toUpperCase().equals("PDF")) {
				PDFTextStripper stripper = new PDFTextStripper();
				pdfDocument = PDDocument.load(file);
				content = stripper.getText(pdfDocument);
			} else if (documentInstance.getExtension().toUpperCase().equals("DOC")) {
				HWPFDocument document = new HWPFDocument(fis);
				docExtractor = new WordExtractor(document);
				content = docExtractor.getText();
			} else if (documentInstance.getExtension().toUpperCase().equals("DOCX")) {
				XWPFDocument document = new XWPFDocument(fis);
				docxExtractor = new XWPFWordExtractor(document);
				content = docxExtractor.getText();
			} else if (documentInstance.getExtension().toUpperCase().equals("PPT") || documentInstance.getExtension().toUpperCase().equals("PPS")) {
				pptExtractor = new PowerPointExtractor(fis);
				content = pptExtractor.getText(true, true, true, true);
			} else if (documentInstance.getExtension().toUpperCase().equals("PPTX") || documentInstance.getExtension().toUpperCase().equals("PPSX")) {
				XMLSlideShow ppt = new XMLSlideShow(fis);
				pptxExtractor = new XSLFPowerPointExtractor(ppt);
				content = pptxExtractor.getText(true, true, true);
			} else if (documentInstance.getExtension().toUpperCase().equals("XLS")) {
				wb = new HSSFWorkbook(fis);
				ExcelExtractor xlsExtractor = new ExcelExtractor(wb);
				content = xlsExtractor.getText();
				xlsExtractor.close();
			} else if (documentInstance.getExtension().toUpperCase().equals("XLSX")) {
				xwb = new XSSFWorkbook(fis);
				XSSFExcelExtractor xlsxExtractor = new XSSFExcelExtractor(xwb);
				content = xlsxExtractor.getText();
				xlsxExtractor.close();
			} else if (documentInstance.getExtension().toUpperCase().equals("TXT")) {
				int contentByte;
				while ((contentByte = fis.read()) != -1) {
					content += String.valueOf((char) contentByte);
				}
			} else {
				// System.err.println("Cannot read content from " + getName() +
				// " ("+ getId() +") due to unsupported file format.");
			}
		} catch (OutOfMemoryError e) {
			System.err.println(Util.getTimeStamp() + "DOC-" + documentInstance.getDocument().getId() + ":Cannot read content from "
					+ documentInstance.getName() + " (" + documentInstance.getId() + ") because the file is too big.");
		} catch (Exception e) {
			System.err.println(Util.getTimeStamp() + "DOC-" + documentInstance.getDocument().getId() + ":Cannot read content from "
					+documentInstance. getName() + " (" + documentInstance.getId() + ") due to error. " + e.getMessage());
		} finally {
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
}
