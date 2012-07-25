package com.oreilly.permitsigns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionFactory;
import com.oreilly.common.interaction.text.formatter.Border;
import com.oreilly.common.interaction.text.formatter.ClearChat;
import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.Permit;
import com.oreilly.permitme.record.PermitPlayer;
import com.oreilly.permitsigns.data.SignHeader;
import com.oreilly.permitsigns.data.SignType;
import com.oreilly.permitsigns.interactions.EditSign;


@SuppressWarnings("serial")
class SignList extends LinkedList< SignRecord > {
}


@SuppressWarnings("serial")
class IntHash< T > extends HashMap< Integer, T > {
}


@SuppressWarnings("serial")
class TwoIntHash< T > extends IntHash< IntHash< T >> {
}


@SuppressWarnings("serial")
class ThreeIntHash< T > extends IntHash< TwoIntHash< T >> {
}


@SuppressWarnings("serial")
class WorldLocation< T > extends HashMap< String, ThreeIntHash< T >> {
	
}


@SuppressWarnings("serial")
class WorldChunk< T > extends HashMap< String, TwoIntHash< T >> {
	
}


public class Signs {
	
	WorldChunk< SignList > signsByChunk = new WorldChunk< SignList >();
	HashMap< String, SignList > signsByPermitAlias = new HashMap< String, SignList >();
	WorldLocation< SignRecord > signsByLocation = new WorldLocation< SignRecord >();
	HashMap< String, LinkedList< SignRecord >> signsByWorld = new HashMap< String, LinkedList< SignRecord >>();
	
	public HashMap< String, SignHeader > signHeaders = new HashMap< String, SignHeader >();
	
	// a record of which players have sign editing currently enabled
	public HashSet< String > signEditingPlayers = new HashSet< String >();
	
	// the factory that creates the text interface for editing sign data
	protected InteractionFactory editSignInteraction = null;
	
	
	//private final ConversationFactory adminSignCreation = null;
	
	// TODO: Timed function to save sign data.
	
