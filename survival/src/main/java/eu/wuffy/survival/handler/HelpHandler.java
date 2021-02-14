package eu.wuffy.survival.handler;

import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.help.HelpCategory;
import eu.wuffy.core.help.HelpSystem;
import eu.wuffy.core.help.IHelp;
import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.config.ConfigHelp;
import eu.wuffy.survival.config.ConfigHelp.Category;
import eu.wuffy.survival.config.ConfigHelp.Element;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.config.ConfigService;

public class HelpHandler extends IHandler<Survival> {

	public HelpHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		ConfigHelp config = ConfigService.getConfig(ConfigHelp.class);
		HelpSystem helpSystem = this.core.getHelpSystem();

		for (Category category : config.categorys) {
			try {
				HelpCategory helpCategory = new HelpCategory(new ItemFactory(category.material)
						.setDisplayName(category.getDisplayName())
						.setLore(category.getLore())
						.addAllFlag());
				helpSystem.addCategory(helpCategory);

				for (Element element : category.elements) {
					try {
						helpSystem.addHelp(helpCategory, new IHelp() {
							
							@Override
							public String getName() {
								return element.name;
							}
							
							@Override
							public ItemStack getDisplayItem() {
								return new ItemFactory(element.material)
										.setDisplayName(element.getDisplayName())
										.setLore(element.getLore())
										.addAllFlag()
										.build();
							}
							
							@Override
							public String getDescription() {
								return element.getDescription();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDisable() {
		this.core.getHelpSystem().clear();
	}
}