package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.DoubleValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditTimeFactor extends TitledInteractionPage {
	
	public EditTimeFactor() {
		super();
		defaultTitle = "Edit Time Based Price Factor";
		withValidator( new DoubleValidator() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "Currently the price is multiplied by " + helper.currentPriceRecord.getTimeFactor() + "\n" +
				"every " + helper.currentPriceRecord.getTimeAmountInterval() + "ticks (" +
				helper.currentPriceRecord.getTimeAmountInterval() / 20 + " seconds)\n" +
				"What would you like the price to be multiplied by?";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// change the value (safe type cast, due to validator)
		Double newValue = (Double)data;
		helper.currentPriceRecord.setTimeFactor( newValue );
		return "The amount the prices is multiplied by is now " + newValue;
	}
	
}
