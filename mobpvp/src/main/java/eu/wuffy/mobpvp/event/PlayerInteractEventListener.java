package eu.wuffy.mobpvp.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.event.EventListener;
import eu.wuffy.mobpvp.kits.Kit;
import eu.wuffy.mobpvp.kits.KitHandler;

public class PlayerInteractEventListener extends EventListener {

	private KitHandler kitHandler;

	public PlayerInteractEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.kitHandler = this.core.getKitHandler();
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();

			if (block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) block.getState();

				if (!sign.getLine(0).equals(MobPvP.PREFIX_SIGN)) {
					return;
				}

				Kit kit = this.kitHandler.searchKit(sign.getLine(1));

				if (kit != null) {
					this.kitHandler.select(player, kit);
				} else {
					player.sendMessage(MobPvP.PREFIX + "Dieses §ckit §7konnte derzeitig nicht gefunden werden§8.");
				}
			}
		}
	}
}
