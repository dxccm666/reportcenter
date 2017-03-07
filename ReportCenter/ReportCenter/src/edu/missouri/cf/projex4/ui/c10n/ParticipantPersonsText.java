package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ParticipantPersonsText {
	
	@En("Participants")
	String screenName();
	
	@En("Add New Participant")
	String addButtonDescription();
	
	@En("Delete Participant")
	String deleteButtonDescription();
	
	ParticipantPersonsTableText table();
	
	CommonText common();
	
	StandardButtonText buttons();

}
