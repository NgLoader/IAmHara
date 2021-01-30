package eu.wuffy.core.npc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.event.PlayerInteractNPCEvent;

public class NPCPacketListener<T extends Core<?>> extends PacketAdapter {

	private final NPCSystem<T> system;
	private final ProtocolManager protocolManager;
	private final PluginManager pluginManager;

	public NPCPacketListener(NPCSystem<T> system) {
		super(system.getCore(), PacketType.Play.Client.USE_ENTITY);
		this.system = system;
		this.pluginManager = Bukkit.getPluginManager();
		this.protocolManager = ProtocolLibrary.getProtocolManager();

		this.protocolManager.addPacketListener(this);
	}

	public void unregister() {
		this.protocolManager.removePacketListener(this);
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		NPC npc = this.system.npcByEntityId.get(packet.getIntegers().read(0));
		if (npc != null) {
			event.setCancelled(true);

			Bukkit.getScheduler().runTask(this.plugin, () -> {
				EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
				if (action == EntityUseAction.ATTACK) {
					this.pluginManager.callEvent(new PlayerInteractNPCEvent(event.getPlayer(), npc, action, null));
				} else if (action == EntityUseAction.INTERACT) {
					this.pluginManager.callEvent(
							new PlayerInteractNPCEvent(event.getPlayer(), npc, action, packet.getHands().read(0)));
				}
			});
		}
	}
}