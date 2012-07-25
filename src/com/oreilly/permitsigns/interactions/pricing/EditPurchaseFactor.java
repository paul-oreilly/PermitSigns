package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.DoubleValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditPurchaseFactor extends TitledInteractionPage {
	
	public EditPurchaseFactor() {
		withValidator( new DoubleValidator() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "The current amount multiplied on purchase is " +
				helper.currentPriceRecord.getPurchaseFactor() + "\n" +
				"Please enter the new factor:";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// since we have a validator, we can safely cast object to Double
		Double newFactor = (Double)data;
		helper.currentPriceRecord.setPurchaseFactor( newFactor );
		return "The purchase factor has been updated to " + newFactor + "\n";
	}
	
}
