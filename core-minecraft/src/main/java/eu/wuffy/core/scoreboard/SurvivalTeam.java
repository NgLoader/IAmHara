package eu.wuffy.core.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import eu.wuffy.core.util.NMSUtil;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_13_R2.ScoreboardTeam;

public class SurvivalTeam {

	private enum SurivalTeamStatus {
		TEAM_CREATE(0),
		TEAM_CHANGE(2),
		TEAM_REMOVE(1),

		ENTRY_ADD(3),
		ENTRY_REMOVE(4);

		private final int value;

		private SurivalTeamStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	private final ScoreboardTeam team;
	private final List<String> entrys = new ArrayList<String>();

	public SurvivalTeam(ScoreboardTeam team) {
		this.team = team;

		NMSUtil.sendPacket(new PacketPlayOutScoreboardTeam(this.team, SurivalTeamStatus.TEAM_CREATE.getValue()));
	}

	public void sendCreatePacket(Player player) {
		NMSUtil.sendPacket(player, new PacketPlayOutScoreboardTeam(this.team, SurivalTeamStatus.TEAM_CREATE.getValue()));

		if (!this.entrys.isEmpty()) {
			NMSUtil.sendPacket(player, new PacketPlayOutScoreboardTeam(this.team, this.entrys, SurivalTeamStatus.ENTRY_ADD.getValue()));
		}
	}

	public void sendRemovePacket(Player player) {
		NMSUtil.sendPacket(player, new PacketPlayOutScoreboardTeam(this.team, SurivalTeamStatus.TEAM_REMOVE.getValue()));
	}

	public void addEntry(List<String> entrys) {
		for (String entry : entrys) {
			if (!this.entrys.contains(entry)) {
				this.entrys.add(entry);
			}
		}

		this.updateEntry(entrys, SurivalTeamStatus.ENTRY_ADD);
	}

	public void removeEntry(List<String> entrys) {
		this.entrys.removeAll(entrys);

		this.updateEntry(entrys, SurivalTeamStatus.ENTRY_REMOVE);
	}

	public void removeAll() {
		this.updateEntry(this.entrys, SurivalTeamStatus.ENTRY_REMOVE);
		this.entrys.clear();
	}

	private void updateEntry(List<String> entrys, SurivalTeamStatus status) {
		PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam(this.team, entrys, status.getValue());
		NMSUtil.sendPacket(packetPlayOutScoreboardTeam);
	}

	public List<String> getEntrys() {
		return this.entrys;
	}

	public ScoreboardTeam getTeam() {
		return this.team;
	}
}