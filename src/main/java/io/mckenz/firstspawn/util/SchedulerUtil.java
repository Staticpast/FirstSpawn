package io.mckenz.firstspawn.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Cross-platform scheduler utility that works with both Spigot/Paper and Folia
 */
public class SchedulerUtil {
    
    private static boolean isFolia = false;
    private static Method getAsyncScheduler = null;
    private static Method getGlobalRegionScheduler = null;
    private static Method getRegionScheduler = null;
    private static Method runNow = null;
    private static Method runDelayed = null;
    private static Method runDelayedEntity = null;
    private static Method getEntityScheduler = null;
    
    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
            
            // Initialize reflection methods for Folia
            Class<?> bukkitClass = Bukkit.class;
            getAsyncScheduler = bukkitClass.getMethod("getAsyncScheduler");
            getGlobalRegionScheduler = bukkitClass.getMethod("getGlobalRegionScheduler");
            getRegionScheduler = bukkitClass.getMethod("getRegionScheduler");
            
            Class<?> asyncSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            runNow = asyncSchedulerClass.getMethod("runNow", Plugin.class, Runnable.class);
            
            Class<?> globalRegionSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            runDelayed = globalRegionSchedulerClass.getMethod("runDelayed", Plugin.class, Runnable.class, long.class);
            
            Class<?> entityClass = Class.forName("org.bukkit.entity.Entity");
            getEntityScheduler = entityClass.getMethod("getScheduler");
            
            Class<?> entitySchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            runDelayedEntity = entitySchedulerClass.getMethod("runDelayed", Plugin.class, Runnable.class, Runnable.class, long.class);
            
        } catch (Exception e) {
            isFolia = false;
        }
    }
    
    /**
     * Checks if the server is running Folia
     * 
     * @return True if running on Folia, false otherwise
     */
    public static boolean isFolia() {
        return isFolia;
    }
    
    /**
     * Runs a task asynchronously on the appropriate scheduler
     * 
     * @param plugin The plugin instance
     * @param task The task to run
     */
    public static void runAsync(Plugin plugin, Runnable task) {
        if (isFolia) {
            try {
                // Use Folia's async scheduler
                Object asyncScheduler = getAsyncScheduler.invoke(null);
                runNow.invoke(asyncScheduler, plugin, task);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to use Folia async scheduler, falling back to Bukkit: " + e.getMessage());
                Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            }
        } else {
            // Use traditional Bukkit scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
    
    /**
     * Runs a delayed task on the appropriate scheduler
     * 
     * @param plugin The plugin instance
     * @param task The task to run
     * @param delayTicks The delay in ticks
     * @param player The player (used for entity scheduling in Folia)
     */
    public static void runDelayed(Plugin plugin, Runnable task, long delayTicks, Player player) {
        if (isFolia) {
            try {
                // Use Folia's entity scheduler for player-related tasks
                Object entityScheduler = getEntityScheduler.invoke(player);
                runDelayedEntity.invoke(entityScheduler, plugin, task, null, delayTicks);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to use Folia entity scheduler, falling back to Bukkit: " + e.getMessage());
                Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            // Use traditional Bukkit scheduler
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
    
    /**
     * Runs a delayed task on the global scheduler (for non-entity related tasks)
     * 
     * @param plugin The plugin instance
     * @param task The task to run
     * @param delayTicks The delay in ticks
     */
    public static void runDelayedGlobal(Plugin plugin, Runnable task, long delayTicks) {
        if (isFolia) {
            try {
                // Use Folia's global scheduler for non-entity tasks
                Object globalScheduler = getGlobalRegionScheduler.invoke(null);
                runDelayed.invoke(globalScheduler, plugin, task, delayTicks);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to use Folia global scheduler, falling back to Bukkit: " + e.getMessage());
                Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            // Use traditional Bukkit scheduler
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
    
    /**
     * Runs a task on the main thread for the given location
     * 
     * @param plugin The plugin instance
     * @param location The location
     * @param task The task to run
     */
    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        if (isFolia) {
            try {
                // Use Folia's region scheduler
                Object regionScheduler = getRegionScheduler.invoke(null);
                Method run = regionScheduler.getClass().getMethod("run", Plugin.class, Location.class, Runnable.class);
                run.invoke(regionScheduler, plugin, location, task);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to use Folia region scheduler, falling back to Bukkit: " + e.getMessage());
                Bukkit.getScheduler().runTask(plugin, task);
            }
        } else {
            // Use traditional Bukkit scheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
