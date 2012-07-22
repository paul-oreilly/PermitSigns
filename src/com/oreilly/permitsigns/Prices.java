package com.oreilly.permitsigns;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.TreeMap;

import net.milkbowl.vault.economy.EconomyResponse;

import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.Players;
import com.oreilly.permitme.record.Permit;
import com.oreilly.permitme.record.PermitPlayer;
import com.oreilly.permitsigns.data.EconomicRatio;
import com.oreilly.permitsigns.events.PermitSignsPriceChangeEvent;


// TODO: Some type of "default" economic data.

public class Prices {
	
	private final TreeMap< String, Double > permitPrices = new TreeMap< String, Double >();
	private final TreeMap< String, String > permitPricesAsStrings = new TreeMap< String, String >();
	// sorted by permitUUID
	private final TreeMap< String, PriceRecord > economicData = new TreeMap< String, PriceRecord >();
	private final TreeMap< String, LinkedList< PriceRecord >> dataByRatio = new TreeMap< String, LinkedList< PriceRecord >>();
	// cached for when ratio data changes
	private final TreeMap< String, LinkedList< String >> cacheRatioDataLinks = new TreeMap< String, LinkedList< String >>();
	
	public long scheduleTicksIntervalForTimeAdjustments = 100L;
	public int scheduleTaskID = 0;
	
	
	public Prices( PermitSigns manager ) {
		// TODO: Setup timed event for updating some prices
		// TODO: Default timed event interval
		scheduleTaskID = manager.getServer().getScheduler().scheduleSyncRepeatingTask( manager, new Runnable() {
			
			@Override
			public void run() {
				if ( PermitSigns.instance != null )
					PermitSigns.instance.prices.scheduledTaskUpdatePrices();
			}
		}, 60L, scheduleTicksIntervalForTimeAdjustments );
	}
	
	
	public void addEconomicData( PriceRecord data ) {
		// TODO: Error checking
		economicData.put( data.permitAlias, data );
		if ( data.ratios != null )
			addRatioRecords( data );
		updatePrice( data );
	}
	
	
	public void scheduledTaskUpdatePrices() {
		for ( PriceRecord data : economicData.values() ) {
			boolean dataChange = false;
			// update ratio decay data if required
			if ( data.fixedRatioDecayDefined ) {
				data.ticksSinceRatioDecayUpdated += scheduleTicksIntervalForTimeAdjustments;
				if ( data.ticksSinceRatioDecayUpdated / 20 > data.fixedRatioDecayInterval ) {
					data.ticksSinceRatioDecayUpdated -= 20 * data.fixedRatioDecayInterval;
					data.variablePrice *= data.fixedRatioDecayFactor;
					dataChange = true;
				}
			}
			// update fixed decay data if required
			if ( data.fixedTimeDecayDefined ) {
				data.ticksSinceTimeDecayUpdated += scheduleTicksIntervalForTimeAdjustments;
				if ( data.ticksSinceTimeDecayUpdated / 20 > data.fixedTimeDecayInterval ) {
					data.ticksSinceTimeDecayUpdated -= 20 * data.fixedTimeDecayInterval;
					data.variablePrice -= data.fixedTimeDecayAmount;
					dataChange = true;
				}
			}
			// adjust to range if required
			if ( data.maxPriceDefined )
				if ( data.variablePrice > data.maxPrice )
					data.variablePrice = data.maxPrice;
			if ( data.minPriceDefined ) {
				if ( data.variablePrice < data.minPrice )
					data.variablePrice = data.minPrice;
			} else if ( data.variablePrice < 0 )
				data.variablePrice = 0;
			// if we have a change, get the data updated
			if ( dataChange )
				updatePrice( data );
		}
	}
	
	
	public void ratioDataChanged( PriceRecord data ) {
		LinkedList< String > oldRecord = cacheRatioDataLinks.remove( data.permitAlias );
		LinkedList< PriceRecord > byRatio = null;
		if ( oldRecord != null ) {
			// remove all existing entries
			for ( String key : oldRecord ) {
				byRatio = dataByRatio.get( key );
				if ( byRatio != null )
					byRatio.remove( data );
			}
		}
		// if there's no longer any ratio data, then remove the primary entry
		boolean removePrimary = false;
		if ( data.ratios == null )
			removePrimary = true;
		else if ( data.ratios.size() == 0 )
			removePrimary = true;
		if ( removePrimary ) {
			byRatio = dataByRatio.get( data.permitAlias );
			if ( byRatio != null )
				byRatio.remove( data );
			data.ratioDefined = false;
		} else {
			addRatioRecords( data );
		}
	}
	
	
	private void addRatioRecords( PriceRecord data ) {
		// cache current data, so we can update if ratio data has later changed
		LinkedList< String > cacheList = cacheRatioDataLinks.get( data.permitAlias );
		if ( cacheList == null ) {
			cacheList = new LinkedList< String >();
			cacheRatioDataLinks.put( data.permitAlias, cacheList );
		}
		// add to the "by ratio" record for the primary permit alias
		LinkedList< PriceRecord > byRatio = dataByRatio.get( data.permitAlias );
		if ( byRatio == null ) {
			byRatio = new LinkedList< PriceRecord >();
			dataByRatio.put( data.permitAlias, byRatio );
		}
		byRatio.add( data );
		// for each ratio entry...
		for ( EconomicRatio ratio : data.ratios.values() ) {
			// add a record of the linked permit to cache
			cacheList.add( ratio.otherAlias );
			// add record to dataByRatio, so we can update when player numbers change later
			byRatio = dataByRatio.get( ratio.otherAlias );
			if ( byRatio == null ) {
				byRatio = new LinkedList< PriceRecord >();
				dataByRatio.put( ratio.otherAlias, byRatio );
			}
			byRatio.add( data );
		}
		updateRatioPrice( data );
	}
	
	
	public TreeMap< String, PriceRecord > getEconomicData() {
		return economicData;
	}
	
	
	public PriceRecord getPriceRecord( String permitAlias ) {
		PriceRecord result = economicData.get( permitAlias );
		if ( result != null )
			return result;
		else {
			// check permit alias is valid
			Permit permit = PermitMe.instance.permits.permitsByAlias.get( permitAlias );
			if ( permit == null )
				return null;
			else {
				// make a default economic data, and use that
				result = new PriceRecord( permitAlias, 10000 );
				addEconomicData( result );
				return result;
			}
		}
	}
	
	
	public void playerGainedPermit( PermitPlayer player, String permitAlias ) {
		PriceRecord data = economicData.get( permitAlias );
		if ( data.purchaseFactorDefined )
			data.variablePrice *= data.purchaseFactor;
		updateRatios( data );
		updatePrice( permitAlias );
	}
	
	
	public void playerLostPermit( PermitPlayer player, String permitAlias ) {
		PriceRecord data = economicData.get( permitAlias );
		updateRatios( data );
		updatePrice( permitAlias );
	}
	
	
	public double getPrice( String permitAlias ) {
		Double price = permitPrices.get( permitAlias );
		if ( price == null ) {
			updatePrice( permitAlias );
			return permitPrices.get( permitAlias );
		}
		else
			return price;
	}
	
	
	public String getPriceString( String permitAlias ) {
		String priceAsString = permitPricesAsStrings.get( permitAlias );
		if ( priceAsString != null )
			return priceAsString;
		else {
			double price = getPrice( permitAlias );
			// TODO: Config on how many decimals (if any)
			DecimalFormat df = new DecimalFormat( "#.##" );
			priceAsString = df.format( price );
			permitPricesAsStrings.put( permitAlias, priceAsString );
		}
		return priceAsString;
	}
	
	
	public double checkPlayerBalance( String playerName ) {
		net.milkbowl.vault.economy.Economy vaultEconomy = PermitSigns.instance.vaultEconomy;
		if ( vaultEconomy == null )
			return 0;
		else
			return vaultEconomy.getBalance( playerName );
	}
	
	
	public boolean takeMoneyFromPlayer( String playerName, double amount ) {
		net.milkbowl.vault.economy.Economy vaultEconomy = PermitSigns.instance.vaultEconomy;
		if ( vaultEconomy == null )
			return false;
		else {
			EconomyResponse response = vaultEconomy.withdrawPlayer( playerName, amount );
			return response.transactionSuccess();
		}
	}
	
	
	protected void priceRecordChange( PriceRecord record ) {
		// TODO.. add to timing functions, etc etc.
		updatePrice( record );
		Config.savePriceData( record );
	}
	
	
	protected boolean updatePrice( String permitAlias ) {
		return updatePrice( permitAlias, false );
	}
	
	
	protected boolean updatePrice( String permitAlias, boolean forceSave ) {
		PriceRecord data = economicData.get( permitAlias );
		if ( data == null ) {
			data = new PriceRecord( permitAlias, 10000 );
			economicData.put( permitAlias, data );
			Config.savePriceData( data );
		}
		return updatePrice( data, forceSave );
	}
	
	
	protected boolean updatePrice( PriceRecord data ) {
		return updatePrice( data, false );
	}
	
	
	protected boolean updatePrice( PriceRecord data, boolean forceSave ) {
		double result;
		boolean dynamicPriceDefined = ( data.purchaseFactorDefined | data.fixedRatioDecayDefined | data.fixedTimeDecayDefined );
		// if only a base price is defined, then the price is the base price...
		if ( !( data.ratioDefined | dynamicPriceDefined ) ) {
			result = roundToFactor( data.basePrice, data.roundingFactor );
		} else {
			if ( data.ratioDefined & dynamicPriceDefined )
				result = Math.max( data.ratioPrice, data.currentPrice );
			else if ( data.ratioDefined )
				result = data.ratioPrice;
			else
				result = data.currentPrice;
			if ( data.maxPriceDefined )
				result = Math.min( result, data.maxPrice );
			if ( data.minPriceDefined )
				result = Math.max( result, data.minPrice );
			result = roundToFactor( result, data.roundingFactor );
		}
		if ( result != data.currentPrice ) {
			PermitSignsPriceChangeEvent permitSignsEvent = new PermitSignsPriceChangeEvent( data.permitAlias, result,
					data.currentPrice );
			PermitSigns.instance.getServer().getPluginManager().callEvent( permitSignsEvent );
			return ( !permitSignsEvent.isCancelled );
		}
		return true;
	}
	
	
	public void priceChangeEventSuccess( PriceRecord data, double newPrice ) {
		data.currentPrice = newPrice;
		permitPrices.put( data.permitAlias, newPrice );
		// save the record
		Config.savePriceData( data );
		// invalidate cache's
		permitPricesAsStrings.remove( data.permitAlias );
	}
	
	
	private double roundToFactor( double data, double factor ) {
		return Math.round( ( data / factor ) ) * factor;
	}
	
	
	private void updateRatios( PriceRecord data ) {
		LinkedList< PriceRecord > list = dataByRatio.get( data.permitAlias );
		if ( list != null )
			for ( PriceRecord ed : list )
				updateRatioPrice( ed );
	}
	
	
	private void updateRatioPrice( PriceRecord data ) {
		Players players = PermitMe.instance.players;
		double aim;
		double current;
		double countSource = players.getPlayerCountByPermit( data.permitAlias );
		double countOther;
		double winningRatio = 0;
		if ( data.ratios != null )
			if ( data.ratios.size() > 0 ) {
				for ( EconomicRatio ratio : data.ratios.values() ) {
					countOther = players.getPlayerCountByPermit( ratio.otherAlias );
					aim = ratio.sourceCount / ratio.otherCount;
					current = countSource / countOther;
					winningRatio = Math.max( winningRatio, aim / current );
				}
				data.ratioPrice = data.basePrice * winningRatio;
			}
	}
	
}
