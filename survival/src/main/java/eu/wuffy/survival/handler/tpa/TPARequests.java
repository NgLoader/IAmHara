package eu.wuffy.survival.handler.tpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TPARequests implements Runnable {

	private static final TextComponent PREFIX_COMPONENT = new TextComponent(Survival.PREFIX);
	private static final TextComponent SPACE_COMPONENT = new TextComponent(" ");

	private final Player player;
	private final Map<Player, TPAInfo> requests = new ConcurrentHashMap<>();

	public TPARequests(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		Long currentTime = System.currentTimeMillis();
		for (Map.Entry<Player, TPAInfo> entry : this.requests.entrySet()) {
			Player player = entry.getKey();

			if (player.isOnline()) {
				if (entry.getValue().expired > currentTime) {
					continue;
				}

				player.sendMessage(Survival.PREFIX + "§7Deine §aTeleportanfrage §7an §8\"§6" + this.player.getName() + "§8 §7ist §cabgelaufen§8.");
			}

			this.requests.remove(player);
		}
	}

	public Player getLastRequest() {
		Entry<Player, TPAInfo> latest = null;
		for (Entry<Player, TPAInfo> entry : this.requests.entrySet()) {
			if (latest == null) {
				latest = entry;
				continue;
			}

			if (latest.getValue().expired < entry.getValue().expired) {
				latest = entry;
			}
		}

		return latest != null ? latest.getKey() : null;
	}

	public boolean canRequest(Player player) {
		TPAInfo info = this.requests.getOrDefault(player, null);
		if (info == null || info.expired == -1) {
			return true;
		}

		if (info.expired < System.currentTimeMillis()) {
			this.requests.remove(player);

			player.sendMessage(Survival.PREFIX + "§7Deine §aTeleportanfrage §7an §8\"§6" + player.getName() + "§8 §7ist §cabgelaufen§8.");
			return false;
		}

		return false;
	}

	public boolean addRequest(Player player) {
		return this.addRequest(player, null);
	}

	public boolean addRequest(Player player, Location location) {
		if (this.player == player || this.requests.containsKey(player)) {
			return false;
		}

		this.requests.put(player, new TPAInfo(System.currentTimeMillis() + 60000, location));

		TextComponent textComponent = new TextComponent(Survival.PREFIX + (location != null ? 
				"§7Du hast eine §aTeleportanfrage §czu §8\"§6" + player.getName() + "§8\" §abekommen§8.\n" :
				"§7Du hast eine §aTeleportanfrage §avon §8\"§6" + player.getName() + "§8\" §abekommen§8.\n"));
		TextComponent acceptComponent = new TextComponent("§aAnnehmen");
		acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + player.getName()));
		TextComponent denyComponent = new TextComponent("§cAblehnen");
		denyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + player.getName()));

		textComponent.addExtra(PREFIX_COMPONENT);
		textComponent.addExtra(acceptComponent);
		textComponent.addExtra(SPACE_COMPONENT);
		textComponent.addExtra(denyComponent);

		this.player.spigot().sendMessage(textComponent);
		player.sendMessage(Survival.PREFIX + "§7Du hast eine §aTeleportanfrage §7an §8\"§6" + this.player.getName() + "§8\" §agesendet§8.");
		return true;
	}

	public boolean denyRequest(Player player) {
		if (this.requests.remove(player) == null) {
			return false;
		}

		this.player.sendMessage(Survival.PREFIX + "§7Du hast die §aTeleportanfrage §7von §8\"§6" + player.getName() + "§8\" §cabgelehnt§8.");
		player.sendMessage(Survival.PREFIX + "§7Deine §aTeleportanfrage §7an §8\"§6" + this.player.getName() + "§8\" §7wurde §cabgelehnt§8.");
		return true;
	}

	public boolean acceptRequest(Player player) {
		TPAInfo info = this.requests.remove(player);
		if (info == null) {
			return false;
		}

		if (info.location != null) {
			this.player.teleport(info.location.add(0, 0.2, 0));
		} else {
			player.teleport(this.player.getLocation().add(0, 0.2, 0));
		}

		this.player.sendMessage(Survival.PREFIX + "§7Du hast die §aTeleportanfrage §7an §8\"§6" + this.player.getName() + "§8\" §aangenommen§8.");
		player.sendMessage(Survival.PREFIX + "§7Deine §aTeleportanfrage §7an §8\"§6" + this.player.getName() + "§8\" §7wurde §aangenommen§8.");
		return true;
	}

	public List<Player> getRequests() {
		Long currentTime = System.currentTimeMillis();
		return this.requests.entrySet().stream().filter(entry -> entry.getValue().expired > currentTime).map(entry -> entry.getKey()).collect(Collectors.toList());
	}

	public Set<Entry<Player, TPAInfo>> getRequestsWithTime() {
		return this.requests.entrySet();
	}

	public void destroy() {
		ArrayList<Player> players = new ArrayList<Player>(this.requests.keySet());
		this.requests.clear();

		players.forEach(player -> player.sendMessage(Survival.PREFIX + "§7Deine §aTeleportanfrage §7an §8\"§6" + this.player.getName() + "§8\" §7wurde §cabgelehnt§8."));
		players.clear();
		players = null;
	}

	public int getCount() {
		return this.requests.size();
	}

	public Player getPlayer() {
		return this.player;
	}
}