package eu.wuffy.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SearchUtil {

	public static List<LivingEntity> searchEntityAndPlayers(String search, World world) {
		return SearchUtil.searchEntityAndPlayers(search, world, -1);
	}

	public static List<LivingEntity> searchEntityAndPlayers(String search, World world, int limit) {
		boolean all = false;

		switch (search) {
		case "@a":
		case "@all":
			if (limit == -1) {
				return world != null ? world.getLivingEntities() : new ArrayList<>();
			}

			all = true;
			break;

		case "@p":
		case "@player":
		case "@players":
			return SearchUtil.searchPlayers(search, false, limit).stream().collect(Collectors.toList());
		}

		List<LivingEntity> found = new ArrayList<>();

		if (world != null) {
			int count = 0;

			for (LivingEntity entity : world.getLivingEntities()) {
				if (found.contains(entity) || !(all || entity.getName().startsWith(search))) {
					continue;
				}

				if (limit != -1) {
					count++;
					if (count > limit) {
						return found;
					}
				}

				found.add(entity);
			}
		}

		return found;
	}

	public static List<Player> searchPlayers(String search, boolean exact) {
		return SearchUtil.searchPlayers(search, exact, -1);
	}

	public static List<Player> searchPlayers(String search, boolean exact, int limit) {
		boolean players = false;

		switch (search) {
			case "@p":
			case "@player":
			case "@players":
				if (limit == -1) {
					return Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
				}

				players = true;
				break;
		}

		List<Player> found = new ArrayList<>();
		int count = 0;

		Player exactPlayer = Bukkit.getPlayerExact(search);
		if (exact && exactPlayer != null) {
			found.add(exactPlayer);
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!(players || player.getDisplayName().startsWith(search)) || found.contains(player))  {
				continue;
			}

			if (limit != -1) {
				count++;
				if (count > limit) {
					return found;
				}
			}

			found.add(player);
		}

		return found;
	}
}