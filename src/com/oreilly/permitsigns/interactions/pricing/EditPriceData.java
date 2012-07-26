package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.interactions.Style;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditPriceData extends MenuPage {
	
	public EditPriceData() {
		super();
		withChoice( "view", new ViewPriceData() );
		withChoice( "base", new EditBasePrice() );
		withAlias( "base", "1" );
		withChoice( "minimum", new EditMinPrice() );
		withAlias( "minimum", "min" );
		withAlias( "minimum", "2" );
		withChoice( "maximum", new EditMaxPrice() );
		withAlias( "maximum", "max" );
		withAlias( "maximum", "3" );
		withChoice( "rounding", new EditRounding() );
		withAlias( "rounding", "4" );
		withAlias( "rounding", "round" );
		withChoice( "variable", new EditVariablePricing() );
		withAlias( "variable", "5" );
		withChoice( "ratio", new EditRatioPricing() );
		withAlias( "ratio", "6" );
		validationFailedMessage = "Please type one of the highlighted options to proceed";
		loopbackOnCompletion = true;
		defaultTitle = "Sign Price Editing Assistant";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "Currently editing prices for " + helper.currentPriceAlias + ":\n\n" +
				"Please select an option (or " + Style.valid( "exit" ) + " to quit)\n" +
				"(type " + Style.valid( "view" ) + " to see the current settings)\n" +
				Style.valid( "1" ) + ". " + Style.valid( "Base" ) + " price\n" +
				Style.valid( "2" ) + ". " + Style.valid( "Minimum" ) + " price\n" +
				Style.valid( "3" ) + ". " + Style.valid( "Maximum" ) + " price\n" +
				Style.valid( "4" ) + ". " + Style.valid( "Rounding" ) + " factor\n" +
				Style.valid( "5" ) + ". " + Style.valid( "Variable" ) + " price settings\n" +
				Style.valid( "6" ) + ". " + Style.valid( "Ratio" ) + " price settings";
	}
	
}
