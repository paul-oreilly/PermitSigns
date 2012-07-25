package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.IntValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditTimeAmountInterval extends TitledInteractionPage {
	
	public EditTimeAmountInterval() {
		super();
		withValidator( new IntValidator() );
		defaultTitle = "Edit Price Adjustment Frequency";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "Currently, " + helper.currentPriceRecord.getTimeAmount() + " is added / removed,\n" +
				"every " + helper.currentPriceRecord.getTimeAmountInterval() + "ticks (" +
				helper.currentPriceRecord.getTimeAmountInterval() / 20 + " seconds)\n" +
				"Some common ticks values are:\n" + EditVariablePricing.COMMON_TICK_COUNTS +
				"What would you like to change the frequency to? (In ticks)";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// change the value (safe type cast, due to validator)
		Integer newValue = (Integer)data;
		helper.currentPriceRecord.setTimeAmountInterval( newValue );
		return "The amount will now be updated every " + newValue + " ticks";
	}
	
}
