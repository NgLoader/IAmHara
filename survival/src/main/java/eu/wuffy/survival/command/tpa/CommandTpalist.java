package eu.wuffy.survival.command.tpa;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.tpa.TPAHandler;
import eu.wuffy.survival.handler.tpa.TPAInfo;
import eu.wuffy.survival.handler.tpa.TPARequests;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CommandTpalist implements CommandExecutor, TabExecutor {

	private final TPAHandler tpaHandler;

	private final TextComponent prefixComponent = new TextComponent(Survival.PREFIX + "   §8● ");
	private final TextComponent spaceComponent = new TextComponent(" ");

	public CommandTpalist(Survival survival) {
		this.tpaHandler = survival.getTpaHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann keine §cteleport §7command nutzen§8.");
			return true;
		}
		if (!sender.hasPermission("survival.tpa.list")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
			return true;
		}

		Player player = (Player) sender;
		TPARequests requests = this.tpaHandler.getRequests(player);
		Set<Entry<Player, TPAInfo>> requestList = requests.getRequestsWithTime();

		if (requestList.isEmpty()) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast derzeitig keine anfragen§8.");
			return true;
		}

		player.sendMessage(Survival.PREFIX + "§8[]§7------§a{ §2Teleport anfragen §a}§7------§8[]");
		player.sendMessage(Survival.PREFIX + " ");

		long currentTimeInMillis = System.currentTimeMillis();
		for (Entry<Player, TPAInfo> entry : requestList) {
			TPAInfo info = entry.getValue();

			if (currentTimeInMillis > info.expired) {
				continue;
			}
			String displayName = entry.getKey().getDisplayName();

			TextComponent textComponent = new TextComponent(Survival.PREFIX + "§8- §a" + displayName + " §8(§6" + (info.location != null ? "Nach ihn" : "Zu dir") + "§8)\n");
			textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aLeuft in §6" + (int) ((info.expired - System.currentTimeMillis()) / 1000) + " §asekunden §aab")));
			TextComponent acceptComponent = new TextComponent("§aAnnehmen");
			acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + displayName));
			acceptComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Teleportanfrage von §8\"§6" + displayName + "§8\" §aannehmen")));
			TextComponent denyComponent = new TextComponent("§cAblehnen");
			denyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + displayName));
			denyComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Teleportanfrage von §8\"§6" + displayName + "§8\" §cablehnen")));

			textComponent.addExtra(this.prefixComponent);
			textComponent.addExtra(acceptComponent);
			textComponent.addExtra(this.spaceComponent);
			textComponent.addExtra(denyComponent);

			player.spigot().sendMessage(textComponent);
		}

		player.sendMessage(Survival.PREFIX + " ");
		player.sendMessage(Survival.PREFIX + "§8[]§7------§a{ §2Teleport anfragen §a}§7------§8[]");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
}