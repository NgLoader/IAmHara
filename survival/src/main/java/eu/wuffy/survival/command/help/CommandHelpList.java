package eu.wuffy.survival.command.help;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.help.HelpHandler;
import eu.wuffy.survival.handler.help.HelpLine;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHelpList implements CommandExecutor {

	private final Survival core;
	private final HelpHandler handler;

	public CommandHelpList(Survival core) {
		this.core = core;
		this.handler = this.core.getHelpHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("wuffy.help.list")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}
		
		if(this.handler.getLines().isEmpty())
			sender.sendMessage(Survival.PREFIX + "§7Die §aHelp §7liste ist §cleer§8.");
		else {
			List<BaseComponent> messages = new ArrayList<BaseComponent>();
			messages.add(new TextComponent(Survival.PREFIX + "§7Loading Help list§8."));

			this.handler.getLines().stream()
				.sorted(Comparator.comparing(HelpLine::getLine))
				.forEach(helpLine -> {
					TextComponent textComponent = new TextComponent(
							"\n§7" + (helpLine.getLine() < 10 ? "0" : "") + helpLine.getLine()
							+ "§8: §8\"§a" + helpLine.message + "§8\""
							+ (helpLine.permission.isEmpty() ? "" : " §8[§7" + helpLine.permission + "§8]")
						);
					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§AKlicke zum §2bearbeiten").create()));
					textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/HelpUpdate %d %s %s", 
							helpLine.line,
							helpLine.permission.isEmpty() ? "-" : helpLine.permission,
							helpLine.message)));
					messages.add(textComponent);
				});

			messages.add(new TextComponent("\n" + Survival.PREFIX + "§7Loaded Help list§8."));

			sender.spigot().sendMessage(messages.toArray(new BaseComponent[0]));
		}
		return true;
	}
}
