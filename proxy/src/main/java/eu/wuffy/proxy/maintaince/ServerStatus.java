package eu.wuffy.proxy.maintaince;

import java.util.HashMap;
import java.util.Map;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import eu.wuffy.synced.MaintainceStatus;

public class ServerStatus {

	private static final long UPDATE_PING = 1000 * 60 * 1;

	private final String serverName;

	private MaintainceStatus status;
	private long currentUpdateTime;
	private byte[] lastPacket;

	private int pingCount = 0;

	public final Map<String, Long> lastVerifiedTime = new HashMap<>();
	public final Map<String, Long> lastUnverifiedTime = new HashMap<>();

	public final Map<String, Boolean> awaitResponse = new HashMap<>();
	public final Map<String, Long> awaitLastSyncTime = new HashMap<>();

	public ServerStatus(String serverName, MaintainceStatus status) {
		this.serverName = serverName;
		this.status = status;
		this.currentUpdateTime = System.currentTimeMillis();

		this.rebuildPacket();
	}

	public void readPacket(ByteArrayDataInput packet) {
		this.status = MaintainceStatus.values()[packet.readInt()];

		this.rebuildPacket();
	}

	public void rebuildPacket() {
		ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
		byteArrayDataOutput.writeShort(0); // Update
		byteArrayDataOutput.writeUTF(this.serverName);
		byteArrayDataOutput.writeLong(this.currentUpdateTime);
		byteArrayDataOutput.writeInt(this.status.ordinal());

		this.lastPacket = byteArrayDataOutput.toByteArray();
	}

	public boolean needPingCheck() {
		return System.currentTimeMillis() - this.currentUpdateTime > ServerStatus.UPDATE_PING;
	}

	public boolean checkIfUpdate(long updateTime) {
		if (this.currentUpdateTime > updateTime) {
			return false;
		}

		this.setLastUpdateTime(updateTime);
		return true;
	}

	public long getLastUpdateTime() {
		return this.currentUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.currentUpdateTime = lastUpdateTime;
		this.rebuildPacket();
	}

	public void setPingCount(int pingCount) {
		this.pingCount = pingCount;
	}

	public int getPingCount() {
		return this.pingCount;
	}

	public MaintainceStatus getStatus() {
		return this.status;
	}

	public void setStatus(MaintainceStatus status) {
		this.status = status;

		this.rebuildPacket();

		if (this.status == MaintainceStatus.OFFLINE) {
			this.awaitResponse.clear();
			this.lastUnverifiedTime.clear();
			this.awaitLastSyncTime.clear();
		} else if (this.pingCount != 0) {
			this.pingCount = 0;
		}
	}

	public byte[] getLastPacket() {
		return this.lastPacket;
	}

	public String getServerName() {
		return this.serverName;
	}
}