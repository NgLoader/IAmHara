package eu.wuffy.survival.common;

import org.bukkit.Material;

import eu.wuffy.core.help.HelpCategory;
import eu.wuffy.core.util.ItemFactory;

public enum SurvivalHelpCategory {

	RULES(new ItemFactory(Material.WRITTEN_BOOK).setDisplayName("§6Regeln").addAllFlag()),
	COMMAND(new ItemFactory(Material.COMMAND_BLOCK).setDisplayName("§dCommands").addAllFlag()),
	HOME(new ItemFactory(Material.RED_BED).setDisplayName("§aHomes").addLore("§eListe and befehlen für homes").addAllFlag()),
	TREE_FELLER(new ItemFactory(Material.OAK_LOG).setDisplayName("§aBaum §2Fäller").addLore("§7Nutze§8: §e/treefeller").addLore("§7Um denn §amodus §7zu §awechseln").addAllFlag()),
	PVP(new ItemFactory(Material.DIAMOND_SWORD).setDisplayName("§4P§cv§4P").addAllFlag());

	private HelpCategory category;

	private SurvivalHelpCategory(ItemFactory item) {
		this.category = new HelpCategory(item);
	}

	public HelpCategory getCategory() {
		return this.category;
	}
}