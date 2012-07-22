package com.oreilly.permitsigns;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import com.oreilly.permitme.events.PermitMeEnableCompleteEvent;
import com.oreilly.permitme.events.PermitMePlayerAddPermitEvent;
import com.oreilly.permitme.events.PermitMePlayerRemovePermitEvent;
import com.oreilly.permitsigns.events.PermitSignsPriceChangeEvent;


public class Events implements Listener {
	
	// TODO: Consider making this a config option, or use "instanceof sign"
	public static final Integer[] SIGN_ID_LIST = { 63, 68 };
	
	
	public Events() {
		super();
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak( BlockBreakEvent event ) {
		if ( event.isCancelled() )
			return;
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if ( block instanceof Sign ) {
			boolean allowed = PermitSigns.instance.signs.checkSignBreak( player, block );
			if ( !allowed ) {
				event.setCancelled( true );
				player.sendMessage( "You don't have the permissions to break this sign" );
			}
		}
	}
	
	
	// monitor, as we don't ever alter the event, we just act based on the
	// outcome.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract( PlayerInteractEvent event ) {
		// if the event is cancelled, don't do anything
		if ( event.isCancelled() )
			return;
		// get basic data
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		// Location blocklocation = block.getLocation();
		// see if block is a sign
		if ( block.getState() instanceof Sign ) {
			// a left click on a sign will refresh it
			if ( action == Action.LEFT_CLICK_BLOCK )
				PermitSigns.instance.signs.leftClicked( player, block.getState() );
			else if ( action == Action.RIGHT_CLICK_BLOCK ) {
				PermitSigns.instance.signs.rightClicked( player, block.getState() );
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChangeEvent( SignChangeEvent event ) {
		// Called when text on a sign is changed (or created)
		if ( event.isCancelled() )
			return;
		Player player = event.getPlayer();
		boolean allowed = PermitSigns.instance.signs.checkSignText( player, event.getLines() );
		if ( !allowed ) {
			event.setCancelled( true );
			player.sendMessage( "You don't have the permission to make a PermitMe sign" );
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPermitMePlayerAddPermit( PermitMePlayerAddPermitEvent event ) {
		if ( event.allow ) {
			PermitSigns.instance.prices.playerGainedPermit( event.permitPlayer, event.permitAlias );
			PermitSigns.instance.tracker.playerGainedPermit( event.permitPlayer, event.permitAlias );
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPermitMePlayerRemovePermit( PermitMePlayerRemovePermitEvent event ) {
		if ( event.allow ) {
			PermitSigns.instance.prices.playerLostPermit( event.permitPlayer, event.permitAlias );
			PermitSigns.instance.tracker.playerLostPermit( event.permitPlayer, event.permitAlias );
		}
	}
	
	
	@EventHandler
	public void onPermitMeEnable( PermitMeEnableCompleteEvent event ) {
		PermitSigns.instance.permitMeReady();
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin( PlayerJoinEvent event ) {
		PermitSigns.instance.tracker.playerJoined( event.getPlayer() );
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKicked( PlayerKickEvent event ) {
		PermitSigns.instance.tracker.playerLeft( event.getPlayer() );
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent( PlayerQuitEvent event ) {
		PermitSigns.instance.tracker.playerLeft( event.getPlayer() );
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad( ChunkLoadEvent event ) {
		PermitSigns.instance.signs.refresh( event.getChunk() );
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPermitSignsPriceChange( PermitSignsPriceChangeEvent event ) {
		if ( !event.isCancelled ) {
			PermitSigns.instance.prices.priceChangeEventSuccess( event.economicData, event.newPrice );
			PermitSigns.instance.signs.refresh( event.permitAlias );
		}
	}
}
