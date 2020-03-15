package eu.wuffy.core.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.mojang.authlib.GameProfile;

import eu.wuffy.synced.util.ReflectionUtil;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.EnumGamemode;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_13_R2.PlayerConnection;

public class NMSUtil {

	public static final String SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
	

	public static int getPlayerPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
		/*
		try {
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + NMSUtil.SERVER_VERSION + ".entity.CraftPlayer");
			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);

			return ping.intValue();
		} catch (Exception e) {
			return -1;
		}
		*/
	}

	public static void sendPacket(Packet<?> ...packets) {
		Bukkit.getOnlinePlayers().forEach(player -> NMSUtil.sendPacket(player, packets));
	}

	public static void sendPacket(Player player, Packet<?> ...packets) {
		PlayerConnection playerConnection = NMSUtil.getPlayerConnection(player);

		for (Packet<?> packet : packets) {
			playerConnection.sendPacket(packet);
		}
	}

	public static void sendPacket(List<Player> players, Packet<?> ...packets) {
		players.forEach(player -> NMSUtil.sendPacket(player, packets));
	}

	public static PacketPlayOutPlayerInfo addToTabList(Player player, String displayName) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER);
		Team team = player.getScoreboard().getEntryTeam(player.getName());

		ReflectionUtil.set(packet, "b", Arrays.asList(packet.new PlayerInfoData(
				NMSUtil.getGameProfile(player),
				0,
				EnumGamemode.NOT_SET,
				new ChatComponentText((team != null ? team.getPrefix() + team.getColor() : "") + displayName))));
		return packet;
	}

	public static PacketPlayOutPlayerInfo removeFromTabList(Player player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER);
		ReflectionUtil.set(packet, "b", Arrays.asList(packet.new PlayerInfoData(
				NMSUtil.getGameProfile(player),
				0,
				null,
				null)));
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