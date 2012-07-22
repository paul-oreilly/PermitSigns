package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.PriceRecord;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;


public class EditBasePrice extends InteractionPage implements HasTitle {
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		String permitAlias = getContextData( String.class, interaction, SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAlias == null )
			return "The required permit alias is invalid";
		PriceRecord data = PermitSigns.instance.prices.getPriceRecord( permitAlias );
		if ( data == null )
			return "Unable to resolve data for " + permitAlias;
		// TODO: Some formatting function for display
		return "The current base price for " + permitAlias + " is " + data.getBasePrice() + ".\n" +
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
		// TODO: This needs to be done in some method that updates system data - maybe an event?
		// DEFINATLY not by altering directly!
		// SUGGEST: Records hide their information (oh nos!) and send events on data change.
		//  can send "RawPriceChange", which economics can pick up, get new price, and send "PriceChange" 
		//  with all the numbers updated.
		if ( priceData.setBasePrice( newPrice ) )
			return "Base price for " + permitAlias + " is now " + newPrice;
		else
			return "Base price has been updated, however an update of the current price was cancelled.";
	}
	
	
	@Override
	public String getTitle() {
		return "Edit Base Price";
	}
}
