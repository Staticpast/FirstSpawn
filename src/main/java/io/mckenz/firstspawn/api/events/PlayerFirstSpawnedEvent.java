package io.mckenz.firstspawn.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event that is called after a player has been teleported to the first spawn location
 */
public class PlayerFirstSpawnedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Location spawnLocation;
    private final boolean isFirstJoin;
    private final boolean welcomeMessageSent;
    
    /**
     * Creates a new PlayerFirstSpawnedEvent
     * 
     * @param player The player who was teleported
     * @param spawnLocation The location the player was teleported to
     * @param isFirstJoin Whether this was the player's first time joining the server
     * @param welcomeMessageSent Whether a welcome message was sent to the player
     */
    public PlayerFirstSpawnedEvent(Player player, Location spawnLocation, boolean isFirstJoin, boolean welcomeMessageSent) {
        this.player = player;
        this.spawnLocation = spawnLocation;
        this.isFirstJoin = isFirstJoin;
        this.welcomeMessageSent = welcomeMessageSent;
    }
    
    /**
     * Gets the player who was teleported
     * 
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the location the player was teleported to
     * 
     * @return The spawn location
     */
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    
    /**
     * Checks if this was the player's first time joining the server
     * 
     * @return True if this was the player's first join, false otherwise
     */
    public boolean isFirstJoin() {
        return isFirstJoin;
    }
    
    /**
     * Checks if a welcome message was sent to the player
     * 
     * @return True if a welcome message was sent, false otherwise
     */
    public boolean isWelcomeMessageSent() {
        return welcomeMessageSent;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
} 