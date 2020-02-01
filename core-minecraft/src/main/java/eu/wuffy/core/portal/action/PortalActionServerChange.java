package eu.wuffy.core.portal.action;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import eu.wuffy.core.portal.PortalAction;

public class PortalActionServerChange extends PortalAction implements Listener {

	private final Map<Player, Long> portalDelay = new HashMap<>();

	private final Plugin plugin;
	private final byte[] pluginMessage;

	public PortalActionServerChange(Plugin plugin, String serverName) {
		this.plugin = plugin;

		ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
		outputStream.writeUTF("Connect");
		outputStream.writeUTF(serverName);

		this.pluginMessage = outputStream.toByteArray();
	}

	@Override
	public void execute(Player player) {
		long delay = this.portalDelay.getOrDefault(player, -1l);

		if (delay != -1 || delay > System.currentTimeMillis()) {
			return;
		}
		this.portalDelay.put(player, System.currentTimeMillis() + 10000);

		player.sendPluginMessage(plugin, "BungeeCord", this.pluginMessage);
	}

	public void removePortalDelay(Player player) {
		this.portalDelay.remove(player);
	}
}
