package eu.wuffy.mobpvp.kits.blaze;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.kits.Kit;
import eu.wuffy.mobpvp.kits.KitInventory;
import eu.wuffy.mobpvp.kits.KitItems;
import eu.wuffy.mobpvp.kits.KitType;

public class KitBlaze extends Kit {

	public KitBlaze(MobPvP core) {
		super(core, KitType.BLAZE, new KitInventory());

		this.inventory.setArmorItem(2, new ItemFactory(Material.LEATHER_CHESTPLATE)
				.setColor(Color.YELLOW)
				.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, false)
				.addAllFlag()
				.build());
		this.inventory.setStorageItem(10, new ItemFactory(Material.BLAZE_ROD)
				.setDisplayName("§cFlamethrower")
				.addAllFlag()
				.build());
		this.inventory.setExtraItem(0, new ItemFactory(Material.BLAZE_ROD)
				.setDisplayName("§cFlamethrowerrrrrrr")
				.addAllFlag()
				.build());

		core.getServer().getPluginManager().registerEvents(this, this.core);
	}

	@Override
	protected void join(Player player) { }

	@Override
	protected void leave(Player player) { }

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (this.isPlayerKit(player)) {
			event.setCancelled(true);
			event.getBlock().setType(Material.BEDROCK);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!this.isRightClick(event.getAction()) || !this.canUseItem(event, KitItems.BLAZE_FLAMETHROWER)) {
			return;
		}

		player.sendMessage("§4Throw flames!");
	}
}