package eu.wuffy.survival.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.config.Config;

@Config(path = Survival.CONFIG_FOLDER, name = "help")
public class ConfigHelp {

	public List<Category> categorys = Arrays.asList(
			new Category(Material.WRITTEN_BOOK, "&6Rules", new String[] {
					"&7Unter dieser kategorie siehst du alle regeln&8."
			},
					new Element("PvP", "", Material.DIAMOND_SWORD, "&4P&cv&4P", new String[] {
							"&7PvP ist außerhalb von lands erlaubt&8."
					})),
			new Category(Material.RED_BED, "&aHomes", new String[] {
					"&eListe and befehlen für homes"
			},
					new Element("Home", "Mit diesen befehl kannst du dich zu ein home teleportieren.", Material.ENDER_PEARL, "&8/&7home &e<&7name&e>", new String[0]),
					new Element("Homes", "Mit diesen befehl kannst du dir deine homes auflisten lassen.", Material.WRITTEN_BOOK, "&8/&7homes", new String[0]),
					new Element("Create", "Mit diesen befehl kannst du dir ein home erstellen.", Material.END_CRYSTAL, "&8/&7addhome &e<&7name&e> &e[&7beschreibung&e]", new String[0]),
					new Element("Delete", "Mit diesen befehl kannst du ein home löschen.", Material.BARRIER, "&8/&7delhome &e<&7name&e>", new String[0])));

	private class Basic {

		public Material material;
		public String displayName;
		public String[] lore;

		public Basic(Material material, String displayName, String[] lore) {
			this.material = material;
			this.displayName = displayName;
			this.lore = lore;
		}

		public String getDisplayName() {
			return ChatColor.translateAlternateColorCodes('&', this.displayName);
		}

		public List<String> getLore() {
			return Stream.of(lore).map(lore -> ChatColor.translateAlternateColorCodes('&', lore)).collect(Collectors.toList());
		}
	}

	public class Category extends Basic {

		public Element[] elements;

		public Category(Material material, String displayName, String[] lore, Element... elements) {
			super(material, displayName, lore);
			this.elements = elements;
		}
	}

	public class Element extends Basic {

		public String name;
		public String description;

		public Element(String name, String description, Material material, String displayName, String[] lore) {
			super(material, displayName, lore);
			this.description = description;
			this.name = name;
		}

		public String getName() {
			return ChatColor.translateAlternateColorCodes('&', this.name);
		}

		public String getDescription() {
			return ChatColor.translateAlternateColorCodes('&', this.description);
		}
	}
}