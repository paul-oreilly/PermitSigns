package com.oreilly.permitsigns.interactions.pricing;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.validator.DoubleValidator;
import com.oreilly.permitsigns.interactions.pricing.helpers.PriceDataRetriever;


public class EditPurchaseAmount extends TitledInteractionPage {
	
	public EditPurchaseAmount() {
		withValidator( new DoubleValidator() );
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		return "The current amount added / removed on purchase is " +
				helper.currentPriceRecord.getPurchaseAmount() + "\n" +
				"Please enter the new amount:";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws GeneralDisplayError,
			ContextDataRequired {
		PriceDataRetriever helper = new PriceDataRetriever( interaction );
		// since we have a validator, we can safely cast object to Double
		Double newAmount = (Double)data;
		helper.currentPriceRecord.setPurchaseAmonut( newAmount );
		return "The purchase adjustment has been updated to " + newAmount + "\n";
	}
	
}
