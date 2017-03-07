package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface DefaultAgreementItemsText {
	
	@En("Default NonBlanket Agreement Items") 
	String screenName();
	
	@En("Add new Default Agreement Item")
	String addButton_help();
	
	@En("Item Name")
	String itemName();
	
	@En("Item #")
	String itemNumber();
	
	@En("Basic Service?")
	String isBasicService();
	
	@En("System Name")
	String itemSystemName();
	
	@En("% of Total")
	String percentTotal();
	
	@En("")
	String contextHelp();
	
	CommonText common();
	
	StandardErrorText errors();

}
