package com.oreilly.permitsigns.records;

import java.util.HashSet;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.Permit;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.data.SignHeader;
import com.oreilly.permitsigns.data.SignType;
import com.oreilly.permitsigns.util.Locations;


public class Sign {
	
	static private int nextFileUID = 1;
	static private HashSet< String > takenUIDs = new HashSet< String >();
	
	public String permitAlias = null;
	public SignType signType = SignType.UNDEFINED;
	public Location location = null;
	public String fileUID = null;
	public SignHeader signHeader = null;
	
	
	static public Sign fromConfigurationSection( ConfigurationSection section, String UID, String errorLocation ) {
		if ( section == null )
			return null;
		String headerString = section.getString( SignConfigConstants.signHeader );
		String aliasString = section.getString( SignConfigConstants.permitAlias );
		String locationString = section.getString( SignConfigConstants.location );
		// TODO: Sanity checks
		SignHeader header = SignHeader.fromString( headerString, errorLocation );
		Location location = Locations.fromString( locationString, PermitSigns.instance.getServer() );
		// TODO: Error message
		if ( location == null )
			return null;
		// TODO: Seperate role based sign class (to store extra data, like
		// ratio's, initial number etc)
		return new Sign( header, aliasString, location, UID );
	}
	
	
	static private String nextUID() {
		int trial = nextFileUID;
		nextFileUID++;
		while ( takenUIDs.contains( trial ) ) {
			trial = nextFileUID;
			nextFileUID++;
		}
		return String.valueOf( trial );
	}
	
	
	public Sign( Location location ) {
		this( new SignHeader( "[!!!]", SignType.UNDEFINED ), "???", location, nextUID() );
	}
	
	
	public Sign( SignHeader signHeader, String permitAlias, Location location ) {
		this( signHeader, permitAlias, location, nextUID() );
	}
	
	
	public Sign( SignHeader signHeader, String permitAlias, Location location, String UID ) {
		this.signHeader = signHeader;
		this.signType = signHeader.type;
		this.permitAlias = permitAlias;
		this.location = location;
		if ( takenUIDs.contains( UID ) )
			UID = nextUID();
		takenUIDs.add( UID );
	}
	
	
	public void saveToConfig( YamlConfiguration config, String path ) {
		config.set( path + "." + SignConfigConstants.permitAlias, permitAlias );
		config.set( path + "." + SignConfigConstants.location, Locations.toString( location ) );
		config.set( path + "." + SignConfigConstants.signHeader, signHeader.toString() );
	}
	
	
	public String toHumanString() {
		return "Sign at " + location.getWorld().getName() + " x" + location.getBlockX() + " y" + location.getBlockY() +
				" z" + location.getBlockZ() + " of type " + signType.toString() + " linked to " + permitAlias;
	}
	
	
	@Override
	public String toString() {
		return "Sign (" + signType.toString() + ") for " + permitAlias;
	}
	
	
	public String[] getPermitDisplay() {
		String[] result = new String[2];
		Permit permit = PermitMe.instance.permits.permitsByAlias.get( permitAlias );
		if ( permit == null ) {
			result[0] = "NA";
			result[1] = "Not found";
			return result;
		}
		String permitName = WordUtils.capitalizeFully( permit.name );
		if ( permitName.length() < 14 ) {
			result[0] = permitName;
			result[1] = "";
		} else {
			String[] wraped = WordUtils.wrap( permitName, 14 ).split( "\n" );
			result[0] = wraped[0];
			result[1] = wraped[1];
		}
		return result;
	}
	
}


class SignConfigConstants {
	
	public static final String permitAlias = "permit";
	public static final String location = "location";
	public static final String signHeader = "signHeader";
}