package io.mckenz.firstspawn.util;

import io.mckenz.firstspawn.FirstSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Checks for updates to the plugin
 */
public class UpdateChecker implements Listener {
    private final FirstSpawn plugin;
    private final int resourceId;
    private final boolean notifyAdmins;
    private boolean updateAvailable = false;
    private String latestVersion = null;

    /**
     * Creates a new UpdateChecker instance
     * 
     * @param plugin The plugin instance
     * @param resourceId The SpigotMC resource ID
     * @param notifyAdmins Whether to notify admins when they join
     */
    public UpdateChecker(FirstSpawn plugin, int resourceId, boolean notifyAdmins) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.notifyAdmins = notifyAdmins;
        
        // Register this class as an event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Checks for updates to the plugin
     */
    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String currentVersion = plugin.getDescription().getVersion();
                latestVersion = fetchLatestVersion();
                
                if (latestVersion == null) {
                    plugin.getLogger().warning("Failed to check for updates.");
                    return;
                }
                
                // Normalize versions for logging
                String normalizedCurrent = normalizeVersion(currentVersion);
                String normalizedLatest = normalizeVersion(latestVersion);
                
                // Compare versions using semantic versioning
                if (!versionsEqual(currentVersion, latestVersion)) {
                    // Check if the latest version is actually newer
                    String[] currentParts = normalizedCurrent.split("\\.");
                    String[] latestParts = normalizedLatest.split("\\.");
                    
                    boolean isNewer = false;
                    for (int i = 0; i < Math.min(currentParts.length, latestParts.length); i++) {
                        int currentPart = Integer.parseInt(currentParts[i]);
                        int latestPart = Integer.parseInt(latestParts[i]);
                        
                        if (latestPart > currentPart) {
                            isNewer = true;
                            break;
                        } else if (latestPart < currentPart) {
                            // Current version is actually newer than "latest"
                            break;
                        }
                    }
                    
                    if (isNewer) {
                        updateAvailable = true;
                        plugin.getLogger().info("A new update is available: v" + latestVersion);
                        plugin.getLogger().info("You are currently running: v" + currentVersion);
                        plugin.getLogger().info("Download the latest version from: https://www.spigotmc.org/resources/" + resourceId);
                    } else {
                        plugin.getLogger().info("You are running the latest version: v" + currentVersion);
                    }
                } else {
                    plugin.getLogger().info("You are running the latest version: v" + currentVersion);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    /**
     * Gets the latest version from SpigotMC
     * 
     * @return The latest version, or null if an error occurred
     * @throws IOException If an I/O error occurs
     */
    private String fetchLatestVersion() throws IOException {
        URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        
        try (InputStream inputStream = url.openStream();
             Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
                return scanner.next();
            }
        }
        
        return null;
    }
    
    /**
     * Compare two version strings for equality
     * 
     * @param version1 The first version string
     * @param version2 The second version string
     * @return True if the versions are equal, false otherwise
     */
    private boolean versionsEqual(String version1, String version2) {
        // Normalize both versions
        String normalizedVersion1 = normalizeVersion(version1);
        String normalizedVersion2 = normalizeVersion(version2);
        
        // Simple string comparison after normalization
        return normalizedVersion1.equals(normalizedVersion2);
    }
    
    /**
     * Normalize a version string for comparison
     * @param version The version string to normalize
     * @return The normalized version string
     */
    private String normalizeVersion(String version) {
        // Remove all 'v' prefixes (handles cases like 'vv1.1.0')
        while (version.startsWith("v")) {
            version = version.substring(1);
        }
        
        // Remove any suffixes like -RELEASE, -SNAPSHOT, etc.
        int dashIndex = version.indexOf('-');
        if (dashIndex > 0) {
            version = version.substring(0, dashIndex);
        }
        
        // Trim any whitespace
        version = version.trim();
        
        // Ensure consistent format for comparison
        // For example, convert "1.1" to "1.1.0" if needed
        String[] parts = version.split("\\.");
        if (parts.length == 2) {
            version = version + ".0";
        }
        
        return version;
    }

    /**
     * Checks if an update is available
     * 
     * @return True if an update is available, false otherwise
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * Gets the latest version
     * 
     * @return The latest version
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Notifies admins when they join if an update is available
     * 
     * @param event The player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (updateAvailable && notifyAdmins && event.getPlayer().hasPermission("firstspawn.update")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6FirstSpawn&8] &7A new update is available: &6v" + latestVersion));
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6FirstSpawn&8] &7You are currently running: &6v" + plugin.getDescription().getVersion()));
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6FirstSpawn&8] &7Download at: &6spigotmc.org/resources/" + resourceId));
            }, 40L); // 2 seconds delay
        }
    }
} 