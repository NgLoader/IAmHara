package eu.wuffy.survival.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.TreeFellerHandler;

public class CommandTreeFeller implements CommandExecutor {

	private final TreeFellerHandler handler;

	public CommandTreeFeller(Survival core) {
		this.handler = core.getTreeFellerHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann diesen §cCommand §7nicht nutzen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (player.hasPermission("wuffy.tree.feller") || this.handler.isEnabledForPlayer(player)) {
			if (this.handler.togglePlayerUsage(player))
				sender.sendMessage(Survival.PREFIX + "§7Du hast den §aBaumfäller §7modus §aaktiviert§8.");
			else
				sender.sendMessage(Survival.PREFIX + "§7Du hast den §aBaumfäller §7modus §cdeaktiviert§8.");
		} else
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
		return true;
	}
}