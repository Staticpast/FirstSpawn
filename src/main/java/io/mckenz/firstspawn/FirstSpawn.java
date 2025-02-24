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

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadConfig();
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("FirstSpawn has been enabled!");
    }

    private void loadConfig() {
        config = getConfig();
        enabled = config.getBoolean("enabled", true);

        // Load spawn location from config
        if (config.contains("firstSpawn")) {
            firstSpawnLocation = new Location(
                getServer().getWorld(config.getString("firstSpawn.world")),
                config.getDouble("firstSpawn.x"),
                config.getDouble("firstSpawn.y"),
                config.getDouble("firstSpawn.z")
            );
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
            getLogger().info("Teleported new player " + player.getName() + " to first spawn location");
        }
    }

    private String formatLocation(Location loc) {
        if (loc == null) return ChatColor.RED + "Not set";
        return String.format("%s%s: %s%.1f, %.1f, %.1f", 
            ChatColor.GREEN, loc.getWorld().getName(),
            ChatColor.YELLOW, loc.getX(), loc.getY(), loc.getZ());
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
                saveConfig();

                sender.sendMessage(ChatColor.GREEN + "First spawn location has been set to your current location!");
                sender.sendMessage(ChatColor.YELLOW + "Location: " + formatLocation(location));
                break;

            case "status":
                sender.sendMessage(ChatColor.GOLD + "FirstSpawn Status:");
                sender.sendMessage(ChatColor.YELLOW + "Plugin enabled: " + 
                    (enabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                sender.sendMessage(ChatColor.YELLOW + "Current spawn location: " + 
                    formatLocation(firstSpawnLocation));
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
                break;

            case "toggle":
                enabled = !enabled;
                config.set("enabled", enabled);
                saveConfig();
                sender.sendMessage(ChatColor.YELLOW + "FirstSpawn is now " + 
                    (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
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