package eu.wuffy.survival.command.economy;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.util.ArrayUtil;
import net.milkbowl.vault.economy.Economy;

public class CommandEconomyBalance implements CommandExecutor, TabExecutor {

	private final Economy economy;

	public CommandEconomyBalance(Survival core) {
		this.economy = core.getVaultHandler().getEconomy();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Survival.PREFIX + "§7Dein derzeitiges guthaben beträgt §a" + this.economy.format(this.economy.getBalance((Player) sender)) + "€§8.");
			return true;
		}

		String search = args[0].toLowerCase();
		Player found = Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(search)).findFirst().orElse(null);

		if (found != null) {
			sender.sendMessage(Survival.PREFIX + "§7Dein derzeitiges guthaben beträgt §a" + this.economy.format(this.economy.getBalance(found)) + "€§8.");
		} else {
			sender.sendMessage(Survival.PREFIX + "§7Der angegebene Spieler §a" + args[0] + " §7ist nicht online§8.");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		} else if (args.length == 1) {
			String search = args[0].toLowerCase();

			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).filter(playerName -> playerName.toLowerCase().startsWith(search)).collect(Collectors.toList());
		}
		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}