package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.PaginationAssistant;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


// TODO: Highlight the "Page x of y"

// views the economic data for the selected PermitAlias (via SelectPermitAlias)
public class ViewPriceData extends TitledInteractionPage {
	
	protected static final String PAGINATION = "viewPriceData_paginator";
	
	// allow 4 for borders, 1 for title, and 2 more for "Page x of y" at the bottom of each page
	private static final int MAX_LINES = InteractionPage.MAX_LINES - 7;
	
	
	public ViewPriceData() {
		super();
		defaultTitle = "Sign Price Details";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		// we may already have generated the display text (and therefore have multiple pages)
		PaginationAssistant pa = interaction.getContextData( PaginationAssistant.class, interaction, PAGINATION );
		if ( pa != null )
			return pa.getDisplayText();
		// otherwise, we need to generate the raw information...
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		pa = new PaginationAssistant( helper.currentPriceRecord.toHumanString(),
				MAX_LINES,
				"Price data for " + helper.currentPriceAlias + "\n" +
						"(type \'edit\' to make changes)\n\n" );
		interaction.context.put( PAGINATION, pa );
		return pa.getDisplayText();
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired {
		// clean data
		String universal = data.toString().toLowerCase().trim();
		PaginationAssistant pa = interaction.getContextData( PaginationAssistant.class, interaction, PAGINATION );
		if ( pa == null )
			throw new ContextDataRequired( PAGINATION, PaginationAssistant.class );
		// see if we have a page command....
		if ( pa.processPageCommand( universal ) )
			return null;
		// if we are not dealing with page data...
		// "edit" will pass on to EditSignData
		if ( universal.contentEquals( "edit" ) ) {
			interaction.nextPage( new EditPriceData() );
			interaction.context.remove( PAGINATION );
		}
		// anything else will exit
		return null;
	}
	
}
