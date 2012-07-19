package com.oreilly.permitsigns;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.oreilly.common.interaction.text.Interaction;


// sign creation process
// - player writes sign
// - if more info is required, sign will show "???" in bottom line
// - player uses "/permitme sign edit" to get into menu of options to modify sign
// (menu activated by clicking on sign)
//  Menu allows changing permit UID, header type, bound location

abstract public class Commands extends com.oreilly.permitme.Commands {
	
	public static void loadCommands() {
		new ActivateEditSignMode( "permitsigns", "sign", "editing", "enable" );
		new ActivateEditSignMode( "permitsigns", "sign", "editing", "on" );
		new DeactivateEditSignMode( "permitsigns", "sign", "editing", "disable" );
		new DeactivateEditSignMode( "permitsigns", "sign", "editing", "off" );
		new PermitSignsAdmin( "permitsigns", "admin" );
	}
	
	
	public Commands( String[] matchingSequence, int dataArgs ) {
		super( matchingSequence, dataArgs );
	}
	
	
	protected boolean standardPermissionCheck( CommandSender sender, String permissionString ) {
		// TODO: Hook into vault for real permission checks
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( "You need to be in game for this command to work" );
			return false; /*
							} else if ( !PermitMe.instance.players.hasPermission( (Player)sender, permissionString ) ) {
							sender.sendMessage( "You do not have the permission to use these commands" );
							return false;
							} else
							return true;*/
		}
		return true;
	}
	
}


class ActivateEditSignMode extends Commands {
	
	public ActivateEditSignMode( String... sequence ) {
		super( sequence, 0 );
	}
	
	
	@Override
	public boolean run( CommandSender sender, Command cmd, String commandLabel, String[] data ) {
		// DEBUG:
		sender.sendMessage( "DEBUG: Activation request" );
		if ( !standardPermissionCheck( sender, "permitsigns.signs.edit" ) )
			return false;
		PermitSigns.instance.signs.activateSignEditingFor( sender );
		sender.sendMessage( "Sign editing is now enabled" );
		return true;
	}
}


class DeactivateEditSignMode extends Commands {
	
	public DeactivateEditSignMode( String... sequence ) {
		super( sequence, 0 );
	}
	
	
	@Override
	public boolean run( CommandSender sender, Command cmd, String commandLabel, String[] data ) {
		if ( !standardPermissionCheck( sender, "permitsigns.signs.edit" ) )
			return false;
		PermitSigns.instance.signs.deactivateSignEditingFor( sender );
		sender.sendMessage( "Sign editing is now disabled" );
		return true;
	}
}


class PermitSignsAdmin extends Commands {
	
	public PermitSignsAdmin( String... sequence ) {
		super( sequence, 0 );
	}
	
	
	@Override
	public boolean run( CommandSender sender, Command cmd, String commandLabel, String[] data ) {
		if ( !standardPermissionCheck( sender, "permitSigns.signs.admin" ) )
			return false;
		Interaction interaction = PermitSigns.instance.adminInteraction.buildInteraction( sender );
		interaction.begin();
		return true;
	}
}