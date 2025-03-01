package io.mckenz.firstspawn.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event that is called when a player is about to be teleported to the first spawn location
 * This event is cancellable, allowing other plugins to prevent the teleportation
 */
public class PlayerFirstSpawnEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Location targetLocation;
    private final boolean isFirstJoin;
    private boolean cancelled;
    private String cancelReason;
    
    /**
     * Creates a new PlayerFirstSpawnEvent
     * 
     * @param player The player being teleported
     * @param targetLocation The location the player will be teleported to
     * @param isFirstJoin Whether this is the player's first time joining the server
     */
    public PlayerFirstSpawnEvent(Player player, Location targetLocation, boolean isFirstJoin) {
        this.player = player;
        this.targetLocation = targetLocation;
        this.isFirstJoin = isFirstJoin;
        this.cancelled = false;
        this.cancelReason = null;
    }
    
    /**
     * Gets the player being teleported
     * 
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the location the player will be teleported to
     * 
     * @return The target location
     */
    public Location getTargetLocation() {
        return targetLocation;
    }
    
    /**
     * Checks if this is the player's first time joining the server
     * 
     * @return True if this is the player's first join, false otherwise
     */
    public boolean isFirstJoin() {
        return isFirstJoin;
    }
    
    /**
     * Gets the reason the event was cancelled, if any
     * 
     * @return The cancel reason, or null if not cancelled or no reason provided
     */
    public String getCancelReason() {
        return cancelReason;
    }
    
    /**
     * Sets the reason the event was cancelled
     * 
     * @param reason The reason for cancellation
     */
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
} 