package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface AdvertisementBreakdownText {
	
	@En("Advertisement Breakdown")
	String screenName();
	
	@En("Add New Advertisement Breakdown")
	String addButtonDescription();
	
	AdvertisementBreakdownTableText table();
	
	CommonText common();
	
	StandardButtonText buttons();

	@En("Column")
	String column();
	
	@En("Project Number")
	String projectnumber();
	
	@En("Row Title")
	String rowtitle();
	
	@En("Row")
	String row();
	
	@En("Amount")
	String amount();

}
