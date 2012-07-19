package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.Permit;


// TODO: Move to PermitME
// TODO: Add a selection helper when no permit matches, with a list of the top x suggestions

public class SelectPermitAlias extends InteractionPage implements HasTitle {
	
	static public final String CONTEXT_SELECTED_ALIAS = "selectedPermitAlias";
	
	
	public SelectPermitAlias() {
		super();
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		// TODO: Later, big multi-page capable list of all permit alias at the sign's location
		return "Please select a permit alias to continue:";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) {
		Permit permit = PermitMe.instance.permits.permitsByAlias.get( data.toString() );
		if ( permit == null ) {
			interaction.pageWaitingForInput = true;
			return "That permit alias was not found";
		} else {
			interaction.context.put( CONTEXT_SELECTED_ALIAS, data.toString() );
			return "Permit alias " + data.toString() + " selected";
		}
	}
	
	
	@Override
	public String getTitle() {
		return "Select a permit alias";
	}
}
