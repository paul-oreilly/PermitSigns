package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.interactions.Constants;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;


public class SelectPriceData extends SelectPermitAlias {
	
	public SelectPriceData() {
		super();
		defaultTitle = "Price Data Selection";
	}
	
	
	@Override
	protected String getTextHeader( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		return "Select the alias for the price data you want to edit:";
	}
	
	
	@Override
	protected String actOnInput( Interaction interaction, String alias ) throws ContextDataRequired,
			GeneralDisplayError {
		interaction.context.put( Constants.PRICE_PERMIT_ALIAS, alias );
		return "Selected " + alias;
	}
}
