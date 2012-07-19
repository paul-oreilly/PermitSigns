package com.oreilly.permitsigns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
import com.oreilly.permitme.record.PermitPlayer;
import com.oreilly.permitsigns.data.SignHeader;
import com.oreilly.permitsigns.data.SignType;
import com.oreilly.permitsigns.interactions.EditSignChoices;
import com.oreilly.permitsigns.records.Sign;


@SuppressWarnings("serial")
class SignList extends LinkedList< Sign > {
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
	WorldLocation< Sign > signsByLocation = new WorldLocation< Sign >();
	HashMap< String, LinkedList< Sign >> signsByWorld = new HashMap< String, LinkedList< Sign >>();
	
	public HashMap< String, SignHeader > signHeaders = new HashMap< String, SignHeader >();
	
	// a record of which players have sign editing currently enabled
	public HashSet< String > signEditingPlayers = new HashSet< String >();
	
	// the factory that creates the text interface for editing sign data
	protected InteractionFactory editSignInteraction = null;
	
	
	//private final ConversationFactory adminSignCreation = null;
	
	public Signs() {
	}
	
	
	public void setupInteractions() {
		editSignInteraction = new InteractionFactory()
				.withExitSequence( "quit", "exit" )
				.withReturnSequence( "return" )
				.withTimeout( 20 )
				.thatExcludesNonPlayersWithMessage( "Only usable from within minecraft, by players" )
				.withPages( new EditSignChoices() )
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
	
	
	public void registerSign( Sign sign ) {
		// add to signsByLocation
		String worldName = sign.location.getWorld().getName();
		ThreeIntHash< Sign > threedee = signsByLocation.get( worldName );
		if ( threedee == null ) {
			threedee = new ThreeIntHash< Sign >();
			signsByLocation.put( worldName, threedee );
		}
		TwoIntHash< Sign > twodee = threedee.get( sign.location.getBlockX() );
		if ( twodee == null ) {
			twodee = new TwoIntHash< Sign >();
			threedee.put( sign.location.getBlockX(), twodee );
		}
		IntHash< Sign > onedee = twodee.get( sign.location.getBlockY() );
		if ( onedee == null ) {
			onedee = new IntHash< Sign >();
			twodee.put( sign.location.getBlockY(), onedee );
		}
		Sign existingSign = onedee.get( sign.location.getBlockZ() );
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
		LinkedList< Sign > worldList = signsByWorld.get( worldName );
		if ( worldList == null ) {
			worldList = new LinkedList< Sign >();
			signsByWorld.put( worldName, worldList );
		}
		worldList.add( sign );
	}
	
	
	public List< Sign > getSignsInWorld( String worldName ) {
		return signsByWorld.get( worldName );
	}
	
	
	public boolean leftClicked( Player player, BlockState block ) {
		refresh( block.getLocation() );
		return true;
	}
	
	
	// TODO: Check boolean return is used for cancelling the event
	public boolean rightClicked( Player player, BlockState block ) {
		Sign permitSign = getSignAtLocation( block.getLocation() );
		
		// editing players, if sneaking (having right clicked a sign), start the 
		// sign editing text interface.
		// (they will previously have switched into an 'edit' mode via command)
		if ( signEditingPlayers.contains( player.getName() ) ) {
			if ( player.isSneaking() ) {
				if ( permitSign == null ) {
					permitSign = new Sign( block.getLocation() );
					registerSign( permitSign );
				}
				Interaction interaction = editSignInteraction.buildInteraction( player );
				interaction.context.put( "sign", permitSign );
				interaction.context.put( "block", block );
				interaction.begin();
			}
			return true;
		}
		return false;
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
	
	
	public void playerPurchase( Player player, Block block ) {
		if ( !( block instanceof Sign ) )
			return;
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block;
		String[] lines = sign.getLines();
		String header = lines[0].toLowerCase().trim();
		if ( signHeaders.containsKey( header ) ) {
			SignHeader signHeader = signHeaders.get( header );
			if ( signHeader.type == SignType.SALE ) {
				if ( PermitMe.instance.players.hasPermission( player, "exempt" ) )
					return;
				else {
					Location location = sign.getLocation();
					Sign permitSign = getSignAtLocation( location );
					if ( permitSign != null ) {
						PermitPlayer permitPlayer = PermitMe.instance.players.getPlayer( player.getName() );
						if ( permitPlayer.permits.contains( permitSign.permitAlias ) )
							return;
						else {
							double balance = PermitSigns.instance.economy.checkPlayerBalance( player.getName() );
							double price = PermitSigns.instance.economy.getPrice( permitSign.permitAlias );
							if ( balance < price ) {
								player.sendMessage( "You don't have enough money to purchase this" );
								return;
							} else {
								if ( PermitMe.instance.addPermitToPlayer( player.getName(), permitSign.permitAlias ) ) {
									if ( !PermitSigns.instance.economy.takeMoneyFromPlayer( player.getName(), price ) )
										PermitMe.instance.removePermitFromPlayer( player.getName(),
												permitSign.permitAlias );
								}
							}
						}
					} else
						PermitMe.log.warning( "[PermitSigns] The sign in " + location.getWorld().getName() + " at x"
								+ location.getBlockX() + " y" + location.getBlockY() + " z" + location.getBlockZ()
								+ " has a PermitSign header, but no associated data" );
				}
			}
		}
	}
	
	
	public void updateMonitorSignsFor( String permitAlias ) {
		SignList list = signsByPermitAlias.get( permitAlias );
		if ( list == null )
			return;
		for ( Sign sign : list )
			if ( sign.signType == SignType.MONITOR )
				refresh( sign );
	}
	
	
	public void updateMonitorSignsFor( LinkedList< String > permitAliasList ) {
		for ( String alias : permitAliasList ) {
			SignList list = signsByPermitAlias.get( alias );
			if ( list == null )
				continue;
			else
				for ( Sign sign : list )
					if ( sign.signType == SignType.MONITOR )
						refresh( sign );
		}
	}
	
	
	public void refreshAllSigns() {
		PermitMe.log.info( "[PermitSigns] DEBUG: Refreshing all sign data" );
		for ( LinkedList< Sign > list : signsByWorld.values() )
			for ( Sign sign : list )
				refresh( sign );
	}
	
	
	public void refresh( Location location ) {
		Sign sign = getSignAtLocation( location );
		if ( sign == null )
			return;
		refresh( sign );
	}
	
	
	public void refresh( Block block ) {
		Sign sign = getSignAtLocation( block.getLocation() );
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
		for ( Sign sign : signs )
			refresh( sign );
	}
	
	
	public void refresh( String permitAlias ) {
		SignList list = signsByPermitAlias.get( permitAlias );
		if ( list != null )
			for ( Sign sign : list )
				refresh( sign );
	}
	
	
	public void refresh( Sign sign ) {
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
					signBlock.setLine( 3, PermitSigns.instance.economy.getPriceString( sign.permitAlias ) );
					break;
				case SALE:
					signBlock.setLine( 3, PermitSigns.instance.economy.getPriceString( sign.permitAlias ) );
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
	
	
	private Sign getSignAtLocation( Location location ) {
		String worldName = location.getWorld().getName();
		ThreeIntHash< Sign > threedee = signsByLocation.get( worldName );
		if ( threedee == null )
			return null;
		TwoIntHash< Sign > twodee = threedee.get( location.getBlockX() );
		if ( twodee == null )
			return null;
		IntHash< Sign > onedee = twodee.get( location.getBlockY() );
		if ( onedee == null )
			return null;
		return onedee.get( location.getBlockZ() );
	}
}