package eu.wuffy.disco.listener;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.disco.Disco;
import eu.wuffy.synced.IHandler;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;

public class LuckPermsHandler extends IHandler<Disco> {

	private final ScoreboardHandler scoreboardHandler;
	private final ChatHandler chatHandler;

	public LuckPermsHandler(Disco core) {
		super(core);

		this.scoreboardHandler = this.core.getScoreboardHandler();
		this.chatHandler = this.core.getChatHandler();
	}

	@Override
	public void onEnable() {
		LuckPermsProvider.get().getEventBus().subscribe(GroupDataRecalculateEvent.class, this::onGroupDataRecalculate);
		LuckPermsProvider.get().getEventBus().subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculate);
		LuckPermsProvider.get().getEventBus().subscribe(UserPromoteEvent.class, this::onUserPromote);
	}

	public void onGroupDataRecalculate(GroupDataRecalculateEvent event) {
		Bukkit.getScheduler().runTask(this.core, () -> {
			this.scoreboardHandler.updateGroups();
			this.chatHandler.updateGroups();
		});
	}

	public void onUserDataRecalculate(UserDataRecalculateEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUser().getUniqueId());

		if (player != null && player.isOnline()) {
			Bukkit.getScheduler().runTask(this.core, () -> {
				this.scoreboardHandler.getPlayerScoreboard(player.getPlayer()).joinTeam(event.getUser().getPrimaryGroup());
				this.chatHandler.updateMessagePattern(player.getPlayer());
			});
		}
	}

	public void onUserPromote(UserPromoteEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUser().getUniqueId());

		if (player != null && player.isOnline() && event.getGroupTo().isPresent()) {
			Bukkit.getScheduler().runTask(this.core, () -> {
				this.scoreboardHandler.getPlayerScoreboard(player.getPlayer()).joinTeam(event.getGroupTo().get());
				this.chatHandler.updateMessagePattern(player.getPlayer());
			});
		}
	}
}