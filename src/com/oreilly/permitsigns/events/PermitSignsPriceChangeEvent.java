package com.oreilly.permitsigns.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.records.EconomicData;


public class PermitSignsPriceChangeEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	public String permitAlias = null;
	public EconomicData economicData = null;
	public double newPrice;
	public double oldPrice;
	public boolean isCancelled = false;
	
	
	public PermitSignsPriceChangeEvent( String permitAlias, double newPrice, double oldPrice ) {
		this.permitAlias = permitAlias;
		this.newPrice = newPrice;
		this.oldPrice = oldPrice;
		if ( economicData == null )
			economicData = PermitSigns.instance.economy.getEconomicData( permitAlias );
	}
	
	
	public PermitSignsPriceChangeEvent( String permitAlias, double newPrice, double oldPrice, EconomicData economicData ) {
		this.permitAlias = permitAlias;
		this.newPrice = newPrice;
		this.oldPrice = oldPrice;
		this.economicData = economicData;
	}
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
