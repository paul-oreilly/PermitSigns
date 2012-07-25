package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.IntValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.RatioDataRetriever;


public class EditPriceRatioSourceCount extends TitledInteractionPage {
	
	public EditPriceRatioSourceCount() {
		super();
		defaultTitle = "Edit Ratio (Source Count)";
		withValidator( new IntValidator() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		RatioDataRetriever helper = new RatioDataRetriever( interaction );
		return "The current ratio is:\n" +
				"( + " + helper.currentRatio.sourceCount + "):" +
				helper.currentRatio.otherCount + " with " +
				helper.currentRatio.otherAlias + "\n" +
				"Please enter a new source number:\n" +
				"(The number in brackets)";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		// we have a validator, so safe cast
		Integer value = (Integer)data;
		RatioDataRetriever helper = new RatioDataRetriever( interaction );
		helper.currentRatio.sourceCount = value;
		helper.currentPriceRecord.updatePriceRatios();
		return "Source count for ratio has been updated to " + value;
	}
	
}
