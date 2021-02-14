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
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class UserDataRecalculateEventListener extends EventListener implements Consumer<UserDataRecalculateEvent> {

	private ScoreboardHandler scoreboardHandler;
	private VanishHandler vanishHandler;
	private ChatHandler chatHandler;

	private EventSubscription<UserDataRecalculateEvent> subscription;

	public UserDataRecalculateEventListener(Survival core) {
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
		this.subscription = LuckPermsProvider.get().getEventBus().subscribe(UserDataRecalculateEvent.class, this);
	}

	@Override
	public void onDisable() {
		if (this.subscription != null) {
			this.subscription.close();
		}
	}

	@Override
	public void accept(UserDataRecalculateEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUser().getUniqueId());

		if (player != null && player.isOnline()) {
			Bukkit.getScheduler().runTask(this.core, () -> {
				this.scoreboardHandler.getPlayerScoreboard(player.getPlayer()).joinTeam(event.getUser().getPrimaryGroup());
				this.vanishHandler.checkTabVisibilityForPlayer(player.getPlayer(), true);
				this.chatHandler.updateMessagePattern(player.getPlayer());
			});
		}
	}
}