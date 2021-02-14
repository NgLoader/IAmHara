package eu.wuffy.survival.event.bukkit;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.TreeFellerHandler;
import eu.wuffy.survival.handler.event.EventListener;

public class BlockBreakEventListener extends EventListener {

	private TreeFellerHandler treeFellerHandler;

	public BlockBreakEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.treeFellerHandler = this.getCore().getTreeFellerHandler();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (!player.isSneaking() && player.getGameMode() == GameMode.SURVIVAL && player.hasPermission("wuffy.tree.feller") && this.treeFellerHandler.isEnabledForPlayer(player)) {
			if (this.treeFellerHandler.onBlockBreak(event.getBlock(), player.getInventory().getItemInMainHand(), 149)) {
				event.setCancelled(true);
				return;
			}
		}
	}
}