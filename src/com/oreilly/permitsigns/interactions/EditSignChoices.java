package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.records.Sign;


public class EditSignChoices extends MenuPage implements HasTitle {
	
	public EditSignChoices() {
		super();
		withChoice( "type", new EditSignType() );
		withChoice( "alias", new EditSignAlias() );
		withAlias( "type", "1" );
		withAlias( "alias", "2" );
		validationFailedMessage = "Please type one of the highlighted options to proceed";
		loopbackOnCompletion = true;
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		Sign sign = (Sign)interaction.context.get( "sign" );
		if ( sign == null )
			return "Display failed to initialise. No value for sign.";
		return "This is a " + sign.signType.toHumanString() + ", linked to " + sign.permitAlias + "\n" +
				"Which action would you like to take?\n" +
				"  1. Change the type of sign\n" +
				"  2. Change the permit alias of the sign (linked permit)";
	}
	
	
	@Override
	public String getTitle() {
		return "Sign Editing Assistant";
	}
	
}
