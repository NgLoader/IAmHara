package eu.wuffy.lobby.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.lobby.Lobby;
import eu.wuffy.synced.ICore;
import eu.wuffy.synced.IHandler;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;

public class PlayerHandler extends IHandler<Lobby> implements Listener {

	private final InventoryHandler inventoryHandler;
	private final BuildHandler buildHandler;
	private final ScoreboardHandler scoreboardHandler;
	private final ChatHandler chatHandler;

	private UserManager userManager;

	public PlayerHandler(Lobby core) {
		super(core);

		this.inventoryHandler = this.core.getInventoryHandler();
		this.buildHandler = this.core.getBuildHandler();
		this.scoreboardHandler = this.core.getScoreboardHandler();
		this.chatHandler = this.core.getChatHandler();
	}

	@Override
	public void onEnable() {
		this.userManager = LuckPermsProvider.get().getUserManager();

		Bukkit.getServer().getPluginManager().registerEvents(this, this.core);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		try {
			player.getInventory().setContents(this.inventoryHandler.getInventory());
	
			this.scoreboardHandler.getTeams().forEach(team -> team.sendCreatePacket(player));
			this.userManager.loadUser(player.getUniqueId()).thenAcceptAsync(user -> this.scoreboardHandler.getPlayerScoreboard(player).joinTeam(user.getPrimaryGroup()));
			this.chatHandler.updateMessagePattern(player);
		} catch (Exception e) {
			e.printStackTrace();
			player.kickPlayer(ICore.PREFIX + "§7Es ist ein §cfehler §7beim laden deiner §cspielerdaten §7aufgetreten§8.");
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage(null);

		if (this.buildHandler.isInBuildMode(player)) {
			this.buildHandler.removeFromBuildMode(player);
		}

		this.scoreboardHandler.removePlayerScoreboard(player);
		this.chatHandler.removeMessagePattern(player);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);

		Bukkit.broadcastMessage(this.chatHandler.getMessagePattern(player)
				.replace("%p", player.getName())
				.replace("%m", player.hasPermission("wuffy.chat.color") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
	}
}
