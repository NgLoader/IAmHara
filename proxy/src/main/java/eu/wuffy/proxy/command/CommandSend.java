package eu.wuffy.proxy.command;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandSend extends Command implements TabExecutor {

	public CommandSend() {
		super("send", "wuffy.command.send");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage(new TextComponent(
					Proxy.PREFIX + "§7/Send §8<§7server§8|§7player§8|§7all§8|§7current§8> §8<§7target§8>"));
			return;
		}

		ServerInfo target = ProxyServer.getInstance().getServerInfo(args[1].toLowerCase());

		if (target == null) {
			sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Der §7angegebene §7Server §8\"§c" + args[1]
					+ "§8\" §7wurden §7nicht §7gefunden§8."));
			return;
		}

		switch (args[0]) {
		case "all":
			ProxyServer.getInstance().getPlayers().forEach(player -> summon(player, target, sender));
			sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7hast §7alle §7aufen §7Server §8\"§a"
					+ target.getName().substring(0, 1).toUpperCase() + target.getName().substring(1).toLowerCase()
					+ "§8\" §agesendet§8."));
			return;

		case "current":
			if (!(sender instanceof ProxiedPlayer)) {
				sender.sendMessage(
						new TextComponent(Proxy.PREFIX + "§7Die §7Console §7kann §7auf §7keinen §cServer §7sein§8."));
				return;
			}
			((ProxiedPlayer) sender).getServer().getInfo().getPlayers()
					.forEach(player -> summon(player, target, sender));
			sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7hast §7alle §7von §8\"§c"
					+ ((ProxiedPlayer) sender).getServer().getInfo().getName().substring(0, 1).toUpperCase()
					+ ((ProxiedPlayer) sender).getServer().getInfo().getName().substring(1).toLowerCase()
					+ "§8\" §7auf §8\"§a" + target.getName().substring(0, 1).toUpperCase()
					+ target.getName().substring(1).toLowerCase() + "§8\" §agesendet§8."));
			return;

		default:
			ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(args[0].toLowerCase());

			if (serverInfo != null) {
				serverInfo.getPlayers().forEach(player -> summon(player, target, sender));
				sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7hast §7alle §7von §8\"§c"
						+ serverInfo.getName().substring(0, 1).toUpperCase()
						+ serverInfo.getName().substring(1).toLowerCase() + "§8\" §7auf §8\"§a"
						+ target.getName().substring(0, 1).toUpperCase() + target.getName().substring(1).toLowerCase()
						+ "§8\" §agesendet§8."));
				return;
			}

			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				summon(player, target, sender);
				sender.sendMessage(
						new TextComponent(Proxy.PREFIX + "§7Du §7hast §7den §aSpieler §8\"§a" + player.getName()
								+ "§8\" §7aufen §7Server §8\"§a" + target.getName().substring(0, 1).toUpperCase()
								+ target.getName().substring(1).toLowerCase() + "§8\" §agesendet§8."));
				return;
			}
			break;
		}
		sender.sendMessage(new TextComponent(
				Proxy.PREFIX + "§7/Send §8<§7server§8|§7player§8|§7all§8|§7current§8> §8<§7target§8>"));
	}

	private void summon(ProxiedPlayer player, ServerInfo target, CommandSender sender) {
		if (player.getServer().getInfo().equals(target) || !target.canAccess(player)) {
			return;
		}

		target.ping(new Callback<ServerPing>() {

			@Override
			public void done(ServerPing serverPing, Throwable throwable) {
				if (throwable == null) {
					if (!(serverPing.getPlayers().getMax() > serverPing.getPlayers().getOnline())) {
						if (player.hasPermission("wuffy.command.send.see") && !player.equals(sender))
							player.sendMessage(new TextComponent(Proxy.PREFIX + "§8\""
									+ (sender instanceof ProxiedPlayer ? "§a" : "§c") + sender.getName()
									+ "§8\" §7hat §7versucht §7dich §7auf §7denn §7Server §8\"§a"
									+ target.getName().substring(0, 1).toUpperCase()
									+ target.getName().substring(1).toLowerCase()
									+ "§8\" §7zu §7senden, §7dieser §7ist §7aber §cvoll§8."));
						else
							player.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7solltest §7aufen §8\"§a"
									+ target.getName().substring(0, 1).toUpperCase()
									+ target.getName().substring(1).toLowerCase()
									+ "§8\" §7Server §agesendet §7werden, §7aber §7dieser §7war §7voll§8."));
						return;
					}

					player.connect(target);

					if (player.hasPermission("wuffy.command.send.see") && !player.equals(sender))
						player.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7wurdest §7von §8\""
								+ (sender instanceof ProxiedPlayer ? "§a" : "§c") + sender.getName()
								+ "§8\" §7aufen §7Server §8\"§a" + target.getName().substring(0, 1).toUpperCase()
								+ target.getName().substring(1).toLowerCase() + "§8\" §7gesendet§8."));
					else
						player.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7wurdest §7aufen §8\"§a"
								+ target.getName().substring(0, 1).toUpperCase()
								+ target.getName().substring(1).toLowerCase() + "§8\" §7Server §agesendet§8."));
				} else {
					if (player.hasPermission("wuffy.command.send.see") && !player.equals(sender))
						player.sendMessage(new TextComponent(
								Proxy.PREFIX + "§8\"" + (sender instanceof ProxiedPlayer ? "§a" : "§c")
										+ sender.getName() + "§8\" §7hat §7versucht §7dich §7auf §7denn §7Server §8\"§a"
										+ target.getName().substring(0, 1).toUpperCase()
										+ target.getName().substring(1).toLowerCase()
										+ "§8\" §7zu §7senden, §7dieser §7ist §7aber §7nicht §cerreichbar§8."));
					else
						player.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7solltest §7aufen §8\"§a"
								+ target.getName().substring(0, 1).toUpperCase()
								+ target.getName().substring(1).toLowerCase()
								+ "§8\" §7Server §agesendet §7werden, §7aber §7dieser §7war §7nicht §cerreichbar§8."));
				}
			}
		});
		return;
	}

	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length > 2 || args.length == 0)
			return ImmutableSet.of();
		Set<String> matches = new HashSet<String>();

		String search = (args.length == 1 ? args[0] : args[1]).toLowerCase();
		matches.addAll(ProxyServer.getInstance().getServers().values().stream()
				.filter(serverInfo -> serverInfo.getName().toLowerCase().startsWith(search))
				.map(serverInfo -> serverInfo.getName()).collect(Collectors.toList()));

		if (args.length == 1) {
			matches.addAll(ProxyServer.getInstance().getPlayers().stream()
					.filter(player -> player.getName().toLowerCase().startsWith(search)).map(player -> player.getName())
					.collect(Collectors.toList()));

			if ("all".startsWith(search))
				matches.add("all");
			if ("current".startsWith(search))
				matches.add("current");
		}

		return matches;
	}
}