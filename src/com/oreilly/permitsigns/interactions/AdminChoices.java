package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.common.interaction.text.pages.MenuPage;


public class AdminChoices extends MenuPage implements HasTitle {
	
	public AdminChoices() {
		// TODO: Extend "WithChoice" to take a chain - so can "SelectEconomicData" then "EditEconomicData"
		// TODO: Add "view economic data", with "SelectEconomicData" then "ViewEconomicData"
		super();
		withChoice( "price", new SelectPermitAlias(), new ViewEconomicData() );
		withAlias( "price", "1" );
		withAlias( "price", "permit" );
		validationFailedMessage = "Please type one of the highlighted options to proceed";
		loopbackOnCompletion = false;
	}
	
	
	// TODO: Add sign editing toggle mode
	@Override
	public String getDisplayText( Interaction interaction ) {
		return "Which action would you like to take?\n" +
				"(To edit sign data, hold sneak while right clicking the sign)\n" +
				"  1. View / edit the price of a permit\n";
		
	}
	
	
	@Override
	public String getTitle() {
		return "Admin Options for Permit Signs";
	}
}
