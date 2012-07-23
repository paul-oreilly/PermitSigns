package com.oreilly.permitsigns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.oreilly.permitsigns.data.EconomicRatio;


public class PriceRecord {
	
	protected double basePrice = 0;
	protected boolean maxPriceDefined = false;
	protected double maxPrice = 0;
	protected boolean minPriceDefined = false;
	protected double minPrice = 0;
	protected double roundingFactor = 1;
	
	// purchase and decay system
	protected double variablePrice = 0;
	// time based amount of a set value..
	protected boolean timeAmountDefined = false;
	protected double timeAmount = 0;
	protected int timeAmountInterval = 0; // in ticks
	// time based factor (multiplied)
	protected boolean timeFactorDefined = false;
	protected double timeFactor = 1;
	protected int timeFactorInterval = 0; // in ticks
	// purchase based set value
	protected boolean purchaseAmountDefined = false;
	protected double purchaseAmount = 0;
	// purchase based factor (multiplied)
	protected boolean purchaseFactorDefined = false;
	protected double purchaseFactor = 1;
	
	// ratio based data - by alias
	protected boolean ratioDefined = false;
	protected HashMap< String, EconomicRatio > ratios = new HashMap< String, EconomicRatio >();
	
	public String permitAlias = null;
	
	// the current price, after all calculations are done
	protected double currentPrice = 0;
	protected double ratioPrice = 0; // cache
	
