package io.mckenz.firstspawn;

import io.mckenz.firstspawn.api.FirstSpawnAPI;
import io.mckenz.firstspawn.api.events.PlayerFirstSpawnEvent;
import io.mckenz.firstspawn.api.events.PlayerFirstSpawnedEvent;
import io.mckenz.firstspawn.commands.FirstSpawnCommand;
import io.mckenz.firstspawn.listeners.PlayerJoinListener;
import io.mckenz.firstspawn.util.LocationFormatter;
import io.mckenz.firstspawn.util.UpdateChecker;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the FirstSpawn plugin
 */
public class FirstSpawn extends JavaPlugin implements FirstSpawnAPI {
    private Location firstSpawnLocation;
    private FileConfiguration config;
    private boolean enabled;
    private boolean debug;
    private String welcomeMessage;
    private boolean setBedSpawn;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadConfig();
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        
        // Register commands
        FirstSpawnCommand commandExecutor = new FirstSpawnCommand(this);
        getCommand("firstspawn").setExecutor(commandExecutor);
        getCommand("firstspawn").setTabCompleter(commandExecutor);
        
        // Register API
        getServer().getServicesManager().register(
            FirstSpawnAPI.class, 
            this, 
            this, 
            org.bukkit.plugin.ServicePriority.Normal
        );
        
        // Initialize update checker if enabled
        if (config.getBoolean("update-checker.enabled", true)) {
            int resourceId = config.getInt("update-checker.resource-id", 122818);
            boolean notifyAdmins = config.getBoolean("update-checker.notify-admins", true);
            
            updateChecker = new UpdateChecker(this, resourceId, notifyAdmins);
            updateChecker.checkForUpdates();
            logDebug("Update checker initialized with resource ID: " + resourceId);
        }
        
