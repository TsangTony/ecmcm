package com.ibm.ecm.mm.util;

import java.util.ArrayList;

public class ReportManager {
	
	public static String DOCUMENT_STATUS_REPORT = "Document Status Report";
	public static String METADATA_STATUS_REPORT = "Metadata Status Report";
	
	public static ArrayList<String> getReports() {
		ArrayList<String> reports = new ArrayList<String>();
		reports.add(ReportManager.DOCUMENT_STATUS_REPORT);
		reports.add(ReportManager.METADATA_STATUS_REPORT);
		return reports;
	}

}
