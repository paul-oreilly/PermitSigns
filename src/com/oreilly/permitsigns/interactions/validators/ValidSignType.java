package com.oreilly.permitsigns.interactions.validators;

import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.ValidationFailedError;
import com.oreilly.common.interaction.text.validator.Validator;
import com.oreilly.permitsigns.PermitSigns;


public class ValidSignType extends Validator {
	
	@Override
	protected Object validate( Object object, InteractionPage page ) throws ValidationFailedError {
		String s = object.toString();
		if ( PermitSigns.instance.signs.getHeader( s ) == null )
			throw new ValidationFailedError( this, "Unable to obtain a sign header from " + s );
		return object;
	}
	
}
