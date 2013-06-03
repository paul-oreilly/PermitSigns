package com.oreilly.permitsigns;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionFactory;
import com.oreilly.common.interaction.text.StyleConstants;
import com.oreilly.common.interaction.text.formatter.Border;
import com.oreilly.common.interaction.text.formatter.ClearChat;
import com.oreilly.common.interaction.text.formatter.Highlight;
import com.oreilly.permitme.PermitMe;
import com.oreilly.permitsigns.interactions.AdminChoices;


/**
 * 
 * 
 * @author paul
 * 
 */

public class PermitSigns extends JavaPlugin {
	
	public static PermitSigns instance = null;
	
	public Config config = null;
	public Signs signs = null;
	public Prices prices = null;
	public PlayerTracker tracker = null;
	
	protected InteractionFactory adminInteraction = null;
	
	public Economy vaultEconomy = null;
	
	
	public PermitSigns() {
		super();
		Commands.loadCommands();
		instance = this;
		
	}
	
	
	@Override
	public void onEnable() {
		
		PermitMe.log.info( "[PermitSigns] Enabling..." );
		
		config = new Config();
		signs = new Signs();
		prices = new com.oreilly.permitsigns.Prices( this );
		tracker = new PlayerTracker();
		
		Config.load();
		
		// register event listeners
		getServer().getPluginManager().registerEvents( new Events(), this );
		Interaction.registerEventListener( this );
		
		if ( setupEconomy() == false )
			PermitMe.log.warning( "[PermitSigns] Vault economy setup has failed." );
		
		// setup interactions
		signs.setupInteractions();
		setupInteractions();
		
		// if permitMe is already loaded, call methods that would otherwise be
		// waiting for it
		// TODO: Put these into a function, and check there is an event that will cal them
		if ( PermitMe.instance != null )
			permitMeReady();
		
	}
	
	
	@Override
	public void onDisable() {
		
		PermitMe.log.info( "[PermitSigns] Disabling..." );
		
		//save sign data
		signs.saveAll();
		prices.saveAll();
	}
	
	
	public void permitMeReady() {
		tracker.permitMeReady();
		signs.refreshAllSigns();
	}
	
	
	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		return Commands.runCommand( sender, cmd, commandLabel, args );
	}
	
	
	protected void setupInteractions() {
		adminInteraction = new InteractionFactory()
				.withExitSequence( "quit", "exit" )
				.withReturnSequence( "return" )
				.withTimeout( 20 )
				.withPages( new AdminChoices() )
				.withFormatter( new Border() )
				.withFormatter( new ClearChat() )
				.withFormatter( new Highlight() )
				.thatExcludesNonPlayersWithMessage( "Only usable from within minecraft, by players" )
				.withStyle( Border.COLOR_TITLE_TEXT, ChatColor.GOLD )
				.withStyle( Border.COLOR_TITLE_BORDER, ChatColor.DARK_RED )
				.withStyle( Border.COLOR_TEXT_BORDER, ChatColor.BLUE );
		Highlight.addHighlightStyle( adminInteraction, StyleConstants.VALID_INPUT, ChatColor.GREEN );
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
