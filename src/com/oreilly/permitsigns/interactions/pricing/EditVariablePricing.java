package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditVariablePricing extends MenuPage {
	
	static public final String COMMON_TICK_COUNTS = 20 + " for 1 second\n" +
			20 * 60 + " for 1 minute\n" +
			20 * 60 * 5 + " for 5 minutes\n" +
			20 * 60 * 60 + " for 1 hour\n" +
			20 * 60 * 60 * 24 + " for 1 day\n";
	
	
	public EditVariablePricing() {
		super();
		defaultTitle = "Variable Pricing Options";
		withChoice( "1", new EditPurchaseAmount() );
		withChoice( "2", new EditPurchaseFactor() );
		withChoice( "3", new EditTimeAmount(), new EditTimeAmountInterval() );
		withChoice( "4", new EditTimeFactor(), new EditTimeFactorInterval() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// build display
		String result = "The current variable pricing for " + helper.currentPriceAlias + " is worth " +
				helper.currentPriceRecord.getVariablePrice() + "\n" +
				"Please select which option you would like to edit:\n";
		if ( helper.currentPriceRecord.getPurchaseAmountDefined() )
			result += "1. Edit the value added / removed on purchase\n" +
					"    - currently " + helper.currentPriceRecord.getPurchaseAmount() + "\n";
		else
			result += "1. Define an amount that will be added / removed on purchase\n";
		if ( helper.currentPriceRecord.getPurchaseFactorDefined() )
			result += "2. Edit the factor the price is multiplied by on purchase\n" +
					"    - currently " + helper.currentPriceRecord.getPurchaseFactor() + ")\n";
		else
			result += "2. Define a factor for the price to be multiplied by on purchase\n";
		if ( helper.currentPriceRecord.getTimeAmountDefined() )
			result += "3. Edit the value added / removed over time\n" +
					"    - currently " + helper.currentPriceRecord.getTimeAmount() +
					" every " + helper.currentPriceRecord.getTimeAmountInterval() +
					" ticks\n";
		else
			result += "3. Define a value to be added / removed over time\n";
		if ( helper.currentPriceRecord.getTimeFactorDefined() )
			result += "4. Edit the factor that the price is multiplied by over time\n" +
					"    - current " + helper.currentPriceRecord.getTimeFactor() +
					" every " + helper.currentPriceRecord.getTimeFactorInterval() +
					" ticks\n";
		else
			result += "4. Define a factor that the price will be multipled by over time\n";
		return result;
	}
	
}
