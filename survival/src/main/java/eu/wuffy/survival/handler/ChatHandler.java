package eu.wuffy.survival.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scoreboard.Team;

import eu.wuffy.core.IHandler;
import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.survival.Survival;
import me.lucko.luckperms.api.Group;

public class ChatHandler extends IHandler<Survival> {

	private final Map<Team, String> prefixes = new HashMap<Team, String>();

	public ChatHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() { }

	@Override
	public void onEnable() { }

	@Override
	public void onDisable() {
		this.prefixes.clear();
	}

	public void onScoreboardTeamUpdate(Team team, Group group) {
		this.prefixes.put(team, GroupUtil.getGroupMetaSorted(group, "chat", "§8[§aunknown§8] §7%p §8» §7%m"));
	}

	public String getChatPrefix(Team team) {
		return this.prefixes.getOrDefault(team, "§8[§aunknown§8]");
	}

	@Override
	public void disable() {
		this.prefixes.clear();
	}
}