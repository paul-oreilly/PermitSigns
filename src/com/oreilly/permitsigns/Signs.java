package com.oreilly.permitsigns;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.oreilly.permitme.PermitMe;
import com.oreilly.permitme.record.PermitPlayer;
import com.oreilly.permitsigns.data.SignType;
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
	
	public HashMap< String, SignType > signHeaders = new HashMap< String, SignType >();
	
	
	public Signs() {
		
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
	}
	
	
	public boolean checkSignBreak( Player player, Block block ) {
		return PermitMe.instance.players.hasPermission( player, "signs.break" );
	}
	
	
	public boolean checkSignText( Player player, String[] lines ) {
		String header = lines[0].toLowerCase().trim();
		if ( signHeaders.containsKey( header ) )
			return PermitMe.instance.players.hasPermission( player, "sign.create" );
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
			SignType type = signHeaders.get( header );
			if ( type == SignType.SALE ) {
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
								if ( PermitMe.instance.addPermitToPlayer( player.getName(), permitSign.permitAlias ) )
									if ( !PermitSigns.instance.economy.takeMoneyFromPlayer( player.getName(), price ) )
										PermitMe.instance.removePermitFromPlayer( player.getName(),
												permitSign.permitAlias );
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
	
	
	public void updateMonitorSignsFor( LinkedList< String > permitAliasList ) {
		for ( String alias : permitAliasList ) {
			SignList list = signsByPermitAlias.get( alias );
			if ( list == null )
				continue;
			else
				for ( Sign sign : list )
					if ( sign.signType == SignType.MONITOR )
						updateSignText( sign );
		}
	}
	
	
	public void refresh( Block block ) {
		Sign sign = getSignAtLocation( block.getLocation() );
		if ( sign == null )
			return;
		updateSignText( sign );
	}
	
	
	public void refresh( Chunk chunk ) {
		TwoIntHash< SignList > twodee = signsByChunk.get( chunk.getWorld() );
		IntHash< SignList > onedee = twodee.get( chunk.getX() );
		if ( onedee == null )
			return;
		SignList signs = onedee.get( chunk.getZ() );
		if ( signs == null )
			return;
		for ( Sign sign : signs )
			updateSignText( sign );
	}
	
	
	private void updateSignText( Sign sign ) {
		if ( sign.location.getChunk().isLoaded() != true )
			return;
		Block block = sign.location.getBlock();
		if ( block instanceof org.bukkit.block.Sign ) {
			org.bukkit.block.Sign SignBlock = (org.bukkit.block.Sign)block;
			switch ( sign.signType ) {
				case SALEPREVIEW:
					SignBlock.setLine( 3, PermitSigns.instance.economy.getPriceString( sign.permitAlias ) );
					return;
				case SALE:
					SignBlock.setLine( 3, PermitSigns.instance.economy.getPriceString( sign.permitAlias ) );
					return;
				case MONITOR:
					int online = PermitSigns.instance.tracker.getOnlinePlayerCountByPermit( sign.permitAlias );
					int total = PermitMe.instance.players.getPlayerCountByPermit( sign.permitAlias );
					SignBlock.setLine( 3, online + "/" + total );
			}
		} // TODO: Error if the location is no longer a sign!
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