	public Signs() {
	}
	
	
	public void saveAll() {
		for ( String worldName : signsByWorld.keySet() )
			Config.saveSigns( worldName );
	}
	
	
	public void setupInteractions() {
		editSignInteraction = new InteractionFactory()
				.withExitSequence( "quit", "exit" )
				.withReturnSequence( "return" )
				.withTimeout( 20 )
				.thatExcludesNonPlayersWithMessage( "Only usable from within minecraft, by players" )
				.withPages( new EditSign() )
				.withFormatter( new Border() )
				.withFormatter( new ClearChat() );
	}
	
	
	public void activateSignEditingFor( CommandSender sender ) {
		signEditingPlayers.add( sender.getName() );
	}
	
	
	public void deactivateSignEditingFor( CommandSender sender ) {
		signEditingPlayers.remove( sender.getName() );
	}
	
	
	public void addHeader( String header, SignType type ) {
		String asKey = header.toLowerCase().trim();
		if ( signHeaders.containsKey( asKey ) )
			return;
		SignHeader newHeader = new SignHeader( header, type );
		signHeaders.put( asKey, newHeader );
	}
	
	
	public SignHeader getHeader( String header ) {
		String universal = header.toLowerCase().trim();
		// try for exact match
		SignHeader result = signHeaders.get( universal );
		if ( result != null )
			return result;
		else {
			// return first header that contains the string
			for ( String key : signHeaders.keySet() ) {
				if ( key.contains( universal ) )
					return signHeaders.get( key );
			}
		}
		return null;
	}
	
	
	public void addSign( SignRecord sign ) {
		// add to signsByLocation
		String worldName = sign.location.getWorld().getName();
		ThreeIntHash< SignRecord > threedee = signsByLocation.get( worldName );
		if ( threedee == null ) {
			threedee = new ThreeIntHash< SignRecord >();
			signsByLocation.put( worldName, threedee );
		}
		TwoIntHash< SignRecord > twodee = threedee.get( sign.location.getBlockX() );
		if ( twodee == null ) {
			twodee = new TwoIntHash< SignRecord >();
			threedee.put( sign.location.getBlockX(), twodee );
		}
		IntHash< SignRecord > onedee = twodee.get( sign.location.getBlockY() );
		if ( onedee == null ) {
			onedee = new IntHash< SignRecord >();
			twodee.put( sign.location.getBlockY(), onedee );
		}
		SignRecord existingSign = onedee.get( sign.location.getBlockZ() );
		if ( existingSign == null )
			onedee.put( sign.location.getBlockZ(), sign );
		else {
			// TODO: Error
			if ( existingSign == sign )
				return;
		}
		// add to signsByChunk
		Chunk chunk = sign.location.getChunk();
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();
		TwoIntHash< SignList > worldRecord = signsByChunk.get( worldName );
		if ( worldRecord == null ) {
			worldRecord = new TwoIntHash< SignList >();
			signsByChunk.put( worldName, worldRecord );
		}
		IntHash< SignList > xRecord = worldRecord.get( chunkX );
		if ( xRecord == null ) {
			xRecord = new IntHash< SignList >();
			worldRecord.put( chunkX, xRecord );
		}
		SignList zRecord = xRecord.get( chunkZ );
		if ( zRecord == null ) {
			zRecord = new SignList();
			xRecord.put( chunkZ, zRecord );
		}
		zRecord.add( sign );
		// add to signsByAlias
		SignList signs = signsByPermitAlias.get( sign.permitAlias );
		if ( signs == null ) {
			signs = new SignList();
			signsByPermitAlias.put( sign.permitAlias, signs );
		}
		signs.add( sign );
		// add to signs by world
		LinkedList< SignRecord > worldList = signsByWorld.get( worldName );
		if ( worldList == null ) {
			worldList = new LinkedList< SignRecord >();
			signsByWorld.put( worldName, worldList );
		}
		worldList.add( sign );
	}
	
	
	public void removeSign( SignRecord sign ) {
		// TODO:
	}
	
	
	public List< SignRecord > getSignsInWorld( String worldName ) {
		return signsByWorld.get( worldName );
	}
	
	
	public boolean leftClicked( Player player, BlockState block ) {
		refresh( block.getLocation() );
		return true;
	}
	
	
	// TODO: Check boolean return is used for cancelling the event
	public boolean rightClicked( Player player, BlockState block ) {
		SignRecord permitSign = getSignAtLocation( block.getLocation() );
		
		// editing players, if sneaking (having right clicked a sign), start the 
		// sign editing text interface.
		// (they will previously have switched into an 'edit' mode via command)
		if ( signEditingPlayers.contains( player.getName() ) ) {
			if ( player.isSneaking() ) {
				if ( permitSign == null ) {
					permitSign = new SignRecord( block.getLocation() );
					addSign( permitSign );
				}
				Interaction interaction = editSignInteraction.buildInteraction( player );
				interaction.context.put( "sign", permitSign );
				interaction.context.put( "block", block );
				interaction.begin();
			} else {
				playerPurchase( player, block );
			}
			return true;
		}
		// otherwise, assume the player wants to purchase the permit...
		playerPurchase( player, block );
		return true;
	}
	
	
	public boolean checkSignBreak( Player player, Block block ) {
		return PermitMe.instance.players.hasPermission( player, "signs.edit" );
	}
	
	
	public boolean checkSignText( Player player, String[] lines ) {
		String header = lines[0].toLowerCase().trim();
		if ( signHeaders.containsKey( header ) )
			return PermitMe.instance.players.hasPermission( player, "sign.edit" );
		else
			return true;
	}
	
	
	public void playerPurchase( Player player, BlockState block ) {
		SignRecord signRecord = getSignAtLocation( block.getLocation() );
		if ( signRecord == null )
			return;
		PermitPlayer permitPlayer = PermitMe.instance.players.getPlayer( player.getName() );
		if ( permitPlayer.permits.contains( signRecord.permitAlias ) )
			return;
		// DEBUG:
		PermitMe.log.info( "[PermitSigns] DEBUG: Player " + player.getDisplayName() + " right clicked sign:\n" +
				signRecord.toHumanString() );
		switch ( signRecord.signType ) {
			case SALE: {
				// if they already have it, let them know, and return...
				if ( permitPlayer.permits.contains( signRecord.permitAlias ) ) {
					player.sendMessage( "You have already obtained this" );
					return;
				}
				// otherwise, check the money.
				double balance = PermitSigns.instance.prices.checkPlayerBalance( player.getName() );
				double price = PermitSigns.instance.prices.getPrice( signRecord.permitAlias );
				if ( balance < price ) {
					player.sendMessage( "You don't have enough money to purchase this" );
					return;
				}
				// attempt the purchase
				if ( PermitMe.instance.addPermitToPlayer( player.getName(), signRecord.permitAlias ) ) {
					// adding the permit was a success...
					if ( !PermitSigns.instance.prices.takeMoneyFromPlayer( player.getName(), price ) ) {
						// but if taking the money wasn't (for some reason), then take the permit away again.
						PermitMe.instance.removePermitFromPlayer( player.getName(),
								signRecord.permitAlias );
						player.sendMessage( "Your money is unavaiable" );
					}
					else {
						Permit permit = PermitMe.instance.permits.permitsByAlias.get( signRecord.permitAlias );
						player.sendMessage( "You have obtained " + WordUtils.capitalize( permit.name ) + "!" );
					}
				}
			}
		}
	}
	
	
	public void updateMonitorSignsFor( String permitAlias ) {
		SignList list = signsByPermitAlias.get( permitAlias );
		if ( list == null )
			return;
		for ( SignRecord sign : list )
			if ( sign.signType == SignType.MONITOR )
				refresh( sign );
	}
	
	
	public void updateMonitorSignsFor( LinkedList< String > permitAliasList ) {
		for ( String alias : permitAliasList ) {
			SignList list = signsByPermitAlias.get( alias );
			if ( list == null )
				continue;
			else
				for ( SignRecord sign : list )
					if ( sign.signType == SignType.MONITOR )
						refresh( sign );
		}
	}
	
	
	public void refreshAllSigns() {
		for ( LinkedList< SignRecord > list : signsByWorld.values() )
			for ( SignRecord sign : list )
				refresh( sign );
	}
	
	
	public void refresh( Location location ) {
		SignRecord sign = getSignAtLocation( location );
		if ( sign == null )
			return;
		refresh( sign );
	}
	
	
	public void refresh( Block block ) {
		SignRecord sign = getSignAtLocation( block.getLocation() );
		if ( sign == null )
			return;
		refresh( sign );
	}
	
	
	public void refresh( Chunk chunk ) {
		// TODO: This method does not appear to work. Test, find out why.
		
		TwoIntHash< SignList > twodee = signsByChunk.get( chunk.getWorld() );
		if ( twodee == null )
			return;
		IntHash< SignList > onedee = twodee.get( chunk.getX() );
		if ( onedee == null )
			return;
		SignList signs = onedee.get( chunk.getZ() );
		if ( signs == null )
			return;
		for ( SignRecord sign : signs )
			refresh( sign );
	}
	
	
	public void refresh( String permitAlias ) {
		SignList list = signsByPermitAlias.get( permitAlias );
		if ( list != null )
			for ( SignRecord sign : list )
				refresh( sign );
	}
	
	
	public void refresh( SignRecord sign ) {
		if ( sign == null ) {
			PermitMe.log.warning( "[PermitSigns] Cannot refresh sign, as sign value is NULL" );
			return;
		}
		if ( sign.location.getChunk().isLoaded() != true ) {
			PermitMe.log.info( "[PermitSigns] DEBUG: Sign not refreshed, as chunk is not loaded:\n" +
					sign.toHumanString() );
			return;
		}
		Block block = sign.location.getBlock();
		BlockState blockState = block.getState();
		if ( blockState instanceof org.bukkit.block.Sign ) {
			org.bukkit.block.Sign signBlock = (org.bukkit.block.Sign)blockState;
			signBlock.setLine( 0, sign.signHeader.asSignText() );
			String[] permitDisplay = sign.getPermitDisplay();
			signBlock.setLine( 1, permitDisplay[0] );
			signBlock.setLine( 2, permitDisplay[1] );
			switch ( sign.signType ) {
				case SALEPREVIEW:
					signBlock.setLine( 3, PermitSigns.instance.prices.getPriceString( sign.permitAlias ) );
					break;
				case SALE:
					signBlock.setLine( 3, PermitSigns.instance.prices.getPriceString( sign.permitAlias ) );
					break;
				case MONITOR:
					int online = PermitSigns.instance.tracker.getOnlinePlayerCountByPermit( sign.permitAlias );
					int total = PermitMe.instance.players.getPlayerCountByPermit( sign.permitAlias );
					signBlock.setLine( 3, online + "/" + total );
					break;
			}
			signBlock.update( true );
		} else {
			PermitMe.log.warning( "[PermitSigns] Sign removed, as location is no longer a sign block:\n" +
					sign.toHumanString() );
			// TODO: Remove sign.
		}
	}
	
	
	private SignRecord getSignAtLocation( Location location ) {
		String worldName = location.getWorld().getName();
		ThreeIntHash< SignRecord > threedee = signsByLocation.get( worldName );
		if ( threedee == null )
			return null;
		TwoIntHash< SignRecord > twodee = threedee.get( location.getBlockX() );
		if ( twodee == null )
			return null;
		IntHash< SignRecord > onedee = twodee.get( location.getBlockY() );
		if ( onedee == null )
			return null;
		return onedee.get( location.getBlockZ() );
	}
	
	
	// internal functions to be called by SignRecord
	
	void _internalSignRecordUpdatedAlias( SignRecord sign, String oldAlias, String newAlias ) {
		SignList list = signsByPermitAlias.get( oldAlias );
		if ( list != null )
			list.remove( sign );
		list = signsByPermitAlias.get( newAlias );
		if ( list == null ) {
			list = new SignList();
			signsByPermitAlias.put( newAlias, list );
		}
		list.add( sign );
	}
	
	
	void _internalSignRecordUpdatedLocation( SignRecord sign, Location oldLocation, Location newLocation ) {
		// TODO:
	}
	
	
	void _internalSignRecordUpdatedSignType( SignRecord sign, SignType oldType, SignType newType ) {
		// TODO:
	}
}