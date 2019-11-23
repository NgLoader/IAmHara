package eu.wuffy.survival.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;

public class CommandSurvival implements CommandExecutor {

	private Survival core;

	public CommandSurvival(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("wuffy.admin")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length > 0) {
			switch (args[0]) {
			case "rl":
			case "reload":
				if (args.length > 1) {
					switch (args[1]) {
					case "dynmap":
						sender.sendMessage(Survival.PREFIX + "§7Dynmap wird §aneugeladen§8.");
						this.core.getDynmapHandler().getDynmapRegion().loadWorldGuardRegions();
						sender.sendMessage(Survival.PREFIX + "§7Dynmap wurde §2neugeladen§8.");
						return true;

					default:
						sender.sendMessage(Survival.PREFIX + "§7Der angegebene command konnte nicht gefunden werden§8.");
						return true;
					}
				}
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene command konnte nicht gefunden werden§8.");
				return true;

			default:
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene command konnte nicht gefunden werden§8.");
				return true;
			}
		}
		sender.sendMessage(Survival.PREFIX + "§7/Survival reload §8<§7dynmap§8>§8.");
		return true;
	}
}