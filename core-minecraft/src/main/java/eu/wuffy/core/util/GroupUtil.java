package eu.wuffy.core.util;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import eu.wuffy.synced.util.Pair;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;

public class GroupUtil {

	public static boolean isPlayerInGroup(Player player, String group) {
		return player.hasPermission("group." + group);
	}

	public static Optional<String> getGroupPrefix(Group group) {
		return group.getNodes().stream()
				.filter(NodeType.PREFIX::matches)
				.map(NodeType.PREFIX::cast)
				.map(node -> new Pair<Integer, String>(node.getPriority(), node.getMetaValue()))
				.sorted(Comparator.comparingInt(Map.Entry::getKey))
				.map(prefix -> prefix.getValue())
				.findFirst();
	}

	public static Optional<String> getGroupSuffix(Group group) {
		return group.getNodes().stream()
				.filter(NodeType.SUFFIX::matches)
				.map(NodeType.SUFFIX::cast)
				.map(node -> new Pair<Integer, String>(node.getPriority(), node.getMetaValue()))
				.sorted(Comparator.comparingInt(Map.Entry::getKey))
				.map(suffix -> suffix.getValue())
				.findFirst();
//		return group.getNodes().stream()
//				.filter(Node::isSuffix)
//				.map(Node::getSuffix)
//				.sorted(Comparator.comparingInt(Map.Entry::getKey))
//				.map(suffix -> suffix.getValue())
//				.findFirst();
	}

	public static String getGroupMeta(Group group, String metaName, String defaultMeta) {
		return group.getNodes().stream()
				.filter(NodeType.META::matches)
				.map(NodeType.META::cast)
				.filter(meta -> meta.getMetaKey().equals(metaName))
				.map(meta -> meta.getMetaValue())
				.findFirst().orElseGet(() -> defaultMeta);
	}

	public static String getGroupMetaSorted(Group group, String metaName, String defaultMeta) {
		return group.getNodes().stream()
				.filter(NodeType.META::matches)
				.map(NodeType.META::cast)
				.filter(meta -> {
					String[] args = meta.getMetaValue().split("\\.");
					return args.length > 1 && meta.getMetaKey().matches("\\d+") && args[0].equals(metaName);
				})
				.map(meta -> new Pair<Integer, String>(Integer.valueOf(meta.getMetaKey()), meta.getMetaValue().substring(meta.getMetaValue().split("\\.")[0].length() + 1)))
				.sorted(Comparator.comparingInt(Map.Entry::getKey))
				.findFirst().orElseGet(() -> new Pair<Integer, String>(-1, defaultMeta)).getValue();
	}
}