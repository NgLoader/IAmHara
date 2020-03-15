package eu.wuffy.disco.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import eu.wuffy.disco.Disco;
import eu.wuffy.disco.event.BuildModeChangeEvent;
import eu.wuffy.disco.listener.InventoryHandler.InventoryEnum;
import eu.wuffy.synced.IHandler;
import fr.cocoraid.prodigynightclub.area.customevents.EnterNightClubEvent;
import fr.cocoraid.prodigynightclub.area.customevents.QuitNightClubEvent;
import fr.cocoraid.prodigynightclub.nightclub.NightClub;

public class ProdigyNightClubHandler extends IHandler<Disco> implements Listener {

	private final BuildHandler buildHandler;
	private final InventoryHandler inventoryHandler;

	public ProdigyNightClubHandler(Disco core) {
		super(core);

		this.buildHandler = this.core.getBuildHandler();
		this.inventoryHandler = this.core.getInventoryHandler();
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this.core);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNightClubJoin(EnterNightClubEvent event) {
		Player player = event.getPlayer();

		if (this.buildHandler.isInBuildMode(player)) {
			event.setCancelled(true);
			event.getNightclub().getArea().getEntered().remove(player);
			return;
		}

		if (player.hasPermission("disco.gui")) {
			player.getInventory().setContents(this.inventoryHandler.getInventory(InventoryEnum.ADMIN_IN_DISCO));
		}
	}

	@EventHandler()
	public void onNightClubQuit(QuitNightClubEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission("disco.gui") && !this.buildHandler.isInBuildMode(player)) {
			player.getInventory().setContents(this.inventoryHandler.getInventory(InventoryEnum.DEFAULT));
		}
	}

	@EventHandler()
	public void onBuildModeChange(BuildModeChangeEvent event) {
		Player player = event.getPlayer();
		NightClub nightClub = NightClub.getNightclubs().get(player.getWorld());

		if (nightClub == null) {
			return;
		}

		if (event.isEnabled()) {
			if (nightClub.getArea().getEntered().contains(player)) {
				nightClub.getArea().getEntered().remove(player);
			}
			return;
		}

		if (nightClub.getArea().getEntered().contains(player)) {
			if (player.hasPermission("disco.gui")) {
				player.getInventory().setContents(this.inventoryHandler.getInventory(InventoryEnum.ADMIN_IN_DISCO));
				return;
			}

			player.getInventory().setContents(this.inventoryHandler.getInventory(InventoryEnum.DEFAULT));
		}
	}
}