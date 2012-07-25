package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.Permit;


// TODO: Move to PermitME
// TODO: Add a selection helper when no permit matches, with a list of the top x suggestions

public class SelectPermitAlias extends TitledInteractionPage {
	
	static public final String CONTEXT_SELECTED_ALIAS = "selectedPermitAlias";
	
	
	public SelectPermitAlias() {
		super();
		defaultTitle = "Select a permit alias";
	}
	
	
	protected String getTextHeader( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		return "Please select a permit alias to continue:";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError {
		// TODO: Later, big multi-page capable list of all permit alias at the sign's location
		return getTextHeader( interaction );
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		String universal = data.toString().toLowerCase().trim();
		Permit permit = PermitMe.instance.permits.permitsByAlias.get( universal );
		if ( permit == null ) {
			interaction.pageWaitingForInput = true;
			return "That permit alias was not found";
		} else {
			return actOnInput( interaction, universal );
		}
	}
	
	
	protected String actOnInput( Interaction interaction, String alias ) throws ContextDataRequired,
			GeneralDisplayError {
		interaction.context.put( CONTEXT_SELECTED_ALIAS, alias );
		return "Selected " + alias;
	}
	
}
