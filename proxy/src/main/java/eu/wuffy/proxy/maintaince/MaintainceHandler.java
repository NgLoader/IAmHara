package eu.wuffy.proxy.maintaince;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import eu.wuffy.proxy.Proxy;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.MaintainceStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MaintainceHandler extends IHandler<Proxy> implements Listener {

	private static final String MESSAGE_CHANNEL_NAME = "Maintaince";

	private final Map<String, ServerStatus> serverStatus = new HashMap<>();

	private int schedulerId = -1;

	public MaintainceHandler(Proxy core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.schedulerId = ProxyServer.getInstance().getScheduler().runAsync(this.core, () -> {
			for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
				String serverName = serverInfo.getName();

				ServerStatus serverStatus = this.serverStatus.get(serverName);

				if (serverStatus == null) {
					continue;
				}

				if (serverStatus.needPingCheck()) {
					serverInfo.ping((serverPing, error) -> {
						if (error != null) {
							if (serverStatus.checkIfUpdate(System.currentTimeMillis())) {
								serverStatus.setPingCount(serverStatus.getPingCount() + 1);

								if (serverStatus.getPingCount() > 5) {
									serverStatus.setStatus(MaintainceStatus.OFFLINE);

									for (ServerStatus status : this.serverStatus.values()) {
										if (status.getServerName().equals(serverStatus.getServerName())) {
											continue;
										}

										this.sendUpdateToServer(ProxyServer.getInstance().getServerInfo(status.getServerName()), status, serverStatus);
									}
								}
							}

							return;
						}

						if (serverStatus.getPingCount() != 0) {
							serverStatus.setPingCount(0);
						}
					});
				}

				if (serverStatus.getStatus() == MaintainceStatus.OFFLINE) {
					return;
				}

				for (Entry<String, Boolean> entry : serverStatus.awaitResponse.entrySet()) {
					if (!entry.getValue()) {
						continue;
					}

					this.sendUpdateToServer(serverInfo, serverStatus, this.serverStatus.getOrDefault(entry.getKey(), null));
				}
			}
		}).getId();
	}

	@Override
	public void onDisable() {
		if (schedulerId != -1) {
			ProxyServer.getInstance().getScheduler().cancel(this.schedulerId);
		}
	}

	@EventHandler
	public void onMessageRecive(PluginMessageEvent event) {
		if (!event.getTag().equals(MESSAGE_CHANNEL_NAME)) {
			return;
		}

		ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(event.getData());
		short type = byteArrayDataInput.readShort();
		String serverName = byteArrayDataInput.readUTF();
		long updateTime = byteArrayDataInput.readLong();

		ServerStatus serverStatus = this.serverStatus.get(serverName);

		if (serverStatus == null) {
			serverStatus = new ServerStatus(serverName, MaintainceStatus.OFFLINE);
			this.serverStatus.put(serverName, serverStatus);
		}

		if (type == 0) { // Update
			MaintainceStatus oldStatus = serverStatus.getStatus();

			serverStatus.setLastUpdateTime(updateTime);
			serverStatus.readPacket(byteArrayDataInput);

			boolean resync = oldStatus == MaintainceStatus.OFFLINE && serverStatus.getStatus() != MaintainceStatus.ONLINE;
			ServerInfo serverInfo = null;

			for (ServerStatus status : this.serverStatus.values()) {
				if (status.getServerName().equals(serverStatus.getServerName()))  {
					continue;
				}

				if (resync) {
					if (serverInfo == null) {
						serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
					}

					this.sendUpdateToServer(serverInfo, serverStatus, status);
				}

				this.sendUpdateToServer(ProxyServer.getInstance().getServerInfo(status.getServerName()), status, serverStatus);
			}
		} else if (type == 1) { // Resync
			String recivedServer = byteArrayDataInput.readUTF();
			long recivedTime = byteArrayDataInput.readLong();

			if (!serverStatus.awaitResponse.getOrDefault(recivedServer, false)) {
				return;
			}

			if (serverStatus.lastUnverifiedTime.getOrDefault(recivedServer, -1l) != recivedTime) {
				return;
			}

			serverStatus.lastVerifiedTime.put(recivedServer, recivedTime);
			serverStatus.awaitResponse.put(recivedServer, false);
			serverStatus.awaitLastSyncTime.remove(recivedServer);
		}
	}

	@EventHandler
	public void onPlayerConnected(ServerConnectedEvent event) {
		ServerInfo serverInfo = event.getServer().getInfo();
		ServerStatus serverStatus = this.serverStatus.get(serverInfo.getName());

		if (serverStatus == null) {
			return;
		}

		Collection<ProxiedPlayer> players = serverInfo.getPlayers();

		if (players.isEmpty()) {
			return;
		}

		for (Entry<String, Boolean> entry : serverStatus.awaitResponse.entrySet()) {
			if (!entry.getValue()) {
				continue;
			}

			this.sendUpdateToServer(serverInfo, serverStatus, this.serverStatus.getOrDefault(entry.getKey(), null));
		}
	}

	private void sendUpdateToServer(ServerInfo serverInfo, ServerStatus serverStatus, ServerStatus update) {
		if (serverInfo == null || serverStatus == null || update == null || serverStatus.getServerName().equals(update.getServerName())) {
			return;
		}

		String serverName = update.getServerName();
		long lastUpdatePacketTime = update.getLastUpdateTime();

		if (serverStatus.lastVerifiedTime.getOrDefault(serverName, -1l) == lastUpdatePacketTime) {
			return;
		}

		if (!serverStatus.awaitResponse.getOrDefault(serverName, false)) {
			serverStatus.awaitResponse.put(serverName, true);
		}
		if (serverStatus.lastUnverifiedTime.getOrDefault(serverName, -1l) != lastUpdatePacketTime) {
			serverStatus.lastUnverifiedTime.put(serverName, lastUpdatePacketTime);
		}

		Collection<ProxiedPlayer> players = serverInfo.getPlayers();
		if (players.isEmpty() || serverStatus.awaitLastSyncTime.getOrDefault(serverName, -1l) > System.currentTimeMillis()) {
			return;
		}

		serverStatus.awaitLastSyncTime.put(serverName, System.currentTimeMillis() + 8000);
		serverInfo.sendData(MESSAGE_CHANNEL_NAME, update.getLastPacket());
	}
}