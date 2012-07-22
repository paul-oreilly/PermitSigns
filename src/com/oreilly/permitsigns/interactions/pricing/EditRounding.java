package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.PriceRecord;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;


public class EditRounding extends TitledInteractionPage {
	
	public EditRounding() {
		super();
		defaultTitle = "Edit Rounding Factor";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		String permitAlias = getContextData( String.class, interaction, SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAlias == null )
			return "The required permit alias is invalid";
		PriceRecord data = PermitSigns.instance.prices.getPriceRecord( permitAlias );
		if ( data == null )
			return "Unable to resolve data for " + permitAlias;
		return "Prices for " + permitAlias + " are currently rounded to the nearest " + data.getRounding() + ".\n" +
				"Please enter a new value, or \'exit\' to quit";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) {
		Double newRounding = null;
		if ( Double.class.isAssignableFrom( data.getClass() ) )
			newRounding = (Double)data;
		else {
			// assume / use string, and try to resolve to a double
			try {
				newRounding = Double.parseDouble( data.toString().trim() );
			} catch ( NumberFormatException error ) {
				newRounding = null;
			}
		}
		if ( newRounding == null )
			return "\"" + data.toString() + "\" is not a valid price";
		// get the data in question...
		String permitAlias = getContextData( String.class, interaction, SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAlias == null )
			return "The required permit alias is invalid";
		PriceRecord priceData = PermitSigns.instance.prices.getPriceRecord( permitAlias );
		if ( priceData == null )
			return "Unable to resolve data for " + permitAlias;
		// change the value
		priceData.setRounding( newRounding );
		return "Prices for " + permitAlias + " will now be rounded to the nearest " + newRounding;
	}
	
}
