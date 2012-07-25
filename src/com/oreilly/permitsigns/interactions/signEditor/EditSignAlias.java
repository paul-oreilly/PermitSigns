package com.oreilly.permitsigns.interactions.signEditor;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.Permit;
import com.oreilly.permitsigns.SignRecord;
import com.oreilly.permitsigns.interactions.SelectPermitAlias;


// TODO: REDO this class!

public class EditSignAlias extends SelectPermitAlias {
	
	public EditSignAlias() {
		super();
		defaultTitle = "Edit Sign Alias";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		// TODO: Later, big multi-page capable list of all permit alias at the sign's location
		return "Please enter new permit alias to link this sign to:";
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError {
		String superResult = super.acceptValidatedInput( interaction, data );
		String permitAlias = interaction.context.get( SelectPermitAlias.CONTEXT_SELECTED_ALIAS ).toString();
		if ( permitAlias != null ) {
			Permit permit = PermitMe.instance.permits.permitsByAlias.get( permitAlias );
			if ( permit != null ) {
				Object signObj = interaction.context.get( "sign" );
				if ( signObj != null )
					if ( signObj instanceof SignRecord ) {
						SignRecord sign = (SignRecord)signObj;
						sign.setPermitAlias( permitAlias );
						return "Sign linkage updated to " + permitAlias;
					}
			}
		}
		return superResult;
	}
	
}
