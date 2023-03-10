package eu.wuffy.core.scoreboard;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.ScoreboardObjective;

public class SurvivalObjectiveFormat extends SurvivalObjective {

	private String format;

	public SurvivalObjectiveFormat(ScoreboardObjective objective, Player player, String identifier, int score, String format, Object... values) {
		super(objective, player, identifier, String.format(format, values), score);
		this.format = format;
	}

	public void update(Object... values) {
		this.updateScoreValue(String.format(this.format, values));
	}
}