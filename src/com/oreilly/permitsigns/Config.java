package com.oreilly.permitsigns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.oreilly.permitme.PermitMe;
import com.oreilly.permitsigns.data.SignHeader;
import com.oreilly.permitsigns.data.SignType;


public class Config {
	
	// TODO: a globalPriceAdjustment that will scale all sign prices
	// (display & effect only, not config saves)
	// TODO: a default economic data
	// TODO: Example file creation
	
	static public File pluginRoot = null;
	static public File priceDir = null;
	static public File signFolder = null;
	static public File conf = null;
	
	
	public static void load() {
		loadFilePaths();
		
		// load main config
		loadConfig();
		
		// load price data
		loadPriceData();
		
		// load sign data
		loadSigns();
	}
	
	
	public static void saveSigns( String worldName ) {
		// signs are saved as a list in "[worldname]_signs.yml"
		
		File source = new File( signFolder + File.separator + worldName + "_signs.yml" );
		YamlConfiguration config = loadYamlFile( source );
		
		List< SignRecord > signsInWorld = PermitSigns.instance.signs.getSignsInWorld( worldName );
		for ( SignRecord subject : signsInWorld ) {
			String key = null;
			if ( subject.fileUID != null )
				if ( subject.fileUID.length() > 0 )
					key = subject.fileUID;
			if ( key == null )
				key = subject.location.getBlockX() + "-" +
						subject.location.getBlockY() + "-" +
						subject.location.getBlockZ();
			subject.saveToConfig( config, key );
		}
		
		try {
			config.save( source );
		} catch ( IOException e ) {
			PermitMe.log.warning( "[PermitSigns] !! IO Exception while saving sign " +
					source.getName() + " to " + source.getPath() );
			e.printStackTrace();
		}
	}
	
	
	public static void savePriceData( PriceRecord data ) {
		File economicFile = new File( priceDir + File.separator + data.permitAlias + ".yml" );
		YamlConfiguration config = loadYamlFile( economicFile );
		data.saveToConfig( config, "economicData" );
		try {
			config.save( economicFile );
		} catch ( IOException e ) {
			PermitMe.log.warning( "[PermitSigns] !! IO Exception while saving economic data " +
					data.permitAlias );
			e.printStackTrace();
		}
	}
	
	
	private static void loadFilePaths() {
		pluginRoot = PermitSigns.instance.getDataFolder();
		conf = new File( pluginRoot.getPath() + File.separator + "config.yml" );
		priceDir = new File( pluginRoot.getPath() + File.separator + "pricing" );
		signFolder = new File( pluginRoot.getPath() + File.separator + "signs" );
	}
	
	
	private static void loadConfig() {
		// load sign header information
		FileConfiguration config = loadYamlFile( conf );
		Signs signs = PermitSigns.instance.signs;
		List< String > headerData = config.getStringList( ConfigConstant.signHeaders );
		if ( headerData != null )
			if ( headerData.size() > 0 )
				for ( String headerString : headerData ) {
					SignHeader header = SignHeader.fromString( headerString, "config.yml" );
					if ( header != null )
						signs.signHeaders.put( header.headerText, header );
				}
		// provide default header's if none have been loaded
		if ( signs.signHeaders.size() == 0 ) {
			signs.addHeader( "[permit]", SignType.SALE );
			signs.addHeader( "[cost]", SignType.SALEPREVIEW );
			signs.addHeader( "[monitor]", SignType.MONITOR );
		}
	}
	
	
	private static void loadSigns() {
		// signs are saved as a list in "[worldname]_signs.yml"
		if ( !signFolder.exists() ) {
			signFolder.mkdirs();
		} else {
			File[] files = signFolder.listFiles();
			if ( files != null )
				for ( File file : files ) {
					if ( !file.getName().endsWith( ".yml" ) )
						continue;
					if ( !file.canRead() ) {
						// TODO: Error message
						continue;
					}
					YamlConfiguration config = loadYamlFile( file );
					for ( String fileUID : config.getKeys( false ) ) {
						SignRecord sign = SignRecord.fromConfigurationSection(
								config.getConfigurationSection( fileUID ), fileUID,
								file.getAbsolutePath() );
						if ( sign != null )
							PermitSigns.instance.signs.addSign( sign );
					}
				}
		}
	}
	
	
	private static void loadPriceData() {
		// pricing is saved by permitAlias in pricing directory
		if ( !priceDir.exists() )
			priceDir.mkdirs();
		else {
			File[] files = priceDir.listFiles();
			if ( files != null )
				for ( File file : files ) {
					if ( !file.getName().endsWith( ".yml" ) )
						continue;
					if ( !file.canRead() ) {
						// TODO: Error message
						continue;
					}
					YamlConfiguration config = loadYamlFile( file );
					PriceRecord data = PriceRecord.fromConfigurationSection(
							config.getConfigurationSection( "economicData" ), file.getAbsoluteFile().getAbsolutePath() );
					if ( data != null ) {
						PermitSigns.instance.prices.addPriceRecord( data );
					}
				}
		}
	}
	
	
	private static YamlConfiguration loadYamlFile( File file ) {
		if ( !file.exists() ) {
			try {
				System.out.print( "**** Files doesn't exist, creating.. *****" );
				PermitMe.log.warning( "[PermitSigns] !! File " + file.getName() + " not found." );
				File parent = file.getParentFile();
				PermitMe.log.warning( "[PermitSigns]   checking directory exists " + parent.getName() );
				if ( file.getParentFile().exists() == false ) {
					parent.mkdirs();
					PermitMe.log.warning( "[PermitSigns]   created directories for " + parent.getAbsolutePath() );
				}
				PermitMe.log.warning( "[PermitSigns]   creating new file " + file.getName() );
				file.createNewFile();
			} catch ( IOException e ) {
				PermitMe.log.warning( "[PermitSigns] !! IO Error while trying to load " + file.getName() + " from "
						+ file.getPath() );
				e.printStackTrace();
			}
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load( file );
		} catch ( FileNotFoundException e ) {
			PermitMe.log.warning( "[PermitSigns] !! File not found error while trying to load " + file.getName()
					+ " from " + file.getPath() + " into config object" );
			e.printStackTrace();
		} catch ( IOException e ) {
			PermitMe.log.warning( "[PermitSigns] !! IO Error while trying to load " + file.getName() + " from "
					+ file.getPath() + " into config object" );
			e.printStackTrace();
		} catch ( InvalidConfigurationException e ) {
			PermitMe.log.warning( "[PermitSigns] !! Error: Invalid configuration in file " + file.getName() + " from "
					+ file.getPath() );
			e.printStackTrace();
		}
		return config;
	}
	
}


class ConfigConstant {
	
	static final public String signHeaders = "signTypes";
}
