package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.pages.MenuPage;
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
				"Please select an option (or exit to quit)\n" +
				"(type \'view\' to see the current settings)\n" +
				"1. Base price\n" +
				"2. Minimum price\n" +
				"3. Maximum price\n" +
				"4. Rounding factor\n" +
				"5. Variable price settings\n" +
				"6. Ratio price settings";
	}
	
}
