package eu.wuffy.survival.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import eu.wuffy.core.util.NMSUtil;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.command.admin.CommandAdminTool;
import eu.wuffy.synced.IHandler;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;

public class VanishHandler extends IHandler<Survival> {

	private static final PacketPlayOutChat ACTION_BAR_IN_VANISH = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"§aDu bist §2unsichtbar\"}"), ChatMessageType.GAME_INFO);

	private final List<Player> hiddenPlayers = new ArrayList<Player>();

	private final Map<Player, GameMode> playerGamemode = new HashMap<Player, GameMode>();
	private final Map<Player, ItemStack[][]> playerInventory = new HashMap<Player, ItemStack[][]>();

	private int scheduleId;

	public VanishHandler(Survival core) {
		super(core);
	}

	@Override
	public void onDisable() {
		this.hiddenPlayers.forEach(hidden -> this.removePlayer(hidden));
	}

	public void onPlayerJoin(Player player) {
		if (this.hiddenPlayers.size() > 0)
			this.checkTabVisibilityForPlayer(player, false);
	}

	public void onPlayerQuit(Player player) {
		if (this.isVanish(player))
			this.removePlayer(player);
	}

	public boolean togglePlayer(Player player) {
		if (this.hiddenPlayers.contains(player)) {
			this.removePlayer(player);
			return false;
		}

		this.addPlayer(player);
		return true;
	}

	public void addPlayer(Player player) {
		if (this.hiddenPlayers.contains(player))
			return;

		this.hiddenPlayers.add(player);

		this.getCore().getDynmapHandler().getDynmap().setPlayerVisiblity(player, false);

		PlayerInventory inventory = player.getInventory();
		ItemStack[][] storage = new ItemStack[4][];

		storage[0] = inventory.getContents();
		storage[1] = inventory.getArmorContents();
		storage[2] = inventory.getExtraContents();
		storage[3] = inventory.getStorageContents();

		this.playerInventory.put(player, storage);
		this.playerGamemode.put(player, player.getGameMode());

		player.getInventory().clear();
		CommandAdminTool.setAdminTool(inventory);

		player.setGameMode(GameMode.SPECTATOR);

		this.makePlayerInTabInvisible(player);
		this.startScheduler();
	}

	public void removePlayer(Player player) {
		if (!this.hiddenPlayers.contains(player))
			return;

		this.hiddenPlayers.remove(player);

		this.getCore().getDynmapHandler().getDynmap().setPlayerVisiblity(player, true);

		player.setGameMode(this.playerGamemode.remove(player));

		PlayerInventory inventory = player.getInventory();
		ItemStack[][] storage = playerInventory.remove(player);

		inventory.clear();
		inventory.setContents(storage[0]);
		inventory.setArmorContents(storage[1]);
		inventory.setExtraContents(storage[2]);
		inventory.setStorageContents(storage[3]);

		this.makePlayerInTabVisible(player);
	}

	public void makePlayerInTabInvisible(Player player) {
		PacketPlayOutPlayerInfo visiblePacket = NMSUtil.addToTabList(player, player.getDisplayName() + " §8[§cVanish§8]§7");
		PacketPlayOutPlayerInfo hidePacket = NMSUtil.addToTabList(player, player.getDisplayName());

		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.hasPermission("wuffy.vanish.see.other") || player.equals(all) || this.hiddenPlayers.contains(all)) {
				all.showPlayer(this.getCore(), player);
				NMSUtil.sendPacket(all, visiblePacket);
			} else {
				all.hidePlayer(this.getCore(), player);
				NMSUtil.sendPacket(all, hidePacket);
			}
		}
	}

	public void makePlayerInTabVisible(Player player) {
		NMSUtil.sendPacket(NMSUtil.addToTabList(player, player.getDisplayName()));

		for (Player all : Bukkit.getOnlinePlayers()) {
			all.showPlayer(this.getCore(), player);
		}
	}

	public void checkTabVisibilityForPlayer(Player player, boolean force) {
		if (force)
			Bukkit.getOnlinePlayers().forEach(all -> NMSUtil.sendPacket(player, NMSUtil.addToTabList(all, all.getName())));

		for (Player hidden : this.hiddenPlayers) {
			if (player.hasPermission("wuffy.vanish.see.other") || hidden.equals(player) || this.hiddenPlayers.contains(player)) {
				player.showPlayer(this.getCore(), hidden);
				NMSUtil.sendPacket(player, NMSUtil.addToTabList(player, player.getDisplayName() + " §8[§cVanish§8]§7"));
			} else {
				player.hidePlayer(this.getCore(), hidden);
				NMSUtil.sendPacket(player, NMSUtil.addToTabList(player, player.getDisplayName()));
			}
		}
	}

	public void hardCheckVisible() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			NMSUtil.sendPacket(NMSUtil.addToTabList(all, all.getDisplayName()));

			for (Player hidden : this.hiddenPlayers) {
				if (all.hasPermission("wuffy.vanish.see.other") || hidden.equals(all)) {
					all.showPlayer(this.getCore(), hidden);
					NMSUtil.sendPacket(all, NMSUtil.addToTabList(hidden, hidden.getDisplayName() + " §8[§cVanish§8]§7"));
				} else {
					all.hidePlayer(this.getCore(), hidden);
					NMSUtil.sendPacket(all, NMSUtil.addToTabList(hidden, hidden.getDisplayName()));
				}
			}
		}
	}

	public void startScheduler() {
		if(!Bukkit.getScheduler().isCurrentlyRunning(this.scheduleId)) {
			this.scheduleId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getCore(), new Runnable() {
				
				@Override
				public void run() {
					if (hiddenPlayers.isEmpty()) {
						Bukkit.getScheduler().cancelTask(scheduleId);
						scheduleId = -1;
					} else {
						for (Player player : hiddenPlayers) {
							NMSUtil.sendPacket(player, VanishHandler.ACTION_BAR_IN_VANISH);

							for (Player all : Bukkit.getOnlinePlayers()) {
								if (!player.equals(all) && all.hasPermission("wuffy.vanish.see.other")) {
									all.spawnParticle(Particle.FLAME, player.getLocation().add(0, player.getGameMode() == GameMode.SPECTATOR ? 1.2 : 2.2, 0), 0, 0D, -0.01D, 0D);
								}
							}
						}
					}
				}
			}, 2, 2);
		}
	}

	public boolean isVanish(Player player) {
		return this.hiddenPlayers.contains(player);
	}
}