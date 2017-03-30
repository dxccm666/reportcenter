package edu.missouri.operations.reportcenter.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.*;

@C10NMessages
public interface ErrorsText {

	@En("You must select a record in the table first.")
	String noSelectedItem();
	
	@En("You are not authorized to view item.")
	String notAuthorizedForItem();

}
