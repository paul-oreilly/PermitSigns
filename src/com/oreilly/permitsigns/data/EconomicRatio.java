package com.oreilly.permitsigns.data;

public class EconomicRatio {
	
	public String otherAlias = null;
	
	// source:other
	public int sourceCount = 1;
	public int otherCount = 1;
	
	
	public static EconomicRatio fromString( String data, String errorLocation ) {
		// expected format is " permit alias x : y" or " permit alias x:y"
		data = data.toLowerCase().trim();
		String[] splity = data.split( ":" );
		// TODO: Error catching, size checking
		int y = Integer.parseInt( splity[splity.length - 1] );
		String[] splitx = splity[0].split( " " );
		int x = Integer.parseInt( splitx[splitx.length - 1] );
		String alias = "";
		for ( int i = 0; i < splitx.length - 2; i++ ) {
			if ( i > 0 )
				alias += " ";
			alias = splitx[i];
		}
		return new EconomicRatio( alias, x, y );
	}
	
	
	public EconomicRatio( String alias, int x, int y ) {
		this.otherAlias = alias;
		this.sourceCount = y;
		this.otherCount = x;
	}
	
	
	@Override
	public String toString() {
		return otherAlias + " " + otherCount + ":" + sourceCount;
	}
	
	
	public String toHumanString() {
		return sourceCount + " for every " + otherCount + " " + otherAlias;
	}
	
}
