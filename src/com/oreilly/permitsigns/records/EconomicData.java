package com.oreilly.permitsigns.records;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.oreilly.permitsigns.data.EconomicRatio;


public class EconomicData {
	
	public double basePrice = 0;
	public boolean maxPriceDefined = false;
	public double maxPrice = 0;
	public boolean minPriceDefined = false;
	public double minPrice = 0;
	public double roundingFactor = 1;
	
	// purchase and decay system
	public double variablePrice = 0;
	public boolean fixedTimeDecayDefined = false;
	public double fixedTimeDecayAmount = 0;
	public int fixedTimeDecayInterval = 0; // in seconds
	public boolean purchaseFactorDefined = false;
	public double purchaseFactor = 1;
	public boolean fixedRatioDecayDefined = false;
	public double fixedRatioDecayFactor = 1;
	public int fixedRatioDecayInterval = 0; // in seconds
	
	// ratio based data - by alias
	public boolean ratioDefined = false;
	public HashMap< String, EconomicRatio > ratios = new HashMap< String, EconomicRatio >();
	
	public String permitAlias = null;
	
	// the current price, after all calculations are done
	public double currentPrice = 0;
	public double ratioPrice = 0; // cache
	
	
	static public EconomicData fromConfigurationSection( ConfigurationSection section, String errorLocation ) {
		String permitAlias = section.getString( EconomicDataConfigConstant.permitAlias );
		if ( permitAlias == null )
			return null;
		double basePrice = section.getDouble( EconomicDataConfigConstant.basePrice, 10000L );
		EconomicData result = new EconomicData( permitAlias, basePrice );
		result.maxPriceDefined = section.contains( EconomicDataConfigConstant.maxPrice );
		if ( result.maxPriceDefined )
			result.maxPrice = section.getDouble( EconomicDataConfigConstant.maxPrice );
		result.minPriceDefined = section.contains( EconomicDataConfigConstant.minPrice );
		if ( result.minPriceDefined )
			result.minPrice = section.getDouble( EconomicDataConfigConstant.minPrice );
		result.variablePrice = section.getDouble( EconomicDataConfigConstant.currentDecayPrice, basePrice );
		result.fixedTimeDecayDefined = section.contains( EconomicDataConfigConstant.fixedTimeDecreaseAmount );
		if ( result.fixedTimeDecayDefined ) {
			result.fixedTimeDecayAmount = section.getDouble( EconomicDataConfigConstant.fixedTimeDecreaseAmount );
			result.fixedTimeDecayInterval = section.getInt( EconomicDataConfigConstant.fixedTimeDecreaseInterval, 3600 );
		}
		result.purchaseFactorDefined = section.contains( EconomicDataConfigConstant.purchaseFactor );
		if ( result.purchaseFactorDefined )
			result.purchaseFactor = section.getDouble( EconomicDataConfigConstant.purchaseFactor );
		result.fixedRatioDecayDefined = section.contains( EconomicDataConfigConstant.fixedRatioDecayFactor );
		if ( result.fixedRatioDecayDefined ) {
			result.fixedRatioDecayFactor = section.getDouble( EconomicDataConfigConstant.fixedRatioDecayFactor );
			result.fixedRatioDecayInterval = section.getInt( EconomicDataConfigConstant.fixedRatioDecayInterval, 3600 );
		}
		if ( section.contains( EconomicDataConfigConstant.ratioHeader ) ) {
			List< String > ratioData = section.getStringList( EconomicDataConfigConstant.ratioHeader );
			for ( String ratioEntry : ratioData ) {
				EconomicRatio ratio = EconomicRatio.fromString( ratioEntry, errorLocation );
				if ( ratio != null )
					result.ratios.put( ratio.otherAlias, ratio );
			}
		}
		return result;
	}
	
	
	public EconomicData( String permitAlias, double basePrice ) {
		this.permitAlias = permitAlias;
		this.basePrice = basePrice;
	}
	
	
	public void saveToConfig( YamlConfiguration config, String path ) {
		config.set( path + EconomicDataConfigConstant.permitAlias, permitAlias );
		config.set( path + EconomicDataConfigConstant.basePrice, basePrice );
		config.set( path + EconomicDataConfigConstant.roundingFactor, roundingFactor );
		if ( maxPriceDefined )
			config.set( path + EconomicDataConfigConstant.maxPrice, maxPrice );
		if ( minPriceDefined )
			config.set( path + EconomicDataConfigConstant.minPrice, minPrice );
		config.set( path + EconomicDataConfigConstant.currentDecayPrice, variablePrice );
		if ( fixedTimeDecayDefined ) {
			config.set( path + EconomicDataConfigConstant.fixedTimeDecreaseAmount, fixedTimeDecayAmount );
			config.set( path + EconomicDataConfigConstant.fixedTimeDecreaseInterval, fixedTimeDecayInterval );
		}
		if ( purchaseFactorDefined )
			config.set( path + EconomicDataConfigConstant.purchaseFactor, purchaseFactor );
		if ( fixedRatioDecayDefined ) {
			config.set( path + EconomicDataConfigConstant.fixedRatioDecayFactor, fixedRatioDecayFactor );
			config.set( path + EconomicDataConfigConstant.fixedRatioDecayInterval, fixedRatioDecayInterval );
		}
		LinkedList< String > ratioData = new LinkedList< String >();
		for ( EconomicRatio ratio : ratios.values() ) {
			String asString = ratio.toString();
			if ( asString != null )
				ratioData.add( asString );
		}
		if ( ratioData.size() > 0 )
			config.set( path + EconomicDataConfigConstant.ratioHeader, ratioData );
	}
	
}


class EconomicDataConfigConstant {
	
	static public final String permitAlias = "permitAlias";
	static public final String basePrice = "price.base";
	static public final String minPrice = "price.min";
	static public final String maxPrice = "price.max";
	static public final String roundingFactor = "price.roundingFactor";
	static public final String currentDecayPrice = "decay.currentPrice";
	static public final String fixedTimeDecreaseAmount = "decay.time.amount";
	static public final String fixedTimeDecreaseInterval = "decay.time.seconds";
	static public final String fixedRatioDecayFactor = "decay.ratio.factor";
	static public final String fixedRatioDecayInterval = "decay.ratio.seconds";
	static public final String purchaseFactor = "decay.purchase.factor";
	static public final String ratioHeader = "ratios";
}