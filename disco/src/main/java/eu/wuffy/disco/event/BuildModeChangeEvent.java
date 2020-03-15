package eu.wuffy.disco.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BuildModeChangeEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Player player;
	private final boolean enabled;

	public BuildModeChangeEvent(Player player, boolean enabled) {
		this.player = player;
		this.enabled = enabled;
	}

	public Player getPlayer() {
		return this.player;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
