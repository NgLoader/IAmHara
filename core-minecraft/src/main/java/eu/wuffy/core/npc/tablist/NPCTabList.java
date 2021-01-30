package eu.wuffy.core.npc.tablist;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.npc.entity.NPCPlayer;
import eu.wuffy.core.npc.tablist.NPCTabListInfo.NPCInfo;
import eu.wuffy.synced.IHandler;

public class NPCTabList <T extends Core<?>> extends IHandler<T> implements Runnable {

	private static final int MAX_PACKETS_SEND = 15;
	protected static final int DEFAULT_TRYS = 2;

	private final Map<Player, NPCTabListInfo> pending = new WeakHashMap<>();
	private final ProtocolManager protocolManager;

	public NPCTabList(T plugin) {
		super(plugin);
		this.protocolManager = ProtocolLibrary.getProtocolManager();

		Bukkit.getScheduler().runTaskTimer(this.core, this, 2, 2);
	}

	public void schedule(Player player, NPCPlayer npc) {
		this.schedule(player, npc, DEFAULT_TRYS);
	}

	public void schedule(Player player, NPCPlayer npc, int trys) {
		Bukkit.getScheduler().runTaskLater(this.core, () -> {
			NPCTabListInfo info = this.pending.remove(player);
			if (info == null) {
				this.pending.put(player, new NPCTabListInfo(npc, trys));
				return;
			}
			info.add(npc, trys);
		}, 2);
	}

	public void cancel(Player player, NPCPlayer npc) {
		NPCTabListInfo removerInfo = this.pending.get(player);
		if (removerInfo != null) {
			removerInfo.remove(npc);
		}
	}

	@Override
	public void run() {
		Iterator<Entry<Player, NPCTabListInfo>> iterator = this.pending.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Player, NPCTabListInfo> entry = iterator.next();
			NPCTabListInfo info = entry.getValue();
			Player player = entry.getKey();

			List<PlayerInfoData> removed = new ArrayList<>();
			Iterator<NPCInfo> npcIterator = info.toRemove.iterator();
			for (int i = 0; i < MAX_PACKETS_SEND; i++) {
				if (!npcIterator.hasNext()) {
					if (info.toRemove.isEmpty()) {
						iterator.remove();
					}
					break;
				}

				NPCInfo npcInfo = npcIterator.next();
				if (--npcInfo.trys < 1) {
					npcIterator.remove();
				}
				removed.add(new PlayerInfoData(npcInfo.npc.getGameProfile(), 0, NativeGameMode.NOT_SET, null));
			}

			if (player.isOnline()) {
				try {
					PacketContainer packetContainer = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
					packetContainer.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
					packetContainer.getPlayerInfoDataLists().write(0, removed);
					this.protocolManager.sendServerPacket(player, packetContainer);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}