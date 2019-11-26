package eu.wuffy.survival.handler.scoreboard;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_14_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.core.util.NMSUtil;
import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.IScoreboardCriteria;
import net.minecraft.server.v1_14_R1.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_14_R1.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_14_R1.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_14_R1.ScoreboardObjective;

public class ScoreboardHandler extends IHandler<Survival> {

	private Scoreboard scoreboard;
	private ScoreboardObjective objective;

	private Map<UUID, Map<String, SurvivalObjective>> objectiveByIdentifier = new HashMap<UUID, Map<String, SurvivalObjective>>();
	private Map<Group, String> prefixByGroup = new HashMap<Group, String>();
	private Map<Group, ChatColor> chatColorByGroup = new HashMap<Group, ChatColor>();
	private Map<Player, Long> needScoreboardUpdate = new ConcurrentHashMap<Player, Long>();

	public ScoreboardHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		this.objective = new ScoreboardObjective(((CraftScoreboard) this.scoreboard).getHandle(),
				"abc",
				IScoreboardCriteria.DUMMY,
				new ChatComponentText("§2I§aam§2Ha§aRa§7.§2de"),
				EnumScoreboardHealthDisplay.INTEGER);

		this.scoreboard.getObjectives().forEach(objective -> objective.unregister());

		LuckPermsApi luckPermsApi = this.getCore().getLuckPermsHandler().getApi().get();

		while(luckPermsApi.getGroupManager().loadAllGroups().isDone());
		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Loaded §a" + luckPermsApi.getGroups().size() + " §7groups§8.");

		this.loadAllGroups();

