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
	protected boolean fixedTimeDecayDefined = false;
	protected double fixedTimeDecayAmount = 0;
	protected int fixedTimeDecayInterval = 0; // in seconds
	protected boolean purchaseFactorDefined = false;
	protected double purchaseFactor = 1;
	// TODO: Add "PurchaseDelta" for fixed amount
	protected boolean fixedRatioDecayDefined = false;
	protected double fixedRatioDecayFactor = 1;
	protected int fixedRatioDecayInterval = 0; // in seconds
	
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
		return ( fixedTimeDecayDefined | purchaseFactorDefined | fixedRatioDecayDefined );
	}
	
	
	public boolean getTimeAmountDefined() {
		return fixedTimeDecayDefined;
	}
	
	
	public void setTimeAmountDefined( boolean newValue ) {
		fixedTimeDecayDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getTimeAmount() {
		return fixedTimeDecayAmount;
	}
	
	
	public void setTimeAmount( double newValue ) {
		fixedTimeDecayAmount = newValue;
		fixedTimeDecayDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public int getTimeAmountInterval() {
		return fixedTimeDecayInterval;
	}
	
	
	public void setTimeAmountInterval( int ticks ) {
		fixedTimeDecayInterval = ticks;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	// TODO: Add fixed amonut, and then meta boolean for either
	
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
		return fixedRatioDecayDefined;
	}
	
	
	public void setTimeFactorDefined( boolean newValue ) {
		fixedRatioDecayDefined = newValue;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public double getTimeFactor() {
		return fixedRatioDecayFactor;
	}
	
	
	public void setTimeFactor( double newFactor ) {
		fixedRatioDecayFactor = newFactor;
		fixedRatioDecayDefined = true;
		PermitSigns.instance.prices.priceRecordChange( this );
	}
	
	
	public int getTimeFactorInterval() {
		return fixedRatioDecayInterval;
	}
	
	
	public void setTimeFactorInterval( int ticks ) {
		fixedRatioDecayInterval = ticks;
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
		config.set( path + "." + EconomicDataConfigConstant.currentDecayPrice, variablePrice );
		if ( fixedTimeDecayDefined ) {
			config.set( path + "." + EconomicDataConfigConstant.fixedTimeDecreaseAmount, fixedTimeDecayAmount );
			config.set( path + "." + EconomicDataConfigConstant.fixedTimeDecreaseInterval, fixedTimeDecayInterval );
		}
		if ( purchaseFactorDefined )
			config.set( path + "." + EconomicDataConfigConstant.purchaseFactor, purchaseFactor );
		if ( fixedRatioDecayDefined ) {
			config.set( path + "." + EconomicDataConfigConstant.fixedRatioDecayFactor, fixedRatioDecayFactor );
			config.set( path + "." + EconomicDataConfigConstant.fixedRatioDecayInterval, fixedRatioDecayInterval );
		}
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
		if ( purchaseFactorDefined | fixedTimeDecayDefined | fixedRatioDecayDefined ) {
			result += bodyPrefix + "Variable pricing is exists ( value of " + withRounding( variablePrice ) + ")\n";
			if ( purchaseFactorDefined )
				result += bodyPrefix + "  A \'purchase factor\' exists:\n" +
						bodyPrefix + "  - When pruchased, the price is multiplied by " + purchaseFactor + "\n";
			if ( fixedTimeDecayDefined )
				result += bodyPrefix + "  A fixed time based adjustment exists:\n" +
						bodyPrefix + "  - Every " + fixedTimeDecayInterval + " seconds" +
						", the price will be adjusted by " + fixedTimeDecayAmount + "\n";
			if ( fixedRatioDecayDefined )
				result += bodyPrefix + "  A ratio time based adjustment exists:\n" +
						bodyPrefix + "  - Every " + fixedRatioDecayInterval + " seconds" +
						", the price will be multiplied by " + fixedRatioDecayFactor + "\n";
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
	static public final String currentDecayPrice = "decay.currentPrice";
	static public final String fixedTimeDecreaseAmount = "decay.time.amount";
	static public final String fixedTimeDecreaseInterval = "decay.time.seconds";
	static public final String fixedRatioDecayFactor = "decay.ratio.factor";
	static public final String fixedRatioDecayInterval = "decay.ratio.seconds";
	static public final String purchaseFactor = "decay.purchase.factor";
	static public final String ratioHeader = "ratios";
}