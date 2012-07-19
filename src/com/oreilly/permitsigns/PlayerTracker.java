package com.oreilly.permitsigns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.bukkit.entity.Player;

import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.PermitPlayer;


public class PlayerTracker {
	
	public HashSet< String > onlinePlayers = new HashSet< String >();
	public HashMap< String, Integer > permitCountByOnlinePlayers = new HashMap< String, Integer >();
	
	
	public PlayerTracker() {
		Player[] players = PermitSigns.instance.getServer().getOnlinePlayers();
		for ( Player player : players )
			onlinePlayers.add( player.getName() );
	}
	
	
	public void permitMeReady() {
		// Called when PermitMe has loaded, so we can get a count of online
		// players
		PermitMe.log.info( "[PermitSigns] Linking with PermitMe" );
		HashMap< String, HashSet< PermitPlayer >> data = PermitMe.instance.players.getAllPlayerCountsByPermit();
		for ( String permitName : data.keySet() ) {
			HashSet< PermitPlayer > players = data.get( permitName );
			for ( PermitPlayer player : players ) {
				if ( onlinePlayers.contains( player.name ) ) {
					Integer count = permitCountByOnlinePlayers.get( permitName );
					if ( count == null )
						permitCountByOnlinePlayers.put( permitName, 1 );
					else
						permitCountByOnlinePlayers.put( permitName, count + 1 );
				}
			}
		}
	}
	
	
	public int getOnlinePlayerCountByPermit( String permitAlias ) {
		Integer count = permitCountByOnlinePlayers.get( permitAlias );
		return ( count == null ) ? 0 : count;
	}
	
	
	public void playerGainedPermit( PermitPlayer player, String permitAlias ) {
		Integer count = permitCountByOnlinePlayers.get( permitAlias );
		if ( count == null )
			permitCountByOnlinePlayers.put( permitAlias, 1 );
		else
			permitCountByOnlinePlayers.put( permitAlias, count + 1 );
		PermitSigns.instance.signs.updateMonitorSignsFor( permitAlias );
	}
	
	
	public void playerLostPermit( PermitPlayer player, String permitAlias ) {
		Integer count = permitCountByOnlinePlayers.get( permitAlias );
		if ( count != null )
			if ( count > 0 )
				permitCountByOnlinePlayers.put( permitAlias, count - 1 );
		PermitSigns.instance.signs.updateMonitorSignsFor( permitAlias );
	}
	
	
	public void playerLeft( Player player ) {
		LinkedList< String > monitorsToUpdate = new LinkedList< String >();
		PermitPlayer permitPlayer = PermitMe.instance.players.getPlayer( player.getName() );
		for ( String permitAlias : permitPlayer.permits ) {
			monitorsToUpdate.add( permitAlias );
			Integer count = permitCountByOnlinePlayers.get( permitAlias );
			if ( count == null )
				continue;
			else
				permitCountByOnlinePlayers.put( permitAlias, count + 1 );
		}
		onlinePlayers.remove( player.getName() );
		PermitSigns.instance.signs.updateMonitorSignsFor( monitorsToUpdate );
	}
	
	
	public void playerJoined( Player player ) {
		LinkedList< String > monitorsToUpdate = new LinkedList< String >();
		PermitPlayer permitPlayer = PermitMe.instance.players.getPlayer( player.getName() );
		for ( String permitAlias : permitPlayer.permits ) {
			monitorsToUpdate.add( permitAlias );
			Integer count = permitCountByOnlinePlayers.get( permitAlias );
			if ( count == null )
				permitCountByOnlinePlayers.put( permitAlias, 1 );
			else
				permitCountByOnlinePlayers.put( permitAlias, count + 1 );
		}
		onlinePlayers.remove( player.getName() );
		PermitSigns.instance.signs.updateMonitorSignsFor( monitorsToUpdate );
	}
}
