package eu.wuffy.core.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.wuffy.core.Core;
import eu.wuffy.core.inventory.InventorySound;
import eu.wuffy.core.inventory.inventory.paginator.CustomPaginatorInventory;
import eu.wuffy.core.inventory.inventory.paginator.PageElement;
import eu.wuffy.core.inventory.inventory.paginator.PaginatorInventory;
import eu.wuffy.core.inventory.listener.ItemAction;
import eu.wuffy.synced.IHandler;

public class HelpSystem extends IHandler<Core<?>> {

	private final Map<HelpCategory, PaginatorInventory> categorys = new HashMap<>();

	private PaginatorInventory inventory = new PaginatorInventory("§aHelp");

	protected String prefix;

	public HelpSystem(Core<?> plugin, String prefix) {
		super(plugin);
		this.prefix = prefix;
	}

	public void addCategory(HelpCategory category) {
		PaginatorInventory inventory = this.categorys.get(category);
		if (inventory == null) {
			inventory = new PaginatorInventory(this.inventory.getName() + " §7- " + category.getTitle());
			inventory.setParent(this.inventory.getPage(0), null);
			categorys.put(category, inventory);

			this.inventory.addItem(
					new PageElement(category.getItem(),
					ItemAction.immovable((playerInventory, customInventory, action, event) -> {
						PaginatorInventory categoryInventory = this.categorys.get(category);
						if (categoryInventory.getItems().isEmpty()) {
							InventorySound.playDenySound(playerInventory.getPlayer());
						} else {
							playerInventory.open(categoryInventory.getPage(0));
						}
					})));
		}
	}

	public void addHelp(HelpCategory category, IHelp help) {
		StringBuilder stringBuilder = new StringBuilder();
		if (!help.getDescription().isEmpty()) {
			stringBuilder.append(this.prefix + "§8[§7]§a§l-----§e{ §a" + help.getName() + " §e}§a§l-----§7[§8]\n");
			stringBuilder.append(this.prefix + "\n");

			StringBuilder lines = new StringBuilder(this.prefix);
			for (String line : help.getDescription().split(" ")) {
				lines.append(line);
				lines.append(" ");

				if (lines.toString().length() > 60) {
					stringBuilder.append(lines.append("\n").toString());
					lines = new StringBuilder(this.prefix);
				}
			}
			if (!lines.toString().isEmpty()) {
				stringBuilder.append(lines.append("\n").toString());
			}

			stringBuilder.append(this.prefix + "\n");
			stringBuilder.append(this.prefix + "§8[§7]§a§l-----§e{ §a" + help.getName() + " §e}§a§l-----§7[§8]");
		}
		String message = stringBuilder.toString();

		this.addCategory(category);
		this.categorys.get(category).addItem(
				new PageElement(help.getDisplayItem(),
						message.isEmpty() ?
								ItemAction.immovable((playerInventory, inventory, action, event) -> {}) :
								ItemAction.immovable((playerInventory, inventory, action, event) -> {
									playerInventory.getPlayer().sendMessage(message);
						})));
	}

	public void clear() {
		this.categorys.clear();
		List<PageElement> elements = new ArrayList<>(this.getInventory().getItems());
		for (PageElement element : elements) {
			this.getInventory().removeItem(element);
		}
	}

	public CustomPaginatorInventory getInventory() {
		return this.inventory.getPage(0);
	}
}
