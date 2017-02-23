package edu.missouri.cf.projex4.ui.desktop.reports;

import edu.missouri.cf.projex4.email.ProjexEmailer;

public class ReportEmailer extends ProjexEmailer {
	
	public ReportEmailer(String reportName, String fileName) {
		
		super("REPORT");
		set("REPORTTITLE", reportName);
		setBody();
		setSubject("Report Generated");
		addFile(fileName);
		
	}

}
