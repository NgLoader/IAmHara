package eu.wuffy.core.npc.npc.entity;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers.NBT;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.feature.NPCEquipment;
import eu.wuffy.core.npc.npc.type.NPCEntityLiving;
import eu.wuffy.core.npc.tablist.NPCTabList;
import eu.wuffy.core.npc.wrapper.EntityFlag;

public class NPCPlayer extends NPCEntityLiving {

	private final NPCEquipment equipment = new NPCEquipment(this, this::sendPacket);
	private final NPCTabList<Core<?>> tabList;

	private WrappedGameProfile gameProfile;

	private boolean tabListVisiblity = false;

	public NPCPlayer(NPCRegistry<Core<?>> registry, String name, Location location) {
		super(registry, 1.62d, location);
		this.tabList = this.manager.getTabList();
		this.gameProfile = new WrappedGameProfile(this.uuid, name);

		this.setFlag(EntityFlag.PLAYER_HAT, true);
		this.setFlag(EntityFlag.PLAYER_CAPE, true);
		this.setFlag(EntityFlag.PLAYER_JACKET, true);
		this.setFlag(EntityFlag.PLAYER_LEFT_SLEEVE, true);
		this.setFlag(EntityFlag.PLAYER_RIGHT_SLEEVE, true);
		this.setFlag(EntityFlag.PLAYER_LEFT_PANTS_LEG, true);
		this.setFlag(EntityFlag.PLAYER_RIGHT_PANTS_LEG, true);
	}

	@Override
	protected void createSpawnPackets() {
		PacketContainer playerInfoPacket = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
		playerInfoPacket.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
		playerInfoPacket.getPlayerInfoDataLists().write(0, Arrays.asList(new PlayerInfoData(
				this.gameProfile,
				0,
				NativeGameMode.NOT_SET,
				WrappedChatComponent.fromText(this.gameProfile.getName()))));
		this.spawnPackets.add(playerInfoPacket);

		PacketContainer namedEntitySpawnPacket = this.protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		namedEntitySpawnPacket.getIntegers().write(0, this.entityId);
		namedEntitySpawnPacket.getUUIDs().write(0, this.uuid);
		namedEntitySpawnPacket.getDoubles().write(0, this.location.getX());
		namedEntitySpawnPacket.getDoubles().write(1, this.location.getY());
		namedEntitySpawnPacket.getDoubles().write(2, this.location.getZ());
		namedEntitySpawnPacket.getBytes().write(0, (byte) ((int) (this.location.getYaw() * 256F / 360F)));
		namedEntitySpawnPacket.getBytes().write(1, (byte) ((int) (this.location.getPitch() * 256F / 360F)));
		this.spawnPackets.add(namedEntitySpawnPacket);

		PacketContainer entityHeadRotationPacket = this.protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		entityHeadRotationPacket.getIntegers().write(0, this.entityId);
		entityHeadRotationPacket.getBytes().write(0, (byte) ((int) (this.location.getYaw() * 256F / 360F)));
		this.spawnPackets.add(entityHeadRotationPacket);

		super.createSpawnPackets();

		this.equipment.addPackets(this.spawnPackets);
	}

	@Override
	protected void createDespawnPackets() {
		PacketContainer playerInfoPacket = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
		playerInfoPacket.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
		playerInfoPacket.getPlayerInfoDataLists().write(0, Arrays.asList(new PlayerInfoData(
				this.gameProfile,
				0,
				NativeGameMode.NOT_SET,
				null)));
		this.despawnPackets.add(playerInfoPacket);

		super.createDespawnPackets();
	}

	@Override
	protected void onSpawn(Player player) {
		if (!tabListVisiblity) {
			this.tabList.schedule(player, this);
		}
	}

	@Override
	public void onDespawn(Player player) {
		if (!tabListVisiblity) {
			this.tabList.cancel(player, this);
		}
	}

	public void setAdditionalHearts(float hearts) {
		this.setMetadata(14, Float.class, hearts);
	}

	public void setScore(int score) {
		this.setMetadata(15, Integer.class, score);
	}

	public void setSkinPartCape(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_CAPE, enabled);
	}

	public void setSkinPartJacket(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_JACKET, enabled);
	}

	public void setSkinPartLeftSleeve(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_LEFT_SLEEVE, enabled);
	}

	public void setSkinPartRightSleeve(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_RIGHT_SLEEVE, enabled);
	}

	public void setSkinPartLeftPantsLeg(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_LEFT_PANTS_LEG, enabled);
	}

	public void setSkinPartRightPantsLeg(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_RIGHT_PANTS_LEG, enabled);
	}

	public void setSkinPartHat(boolean enabled) {
		this.setFlag(EntityFlag.PLAYER_HAT, enabled);
	}

	public void setMainHand(EnumWrappers.Hand hand) {
		this.setMetadata(17, Byte.class, (byte) hand.ordinal());
	}

	public void setLeftShoulderEntityData(NBT nbt) {
		// TODO implement
	}

	public void setRightShoulderEntityData(NBT nbt) {
		// TODO implement
	}

	public void setTextures(String value, String signature) {
		this.gameProfile.getProperties().removeAll("textures");
		this.gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));
	}

	public void updateGameProfile() {
		this.setDirty();
		this.respawn();
	}

	public void setTabListVisiblity(boolean tabListVisiblity) {
		this.tabListVisiblity = tabListVisiblity;
	}

	public boolean isTabListVisiblity() {
		return tabListVisiblity;
	}

	public WrappedGameProfile getGameProfile() {
		return this.gameProfile;
	}

	public NPCEquipment getEquipment() {
		return this.equipment;
	}

	public NPCTabList<Core<?>> getTabList() {
		return this.tabList;
	}
}