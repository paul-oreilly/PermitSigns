package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.DoubleValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditTimeAmount extends TitledInteractionPage {
	
	public EditTimeAmount() {
		super();
		defaultTitle = "Edit Time Based Price Ajustment";
		withValidator( new DoubleValidator() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "Currently, " + helper.currentPriceRecord.getTimeAmount() + " is added / removed,\n" +
				"every " + helper.currentPriceRecord.getTimeAmountInterval() +
				"ticks (" + helper.currentPriceRecord.getTimeAmountInterval() / 20 + " seconds)\n" +
				"What would you like the amount to?";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// change the value (safe type cast, due to validator)
		Double newValue = (Double)data;
		helper.currentPriceRecord.setTimeAmount( newValue );
		return "The amount the prices changes by is now " + newValue;
	}
	
}
