package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.DoubleValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditRounding extends TitledInteractionPage {
	
	public EditRounding() {
		super();
		withValidator( new DoubleValidator() );
		defaultTitle = "Edit Rounding Factor";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "Prices for " + helper.currentPriceAlias + " are currently rounded to the nearest " +
				helper.currentPriceRecord.getRounding() + ".\n" +
				"Please enter a new value, or \'exit\' to quit";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// change the value (safe type cast, due to validator)
		Double newValue = (Double)data;
		helper.currentPriceRecord.setRounding( newValue );
		return "Prices for " + helper.currentPriceAlias + " will now be rounded to the nearest " + newValue;
	}
	
}
