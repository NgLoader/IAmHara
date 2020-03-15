package eu.wuffy.disco.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import eu.wuffy.disco.Disco;
import eu.wuffy.disco.event.BuildModeChangeEvent;
import eu.wuffy.disco.listener.InventoryHandler.InventoryEnum;
import eu.wuffy.synced.IHandler;

public class BuildHandler extends IHandler<Disco> {

	private final List<Player> inBuildMode = new ArrayList<>();

	public BuildHandler(Disco core) {
		super(core);
	}

	public void addToBuildMode(Player player) {
		this.inBuildMode.add(player);

		player.getInventory().clear();
		player.setGameMode(GameMode.CREATIVE);

		Bukkit.getScheduler().runTask(this.core,
				() -> Bukkit.getPluginManager().callEvent(new BuildModeChangeEvent(player, true)));
	}

	public void removeFromBuildMode(Player player) {
		this.inBuildMode.remove(player);

		player.getInventory().clear();
		player.getInventory().setContents(this.core.getInventoryHandler().getInventory(InventoryEnum.DEFAULT));
		player.setGameMode(GameMode.ADVENTURE);

		Bukkit.getScheduler().runTask(this.core,
				() -> Bukkit.getPluginManager().callEvent(new BuildModeChangeEvent(player, false)));
	}

	public boolean isInBuildMode(Player player) {
		return this.inBuildMode.contains(player);
	}
}