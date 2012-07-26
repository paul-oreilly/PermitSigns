package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.DoubleValidator;
import com.oreilly.permitsigns.interactions.Style;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditMinPrice extends TitledInteractionPage {
	
	public EditMinPrice() {
		super();
		withValidator( new DoubleValidator() );
		defaultTitle = "Edit Minimum Price";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "The current minimum price for " + helper.currentPriceAlias + " is " +
				helper.currentPriceRecord.getMinPrice() + ".\n" +
				"Please enter a new value, or type " + Style.valid( "exit" ) + " to quit";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// change the value - safe cast, due to validator
		Double newPrice = (Double)data;
		helper.currentPriceRecord.setMinPrice( newPrice );
		return "Minimum price for " + helper.currentPriceAlias + " is now " + newPrice;
	}
	
}
