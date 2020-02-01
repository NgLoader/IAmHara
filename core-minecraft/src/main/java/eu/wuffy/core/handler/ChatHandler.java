package eu.wuffy.core.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.wuffy.core.Core;
import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.synced.IHandler;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class ChatHandler extends IHandler<Core<?>> {

	private static final String DEFAULT_MESSAGE_PATTERN = "§8[§aunknown§8] §7%p §8» §7%m";
	private static final String DEFAULT_GROUP_PATTERN = "§8[§a%g§8] §7%p §8» §7%m";

	private final Map<Player, String> playerMessagePattern = new HashMap<>();
	private final Map<String, String> groupMessagePattern = new HashMap<>();

	public ChatHandler(Core<?> core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.updateGroups();
	}

	@Override
	public void onDisable() {
		this.playerMessagePattern.clear();
		this.groupMessagePattern.clear();
	}

	public String getMessagePattern(Player player) {
		return this.playerMessagePattern.getOrDefault(player, ChatHandler.DEFAULT_MESSAGE_PATTERN);
	}

	public void updateMessagePattern(Player player) {
		try {
			User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

			this.playerMessagePattern.put(player, this.groupMessagePattern.getOrDefault(user.getPrimaryGroup(), ChatHandler.DEFAULT_MESSAGE_PATTERN));
		} catch (Exception e) {
			e.printStackTrace();

			this.playerMessagePattern.put(player, ChatHandler.DEFAULT_MESSAGE_PATTERN);
		}
	}

	public void updateGroups() {
		this.groupMessagePattern.clear();

		for(Group group : LuckPermsProvider.get().getGroupManager().getLoadedGroups()) {
			this.groupMessagePattern.put(group.getName(), GroupUtil.getGroupMetaSorted(group, "chat", ChatHandler.DEFAULT_GROUP_PATTERN.replace("%g", group.getFriendlyName())));
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			this.updateMessagePattern(player);
		}
	}

	public void removeMessagePattern(Player player) {
		this.playerMessagePattern.remove(player);
	}
}