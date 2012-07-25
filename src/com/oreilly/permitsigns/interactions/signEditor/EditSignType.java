package com.oreilly.permitsigns.interactions.signEditor;

import java.util.HashMap;
import java.util.Iterator;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.formatter.Highlighter;
import com.oreilly.common.interaction.text.interfaces.HighlightClient;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.SignRecord;
import com.oreilly.permitsigns.data.SignHeader;
import com.oreilly.permitsigns.interactions.validators.ValidSignType;


// TODO: Update this class - refactor.

public class EditSignType extends TitledInteractionPage implements HighlightClient {
	
	public EditSignType() {
		super();
		withFormatter( new Highlighter( this ) );
		withValidator( new ValidSignType() );
		validationFailedMessage = "Unable to find a valid header based on %input";
		defaultTitle = "Edit Sign Type";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		SignRecord sign = (SignRecord)interaction.context.get( "sign" );
		org.bukkit.block.Sign blockSign = (org.bukkit.block.Sign)interaction.context.get( "block" );
		HashMap< String, SignHeader > signHeaders = PermitSigns.instance.signs.signHeaders;
		String display = "The current header is " + blockSign.getLines()[0] +
				"\n(" + sign.getSignType().toHumanString() + ")\n" +
				"Header choices are:\n";
		for ( String key : signHeaders.keySet() ) {
			SignHeader header = signHeaders.get( key );
			display += "  " + header.headerText + " (" + header.type.toHumanString() + ")\n";
		}
		display += "Please enter the choice you wish to change the sign to:";
		return display;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) {
		SignRecord sign = (SignRecord)interaction.context.get( "sign" );
		String s = (String)data;
		// check for an exact match..
		SignHeader signHeader = PermitSigns.instance.signs.getHeader( s );
		if ( signHeader == null ) {
			interaction.pageWaitingForInput = true;
			return "The sign header information for " + s + " was not found.";
		}
		if ( signHeader.type == null ) {
			interaction.pageWaitingForInput = true;
			return "The sign header information for " + s + " is invalid.";
		}
		sign.setSignHeader( signHeader );
		PermitSigns.instance.signs.refresh( sign );
		return "Sign type updated to " + sign.getSignType().toString() + " with display " + s;
	}
	
	
	@Override
	public HashMap< String, Iterator< String >> getHighlightList() {
		HashMap< String, Iterator< String >> result = new HashMap< String, Iterator< String >>();
		result.put( Highlighter.PLAYER_CHOICES, PermitSigns.instance.signs.signHeaders.keySet().iterator() );
		return result;
	}
}
