package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.common.interaction.text.pages.MenuPage;


public class AdminChoices extends MenuPage implements HasTitle {
	
	public AdminChoices() {
		// TODO: Extend "WithChoice" to take a chain - so can "SelectEconomicData" then "EditEconomicData"
		// TODO: Add "view economic data", with "SelectEconomicData" then "ViewEconomicData"
		super();
		withChoice( "prices", new SelectPermitAlias(), new EditEconomicData() );
		withAlias( "prices", "1" );
		withAlias( "prices", "economic" );
		validationFailedMessage = "Please type one of the highlighted options to proceed";
		loopbackOnCompletion = false;
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		return "Which action would you like to take?\n" +
				"(To edit sign data, hold sneak while right clicking the sign)\n" +
				"  1. View or edit economic data (sign prices)\n";
	}
	
	
	@Override
	public String getTitle() {
		return "Admin Options for Permit Signs";
	}
}
