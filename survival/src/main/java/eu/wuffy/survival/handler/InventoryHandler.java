package eu.wuffy.survival.handler;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.wuffy.core.IHandler;
import eu.wuffy.survival.Survival;

public class InventoryHandler extends IHandler<Survival> {

//	private static final PacketPlayOutChat ACTION_BAR_LOADING = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"§aDeine §2Spielerdaten §awerden §2geladen\"}"), ChatMessageType.GAME_INFO);
//	private static final PacketPlayOutChat ACTION_BAR_LOADED = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"§aDeine §2Spielerdaten §awurden §2geladen\"}"), ChatMessageType.GAME_INFO);

	private final List<Player> loaded = new LinkedList<Player>();
	private final List<Player> load = new LinkedList<Player>();

	private int loadScheduleId;

	public InventoryHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() { }

	@Override
	public void onEnable() { }

	@Override
	public void onDisable() { }

	public void onPlayerJoin(Player player) {
		synchronized (this.loaded) {
			this.loaded.remove(player);
		}

		synchronized (this.load) {
			this.load.add(player);
		}
	}

	public void onPlayerQuit(Player player) {
		synchronized (this.load) {
			this.load.remove(player);
		}

		synchronized (this.loaded) {
			this.loaded.remove(player);
		}

//		// Store
//		for (Statistic statistic : Statistic.values()) {
//			if (statistic.getType() == Type.ITEM) {
//				for (Material item : ItemFactory.MATERIAL_ITEMS) {
//				}
//			} else if (statistic.getType() == Type.BLOCK) {
//				for (Material block : ItemFactory.MATERIAL_BLOCKS) {
//					
//				}
//			} else if (statistic.getType() == Type.ENTITY) {
//				for (EntityType entityType : EntityType.values()) {
//					
//				}
//			} else {
//				
//			}
//		}
//
//		for (Advancement advancement : Core.ADVANCEMENT_LIST) {
//			for (String criteria : advancement.getCriteria()) {
//			}
//		}
	}

	public void loadScheduler() {
		if(!Bukkit.getScheduler().isCurrentlyRunning(this.loadScheduleId)) {
			this.loadScheduleId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getCore(), new Runnable() {
				
				@Override
				public void run() {
					Player player;
					synchronized (loaded) {
						synchronized (load) {
							if (load.isEmpty()) {
								Bukkit.getScheduler().cancelTask(loadScheduleId);
								return;
							}

							player = load.remove(0);

							if (player.isOnline()) {
								loaded.add(player);
							}
						}
					}
				}
			}, 2, 2);
		}
	}

	public boolean isLoaded(Player player) {
		return this.loaded.contains(player);
	}
}