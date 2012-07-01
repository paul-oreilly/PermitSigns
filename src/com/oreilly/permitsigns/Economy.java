package com.oreilly.permitsigns;

import java.text.DecimalFormat;
import java.util.TreeMap;

import net.milkbowl.vault.economy.EconomyResponse;

import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.Players;
import com.oreilly.permitme.record.PermitPlayer;
import com.oreilly.permitsigns.data.EconomicRatio;
import com.oreilly.permitsigns.records.EconomicData;


public class Economy {
	
	private final TreeMap< String, Double > permitPrices = new TreeMap< String, Double >();
	private final TreeMap< String, String > permitPricesAsStrings = new TreeMap< String, String >();
	private final TreeMap< String, EconomicData > economicData = new TreeMap< String, EconomicData >();
	
	
	public Economy() {
		
	}
	
	
	public void addEconomicData( EconomicData data ) {
		// TODO: Error checking
		economicData.put( data.permitAlias, data );
		updateRatioPrice( data );
	}
	
	
	public void playerGainedPermit( PermitPlayer player, String permitAlias ) {
		calculatePrice( permitAlias );
	}
	
	
	public void playerLostPermit( PermitPlayer player, String permitAlias ) {
		calculatePrice( permitAlias );
	}
	
	
	public double getPrice( String permitAlias ) {
		Double price = permitPrices.get( permitAlias );
		if ( price == null )
			return calculatePrice( permitAlias );
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
	
	
	private double calculatePrice( String permitAlias ) {
		EconomicData data = economicData.get( permitAlias );
		if ( data == null ) {
			data = new EconomicData( permitAlias, 10000 );
			economicData.put( permitAlias, data );
		}
		double result;
		boolean dynamicPriceDefined = ( data.purchaseFactorDefined | data.fixedRatioDecayDefined | data.fixedTimeDecayDefined );
		// if only a base price is defined, then the price is the base price...
		if ( !( data.ratioDefined | dynamicPriceDefined ) ) {
			result = roundToFactor( data.basePrice, data.roundingFactor );
			permitPrices.put( permitAlias, result );
			return result;
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
			permitPrices.put( permitAlias, result );
			return result;
		}
	}
	
	
	private double roundToFactor( double data, double factor ) {
		return Math.round( ( data / factor ) ) * factor;
	}
	
	
	private void updateRatioPrice( EconomicData data ) {
		// TODO:
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
