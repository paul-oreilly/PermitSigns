package com.oreilly.permitsigns;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.oreilly.permitme.PermitMe;


/**
 * 
 * 
 * @author paul
 * 
 */

public class PermitSigns extends JavaPlugin {
	
	public static PermitSigns instance = null;
	
	public Signs signs = null;
	public com.oreilly.permitsigns.Economy economy = null;
	public PlayerTracker tracker = null;
	
	public net.milkbowl.vault.economy.Economy vaultEconomy = null;
	
	
	public PermitSigns() {
		super();
		CommandList.loadCommands();
		instance = this;
	}
	
	
	@Override
	public void onEnable() {
		
		signs = new Signs();
		economy = new com.oreilly.permitsigns.Economy();
		tracker = new PlayerTracker();
		
		Config.load();
		
		// register event listeners
		getServer().getPluginManager().registerEvents( new Events(), this );
		
		// if permitMe is already loaded, call methods that would otherwise be
		// waiting for it
		if ( PermitMe.instance != null )
			tracker.permitMeReady();
		
		if ( setupEconomy() == false )
			PermitMe.log.warning( "[PermitSigns] Vault economy setup has failed." );
	}
	
	
	private boolean setupEconomy() {
		if ( getServer().getPluginManager().getPlugin( "Vault" ) == null ) {
			return false;
		}
		RegisteredServiceProvider< Economy > rsp = getServer().getServicesManager().getRegistration( Economy.class );
		if ( rsp == null ) {
			return false;
		}
		vaultEconomy = rsp.getProvider();
		return vaultEconomy != null;
	}
}
