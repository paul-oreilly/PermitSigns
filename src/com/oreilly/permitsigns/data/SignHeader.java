package com.oreilly.permitsigns.data;

public class SignHeader {
	
	public String headerText = "Undefined";
	public SignType type = SignType.UNDEFINED;
	
	
	public static SignHeader fromString( String data, String errorLocation ) {
		// expected format is [type] : [header text]
		// header text will be trimed, moved to lower case (to make case
		// insensitive)
		String[] split = data.split( ":" );
		// TODO: Error checking, messages etc
		String typeString = split[0].trim().toLowerCase();
		SignType type = SignType.fromString( typeString, errorLocation );
		// TODO: Error messages etc
		if ( type != null ) {
			String header = split[1].toLowerCase().trim();
			return new SignHeader( header, type );
		} else
			return null;
	}
	
	
	public static String toString( String headerText, SignType type ) {
		return type.toString() + ": " + headerText;
	}
	
	
	public SignHeader( String header, SignType type ) {
		this.headerText = header;
		this.type = type;
	}
	
	
	public String asSignText() {
		return headerText.toUpperCase();
	}
	
	
	@Override
	public String toString() {
		return SignHeader.toString( headerText, type );
	}
}
