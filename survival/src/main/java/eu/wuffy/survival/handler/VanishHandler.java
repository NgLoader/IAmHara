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

import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.core.util.PlayerUtil;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.command.admin.CommandAdminTool;
import eu.wuffy.synced.IHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;

public class VanishHandler extends IHandler<Survival> {

	private final List<Player> hiddenPlayers = new ArrayList<Player>();

	private final Map<Player, GameMode> playerGamemode = new HashMap<Player, GameMode>();
	private final Map<Player, ItemStack[][]> playerInventory = new HashMap<Player, ItemStack[][]>();

	private LuckPerms luckPerms;

	private int scheduleId;

	public VanishHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.luckPerms = LuckPermsProvider.get();
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

		this.makePlayerInTabInvisible(player);
		this.startScheduler();

		player.setGameMode(GameMode.SPECTATOR);
	}

	public void removePlayer(Player player) {
		if (!this.hiddenPlayers.contains(player))
			return;

		this.hiddenPlayers.remove(player);

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
		Group group = this.luckPerms.getGroupManager().getGroup(this.luckPerms.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());
		String prefix = GroupUtil.getGroupPrefix(group).orElseGet(() -> "§a");
		String color = GroupUtil.getGroupMetaSorted(group, "tablist-color", "a");
		PacketPlayOutPlayerInfo visiblePacket = PlayerUtil.addToTabList(player, String.format("%s§%s%s §8[§cV§8]", prefix, color, player.getDisplayName()));
		PacketPlayOutPlayerInfo hidePacket = PlayerUtil.addToTabList(player, String.format("%s§%s%s", prefix, color, player.getDisplayName()));

		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.hasPermission("wuffy.vanish.see.other") || player.equals(all) || this.hiddenPlayers.contains(all)) {
				all.showPlayer(this.getCore(), player);
				PlayerUtil.sendPacket(all, visiblePacket);
			} else {
				all.hidePlayer(this.getCore(), player);
				PlayerUtil.sendPacket(all, hidePacket);
			}
		}
	}

	public void makePlayerInTabVisible(Player player) {
		Group group = this.luckPerms.getGroupManager().getGroup(this.luckPerms.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());
		String prefix = GroupUtil.getGroupPrefix(group).orElseGet(() -> "§a");
		String color = GroupUtil.getGroupMetaSorted(group, "tablist-color", "a");

		PlayerUtil.sendPacket(Bukkit.getOnlinePlayers(), PlayerUtil.addToTabList(player, String.format("%s§%s%s", prefix, color, player.getDisplayName())));
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.showPlayer(this.getCore(), player);
		}
	}

	public void checkTabVisibilityForPlayer(Player player, boolean force) {
//		Group group = this.luckPerms.getGroupManager().getGroup(this.luckPerms.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());
//		String prefix = GroupUtil.getGroupPrefix(group).orElseGet(() -> "§a");
//		String color = GroupUtil.getGroupMetaSorted(group, "tablist-color", "a");
//		PacketPlayOutPlayerInfo visiblePacket = PlayerUtil.addToTabList(player, String.format("%s§%s%s §8[§cV§8]§7", prefix, color, player.getDisplayName()));
//		PacketPlayOutPlayerInfo hidePacket = PlayerUtil.addToTabList(player, String.format("%s§%s%s", prefix, color, player.getDisplayName()));
//
//		if (force) {
//			Bukkit.getOnlinePlayers().forEach(all -> PlayerUtil.sendPacket(player, PlayerUtil.addToTabList(all, all.getName())));
//		}
//
//		for (Player hidden : this.hiddenPlayers) {
//			if (player.hasPermission("wuffy.vanish.see.other") || hidden.equals(player) || this.hiddenPlayers.contains(player)) {
//				player.showPlayer(this.getCore(), hidden);
//				PlayerUtil.sendPacket(player, visiblePacket);
//			} else {
//				player.hidePlayer(this.getCore(), hidden);
//				PlayerUtil.sendPacket(player, hidePacket);
//			}
//		}
	}

	public void hardCheckVisible() {
//		for (Player all : Bukkit.getOnlinePlayers()) {
//			PlayerUtil.sendPacket(Bukkit.getOnlinePlayers(), PlayerUtil.addToTabList(all, all.getDisplayName()));
//
//			for (Player hidden : this.hiddenPlayers) {
//				Group group = this.luckPerms.getGroupManager().getGroup(this.luckPerms.getUserManager().getUser(hidden.getUniqueId()).getPrimaryGroup());
//				String prefix = GroupUtil.getGroupPrefix(group).orElseGet(() -> "§a");
//				String color = GroupUtil.getGroupMetaSorted(group, "tablist-color", "a");
//				PacketPlayOutPlayerInfo visiblePacket = PlayerUtil.addToTabList(hidden, String.format("%s§%s%s §8[§cV§8]§7", prefix, color, hidden.getDisplayName()));
//				PacketPlayOutPlayerInfo hidePacket = PlayerUtil.addToTabList(hidden, String.format("%s§%s%s", prefix, color, hidden.getDisplayName()));
//
//				if (all.hasPermission("wuffy.vanish.see.other") || hidden.equals(all)) {
//					all.showPlayer(this.getCore(), hidden);
//					PlayerUtil.sendPacket(all, visiblePacket);
//				} else {
//					all.hidePlayer(this.getCore(), hidden);
//					PlayerUtil.sendPacket(all, hidePacket);
//				}
//			}
//		}
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
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aDu bist §2unsichtbar"));

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