package com.oreilly.permitsigns.data;

import com.oreilly.permitsigns.SignRecord;


public class SignEditDialog {
	
	public SignEditDialogState state = SignEditDialogState.SIGN_SELECTION;
	public SignRecord selectedSign = null;
	
	
	public SignEditDialog() {
	}
}