        getLogger().info("FirstSpawn has been enabled!");
        logDebug("Debug mode is enabled");
    }

    /**
     * Loads configuration from config.yml
     */
    public void loadConfig() {
        reloadConfig();
        config = getConfig();
        enabled = config.getBoolean("enabled", true);
        debug = config.getBoolean("debug", false);
        welcomeMessage = config.getString("welcome-message", "");
        setBedSpawn = config.getBoolean("firstSpawn.set-bed-spawn", true);

        // Load spawn location from config
        if (config.contains("firstSpawn")) {
            try {
                String worldName = config.getString("firstSpawn.world");
                if (getServer().getWorld(worldName) == null) {
                    getLogger().warning("World '" + worldName + "' not found! First spawn location will not be set.");
                    firstSpawnLocation = null;
                    return;
                }
                
                firstSpawnLocation = new Location(
                    getServer().getWorld(worldName),
                    config.getDouble("firstSpawn.x"),
                    config.getDouble("firstSpawn.y"),
                    config.getDouble("firstSpawn.z")
                );
                
                // Set direction if specified
                String direction = config.getString("firstSpawn.direction", "");
                if (!direction.isEmpty()) {
                    float yaw = LocationFormatter.getYawFromDirection(direction);
                    if (yaw >= 0) {
                        firstSpawnLocation.setYaw(yaw);
                    } else {
                        logDebug("Invalid direction in config: " + direction);
                    }
                }
                
                logDebug("Loaded spawn location: " + LocationFormatter.formatLocationRaw(firstSpawnLocation));
                logDebug("Set bed spawn: " + (setBedSpawn ? "enabled" : "disabled"));
            } catch (Exception e) {
                getLogger().warning("Error loading spawn location: " + e.getMessage());
                firstSpawnLocation = null;
            }
        }
    }

    /**
     * Logs a debug message if debug mode is enabled
     * 
     * @param message The message to log
     */
    public void logDebug(String message) {
        if (debug) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FirstSpawn has been disabled!");
    }

    // API Methods
    
    @Override
    public Location getFirstSpawnLocation() {
        return firstSpawnLocation;
    }
    
    @Override
    public void setFirstSpawnLocation(Location location) {
        if (location == null) {
            return;
        }
        
        firstSpawnLocation = location.clone();
        
        // Save to config
        config.set("firstSpawn.world", location.getWorld().getName());
        config.set("firstSpawn.x", location.getX());
        config.set("firstSpawn.y", location.getY());
        config.set("firstSpawn.z", location.getZ());
        
        // Save direction based on player's yaw
        String direction = LocationFormatter.getDirectionFromYaw(location.getYaw());
        config.set("firstSpawn.direction", direction);
        
        saveConfig();
        logDebug("Set spawn location to: " + LocationFormatter.formatLocationRaw(location) + 
                (direction.isEmpty() ? "" : " facing " + direction));
    }
    
    @Override
    public boolean teleportToFirstSpawn(Player player) {
        return teleportToFirstSpawn(player, setBedSpawn, true);
    }
    
    @Override
    public boolean teleportToFirstSpawn(Player player, boolean setBedSpawn, boolean sendWelcomeMessage) {
        if (player == null || firstSpawnLocation == null) {
            return false;
        }
        
        // Check if plugin functionality is enabled
        if (!isPluginFunctionalityEnabled()) {
            return false;
        }
        
        try {
            // Check if this is the player's first join
            boolean isFirstJoin = !player.hasPlayedBefore();
            
            // Call the pre-teleport event
            PlayerFirstSpawnEvent event = new PlayerFirstSpawnEvent(player, firstSpawnLocation, isFirstJoin);
            getServer().getPluginManager().callEvent(event);
            
            // Check if the event was cancelled
            if (event.isCancelled()) {
                logDebug("Teleport to first spawn was cancelled" + 
                        (event.getCancelReason() != null ? ": " + event.getCancelReason() : ""));
                return false;
            }
            
            // Teleport the player
            player.teleport(firstSpawnLocation);
            
            // Set bed spawn if requested
            if (setBedSpawn) {
                player.setBedSpawnLocation(firstSpawnLocation, true);
                logDebug("Set bed spawn location for " + player.getName());
            }
            
            // Send welcome message if configured and requested
            boolean messageSent = false;
            if (sendWelcomeMessage && !welcomeMessage.isEmpty()) {
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', welcomeMessage));
                messageSent = true;
            }
            
            // Call the post-teleport event
            PlayerFirstSpawnedEvent spawnedEvent = new PlayerFirstSpawnedEvent(
                player, firstSpawnLocation, isFirstJoin, messageSent);
            getServer().getPluginManager().callEvent(spawnedEvent);
            
            logDebug("Teleported player " + player.getName() + " to first spawn location");
            return true;
        } catch (Exception e) {
            getLogger().warning("Error teleporting player to first spawn: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isPluginEnabled() {
        return isPluginFunctionalityEnabled();
    }
    
    @Override
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
    
    @Override
    public void setWelcomeMessage(String message) {
        welcomeMessage = message;
        config.set("welcome-message", message);
        saveConfig();
    }
    
    @Override
    public void registerEvents(Plugin plugin, Listener listener) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }
    
    /**
     * Checks if the plugin functionality is enabled
     * 
     * @return True if enabled, false otherwise
     */
    public boolean isPluginFunctionalityEnabled() {
        return enabled;
    }
    
    /**
     * Sets whether the plugin functionality is enabled
     * 
     * @param enabled True to enable, false to disable
     */
    public void setPluginFunctionalityEnabled(boolean enabled) {
        this.enabled = enabled;
        config.set("enabled", enabled);
        saveConfig();
    }
    
    /**
     * Checks if debug mode is enabled
     * 
     * @return True if debug mode is enabled, false otherwise
     */
    public boolean isDebugEnabled() {
        return debug;
    }
    
    /**
     * Sets whether debug mode is enabled
     * 
     * @param debug True to enable debug mode, false to disable
     */
    public void setDebugEnabled(boolean debug) {
        this.debug = debug;
        config.set("debug", debug);
        saveConfig();
    }
    
    /**
     * Checks if the plugin should set the player's bed spawn location
     * 
     * @return True if bed spawn should be set, false otherwise
     */
    public boolean isSetBedSpawnEnabled() {
        return setBedSpawn;
    }
    
    /**
     * Sets whether the plugin should set the player's bed spawn location
     * This is only used internally when loading the configuration
     * 
     * @param setBedSpawn True to set bed spawn, false otherwise
     */
    void setSetBedSpawnEnabled(boolean setBedSpawn) {
        this.setBedSpawn = setBedSpawn;
        config.set("firstSpawn.set-bed-spawn", setBedSpawn);
        saveConfig();
    }
    
    /**
     * Gets the update checker instance
     * 
     * @return The update checker instance, or null if update checking is disabled
     */
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
} 