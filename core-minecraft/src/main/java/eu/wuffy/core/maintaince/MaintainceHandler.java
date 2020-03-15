package eu.wuffy.core.maintaince;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import eu.wuffy.core.Core;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.MaintainceStatus;

public class MaintainceHandler extends IHandler<Core<?>> implements PluginMessageListener {

	private static final String MESSAGE_CHANNEL_NAME = "Maintaince";

	private final Map<String, List<MaintainceConsumer>> consumers = new HashMap<>();
	private final Map<String, Long> responses = new HashMap<String, Long>();

	public MaintainceHandler(Core<?> core) {
		super(core);
	}

	public void registerChannel() {
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this.core, MESSAGE_CHANNEL_NAME);
	    Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this.core, MESSAGE_CHANNEL_NAME, this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(MESSAGE_CHANNEL_NAME)) {
			return;
		}

		ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(message);
		String server = byteArrayDataInput.readUTF();
		Long responseTime = byteArrayDataInput.readLong();

		if (this.consumers.containsKey(server)) {
			long lastResponse = responses.getOrDefault(server, -1l);

			if (lastResponse != -1 && lastResponse > responseTime) {
				return;
			}
			responses.put(server, responseTime);

			MaintainceStatus mode = MaintainceStatus.values()[byteArrayDataInput.readInt()];

			for (MaintainceConsumer consumer : this.consumers.get(server)) {
				try {
					consumer.recive(this, mode, server, responseTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendStatusUpdate(Player player, MaintainceStatus mode, String server) {
		ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
		byteArrayDataOutput.writeShort(0); // Update
		byteArrayDataOutput.writeUTF(server);
		byteArrayDataOutput.writeLong(System.currentTimeMillis());
		byteArrayDataOutput.writeInt(mode.ordinal());

		player.sendPluginMessage(this.core, MESSAGE_CHANNEL_NAME, byteArrayDataOutput.toByteArray());
	}

	public void addConsumer(String server, MaintainceConsumer consumer) {
		List<MaintainceConsumer> consumers = this.consumers.get(server);

		if (consumers == null) {
			consumers = new ArrayList<>();
			this.consumers.put(server, consumers);
		}

		if (consumers.contains(consumer)) {
			return;
		}

		consumers.add(consumer);
	}
}