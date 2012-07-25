package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.data.EconomicRatio;
import com.oreilly.permitsigns.interactions.Constants;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class CreatePriceRatio extends SelectPermitAlias {
	
	public CreatePriceRatio() {
		super();
		defaultTitle = "Create a new price ratio";
	}
	
	
	@Override
	protected String getTextHeader( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever data = new PriceDataRetriever( interaction );
		return "To add a new ratio record to the price data\n" +
				"for " + data.currentPriceAlias + ",\n" +
				"Please enter the alias you want the ratio to be based from:";
	}
	
	
	@Override
	protected String actOnInput( Interaction interaction, String alias ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever data = new PriceDataRetriever( interaction );
		EconomicRatio ratio = new EconomicRatio( data.currentPriceAlias, 1, 1 );
		data.currentPriceRecord.addNewPriceRatio( ratio );
		interaction.context.put( Constants.PRICE_SELECTED_RATIO, ratio );
		return "New ratio created, linked to " + alias;
	}
	
}
