package com.oreilly.permitsigns.util;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Locations {

	public static String toString(Location location) {
		return "world:" + location.getWorld() + " x:" + location.getBlockX()
				+ " y:" + location.getBlockY() + " z:" + location.getBlockZ();
	}

	public static Location fromString(String data, Server server) {
		Location result = null;
		World world = null;
		String worldName = null;
		float x = 0;
		boolean hasX = false;
		float y = 0;
		boolean hasY = false;
		float z = 0;
		boolean hasZ = false;
		String[] splitData = data.split(" ");
		// TODO: Try + catch for error catching
		for (String item : splitData) {
			if (item.startsWith("world:")) {
				worldName = item.substring(6);
				continue;
			}
			if (item.startsWith("x:")) {
				x = Integer.parseInt(item.substring(2));
				hasX = true;
				continue;
			}
			if (item.startsWith("y:")) {
				y = Integer.parseInt(item.substring(2));
				hasY = true;
				continue;
			}
			if (item.startsWith("z:")) {
				z = Integer.parseInt(item.substring(2));
				hasZ = true;
				continue;
			}
		}

		if (worldName != null)
			world = server.getWorld(worldName);
		// TODO: Sanity checking
		if (world != null)
			result = new Location(world, x, y, z);
		return result;
	}
}
