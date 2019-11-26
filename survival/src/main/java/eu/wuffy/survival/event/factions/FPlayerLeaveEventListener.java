package eu.wuffy.survival.event.factions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerLeaveEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;
import eu.wuffy.survival.handler.scoreboard.ScoreboardHandler;
import eu.wuffy.survival.handler.scoreboard.SurvivalObjectiveFormat;

public class FPlayerLeaveEventListener extends EventListener {

	private ScoreboardHandler scoreboardHandler;

	public FPlayerLeaveEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {

		this.scoreboardHandler = this.getCore().getScoreboardHandler();
	}

	@EventHandler
	public void onFPlayerLeave(FPlayerLeaveEvent event) {
		Faction faction = event.getFaction();
		int factionOnline = faction.getOnlinePlayers().size();
		int factionSize = faction.getFPlayers().size();

		for (Player online : faction.getOnlinePlayers()) {
			SurvivalObjectiveFormat objective = (SurvivalObjectiveFormat) this.scoreboardHandler.getObjective(online, "factionOnline");

			if (objective != null) {
				objective.update(factionOnline, factionSize);
			}
		}
	}
}