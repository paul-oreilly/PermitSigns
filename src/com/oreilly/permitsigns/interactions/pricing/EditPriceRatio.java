package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.interactions.Style;
import com.oreilly.permitsigns.interactions.pricing.helpers.RatioDataRetriever;


// edit a single price ratio
public class EditPriceRatio extends MenuPage {
	
	public static final String SELECTED = "editPriceRatio_selected";
	
	
	public EditPriceRatio() {
		super();
		defaultTitle = "Edit Price Ratio";
		withChoice( "1", new EditRatioPricing() );
		withChoice( "2", new EditPriceRatioSourceCount() );
		withChoice( "3", new EditPriceRatioOtherCount() );
		withChoice( "4", new EditPriceRatioLinkedAlias() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		RatioDataRetriever helper = new RatioDataRetriever( interaction );
		return "Editing price ratio data for " + helper.currentPriceAlias + "\n" +
				"This ratio is " + helper.currentRatio.sourceCount + ":" + helper.currentRatio.otherCount + " of " +
				helper.currentRatio.otherAlias + "\n" +
				"([source]:[other] of alias)\n" +
				"Please select an action:\n" +
				Style.valid( "1" ) + ". Return to the list of price ratio's\n" +
				Style.valid( "2" ) + ". Edit the ratio's source count\n" +
				Style.valid( "3" ) + ". Edit the ratio's other count\n" +
				Style.valid( "4" ) + ". Edit the alias the ratio linked to\n";
	}
	
}
