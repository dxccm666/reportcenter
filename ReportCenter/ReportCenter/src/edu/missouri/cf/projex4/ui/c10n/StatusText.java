package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface StatusText {
	
	@En("change status")
	String changeStatusButton();
	
	@En("")
	String changeStatusButton_help();

}
