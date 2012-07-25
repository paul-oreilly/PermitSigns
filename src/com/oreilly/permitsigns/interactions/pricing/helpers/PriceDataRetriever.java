package com.oreilly.permitsigns.interactions.pricing.helpers;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.PriceRecord;
import com.oreilly.permitsigns.interactions.Constants;


public class PriceDataRetriever {
	
	public String currentPriceAlias = null;
	public PriceRecord currentPriceRecord = null;
	
	
	public PriceDataRetriever( Interaction interaction ) throws ContextDataRequired,
			GeneralDisplayError {
		currentPriceAlias = interaction.getContextData( String.class, interaction, Constants.PRICE_PERMIT_ALIAS, true );
		currentPriceRecord = PermitSigns.instance.prices.getPriceRecord( currentPriceAlias );
		if ( currentPriceRecord == null )
			throw new GeneralDisplayError( "Pricing data for " + currentPriceAlias + " was not found" );
	}
}
