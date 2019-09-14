package eu.wuffy.core.util;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import eu.wuffy.synced.util.Pair;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.Node;

public class GroupUtil {

	public static boolean isPlayerInGroup(Player player, String group) {
		return player.hasPermission("group." + group);
	}

	public static Optional<String> getGroupPrefix(Group group) {
		return group.getAllNodes().stream()
				.filter(Node::isPrefix)
				.map(Node::getPrefix)
				.sorted(Comparator.comparingInt(Map.Entry::getKey))
				.map(prefix -> prefix.getValue())
				.findFirst();
	}

	public static Optional<String> getGroupSuffix(Group group) {
		return group.getAllNodes().stream()
				.filter(Node::isSuffix)
				.map(Node::getSuffix)
				.sorted(Comparator.comparingInt(Map.Entry::getKey))
				.map(suffix -> suffix.getValue())
				.findFirst();
	}

	public static String getGroupMeta(Group group, String metaName, String defaultMeta) {
		return group.getAllNodes().stream()
				.filter(Node::isMeta)
				.map(Node::getMeta)
				.filter(meta -> meta.getKey().equals(metaName))
				.map(meta -> meta.getValue())
				.findFirst().orElseGet(() -> defaultMeta);
	}

	public static String getGroupMetaSorted(Group group, String metaName, String defaultMeta) {
		return group.getAllNodes().stream()
				.filter(Node::isMeta)
				.map(Node::getMeta)
				.filter(meta -> {
					String[] args = meta.getValue().split("\\.");
					return args.length > 1 && meta.getKey().matches("\\d+") && args[0].equals(metaName);
				})
				.map(meta -> new Pair<Integer, String>(Integer.valueOf(meta.getKey()), meta.getValue().substring(meta.getValue().split("\\.")[0].length() + 1)))
				.sorted(Comparator.comparingInt(Map.Entry::getKey))
				.findFirst().orElseGet(() -> new Pair<Integer, String>(-1, defaultMeta)).getValue();
	}
}