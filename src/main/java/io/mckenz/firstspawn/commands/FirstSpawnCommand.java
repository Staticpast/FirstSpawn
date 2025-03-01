package io.mckenz.firstspawn.commands;

import io.mckenz.firstspawn.FirstSpawn;
import io.mckenz.firstspawn.util.LocationFormatter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command executor and tab completer for the FirstSpawn plugin
 */
public class FirstSpawnCommand implements CommandExecutor, TabCompleter {
    
    private final FirstSpawn plugin;
    private final List<String> subcommands = Arrays.asList("set", "status", "test", "toggle", "reload", "debug");
    
    /**
     * Constructor for the command executor
     * 
     * @param plugin Reference to the main plugin instance
     */
    public FirstSpawnCommand(FirstSpawn plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("firstspawn")) {
            return false;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        
        // Check permissions for each subcommand
        if (!hasPermission(sender, subCommand)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        switch (subCommand) {
            case "set":
                return handleSetCommand(sender);
                
            case "status":
                return handleStatusCommand(sender);
                
            case "test":
                return handleTestCommand(sender);
                
            case "toggle":
                return handleToggleCommand(sender);
                
            case "debug":
                return handleDebugCommand(sender);
                
            case "reload":
                return handleReloadCommand(sender);
                
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /firstspawn for help.");
                return true;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subcommands.stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .filter(s -> hasPermission(sender, s))
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Checks if the sender has permission for a specific subcommand
     * 
     * @param sender The command sender
     * @param subCommand The subcommand to check
     * @return True if the sender has permission, false otherwise
     */
    private boolean hasPermission(CommandSender sender, String subCommand) {
        if (sender.hasPermission("firstspawn.admin")) {
            return true;
        }
        
        return sender.hasPermission("firstspawn." + subCommand);
    }
    
    /**
     * Shows the help message to the sender
     * 
     * @param sender The command sender
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "FirstSpawn Commands:");
        
        if (hasPermission(sender, "set")) {
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn set " + ChatColor.WHITE + "- Set spawn location");
        }
        
        if (hasPermission(sender, "status")) {
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn status " + ChatColor.WHITE + "- Show current settings");
        }
        
        if (hasPermission(sender, "test")) {
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn test " + ChatColor.WHITE + "- Test teleport to spawn");
        }
        
        if (hasPermission(sender, "toggle")) {
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn toggle " + ChatColor.WHITE + "- Enable/disable plugin");
        }
        
        if (hasPermission(sender, "reload")) {
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn reload " + ChatColor.WHITE + "- Reload configuration");
        }
        
        if (hasPermission(sender, "debug")) {
            sender.sendMessage(ChatColor.YELLOW + "/firstspawn debug " + ChatColor.WHITE + "- Toggle debug mode");
        }
    }
    
    /**
     * Handles the set subcommand
     * 
     * @param sender The command sender
     * @return True if the command was handled successfully
     */
    private boolean handleSetCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        plugin.setFirstSpawnLocation(location);
        
        String direction = LocationFormatter.getDirectionFromYaw(location.getYaw());
        
        sender.sendMessage(ChatColor.GREEN + "First spawn location has been set to your current location!");
        sender.sendMessage(ChatColor.YELLOW + "Location: " + LocationFormatter.formatLocation(location) + 
                          (direction.isEmpty() ? "" : ChatColor.YELLOW + " facing " + direction));
        
        return true;
    }
    
    /**
     * Handles the status subcommand
     * 
     * @param sender The command sender
     * @return True if the command was handled successfully
     */
    private boolean handleStatusCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "FirstSpawn Status:");
        sender.sendMessage(ChatColor.YELLOW + "Plugin enabled: " + 
            (plugin.isEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.YELLOW + "Debug mode: " + 
            (plugin.isDebugEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Set bed spawn: " + 
            (plugin.isSetBedSpawnEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") +
            ChatColor.GRAY + " (config.yml)");
        sender.sendMessage(ChatColor.YELLOW + "Current spawn location: " + 
            LocationFormatter.formatLocation(plugin.getFirstSpawnLocation()));
        sender.sendMessage(ChatColor.YELLOW + "Welcome message: " + 
            (plugin.getWelcomeMessage().isEmpty() ? ChatColor.RED + "None" : 
             ChatColor.GREEN + plugin.getWelcomeMessage()));
        
        return true;
    }
    
    /**
     * Handles the test subcommand
     * 
     * @param sender The command sender
     * @return True if the command was handled successfully
     */
    private boolean handleTestCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Location firstSpawnLocation = plugin.getFirstSpawnLocation();
        if (firstSpawnLocation == null) {
            sender.sendMessage(ChatColor.RED + "Spawn location is not set!");
            return true;
        }
        
        Player testPlayer = (Player) sender;
        plugin.teleportToFirstSpawn(testPlayer, plugin.isSetBedSpawnEnabled(), true);
        sender.sendMessage(ChatColor.GREEN + "Teleported to first spawn location!");
        
        if (plugin.isSetBedSpawnEnabled()) {
            sender.sendMessage(ChatColor.YELLOW + "Your bed spawn location has been set to the first spawn location.");
        }
        
        return true;
    }
    
    /**
     * Handles the toggle subcommand
     * 
     * @param sender The command sender
     * @return True if the command was handled successfully
     */
    private boolean handleToggleCommand(CommandSender sender) {
        boolean newState = !plugin.isEnabled();
        plugin.setEnabled(newState);
        
        sender.sendMessage(ChatColor.YELLOW + "FirstSpawn is now " + 
            (newState ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
        
        return true;
    }
    
    /**
     * Handles the debug subcommand
     * 
     * @param sender The command sender
     * @return True if the command was handled successfully
     */
    private boolean handleDebugCommand(CommandSender sender) {
        boolean newState = !plugin.isDebugEnabled();
        plugin.setDebugEnabled(newState);
        
        sender.sendMessage(ChatColor.YELLOW + "Debug mode is now " + 
            (newState ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
        
        return true;
    }
    
    /**
     * Handles the reload subcommand
     * 
     * @param sender The command sender
     * @return True if the command was handled successfully
     */
    private boolean handleReloadCommand(CommandSender sender) {
        plugin.loadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
        
        return true;
    }
} 