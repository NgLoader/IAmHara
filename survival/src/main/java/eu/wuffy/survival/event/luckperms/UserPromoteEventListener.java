package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.event.EventListener;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.track.UserPromoteEvent;

public class UserPromoteEventListener extends EventListener implements Consumer<UserPromoteEvent> {

	private ScoreboardHandler scoreboardHandler;
	private VanishHandler vanishHandler;
	private ChatHandler chatHandler;

	public UserPromoteEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.scoreboardHandler = this.core.getScoreboardHandler();
		this.vanishHandler = this.core.getVanishHandler();
		this.chatHandler = this.core.getChatHandler();
	}

	@Override
	public void onEnable() {
		LuckPermsProvider.get().getEventBus().subscribe(UserPromoteEvent.class, this);
	}

	@Override
	public void accept(UserPromoteEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUser().getUniqueId());

		if (player != null && player.isOnline() && event.getGroupTo().isPresent()) {
			Bukkit.getScheduler().runTask(this.core, () -> {
				this.scoreboardHandler.getPlayerScoreboard(player.getPlayer()).joinTeam(event.getGroupTo().get());
				this.vanishHandler.checkTabVisibilityForPlayer(player.getPlayer(), true);
				this.chatHandler.updateMessagePattern(player.getPlayer());
			});
		}
	}
}