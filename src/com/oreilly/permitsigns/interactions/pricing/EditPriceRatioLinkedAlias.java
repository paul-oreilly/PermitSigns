package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;
import com.oreilly.permitsigns.interactions.pricing.helpers.RatioDataRetriever;


public class EditPriceRatioLinkedAlias extends SelectPermitAlias {
	
	public EditPriceRatioLinkedAlias() {
		super();
		defaultTitle = "Edit Price Ratio (Alias)";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		RatioDataRetriever helper = new RatioDataRetriever( interaction );
		return "This ratio for " + helper.currentPriceAlias + " is currently\n" +
				helper.currentRatio.sourceCount + " " + helper.currentPriceAlias + " to\n" +
				helper.currentRatio.otherCount + " " + helper.currentRatio.otherAlias + "\n" +
				"What is the new permit alias you want to link to?";
	}
	
	
	@Override
	protected String actOnInput( Interaction interaction, String alias ) throws ContextDataRequired,
			GeneralDisplayError {
		RatioDataRetriever helper = new RatioDataRetriever( interaction );
		helper.currentRatio.otherAlias = alias;
		helper.currentPriceRecord.updatePriceRatios();
		return "Ratio now linked to " + alias;
	}
	
}
