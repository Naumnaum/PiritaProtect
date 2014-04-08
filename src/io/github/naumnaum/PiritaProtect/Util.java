package io.github.naumnaum.PiritaProtect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Openable;

public class Util {

	public static boolean isTagAndValue(String Name, String Value) {
		for (Tag tag : PiritaProtect.Plugin.Tags) {
			if (tag.getName().equalsIgnoreCase(Name)
					&& tag.getValues().contains(Value.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTag(String Name) {
		for (Tag tag : PiritaProtect.Plugin.Tags) {
			if (tag.getName().equalsIgnoreCase(Name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean poweringDoor(Block block) {
		return block.getRelative(BlockFace.NORTH).getState().getData() instanceof Openable
				|| block.getRelative(BlockFace.SOUTH).getState().getData() instanceof Openable
				|| block.getRelative(BlockFace.EAST).getState().getData() instanceof Openable
				|| block.getRelative(BlockFace.WEST).getState().getData() instanceof Openable
				|| block.getRelative(BlockFace.DOWN).getState().getData() instanceof Openable
				|| block.getRelative(BlockFace.UP).getState().getData() instanceof Openable;
	}

	public static UserType parseUserType(String str) {
		for (UserType Type : PiritaProtect.Plugin.Types) {
			if (Type.getName().equalsIgnoreCase(str)) {
				return Type;
			}
		}
		return null;
	}

	public static Region regionfromString(String str, String str2) {
		String[] Splits = str.split("\\:");
		Location loc = new Location(PiritaProtect.Plugin.getServer().getWorld(
				Splits[0]), 0, 0, 0);
		loc.setX(Double.parseDouble(Splits[1]));
		loc.setY(Double.parseDouble(Splits[2]));
		loc.setZ(Double.parseDouble(Splits[3]));
		Splits = str2.split("\\:");
		Location loc2 = new Location(PiritaProtect.Plugin.getServer().getWorld(
				Splits[0]), 0, 0, 0);
		loc2.setX(Double.parseDouble(Splits[1]));
		loc2.setY(Double.parseDouble(Splits[2]));
		loc2.setZ(Double.parseDouble(Splits[3]));
		Region cube = new Region(loc, loc2);
		return cube;
	}

	public static String loc2str(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":"
				+ loc.getBlockY() + ":" + loc.getBlockZ();
	}

	public static boolean isBlockSolid(Block block) {
		return block.getType().isOccluding()
				|| block.getType() == Material.SOIL
				|| block.getType() == Material.LEAVES
				|| block.getType() == Material.SNOW
				|| block.getType() == Material.STAINED_GLASS
				|| block.getType() == Material.STAINED_GLASS_PANE
				|| block.getType() == Material.GLASS;
	}

	public static Block GetLowestBlockRelative(Location Loc, Location rLoc) {
		Location loc = Loc.clone();
		Location rloc = rLoc.clone();
		loc.setY(rloc.getY());
		Block Use = loc.getWorld().getBlockAt(loc);
		if (isBlockSolid(Use) || Use.isLiquid()) {
			return Use;
		} else {
			while (!isBlockSolid(Use) && !Use.isLiquid() && loc.getBlockY() > 0) {
				Use = loc.getWorld().getBlockAt(loc.add(0, -1, 0));
			}
			return loc.getWorld().getBlockAt(loc);
		}
	}

	public static Block GetLowestBlock(Location Loc) {
		Location loc = Loc.clone();
		Block Use = loc.getWorld().getBlockAt(loc);
		if (isBlockSolid(Use) || Use.isLiquid()) {
			return Use;
		} else {
			while (!isBlockSolid(Use) && !Use.isLiquid() && loc.getBlockY() > 0) {
				Use = loc.getWorld().getBlockAt(loc.add(0, -1, 0));
			}
			return loc.getWorld().getBlockAt(loc);
		}
	}
}
