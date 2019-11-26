package eu.wuffy.survival.handler.event;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.event.bukkit.AsyncPlayerChatEventListener;
import eu.wuffy.survival.event.bukkit.BlockBreakEventListener;
import eu.wuffy.survival.event.bukkit.EntityDamageEventListener;
import eu.wuffy.survival.event.bukkit.EntityPickupItemEventListener;
import eu.wuffy.survival.event.bukkit.FoodLevelChangeEventListener;
import eu.wuffy.survival.event.bukkit.PlayerJoinEventListener;
import eu.wuffy.survival.event.bukkit.PlayerQuitEventListener;
import eu.wuffy.survival.event.factions.FPlayerEnteredFactionEventListener;
import eu.wuffy.survival.event.factions.FPlayerJoinEventListener;
import eu.wuffy.survival.event.factions.FPlayerLeaveEventListener;
import eu.wuffy.survival.event.factions.FactionCreateEventListener;
import eu.wuffy.survival.event.luckperms.GroupDataRecalculateEventListener;
import eu.wuffy.survival.event.luckperms.UserLoadEventListener;
import eu.wuffy.survival.event.luckperms.UserPromoteEventListener;
import eu.wuffy.synced.IHandler;

public class EventHandler extends IHandler<Survival> {

	private final List<EventListener> events = new LinkedList<EventListener>();

	public EventHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		// LuckPerms
		this.events.add(new UserPromoteEventListener(this.getCore()));
		this.events.add(new UserLoadEventListener(this.getCore()));
		this.events.add(new GroupDataRecalculateEventListener(this.getCore()));

		// Factions
		this.events.add(new FactionCreateEventListener(this.getCore()));
		this.events.add(new FPlayerEnteredFactionEventListener(this.getCore()));
		this.events.add(new FPlayerJoinEventListener(this.getCore()));
		this.events.add(new FPlayerLeaveEventListener(this.getCore()));

		// Bukkit
		this.events.add(new AsyncPlayerChatEventListener(this.getCore()));
		this.events.add(new PlayerJoinEventListener(this.getCore()));
		this.events.add(new PlayerQuitEventListener(this.getCore()));
		this.events.add(new EntityDamageEventListener(this.getCore()));
		this.events.add(new EntityPickupItemEventListener(this.getCore()));
		this.events.add(new FoodLevelChangeEventListener(this.getCore()));
		this.events.add(new BlockBreakEventListener(this.getCore()));

		this.events.stream().forEach(EventListener::init);
	}

	@Override
	public void onEnable() {
		this.events.stream().filter(event -> !event.isEnabled()).forEach(EventListener::enable);
	}

	@Override
	public void onDisable() {
		this.events.stream().filter(event -> event.isEnabled()).forEach(EventListener::disable);
	}

	public void add(EventListener listener) {
		this.events.add(listener);

		if (this.isEnabled()) {
			listener.onInit();
			listener.onEnable();
		}
	}

	public List<EventListener> getEvents() {
		return Collections.unmodifiableList(this.events);
	}
}
