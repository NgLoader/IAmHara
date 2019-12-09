package eu.wuffy.mobpvp.kits.blaze;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.PlayerInventory;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.kits.Kit;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public class KitBlaze extends Kit {

	public KitBlaze(MobPvP core) {
		super(core, "§4Blaze", DisguiseType.BLAZE);
	}

	@Override
	protected void join(Player player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setChestplate(new ItemFactory(Material.LEATHER_CHESTPLATE)
				.setColor(Color.YELLOW)
				.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, false)
				.addAllFlag()
				.build());
	}

	@Override
	protected void leave(Player player) {
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (this.isPlayerKit(player)) {
			event.getBlock().setType(Material.BEDROCK);
		}
	}
}