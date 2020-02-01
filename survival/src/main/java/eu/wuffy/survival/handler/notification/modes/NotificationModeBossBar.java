package eu.wuffy.survival.handler.notification.modes;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.notification.NotificationModeTextFlow;

public class NotificationModeBossBar extends NotificationModeTextFlow implements Listener {

	private BossBar bossBar;

	public NotificationModeBossBar(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
		this.bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20);
		this.bossBar.setVisible(false);

		Bukkit.getOnlinePlayers().forEach(player -> this.bossBar.addPlayer(player));
		Bukkit.getPluginManager().registerEvents(this, this.core);
	}

	@Override
	public void onDisable() {
		super.onDisable();

		if (this.bossBar != null) {
			this.bossBar.removeAll();
			this.bossBar = null;
		}
	}

	@Override
	protected void onTick() { }

	@Override
	protected void startDisplay() {
		bossBar.setTitle("");
		bossBar.setProgress(0D);
		bossBar.setVisible(true);
	}

	@Override
	protected void stopDisplay() {
		this.bossBar.setVisible(false);
	}

	@Override
	protected void displayTileText(String text) {
		if (this.hasNextStep()) {
			text = text.substring(0, text.length() - 1) + "§k" + text.substring(text.length() - 1);
		}

		this.bossBar.setTitle(text);

		double progress = (100D / finalText.length() * text.length()) / 100D;
		this.bossBar.setProgress(progress > 1D ? 1D : progress);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (this.bossBar != null) {
			this.bossBar.addPlayer(event.getPlayer());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (this.bossBar != null) {
			this.bossBar.removePlayer(event.getPlayer());
		}
	}
}
