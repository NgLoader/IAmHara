package eu.wuffy.core.npc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.event.NPCCreatedEvent;
import eu.wuffy.core.npc.event.NPCDeletedEvent;
import eu.wuffy.core.npc.event.NPCVisibilityChangeEvent;

public abstract class NPC {

	protected final Core<?> plugin;
	protected final NPCSystem<?> manager;
	protected final NPCRegistry<?> registry;
	protected final ProtocolManager protocolManager;

	protected final int entityId;

	protected NPCProperties properties = new NPCProperties();

	protected Location location;
	protected double range = 40 * 40;

	protected Set<Player> inRange = new HashSet<>();
	protected Set<Player> viewers = new HashSet<>();

	protected List<PacketContainer> spawnPackets = new ArrayList<>();
	protected List<PacketContainer> despawnPackets = new ArrayList<>();
	private boolean dirty;

	private boolean created;

	public NPC(NPCRegistry<?> registry) {
		this.registry = registry;
		this.plugin = this.registry.getCore();
		this.manager = this.registry.getNpcSystem();
		this.protocolManager = ProtocolLibrary.getProtocolManager();

		this.entityId = this.manager.nextEntityCount();
		this.manager.npcByEntityId.put(this.entityId, this);

		this.registry.npcs.add(this);
	}

	//TODO remove unchecked suppress warning
	@SuppressWarnings("unchecked")
	public <T extends NPC> T create() {
		if (this.created) {
			return (T) this;
		}
		this.created = true;
		this.dirty = false;

		this.spawnPackets.clear();
		this.despawnPackets.clear();
		this.createSpawnPackets();
		this.createDespawnPackets();

		Bukkit.getPluginManager().callEvent(new NPCCreatedEvent(this));
		return (T) this;
	}

	protected abstract void createSpawnPackets();
	protected abstract void createDespawnPackets();

	protected void onSpawn(Player player) { }
	protected void onDespawn(Player player) { }

	public abstract void updateLocation();

	protected void spawn(Player player) {
		if (this.dirty) {
			this.spawnPackets.clear();
			this.createSpawnPackets();
			this.dirty = false;
		}
		this.sendPacket(player, this.spawnPackets);
		this.onSpawn(player);
	}

	protected void despawn(Player player) {
		this.sendPacket(player, this.despawnPackets);
		this.onDespawn(player);
	}

	public void respawn() {
		this.inRange.forEach(player -> this.respawn(player));
	}

	public void respawn(Player player) {
		this.despawn(player);
		this.spawn(player);
	}

	public void hide(Player player) {
		Bukkit.getPluginManager().callEvent(new NPCVisibilityChangeEvent(player, this, false));

		if (this.viewers.remove(player) && this.inRange.remove(player)) {
			this.despawn(player);
		} else {
			this.inRange.remove(player);
		}
	}

	public void show(Player player) {
		Bukkit.getPluginManager().callEvent(new NPCVisibilityChangeEvent(player, this, true));

		if (this.viewers.add(player) && this.isInRange(player.getLocation())) {
			this.inRange.add(player);
			this.spawn(player);
		}
	}

	public void checkInRange(Player player) {
		if (this.viewers.contains(player)) {
			this._checkInRange(player);
		}
	}

	private void _checkInRange(Player player) {
		boolean inRange = this.isInRange(player.getLocation());
		if (!inRange && this.inRange.remove(player)) {
			this.despawn(player);
			return;
		}
		if (inRange && this.inRange.add(player)) {
			this.spawn(player);
			return;
		}
	}

	public void checkInRange() {
		this.viewers.forEach(this::_checkInRange);
	}

	public boolean isInRange(Location location) {
		return this.location.distanceSquared(location) < this.range;
	}

	protected void sendPacket(PacketContainer... packets) {
		this.inRange.forEach(player -> this.sendPacket(player, packets));
	}

	protected void sendPacket(Player player, PacketContainer... packets) {
		try {
			for (PacketContainer packet : packets) {
				this.protocolManager.sendServerPacket(player, packet);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	protected void sendPacket(List<PacketContainer> packets) {
		this.inRange.forEach(player -> this.sendPacket(player, packets));
	}

	protected void sendPacket(Player player, List<PacketContainer> packets) {
		try {
			for (PacketContainer packet : packets) {
				this.protocolManager.sendServerPacket(player, packet);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		Bukkit.getPluginManager().callEvent(new NPCDeletedEvent(this));

		this.registry.npcs.remove(this);
		this.manager.npcByEntityId.remove(this.entityId);
		this.viewers.forEach(this::hide);
		this.viewers.clear();
		this.inRange.clear();
		this.spawnPackets.clear();
		this.despawnPackets.clear();
		this.manager.entityCountIds.add(this.entityId);
	}

	public boolean isCreated() {
		return this.created;
	}

	public void setDirty() {
		this.dirty = true;
	}

	public double getRange() {
		return this.range;
	}

	public void setRange(double range) {
		this.range = range * range;
		this.checkInRange();
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
		this.updateLocation();
	}

	public Set<Player> getInRange() {
		return this.inRange;
	}

	public Set<Player> getVisible() {
		return this.viewers;
	}

	public NPCProperties getProperties() {
		return this.properties;
	}

	public NPCSystem<?> getManager() {
		return this.manager;
	}

	public int getEntityId() {
		return this.entityId;
	}
}
