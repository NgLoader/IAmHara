package eu.wuffy.survival.handler.home;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.help.IHelp;
import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.common.SurvivalHelpCategory;
import eu.wuffy.synced.IHandler;

public class HomeHandler extends IHandler<Survival> {

	private Map<UUID, List<Home>> homes = new HashMap<UUID, List<Home>>();

	public HomeHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		core.getHelpSystem().addHelp(SurvivalHelpCategory.HOME.getCategory(), new IHelp() {
			
			@Override
			public String getName() {
				return "Home";
			}
			
			@Override
			public ItemStack getDisplayItem() {
				return new ItemFactory(Material.ENDER_PEARL, "§8/§7home §e<§7name§e>").build();
			}
			
			@Override
			public String getDescription() {
				return "Mit diesen befehl kannst du dich zu ein home teleportieren.";
			}
			
			@Override
			public String[] getAliases() {
				return new String[0];
			}
		});
		core.getHelpSystem().addHelp(SurvivalHelpCategory.HOME.getCategory(), new IHelp() {
			
			@Override
			public String getName() {
				return "Homes";
			}
			
			@Override
			public ItemStack getDisplayItem() {
				return new ItemFactory(Material.PAPER, "§8/§7homes").build();
			}
			
			@Override
			public String getDescription() {
				return "Mit diesen befehl kannst du dir deine homes auflisten lassen.";
			}
			
			@Override
			public String[] getAliases() {
				return new String[0];
			}
		});
		core.getHelpSystem().addHelp(SurvivalHelpCategory.HOME.getCategory(), new IHelp() {
			
			@Override
			public String getName() {
				return "Create";
			}
			
			@Override
			public ItemStack getDisplayItem() {
				return new ItemFactory(Material.END_CRYSTAL, "§8/§7addhome §e<§7name§e> §e[§7beschreibung§e]").build();
			}
			
			@Override
			public String getDescription() {
				return "Mit diesen befehl kannst du dir ein home erstellen.";
			}
			
			@Override
			public String[] getAliases() {
				return new String[0];
			}
		});
		core.getHelpSystem().addHelp(SurvivalHelpCategory.HOME.getCategory(), new IHelp() {
			
			@Override
			public String getName() {
				return "Delete";
			}
			
			@Override
			public ItemStack getDisplayItem() {
				return new ItemFactory(Material.BARRIER, "§8/§7delhome §e<§7name§e>").build();
			}
			
			@Override
			public String getDescription() {
				return "Mit diesen befehl kannst du ein home löschen.";
			}
			
			@Override
			public String[] getAliases() {
				return new String[0];
			}
		});
	}

	@Override
	public void onEnable() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			try {
				this.load(player.getUniqueId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public void load(UUID uuid) throws SQLException {
		if (this.homes.containsKey(uuid))
			this.homes.get(uuid).clear();
		else
			this.homes.put(uuid, new ArrayList<Home>());

		this.getCore().getDatabase().loadHomes(uuid).forEach(home -> this.homes.get(uuid).add(home));
	}

	public void unload(UUID uuid) {
		if (this.homes.containsKey(uuid))
			this.homes.remove(uuid);
	}

	public Home get(UUID uuid, String name) {
		if (!this.homes.containsKey(uuid))
			try {
				this.load(uuid);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		Optional<Home> found = this.homes.get(uuid).stream().filter(home -> home.name.equalsIgnoreCase(name)).findFirst();

		if(found.isPresent())
			return found.get();
		return null;
	}

	public boolean exist(UUID uuid, String name) {
		return this.homes.containsKey(uuid) && homes.get(uuid).stream().filter(home -> home.name.equalsIgnoreCase(name)).findAny().isPresent();
	}

	public Home create(UUID uuid, String name, String description, Location location) throws SQLException {
		Home home = this.getCore().getDatabase().createHome(uuid, name, description, location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		if (!this.homes.containsKey(uuid))
			this.load(uuid);
		this.homes.get(uuid).add(home);

		return home;
	}

	public void delete(UUID uuid, Home home) throws SQLException {
		if (this.homes.containsKey(uuid))
			this.homes.get(uuid).remove(home);

		this.getCore().getDatabase().deleteHome(home);
	}

	public List<Home> getHomesOfPlayer(UUID uuid) {
		if (this.homes.containsKey(uuid))
			return this.homes.get(uuid);
		return new ArrayList<Home>();
	}
}