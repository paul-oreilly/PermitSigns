package com.oreilly.permitsigns.data;

import com.oreilly.permitsigns.records.Sign;


public class SignEditDialog {
	
	public SignEditDialogState state = SignEditDialogState.SIGN_SELECTION;
	public Sign selectedSign = null;
	
	
	public SignEditDialog() {
	}
}
