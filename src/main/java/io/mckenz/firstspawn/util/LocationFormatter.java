package io.mckenz.firstspawn.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Utility class for formatting location information
 */
public class LocationFormatter {
    
    /**
     * Formats a location with color codes for display to players
     * 
     * @param loc The location to format
     * @return A colored string representation of the location
     */
    public static String formatLocation(Location loc) {
        if (loc == null) return ChatColor.RED + "Not set";
        return String.format("%s%s: %s%.1f, %.1f, %.1f", 
            ChatColor.GREEN, loc.getWorld().getName(),
            ChatColor.YELLOW, loc.getX(), loc.getY(), loc.getZ());
    }
    
    /**
     * Formats a location without color codes for logging
     * 
     * @param loc The location to format
     * @return A plain string representation of the location
     */
    public static String formatLocationRaw(Location loc) {
        if (loc == null) return "Not set";
        return String.format("%s: %.1f, %.1f, %.1f", 
            loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }
    
    /**
     * Determines the cardinal direction (NORTH, EAST, SOUTH, WEST) from a yaw value
     * 
     * @param yaw The yaw value to convert
     * @return A string representing the cardinal direction
     */
    public static String getDirectionFromYaw(float yaw) {
        if (yaw >= 135 && yaw < 225) {
            return "NORTH";
        } else if (yaw >= 225 && yaw < 315) {
            return "EAST";
        } else if ((yaw >= 315 && yaw <= 360) || (yaw >= 0 && yaw < 45)) {
            return "SOUTH";
        } else if (yaw >= 45 && yaw < 135) {
            return "WEST";
        }
        return "";
    }
    
    /**
     * Converts a cardinal direction string to a yaw value
     * 
     * @param direction The direction string (NORTH, EAST, SOUTH, WEST)
     * @return The corresponding yaw value, or -1 if invalid
     */
    public static float getYawFromDirection(String direction) {
        if (direction == null || direction.isEmpty()) {
            return -1f;
        }
        
        switch (direction.toUpperCase()) {
            case "NORTH":
                return 180f;
            case "EAST":
                return 270f;
            case "SOUTH":
                return 0f;
            case "WEST":
                return 90f;
            default:
                return -1f;
        }
    }
} 