	// other data not saved
	protected int ticksSinceTimeDecayUpdated = 0;
	protected int ticksSinceRatioDecayUpdated = 0;
	
	
	static public PriceRecord fromConfigurationSection( ConfigurationSection section, String errorLocation ) {
		String permitAlias = section.getString( EconomicDataConfigConstant.permitAlias );
		if ( permitAlias == null )
			return null;
		double basePrice = section.getDouble( EconomicDataConfigConstant.basePrice, 10000L );
		PriceRecord result = new PriceRecord( permitAlias, basePrice );
		result.maxPriceDefined = section.contains( EconomicDataConfigConstant.maxPrice );
		if ( result.maxPriceDefined )
			result.maxPrice = section.getDouble( EconomicDataConfigConstant.maxPrice );
		result.minPriceDefined = section.contains( EconomicDataConfigConstant.minPrice );
		if ( result.minPriceDefined )
			result.minPrice = section.getDouble( EconomicDataConfigConstant.minPrice );
		result.variablePrice = section.getDouble( EconomicDataConfigConstant.variablePrice, basePrice );
		result.timeAmountDefined = section.contains( EconomicDataConfigConstant.timeAmount );
		if ( result.timeAmountDefined ) {
			result.timeAmount = section.getDouble( EconomicDataConfigConstant.timeAmount );
			result.timeAmountInterval = section.getInt( EconomicDataConfigConstant.timeAmountInterval, 3600 );
		}
		result.purchaseFactorDefined = section.contains( EconomicDataConfigConstant.purchaseFactor );
		if ( result.purchaseFactorDefined )
			result.purchaseFactor = section.getDouble( EconomicDataConfigConstant.purchaseFactor );
		result.purchaseAmountDefined = section.contains( EconomicDataConfigConstant.purchaseAmount );
		if ( result.purchaseAmountDefined )
			result.purchaseAmount = section.getDouble( EconomicDataConfigConstant.purchaseAmount );
		result.timeFactorDefined = section.contains( EconomicDataConfigConstant.timeFactor );
		if ( result.timeFactorDefined ) {
			result.timeFactor = section.getDouble( EconomicDataConfigConstant.timeFactor );
			result.timeFactorInterval = section.getInt( EconomicDataConfigConstant.timeFactorInterval, 3600 );
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
	
	
	public PriceRecord( String permitAlias, double basePrice ) {
		this.permitAlias = permitAlias;
		this.basePrice = basePrice;
	}
	
	
	public double getBasePrice() {
		return basePrice;
	}
	
	
	public void setBasePrice( double newPrice ) {
		basePrice = newPrice;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getMaxPrice() {
		return maxPrice;
	}
	
	
	public void setMaxPrice( double newPrice ) {
		maxPrice = newPrice;
		maxPriceDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getMinPrice() {
		return minPrice;
	}
	
	
	public void setMinPrice( double newPrice ) {
		minPrice = newPrice;
		minPriceDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getRounding() {
		return roundingFactor;
	}
	
	
	public void setRounding( double newFactor ) {
		roundingFactor = newFactor;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getVariablePrice() {
		return variablePrice;
	}
	
	
	public void setVariablePrice( double newPrice ) {
		variablePrice = newPrice;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public boolean getVariablePriceDefined() {
		return ( timeAmountDefined | purchaseFactorDefined | timeFactorDefined | purchaseAmountDefined );
	}
	
	
	public boolean getTimeAmountDefined() {
		return timeAmountDefined;
	}
	
	
	public void setTimeAmountDefined( boolean newValue ) {
		timeAmountDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getTimeAmount() {
		return timeAmount;
	}
	
	
	public void setTimeAmount( double newValue ) {
		timeAmount = newValue;
		timeAmountDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public int getTimeAmountInterval() {
		return timeAmountInterval;
	}
	
	
	public void setTimeAmountInterval( int ticks ) {
		timeAmountInterval = ticks;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public boolean getPurchaseAmountDefined() {
		return purchaseAmountDefined;
	}
	
	
	public void setPurchaseAmountDefined( boolean newValue ) {
		purchaseAmountDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getPurchaseAmount() {
		return purchaseAmount;
	}
	
	
	public void setPurchaseAmonut( double newValue ) {
		purchaseAmount = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public boolean getPurchaseFactorDefined() {
		return purchaseFactorDefined;
	}
	
	
	public void setPruchaseFactorDefined( boolean newValue ) {
		purchaseFactorDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getPurchaseFactor() {
		return purchaseFactor;
	}
	
	
	public void setPurchaseFactor( double newFactor ) {
		purchaseFactor = newFactor;
		purchaseFactorDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public boolean getTimeFactorDefined() {
		return timeFactorDefined;
	}
	
	
	public void setTimeFactorDefined( boolean newValue ) {
		timeFactorDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getTimeFactor() {
		return timeFactor;
	}
	
	
	public void setTimeFactor( double newFactor ) {
		timeFactor = newFactor;
		timeFactorDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public int getTimeFactorInterval() {
		return timeFactorInterval;
	}
	
	
	public void setTimeFactorInterval( int ticks ) {
		timeFactorInterval = ticks;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public boolean getRatioPriceDefined() {
		return ratioDefined;
	}
	
	
	public void setRatioPriceDefined( boolean newValue ) {
		ratioDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getRatioPrice() {
		return ratioPrice;
	}
	
	
	public List< EconomicRatio > getPriceRatios() {
		LinkedList< EconomicRatio > result = new LinkedList< EconomicRatio >();
		result.addAll( ratios.values() );
		return result;
	}
	
	
	// TODO: add and remove price ratio functions
	
	// IO Functions...
	
	public void saveToConfig( YamlConfiguration config, String path ) {
		config.set( path + "." + EconomicDataConfigConstant.permitAlias, permitAlias );
		config.set( path + "." + EconomicDataConfigConstant.basePrice, basePrice );
		config.set( path + "." + EconomicDataConfigConstant.roundingFactor, roundingFactor );
		if ( maxPriceDefined )
			config.set( path + "." + EconomicDataConfigConstant.maxPrice, maxPrice );
		if ( minPriceDefined )
			config.set( path + "." + EconomicDataConfigConstant.minPrice, minPrice );
		config.set( path + "." + EconomicDataConfigConstant.variablePrice, variablePrice );
		if ( timeAmountDefined ) {
			config.set( path + "." + EconomicDataConfigConstant.timeAmount, timeAmount );
			config.set( path + "." + EconomicDataConfigConstant.timeAmountInterval, timeAmountInterval );
		}
		if ( timeFactorDefined ) {
			config.set( path + "." + EconomicDataConfigConstant.timeFactor, timeFactor );
			config.set( path + "." + EconomicDataConfigConstant.timeFactorInterval, timeFactorInterval );
		}
		if ( purchaseFactorDefined )
			config.set( path + "." + EconomicDataConfigConstant.purchaseFactor, purchaseFactor );
		if ( purchaseAmountDefined )
			config.set( path + "." + EconomicDataConfigConstant.purchaseAmount, purchaseAmount );
		LinkedList< String > ratioData = new LinkedList< String >();
		for ( EconomicRatio ratio : ratios.values() ) {
			String asString = ratio.toString();
			if ( asString != null )
				ratioData.add( asString );
		}
		if ( ratioData.size() > 0 )
			config.set( path + "." + EconomicDataConfigConstant.ratioHeader, ratioData );
	}
	
	
	@Override
	public String toString() {
		return "PermitSigns economic data for " + permitAlias;
	}
	
	
	public String toHumanString() {
		return toHumanString( true, "  " );
	}
	
	
	public String toHumanString( boolean showHeader, String bodyPrefix ) {
		String result = "";
		if ( showHeader )
			result += "PermitSigns economic data for " + permitAlias + ":\n";
		result += bodyPrefix + "The current cost is " + withRounding( currentPrice ) + "\n";
		// min and max prices...
		if ( minPriceDefined | maxPriceDefined ) {
			result += bodyPrefix + " (with a ";
			if ( minPriceDefined )
				result += "minimum of " + withRounding( minPrice );
			if ( minPriceDefined & maxPriceDefined )
				result += " and a ";
			if ( maxPriceDefined )
				result += "maximum of " + withRounding( maxPrice );
			result += ")\n";
		} else
			result += bodyPrefix + "(No Minimum and / or maxium prices)\n";
		// rounding factor
		result += bodyPrefix + "Prices are always rounded to the nearest " + roundingFactor + "\n\n";
		// variable pricing
		if ( getVariablePriceDefined() ) {
			result += bodyPrefix + "Variable pricing is exists ( value of " + withRounding( variablePrice ) + ")\n";
			if ( purchaseFactorDefined )
				result += bodyPrefix + "  On purchase, the price is multiplied by " + purchaseFactor + "\n";
			if ( purchaseAmountDefined )
				result += bodyPrefix + "  On purchase, the price is adjusted by " + purchaseAmount + "\n";
			if ( timeAmountDefined )
				result += bodyPrefix + "  Every " + timeAmountInterval + " seconds" +
						", the price is adjusted by " + timeAmount + "\n";
			if ( timeFactorDefined )
				result += bodyPrefix + "  Every " + timeFactorInterval + " seconds" +
						", the price is be multiplied by " + timeFactor + "\n";
		} else
			result += bodyPrefix + "No Variable pricing.\n";
		// ratio pricing
		result += "\n";
		if ( ratioDefined ) {
			result += bodyPrefix + "Ratio based pricing exists (with a current value of " + ratioPrice + ")\n" +
					bodyPrefix + "  The pricing is based on the following links:\n";
			for ( EconomicRatio ratio : ratios.values() )
				result += bodyPrefix + "  " + ratio.sourceCount + ":" + ratio.otherCount + " -> " + ratio.otherAlias +
						"\n";
		} else
			result += bodyPrefix + "No ratio based pricing.\n";
		return result;
	}
	
	
	public double withRounding( double data ) {
		return Math.floor( data / roundingFactor ) * roundingFactor;
	}
	
}


class EconomicDataConfigConstant {
	
	static public final String permitAlias = "permitAlias";
	static public final String basePrice = "price.base";
	static public final String minPrice = "price.min";
	static public final String maxPrice = "price.max";
	static public final String roundingFactor = "price.roundingFactor";
	static public final String variablePrice = "price.variable.currentPrice";
	static public final String timeAmount = "price.variable.time.amount";
	static public final String timeAmountInterval = "price.variable.time.ticks";
	static public final String timeFactor = "price.variable.factor.factor";
	static public final String timeFactorInterval = "price.variable.factor.ticks";
	static public final String purchaseFactor = "price.variable.purchase.factor";
	static public final String purchaseAmount = "price.variable.purchase.amount";
	static public final String ratioHeader = "price.ratios";
}