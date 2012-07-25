package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.SignRecord;
import com.oreilly.permitsigns.interactions.signEditor.EditSignAlias;
import com.oreilly.permitsigns.interactions.signEditor.EditSignType;


public class EditSign extends MenuPage {
	
	public EditSign() {
		super();
		withChoice( "type", new EditSignType() );
		withChoice( "alias", new EditSignAlias() );
		withAlias( "type", "1" );
		withAlias( "alias", "2" );
		validationFailedMessage = "Please type one of the highlighted options to proceed";
		loopbackOnCompletion = true;
		defaultTitle = "Sign Editing Assistant";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		SignRecord sign = (SignRecord)interaction.context.get( "sign" );
		if ( sign == null )
			return "Display failed to initialise. No value for sign.";
		return "This is a " + sign.getSignType().toHumanString() + ", linked to " + sign.getPermitAlias() + "\n" +
				"Which action would you like to take?\n" +
				"  1. Change the type of sign\n" +
				"  2. Change the permit alias of the sign (linked permit)";
	}
	
}
