package com.yonn2222.clientsortpaper.event;

import com.yonn2222.clientsortpaper.operation.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public final class ClientSortOperationEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Operation operation;
    private final Inventory source;
    private final Inventory target;
    private boolean cancelled;

    public ClientSortOperationEvent(Player player, Operation operation, Inventory source, Inventory target) {
        this.player = player;
        this.operation = operation;
        this.source = source;
        this.target = target;
    }

    public Player getPlayer() {
        return player;
    }

    public Operation getOperation() {
        return operation;
    }

    public Inventory getSource() {
        return source;
    }

    public Inventory getTarget() {
        return target;
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
