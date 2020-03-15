package eu.wuffy.lobby;

import org.bukkit.Bukkit;

import eu.wuffy.core.Core;
import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.hologram.HologramHandler;
import eu.wuffy.core.portal.PortalHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.lobby.command.CommandBuild;
import eu.wuffy.lobby.database.LobbyDatabase;
import eu.wuffy.lobby.handler.BuildHandler;
import eu.wuffy.lobby.handler.DisallowHandler;
import eu.wuffy.lobby.handler.InventoryHandler;
import eu.wuffy.lobby.handler.LobbyHologramHandler;
import eu.wuffy.lobby.handler.LobbyPortalHandler;
import eu.wuffy.lobby.handler.LuckPermsHandler;
import eu.wuffy.lobby.handler.PlayerHandler;
import eu.wuffy.lobby.handler.TeleportHandler;
import eu.wuffy.synced.IHandler;

public class Lobby extends Core<LobbyDatabase> {

	public static final String PREFIX = "§8[§2Lobby§8] ";

	private final ScoreboardHandler scoreboardHandler;
	private final ChatHandler chatHandler;
	private final DisallowHandler disallowHandler;
	private final TeleportHandler teleportHandler;
	private final InventoryHandler inventoryHandler;
	private final PlayerHandler playerHandler;
	private final BuildHandler buildHandler;
	private final PortalHandler portalHandler;
	private final HologramHandler hologramHandler;
	private final LuckPermsHandler luckPermsHandler;

	public Lobby() {
		this.scoreboardHandler = new ScoreboardHandler(this);
		this.chatHandler = new ChatHandler(this);
		this.buildHandler = new BuildHandler(this);
		this.disallowHandler = new DisallowHandler(this);
		this.teleportHandler = new TeleportHandler(this);
		this.inventoryHandler = new InventoryHandler(this);
		this.playerHandler = new PlayerHandler(this);
		this.portalHandler = new LobbyPortalHandler(this);
		this.hologramHandler = new LobbyHologramHandler(this);
		this.luckPermsHandler = new LuckPermsHandler(this);
	}

	@Override
	public void onLoad() {
		IHandler.getHandlers().forEach(IHandler::init);
	}

	@Override
	public void onEnable() {
		IHandler.getHandlers().forEach(IHandler::enable);

		getCommand("buildmode").setExecutor(new CommandBuild(this));

		Bukkit.setWhitelist(false);
		Bukkit.getConsoleSender().sendMessage(Lobby.PREFIX + "§2Enabled§8!");
	}

	@Override
	public void onDisable() {
		Bukkit.setWhitelist(true);

		IHandler.destroy();
		Bukkit.getScheduler().cancelTasks(this);

		this.getDatabase().closeConnection();

		Bukkit.getConsoleSender().sendMessage(Lobby.PREFIX + "§4Disabled§8!");
	}

	public ScoreboardHandler getScoreboardHandler() {
		return this.scoreboardHandler;
	}

	public ChatHandler getChatHandler() {
		return this.chatHandler;
	}

	public DisallowHandler getDisallowHandler() {
		return this.disallowHandler;
	}

	public TeleportHandler getTeleportHandler() {
		return this.teleportHandler;
	}

	public InventoryHandler getInventoryHandler() {
		return this.inventoryHandler;
	}

	public PlayerHandler getPlayerHandler() {
		return this.playerHandler;
	}

	public BuildHandler getBuildHandler() {
		return this.buildHandler;
	}

	public PortalHandler getPortalHandler() {
		return this.portalHandler;
	}

	public HologramHandler getHologramHandler() {
		return this.hologramHandler;
	}

	public LuckPermsHandler getLuckPermsHandler() {
		return this.luckPermsHandler;
	}
}