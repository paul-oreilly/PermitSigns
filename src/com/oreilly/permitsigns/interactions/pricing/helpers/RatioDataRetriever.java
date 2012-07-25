package com.oreilly.permitsigns.interactions.pricing.helpers;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.data.EconomicRatio;
import com.oreilly.permitsigns.interactions.Constants;


public class RatioDataRetriever extends PriceDataRetriever {
	
	public EconomicRatio currentRatio = null;
	
	
	public RatioDataRetriever( Interaction interaction ) throws ContextDataRequired,
			GeneralDisplayError {
		super( interaction );
		currentRatio = interaction.getContextData( EconomicRatio.class, interaction,
				Constants.PRICE_SELECTED_RATIO, true );
	}
}
