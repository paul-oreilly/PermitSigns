package com.oreilly.permitsigns.records;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.data.SignType;
import com.oreilly.permitsigns.util.Locations;


public class Sign {
	
	public String permitAlias = null;
	public SignType signType = SignType.UNDEFINED;
	public Location location = null;
	
	
	static public Sign fromConfigurationSection( ConfigurationSection section, String errorLocation ) {
		String stringType = section.getString( SignConfigConstants.type );
		if ( stringType == null )
			return null;
		SignType type = SignType.fromString( stringType, errorLocation );
		String aliasString = section.getString( SignConfigConstants.permitAlias );
		String locationString = section.getString( SignConfigConstants.location );
		if ( locationString == null )
			return null; // TODO: Error message
		Location location = Locations.fromString( locationString, PermitSigns.instance.getServer() );
		// TODO: Error message
		if ( location == null )
			return null;
		// TODO: Seperate role based sign class (to store extra data, like
		// ratio's, initial number etc)
		return new Sign( type, aliasString, location );
	}
	
	
	public Sign( SignType signType, String permitAlias, Location location ) {
		this.signType = signType;
		this.permitAlias = permitAlias;
		this.location = location;
	}
	
	
	public void saveToConfig( YamlConfiguration config, String path ) {
		config.set( path + SignConfigConstants.type, signType.toString() );
		config.set( path + SignConfigConstants.permitAlias, permitAlias );
		config.set( path + SignConfigConstants.location, Locations.toString( location ) );
	}
	
}


class SignConfigConstants {
	
	public static final String type = "type";
	public static final String permitAlias = "permit";
	public static final String location = "location";
}