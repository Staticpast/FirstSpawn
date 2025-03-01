package io.mckenz.firstspawn.api.examples;

import io.mckenz.firstspawn.api.FirstSpawnAPI;
import io.mckenz.firstspawn.api.events.PlayerFirstSpawnEvent;
import io.mckenz.firstspawn.api.events.PlayerFirstSpawnedEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example class demonstrating how to use the FirstSpawn API
 * This is not part of the actual plugin functionality, but serves as documentation
 */
public class FirstSpawnAPIExample extends JavaPlugin implements Listener {
    
    private FirstSpawnAPI firstSpawnAPI;
    
    @Override
    public void onEnable() {
        // Get the FirstSpawn API
        RegisteredServiceProvider<FirstSpawnAPI> provider = 
            Bukkit.getServicesManager().getRegistration(FirstSpawnAPI.class);
            
        if (provider != null) {
            firstSpawnAPI = provider.getProvider();
            
            // Register our event listeners with FirstSpawn
            // This is the recommended way to listen for FirstSpawn events
            firstSpawnAPI.registerEvents(this, this);
            
            getLogger().info("Successfully hooked into FirstSpawn API!");
        } else {
            getLogger().warning("FirstSpawn plugin not found! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register commands, etc.
    }
    
    /**
     * Example of using the API to teleport a player
     * 
     * @param player The player to teleport
     */
    public void teleportPlayerToFirstSpawn(Player player) {
        if (firstSpawnAPI != null) {
            // Check if the plugin is enabled
            if (!firstSpawnAPI.isPluginEnabled()) {
                player.sendMessage(ChatColor.RED + "FirstSpawn is currently disabled!");
                return;
            }
            
            // Get the first spawn location
            if (firstSpawnAPI.getFirstSpawnLocation() == null) {
                player.sendMessage(ChatColor.RED + "First spawn location is not set!");
                return;
            }
            
            // Teleport the player with custom options
            boolean success = firstSpawnAPI.teleportToFirstSpawn(player, false, true);
            
            if (success) {
                player.sendMessage(ChatColor.GREEN + "You have been teleported to the first spawn location!");
            } else {
                player.sendMessage(ChatColor.RED + "Failed to teleport to first spawn location!");
            }
        }
    }
    
    /**
     * Example of listening for the pre-teleport event
     * This event is called before a player is teleported to the first spawn location
     * It can be cancelled to prevent the teleportation
     */
    @EventHandler
    public void onPlayerFirstSpawn(PlayerFirstSpawnEvent event) {
        Player player = event.getPlayer();
        
        // Example: Prevent teleportation if the player is in combat
        if (isPlayerInCombat(player)) {
            event.setCancelled(true);
            event.setCancelReason("You cannot teleport while in combat!");
            player.sendMessage(ChatColor.RED + "You cannot teleport to first spawn while in combat!");
        }
        
        // Example: Modify the event
        getLogger().info("Player " + player.getName() + " is about to be teleported to first spawn!");
    }
    
    /**
     * Example of listening for the post-teleport event
     * This event is called after a player has been teleported to the first spawn location
     */
    @EventHandler
    public void onPlayerFirstSpawned(PlayerFirstSpawnedEvent event) {
        Player player = event.getPlayer();
        
        // Example: Give the player a welcome kit if it's their first join
        if (event.isFirstJoin()) {
            giveWelcomeKit(player);
            player.sendMessage(ChatColor.GREEN + "You have received a welcome kit!");
        }
        
        // Example: Log the event
        getLogger().info("Player " + player.getName() + " has been teleported to first spawn!");
    }
    
    /**
     * Example method to check if a player is in combat
     * This would be implemented by your plugin
     */
    private boolean isPlayerInCombat(Player player) {
        // Your implementation here
        return false;
    }
    
    /**
     * Example method to give a player a welcome kit
     * This would be implemented by your plugin
     */
    private void giveWelcomeKit(Player player) {
        // Your implementation here
    }
} 