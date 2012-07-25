package com.oreilly.permitsigns.interactions.signEditor.helpers;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.permitsigns.SignRecord;
import com.oreilly.permitsigns.interactions.Constants;


public class SignDataRetriever {
	
	public String alias = null;
	public SignRecord sign = null;
	
	
	public SignDataRetriever( Interaction interaction ) throws ContextDataRequired {
		alias = interaction.getContextData( String.class, interaction, Constants.SIGN_SELECTED_ALIAS, true );
		sign = interaction.getContextData( SignRecord.class, interaction, Constants.SIGN_SELECTED_SIGN, true );
	}
}
