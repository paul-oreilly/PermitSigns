package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.PriceRecord;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.interactions.pricing.EditBasePrice;
import com.oreilly.permitsigns.interactions.pricing.EditMaxPrice;
import com.oreilly.permitsigns.interactions.pricing.EditMinPrice;
import com.oreilly.permitsigns.interactions.pricing.EditRatioPricing;
import com.oreilly.permitsigns.interactions.pricing.EditRounding;
import com.oreilly.permitsigns.interactions.pricing.EditVariablePricing;


public class EditEconomicData extends MenuPage implements HasTitle {
	
	public EditEconomicData() {
		super();
		withChoice( "base", new EditBasePrice() );
		withAlias( "base", "1" );
		withChoice( "minimum", new EditMinPrice() );
		withAlias( "minimum", "min" );
		withAlias( "minimum", "2" );
		withChoice( "maximum", new EditMaxPrice() );
		withAlias( "maximum", "max" );
		withAlias( "maximum", "3" );
		withChoice( "rounding", new EditRounding() );
		withAlias( "rounding", "4" );
		withAlias( "rounding", "round" );
		withChoice( "variable", new EditVariablePricing() );
		withAlias( "variable", "var" );
		withAlias( "variable", "v" );
		withAlias( "variable", "5" );
		withChoice( "ratio", new EditRatioPricing() );
		withAlias( "ratio", "r" );
		withAlias( "ratio", "6" );
		validationFailedMessage = "Please type one of the highlighted options to proceed";
	}
	
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Sign Price Editing Assistant";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		Object permitAliasObj = interaction.context.get( SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAliasObj != null ) {
			String permitAlias = permitAliasObj.toString();
			PriceRecord economicData = PermitSigns.instance.prices.getPriceRecord( permitAlias );
			if ( economicData != null ) {
				return "Currently editing prices for " + permitAlias + ":\n\n" +
						"Please select an option to edit (or exit to quit)\n" +
						"1. Base price\n" +
						"2. Minimum price\n" +
						"3. Maximum price\n" +
						"4. Rounding factor\n" +
						"5. Variable price settings\n" +
						"6. Ratio price settings";
			} else
				return "No matching economic data found for " + permitAlias;
		}
		return "No permit alias selected, unable to find matching economic data";
	}
	
}
