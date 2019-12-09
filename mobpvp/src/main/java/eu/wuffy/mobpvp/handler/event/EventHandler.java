package eu.wuffy.mobpvp.handler.event;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.synced.IHandler;

public class EventHandler extends IHandler<MobPvP> {

	private final List<EventListener> events = new LinkedList<EventListener>();

	public EventHandler(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {

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
