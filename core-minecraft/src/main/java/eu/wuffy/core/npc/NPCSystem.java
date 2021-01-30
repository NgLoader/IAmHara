package eu.wuffy.core.npc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.tablist.NPCTabList;
import eu.wuffy.core.util.MCReflectionUtil;
import eu.wuffy.synced.IHandler;

public class NPCSystem <T extends Core<?>> extends IHandler<T> implements Listener {

	private static final Field ENTITY_COUNT_FIELD = MCReflectionUtil.getField(MCReflectionUtil.getMinecraftServerClass("Entity"), "entityCount");

	private final Random random = new Random();

	private final AtomicInteger entityCount;
	protected final Set<Integer> entityCountIds = new HashSet<>();

	private final Set<Long> uuidInUse = new HashSet<>();
	protected final Map<Integer, NPC> npcByEntityId = new HashMap<>();

	protected final List<NPCRegistry<T>> registries = new ArrayList<>();
	private final NPCRegistry<T> defaultRegistry;

	private final NPCTabList<Core<?>> tabList;
	private final NPCPacketListener<T> packetListener;

	public NPCSystem(T plugin) throws IllegalArgumentException, IllegalAccessException {
		super(plugin);

		this.entityCount = (AtomicInteger) ENTITY_COUNT_FIELD.get(null);

		this.defaultRegistry = new NPCRegistry<T>(this, this.core);
		this.registries.add(this.defaultRegistry);

		this.tabList = new NPCTabList<Core<?>>(this.core);
		this.packetListener = new NPCPacketListener<T>(this);

		Bukkit.getServer().getPluginManager().registerEvents(this, this.core);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.registries.forEach(registry -> registry.hideAll(event.getPlayer()));
	}

	public NPCRegistry<T> createRegestry() {
		NPCRegistry<T> registry = new NPCRegistry<T>(this, this.core);
		this.registries.add(registry);
		return registry;
	}

	public UUID generateUUID() {
		long mostSigBits;
		do {
			mostSigBits = this.random.nextLong();
		} while (!uuidInUse.add(mostSigBits));
		return new UUID(mostSigBits, 0);
	}

	protected int nextEntityCount() {
		Iterator<Integer> iterator = this.entityCountIds.iterator();
		if (iterator.hasNext()) {
			int entityId = iterator.next();
			iterator.remove();
			return entityId;
		}
		return this.entityCount.incrementAndGet();
	}

	public NPCRegistry<T> getDefaultRegistry() {
		return this.defaultRegistry;
	}

	public List<NPCRegistry<T>> getRegistries() {
		return this.registries;
	}

	public NPCPacketListener<T> getPacketListener() {
		return this.packetListener;
	}

	public NPCTabList<Core<?>> getTabList() {
		return this.tabList;
	}
}