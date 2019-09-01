package eu.wuffy.core.util;

import org.bukkit.entity.Player;

import eu.wuffy.synced.Rank;

public class RankUtil {

	public static Rank getRankByPermission(Player player) {
		for (Rank rank : Rank.values())
			if(player.hasPermission("wuffy." + rank.name().toLowerCase()))
				return rank;
		return Rank.PLAYER;
	}
}