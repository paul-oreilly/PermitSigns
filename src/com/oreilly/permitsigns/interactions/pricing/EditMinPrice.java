package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.PriceRecord;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;


public class EditMinPrice extends TitledInteractionPage {
	
	public EditMinPrice() {
		super();
		defaultTitle = "Edit Minimum Price";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		String permitAlias = getContextData( String.class, interaction, SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAlias == null )
			return "The required permit alias is invalid";
		PriceRecord data = PermitSigns.instance.prices.getPriceRecord( permitAlias );
		if ( data == null )
			return "Unable to resolve data for " + permitAlias;
		return "The current minimum price for " + permitAlias + " is " + data.getMinPrice() + ".\n" +
				"Please enter a new value, or \'exit\' to quit";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) {
		Double newPrice = null;
		if ( Double.class.isAssignableFrom( data.getClass() ) )
			newPrice = (Double)data;
		else {
			// assume / use string, and try to resolve to a double
			try {
				newPrice = Double.parseDouble( data.toString().trim() );
			} catch ( NumberFormatException error ) {
				newPrice = null;
			}
		}
		if ( newPrice == null )
			return "\"" + data.toString() + "\" is not a valid price";
		// get the data in question...
		String permitAlias = getContextData( String.class, interaction, SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAlias == null )
			return "The required permit alias is invalid";
		PriceRecord priceData = PermitSigns.instance.prices.getPriceRecord( permitAlias );
		if ( priceData == null )
			return "Unable to resolve data for " + permitAlias;
		// change the value
		priceData.setMinPrice( newPrice );
		return "Minimum price for " + permitAlias + " is now " + newPrice;
	}
	
}
