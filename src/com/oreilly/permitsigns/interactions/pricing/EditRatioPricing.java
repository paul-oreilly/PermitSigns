package com.oreilly.permitsigns.interactions.pricing;

import java.util.HashMap;
import java.util.List;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.PaginationAssistant;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.PriceRecord;
import com.oreilly.permitsigns.data.EconomicRatio;
import com.oreilly.permitsigns.interactions.Style;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditRatioPricing extends TitledInteractionPage {
	
	protected static final String PAGINATION = "editRatioPricing_pagination";
	protected static final String CHOICES = "editRatioPricing_choices";
	
	
	public EditRatioPricing() {
		super();
		defaultTitle = "Edit Price Ratio (List)";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PaginationAssistant pa = interaction.getContextData( PaginationAssistant.class, interaction, PAGINATION );
		if ( pa == null ) {
			// display depends on if we have ratio's existing or not...
			PriceDataRetriever helper = new PriceDataRetriever( interaction );
			List< EconomicRatio > priceRatios = helper.currentPriceRecord.getPriceRatios();
			if ( priceRatios.size() == 0 ) {
				pa = new PaginationAssistant(
						"No pricing ratio's are currently defined for " + helper.currentPriceAlias + "\n" +
								"You have only significant choice:\n" +
								"Type " + Style.valid( "new" ) + " to add a pricing ratio",
						InteractionPage.MAX_LINES - 4, "" );
			} else {
				String ratioList = generateRatioList( helper.currentPriceRecord, interaction );
				pa = new PaginationAssistant( ratioList, InteractionPage.MAX_LINES - 4,
						"Editing ratio data for " + helper.currentPriceAlias + "\n" +
								"Type " + Style.valid( "new" ) + " to add a pricing ratio," +
								" or select a ratio to edit:\n\n" );
			}
			interaction.context.put( PAGINATION, pa );
		}
		return pa.getDisplayText();
	}
	
	
	protected String generateRatioList( PriceRecord pricing, Interaction interaction ) {
		Integer count = 1;
		String result = "";
		HashMap< String, EconomicRatio > choices = new HashMap< String, EconomicRatio >();
		for ( EconomicRatio ratio : pricing.getPriceRatios() ) {
			result += Style.valid( count.toString() ) + ". " + ratio.sourceCount + ":" + ratio.otherCount + " with " +
					ratio.otherAlias;
			choices.put( count.toString(), ratio );
			count += 1;
		}
		interaction.context.put( CHOICES, choices );
		return result;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired {
		// get the required data
		String universal = data.toString().toLowerCase().trim();
		PaginationAssistant pa = interaction.getContextData( PaginationAssistant.class, interaction, PAGINATION );
		if ( pa == null )
			throw new ContextDataRequired( PAGINATION, PaginationAssistant.class );
		// if our data is "new", we can respond now (and need to - there may not be a "choices" in the context!)
		if ( universal.contains( "new" ) ) {
			interaction.nextPage( new CreatePriceRatio() );
			interaction.context.remove( PAGINATION );
			return null;
		}
		// otherwise, we see if we have a page command..
		if ( pa.processPageCommand( universal ) )
			return null;
		// last check, for a preset choice - in which case we pass on to the next dialog
		@SuppressWarnings("unchecked")
		HashMap< String, EconomicRatio > choices = interaction.getContextData( HashMap.class, interaction, PAGINATION );
		if ( choices == null )
			throw new ContextDataRequired( CHOICES, HashMap.class );
		EconomicRatio ratio = choices.get( universal );
		// if we have a match, continue.. if not, ask again.
		if ( ratio != null ) {
			interaction.context.put( EditPriceRatio.SELECTED, ratio );
			interaction.nextPage( new EditPriceRatio() );
			interaction.context.remove( PAGINATION );
			return null;
		} else {
			interaction.pageWaitingForInput = true;
			return "Unable to resolve option from \"" + data.toString() + "\"";
		}
	}
	
}
