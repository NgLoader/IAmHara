package eu.wuffy.proxy.command;

import eu.wuffy.synced.util.ArrayUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public abstract class CommandFind extends Command implements TabExecutor {

	public CommandFind() {
		super("find", "wuffy.command.find", "search");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}