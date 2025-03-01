package io.mckenz.firstspawn.listeners;

import io.mckenz.firstspawn.FirstSpawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener for player join events to handle first-time spawning
 */
public class PlayerJoinListener implements Listener {
    
    private final FirstSpawn plugin;
    
    /**
     * Constructor for the listener
     * 
     * @param plugin Reference to the main plugin instance
     */
    public PlayerJoinListener(FirstSpawn plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handles player join events
     * 
     * @param event The player join event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isEnabled()) return;
        
        Player player = event.getPlayer();
        Location firstSpawnLocation = plugin.getFirstSpawnLocation();
        
        // Check if player has joined before
        if (!player.hasPlayedBefore() && firstSpawnLocation != null) {
            // Use the API method which will fire the appropriate events
            plugin.teleportToFirstSpawn(player, true, true);
        }
    }
} 