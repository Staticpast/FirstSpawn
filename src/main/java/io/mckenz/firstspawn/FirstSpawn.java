package io.mckenz.firstspawn;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class FirstSpawn extends JavaPlugin implements Listener {
    private Location firstSpawnLocation;
    private FileConfiguration config;
    private boolean enabled;
    private boolean debug;
    private String welcomeMessage;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadConfig();
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        logDebug("FirstSpawn has been enabled!");
    }

    private void loadConfig() {
        config = getConfig();
        enabled = config.getBoolean("enabled", true);
        debug = config.getBoolean("debug", false);
        welcomeMessage = config.getString("welcome-message", "");

        // Load spawn location from config
        if (config.contains("firstSpawn")) {
            try {
                firstSpawnLocation = new Location(
                    getServer().getWorld(config.getString("firstSpawn.world")),
                    config.getDouble("firstSpawn.x"),
                    config.getDouble("firstSpawn.y"),
                    config.getDouble("firstSpawn.z")
                );
                
                // Set direction if specified
                String direction = config.getString("firstSpawn.direction", "");
                if (!direction.isEmpty()) {
                    try {
                        switch (direction.toUpperCase()) {
                            case "NORTH":
                                firstSpawnLocation.setYaw(180f);
                                break;
                            case "EAST":
                                firstSpawnLocation.setYaw(270f);
                                break;
                            case "SOUTH":
                                firstSpawnLocation.setYaw(0f);
                                break;
                            case "WEST":
                                firstSpawnLocation.setYaw(90f);
                                break;
                            default:
                                logDebug("Invalid direction in config: " + direction);
                                break;
                        }
                    } catch (Exception e) {
                        logDebug("Error setting direction: " + e.getMessage());
                    }
                }
                
                logDebug("Loaded spawn location: " + formatLocationRaw(firstSpawnLocation));
            } catch (Exception e) {
                getLogger().warning("Error loading spawn location: " + e.getMessage());
                firstSpawnLocation = null;
            }
        }
    }

    private void logDebug(String message) {
        if (debug) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FirstSpawn has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        
        Player player = event.getPlayer();
        
        // Check if player has joined before
        if (!player.hasPlayedBefore() && firstSpawnLocation != null) {
            // Teleport player to first spawn location
            player.teleport(firstSpawnLocation);
            // Set their spawn point
            player.setBedSpawnLocation(firstSpawnLocation, true);
            
            // Send welcome message if configured
            if (!welcomeMessage.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', welcomeMessage));
            }
            
            logDebug("Teleported new player " + player.getName() + " to first spawn location");
        }
    }

    private String formatLocation(Location loc) {
        if (loc == null) return ChatColor.RED + "Not set";
        return String.format("%s%s: %s%.1f, %.1f, %.1f", 
            ChatColor.GREEN, loc.getWorld().getName(),
            ChatColor.YELLOW, loc.getX(), loc.getY(), loc.getZ());
    }
    
    private String formatLocationRaw(Location loc) {
        if (loc == null) return "Not set";
        return String.format("%s: %.1f, %.1f, %.1f", 
            loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("firstspawn")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "FirstSpawn Commands:");
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn set " + ChatColor.WHITE + "- Set spawn location");
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn status " + ChatColor.WHITE + "- Show current settings");
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn test " + ChatColor.WHITE + "- Test teleport to spawn");
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn toggle " + ChatColor.WHITE + "- Enable/disable plugin");
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn reload " + ChatColor.WHITE + "- Reload configuration");
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn debug " + ChatColor.WHITE + "- Toggle debug mode");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                    return true;
                }

                Player player = (Player) sender;
                Location location = player.getLocation();
                firstSpawnLocation = location;

                // Save to config
                config.set("firstSpawn.world", location.getWorld().getName());
                config.set("firstSpawn.x", location.getX());
                config.set("firstSpawn.y", location.getY());
                config.set("firstSpawn.z", location.getZ());
                
                // Save direction based on player's yaw
                float yaw = location.getYaw();
                String direction = "";
                if (yaw >= 135 && yaw < 225) {
                    direction = "NORTH";
                } else if (yaw >= 225 && yaw < 315) {
                    direction = "EAST";
                } else if ((yaw >= 315 && yaw <= 360) || (yaw >= 0 && yaw < 45)) {
                    direction = "SOUTH";
                } else if (yaw >= 45 && yaw < 135) {
                    direction = "WEST";
                }
                config.set("firstSpawn.direction", direction);
                
                saveConfig();
                logDebug("Set spawn location to: " + formatLocationRaw(location) + " facing " + direction);

                sender.sendMessage(ChatColor.GREEN + "First spawn location has been set to your current location!");
                sender.sendMessage(ChatColor.YELLOW + "Location: " + formatLocation(location) + 
                                  (direction.isEmpty() ? "" : ChatColor.YELLOW + " facing " + direction));
                break;

            case "status":
                sender.sendMessage(ChatColor.GOLD + "FirstSpawn Status:");
                sender.sendMessage(ChatColor.YELLOW + "Plugin enabled: " + 
                    (enabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                sender.sendMessage(ChatColor.YELLOW + "Debug mode: " + 
                    (debug ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
                sender.sendMessage(ChatColor.YELLOW + "Current spawn location: " + 
                    formatLocation(firstSpawnLocation));
                sender.sendMessage(ChatColor.YELLOW + "Welcome message: " + 
                    (welcomeMessage.isEmpty() ? ChatColor.RED + "None" : 
                     ChatColor.GREEN + welcomeMessage));
                break;

            case "test":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                    return true;
                }
                if (firstSpawnLocation == null) {
                    sender.sendMessage(ChatColor.RED + "Spawn location is not set!");
                    return true;
                }
                Player testPlayer = (Player) sender;
                testPlayer.teleport(firstSpawnLocation);
                sender.sendMessage(ChatColor.GREEN + "Teleported to first spawn location!");
                
                // Show welcome message in test mode too
                if (!welcomeMessage.isEmpty()) {
                    testPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', welcomeMessage));
                    sender.sendMessage(ChatColor.YELLOW + "Sent welcome message: " + 
                                      ChatColor.GREEN + welcomeMessage);
                }
                break;

            case "toggle":
                enabled = !enabled;
                config.set("enabled", enabled);
                saveConfig();
                sender.sendMessage(ChatColor.YELLOW + "FirstSpawn is now " + 
                    (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                break;
                
            case "debug":
                debug = !debug;
                config.set("debug", debug);
                saveConfig();
                sender.sendMessage(ChatColor.YELLOW + "Debug mode is now " + 
                    (debug ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                break;

            case "reload":
                loadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /firstspawn for help.");
                break;
        }
        return true;
    }
} 