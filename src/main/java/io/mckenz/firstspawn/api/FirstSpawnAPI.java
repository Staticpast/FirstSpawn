package io.mckenz.firstspawn.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * API interface for the FirstSpawn plugin
 * This allows other plugins to interact with FirstSpawn functionality
 */
public interface FirstSpawnAPI {
    
    /**
     * Gets the current first spawn location
     * 
     * @return The first spawn location, or null if not set
     */
    Location getFirstSpawnLocation();
    
    /**
     * Sets the first spawn location
     * 
     * @param location The location to set as the first spawn
     */
    void setFirstSpawnLocation(Location location);
    
    /**
     * Teleports a player to the first spawn location
     * 
     * @param player The player to teleport
     * @return True if teleported successfully, false otherwise
     */
    boolean teleportToFirstSpawn(Player player);
    
    /**
     * Teleports a player to the first spawn location with additional options
     * 
     * @param player The player to teleport
     * @param setBedSpawn Whether to set the player's bed spawn location
     * @param sendWelcomeMessage Whether to send the welcome message
     * @return True if teleported successfully, false otherwise
     */
    boolean teleportToFirstSpawn(Player player, boolean setBedSpawn, boolean sendWelcomeMessage);
    
    /**
     * Checks if the plugin is currently enabled
     * 
     * @return True if enabled, false otherwise
     */
    boolean isPluginEnabled();
    
    /**
     * Gets the welcome message that is sent to new players
     * 
     * @return The welcome message
     */
    String getWelcomeMessage();
    
    /**
     * Sets the welcome message that is sent to new players
     * 
     * @param message The welcome message to set
     */
    void setWelcomeMessage(String message);
    
    /**
     * Registers a listener for FirstSpawn events
     * This is the recommended way to listen for FirstSpawn events
     * 
     * @param plugin The plugin registering the listener
     * @param listener The listener to register
     */
    void registerEvents(Plugin plugin, Listener listener);
} 