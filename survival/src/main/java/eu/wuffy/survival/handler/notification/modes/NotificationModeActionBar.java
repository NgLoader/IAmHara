package eu.wuffy.survival.handler.notification.modes;

import org.bukkit.Bukkit;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.notification.NotificationModeTextFlow;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class NotificationModeActionBar extends NotificationModeTextFlow {

	private BaseComponent[] component;

	public NotificationModeActionBar(Survival core) {
		super(core);
	}

	@Override
	protected void onTick() {
		Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, this.component));
	}

	@Override
	protected void displayTileText(String text) {
		if (this.hasNextStep()) {
			text = text.substring(0, text.length() - 1) + "§k" + text.substring(text.length() - 1);
		}

		this.component = TextComponent.fromLegacyText(text);
	}

	@Override
	protected void startDisplay() { }

	@Override
	protected void stopDisplay() { }

	@Override
	public void onInit() { }

	@Override
	public void onEnable() { }
}
