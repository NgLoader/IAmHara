package eu.wuffy.core.util;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.mojang.authlib.GameProfile;

import eu.wuffy.synced.util.ReflectionUtil;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class PlayerUtil {

	public static final String SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);

	public static int getPlayerPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
	}

	public static void sendPacket(Collection<? extends Player> players, Packet<?>... packets) {
		for (Player player : players) {
			PlayerUtil.sendPacket(player, packets);
		}
	}

	public static void sendPacket(Player[] players, Packet<?>... packets) {
		for (Player player : players) {
			PlayerUtil.sendPacket(player, packets);
		}
	}

	public static void sendPacket(Player player, Packet<?>... packets) {
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		for (Packet<?> packet : packets) {
			playerConnection.sendPacket(packet);
		}
	}

	public static PacketPlayOutPlayerInfo addToTabList(Player player, String displayName) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER);
		Team team = player.getScoreboard().getEntryTeam(player.getName());

		ReflectionUtil.setField(packet, "b", Arrays.asList(packet.new PlayerInfoData(
				PlayerUtil.getGameProfile(player),
				0,
				EnumGamemode.NOT_SET,
				new ChatComponentText((team != null ? team.getPrefix() + team.getColor() : "") + displayName))));
		return packet;
	}

	public static PacketPlayOutPlayerInfo removeFromTabList(Player player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER);
		ReflectionUtil.setField(packet, "b", Arrays.asList(packet.new PlayerInfoData(PlayerUtil.getGameProfile(player), 0, null, null)));
		return packet;
	}

	public static CraftPlayer getCraftPlayer(Player player) {
		return ((CraftPlayer) player);
	}

	public static GameProfile getGameProfile(CraftPlayer player) {
		return player.getProfile();
	}

	public static GameProfile getGameProfile(Player player) {
		return ((CraftPlayer) player).getProfile();
	}

	public static PlayerConnection getPlayerConnection(CraftPlayer player) {
		return player.getHandle().playerConnection;
	}

	public static PlayerConnection getPlayerConnection(Player player) {
		return ((CraftPlayer) player).getHandle().playerConnection;
	}
}