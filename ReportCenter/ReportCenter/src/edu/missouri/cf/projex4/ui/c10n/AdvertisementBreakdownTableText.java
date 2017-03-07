package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface AdvertisementBreakdownTableText {
	
	@En("")
	String contextHelp();
	
	@En("ID")
	String id();
	
	@En("Object ID")
	String objectid();
	
	@En("Row Stamp")
	String rowstamp();
	
	@En("Title")
	String title();
	
	@En("PCS Project ID")
	String pcsprojectid();
	
	@En("PCS Row")
	String pcsrow();
	
	@En("PCS Column")
	String pcscolumn();
	
	@En("Amount")
	String amount();
	
	
}
