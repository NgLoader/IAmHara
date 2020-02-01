package eu.wuffy.lobby.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import eu.wuffy.lobby.Lobby;
import eu.wuffy.synced.IHandler;

public class BuildHandler extends IHandler<Lobby> {

	private final List<Player> inBuildMode = new ArrayList<>();

	public BuildHandler(Lobby core) {
		super(core);
	}

	public void addToBuildMode(Player player) {
		this.inBuildMode.add(player);

		player.getInventory().clear();
		player.setGameMode(GameMode.CREATIVE);
	}

	public void removeFromBuildMode(Player player) {
		this.inBuildMode.remove(player);

		player.getInventory().clear();
		player.getInventory().setContents(this.core.getInventoryHandler().getInventory());
		player.setGameMode(GameMode.ADVENTURE);
	}

	public boolean isInBuildMode(Player player) {
		return this.inBuildMode.contains(player);
	}
}