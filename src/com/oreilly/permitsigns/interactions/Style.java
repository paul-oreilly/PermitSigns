package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.StyleConstants;
import com.oreilly.common.interaction.text.formatter.Highlight;


public class Style {
	
	public static String valid( String input ) {
		return Highlight.HighlightAs( StyleConstants.VALID_INPUT, input );
	}
}