		Bukkit.getScheduler().runTaskTimerAsynchronously(this.getCore(), new Runnable() {
			
			@Override
			public void run() {
				long currentTimeMillis = System.currentTimeMillis();

				for (Map.Entry<Player, Long> entry : needScoreboardUpdate.entrySet()) {
					if (entry.getValue() < currentTimeMillis) {
						Player player = entry.getKey();
						needScoreboardUpdate.remove(player);

						if (player.isOnline()) {
							Bukkit.getOnlinePlayers().forEach(online -> NMSUtil.sendPacket(player, NMSUtil.addToTabList(online, online.getCustomName())));
						}
					}
				}
			}
		}, 0, 20);
	}

	public void onPlayerJoin(Player player, Group group) {
		try {
			player.setCustomName(this.getTeamPrefix(group) + player.getName());

			NMSUtil.sendPacket(NMSUtil.addToTabList(player, player.getCustomName()));
			this.needScoreboardUpdate.put(player, System.currentTimeMillis() + 500);

			this.createObjectiveScoreboard(player);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void onPlayerQuit(OfflinePlayer player) {
		this.objectiveByIdentifier.remove(player.getUniqueId());
	}

	public void updateFaction(Player player) {
		FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
		if (fPlayer.hasFaction()) {
			Faction faction = fPlayer.getFaction();

			this.createObjective(player, "factionSpacer", " ", 5);
			this.createObjective(player, "faction", "§9Faction§7: §a" + faction.getTag(), 4);
			this.addObjective(player, new SurvivalObjectiveFormat(this.objective, player, "factionOnline", 3, "§7- §9Online§7: §a%s§7/§c%s", faction.getOnlinePlayers().size(), faction.getFPlayers().size()));
			this.addObjective(player, new SurvivalObjectiveFormat(this.objective, player, "factionBank", 2, "§7- §9Bank§7: §e%s", this.getCore().getVaultHandler().getEconomy().format(10.42)));
		} else {
			
		}
	}

	public void updateMoney(Player player) {
		this.createObjective(player, null, "  ", 1);
		this.addObjective(player, new SurvivalObjectiveFormat(this.objective, player, "money", 0, "§9Geld§8: §e%s", this.getCore().getVaultHandler().getEconomy().format(this.getCore().getVaultHandler().getEconomy().getBalance(player))));
	}

	public void createObjectiveScoreboard(Player player) {
		this.objectiveByIdentifier.put(player.getUniqueId(), new HashMap<String, SurvivalObjective>());

		PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjectiveDelete = new PacketPlayOutScoreboardObjective(this.objective, 1);
		PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjectiveCreate = new PacketPlayOutScoreboardObjective(this.objective, 0);
		PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective = new PacketPlayOutScoreboardDisplayObjective(1, this.objective);

		NMSUtil.sendPacket(player,
				packetPlayOutScoreboardObjectiveDelete,
				packetPlayOutScoreboardObjectiveCreate,
				packetPlayOutScoreboardDisplayObjective);
	}

	public SurvivalObjective createObjective(Player player, String identifier, String scoreValue, int score) {
		SurvivalObjective objective = getObjective(player, identifier);

		if (objective != null) {
			this.removeObjective(objective);
		}

		this.addObjective(objective = new SurvivalObjective(this.objective, player, identifier, scoreValue, score), identifier);
		return objective;
	}

	public void reScoreAll(Player player) {
		int currentScore = 0;
		for(SurvivalObjective objective : this.objectiveByIdentifier.get(player.getUniqueId()).values().stream()
				.sorted(Comparator.comparingInt(SurvivalObjective::getScore))
				.collect(Collectors.toList())) {
			objective.updateScore(currentScore++);
		}
	}

	public SurvivalObjective addObjective(Player player, SurvivalObjective objective) {
		if (objective == null) {
			return null;
		}

		return this.addObjective(objective, objective.identifier);
	}

	public SurvivalObjective addObjective(SurvivalObjective objective, String identifier) {
		this.objectiveByIdentifier.get(objective.uuid).put(identifier, objective);
		objective.update();

		return objective;
	}

	public SurvivalObjective getObjective(Player player, String identifier) {
		return this.objectiveByIdentifier.get(player.getUniqueId()).get(identifier);
	}

	public boolean hasObjective(Player player, String identifier) {
		return this.objectiveByIdentifier.get(player.getUniqueId()).containsKey(identifier);
	}

	public void removeObjective(Player player, String identifier) {
		SurvivalObjective objective = this.objectiveByIdentifier.get(player.getUniqueId()).remove(identifier);

		if (objective != null) {
			objective.remove();
		}
	}

	public void removeObjective(SurvivalObjective objective) {
		this.objectiveByIdentifier.get(objective.uuid).remove(objective.identifier);
		objective.remove();
	}

	public void createTeamPrefix(Group group) {
		this.prefixByGroup.put(group, this.getTeamSorted(group) + ChatColor.translateAlternateColorCodes('&', GroupUtil.getGroupPrefix(group).orElseGet(() -> GroupUtil.getGroupMeta(group, "color", "§a") + group.getFriendlyName())));
		this.chatColorByGroup.put(group, ChatColor.getByChar(GroupUtil.getGroupMetaSorted(group, "tablist-color", "a")));
	}

	public String getTeamPrefix(Group group) {
		String prefix = this.prefixByGroup.get(group);

		if (prefix == null) {
			this.createTeamPrefix(group);
			prefix = this.prefixByGroup.get(group);
		}

		return prefix;
	}

	public String getTeamSorted(Group group) {
		String teamName = "§" + "ABCEDFGHIJKLMNOPQRSTUFWXYZ".charAt(25 - group.getWeight().orElseGet(() -> 25)); // + "_" + group.getName().toUpperCase();
		if(teamName.length() > 16)
			teamName = teamName.substring(0, 16);
		return teamName;
	}

	public void loadAllGroups() {
		LuckPermsApi luckPermsApi = this.getCore().getLuckPermsHandler().getApi().get();

		this.prefixByGroup.clear();
		this.chatColorByGroup.clear();
		for(Group group : luckPermsApi.getGroups())
			this.createTeamPrefix(group);

		Bukkit.getOnlinePlayers().forEach(player -> this.onPlayerJoin(player, luckPermsApi.getGroup(luckPermsApi.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup())));
	}

	public ScoreboardObjective getObjective() {
		return this.objective;
	}
}