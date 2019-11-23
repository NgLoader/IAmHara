package eu.wuffy.survival.command.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.warp.Warp;
import eu.wuffy.survival.handler.warp.WarpAlias;
import eu.wuffy.survival.handler.warp.WarpHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandWarpList implements CommandExecutor {

	private final Survival core;
	private final WarpHandler handler;

	public CommandWarpList(Survival core) {
		this.core = core;
		this.handler = this.core.getWarpHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("wuffy.warp.list")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (this.handler.getWarps().size() == 0) {
			sender.sendMessage(Survival.PREFIX + "§7Es exestieren derzeitig keine §cwarps§8.");
			return true;
		}
		
		sender.sendMessage(Survival.PREFIX + "§8[]§7------§a{ §2Warp Liste §a}§7------§8[]");
		sender.sendMessage(Survival.PREFIX + " ");

		for (Warp warp : this.core.getWarpHandler().getWarps())
			if (!warp.permission.equals("") && !sender.hasPermission(warp.permission))
				continue;
			else {
//				sender.sendMessage(Freebuild.PREFIX + "§8- §a" + warp.getName() + (warp.getDescription().isEmpty() ? "" : " §8(§7" + warp.getDescription() + "§8)"));
				TextComponent textComponent = new TextComponent(Survival.PREFIX + "§8- §a" + warp.name + (warp.description.isEmpty() ? "" : " §8(§7" + warp.description + "§8)"));
				textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aKlick zum §2teleportieren").create()));
				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.name));
				sender.spigot().sendMessage(textComponent);

				for (WarpAlias alias : warp.aliases) {
					TextComponent textComponent2 = new TextComponent(Survival.PREFIX + "     §8> §a" + alias.alias);
					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aKlick zum §2teleportieren").create()));
					textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.name));
					sender.spigot().sendMessage(textComponent2);
				}
			}

		sender.sendMessage(Survival.PREFIX + " ");
		sender.sendMessage(Survival.PREFIX + "§8[]§7------§a{ §2Warp Liste §a}§7------§8[]");
		return true;
	}
}
