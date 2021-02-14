package eu.wuffy.survival.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.config.ConfigChair;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.config.ConfigService;

public class ChairHandler extends IHandler<Survival> implements Listener, Runnable {

	private static final String METADATA_CHAIR = "chair";

	private final FixedMetadataValue chairMetadataValue;

	private final List<BlockFace> aroundBlockFaces = Stream.of(
			BlockFace.SELF,
			BlockFace.EAST, BlockFace.NORTH_EAST,
			BlockFace.NORTH, BlockFace.SOUTH_EAST,
			BlockFace.WEST, BlockFace.NORTH_WEST,
			BlockFace.SOUTH, BlockFace.SOUTH_WEST,
			BlockFace.UP)
			.collect(Collectors.toList());

	private final Set<Material> allowedChairs = new HashSet<>();
	private final Map<Material, Set<BlockFace>> allowedBlockFaces = new HashMap<>();
	private final Map<Material, Double> chairArmorStandDistance = new HashMap<>(); 

	private final Map<Player, ChairSitter> chairInUseByPlayer = new HashMap<>();
	private final Map<Location, ChairSitter> chairInUseByLocation = new HashMap<>();

	private final Map<Player, Long> cooldown = new WeakHashMap<>();

	private ChairProtocolListener chairProtocolListener;

	protected String prefix;
	protected ConfigChair config;

	public ChairHandler(Survival plugin, String prefix)  {
		super(plugin);
		this.prefix = prefix;

		this.chairMetadataValue = new FixedMetadataValue(this.core, true);
	}

	@Override
	public void onEnable() {
		this.config = ConfigService.getConfig(ConfigChair.class);
		this.config.chairs.forEach(chair -> this.addChairsByMaterialName(chair.suffix, chair.height, chair.blockFaces));

		Bukkit.getPluginManager().registerEvents(this, this.core);

		Bukkit.getScheduler().runTaskTimer(this.core, this, 20 * 10, 20 * 10);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.core, this::runWorldCheck, 20 * 60 * 2, 20 * 60 * 2);

		this.chairProtocolListener = new ChairProtocolListener(this);
	}

	@Override
	public void onDisable() {
		this.chairProtocolListener.unregister();
	}

	@Override
	public void run() {
		Iterator<ChairSitter> sitters = this.chairInUseByLocation.values().iterator();
		while (sitters.hasNext()) {
			ChairSitter sitter = sitters.next();

			if (!sitter.armorStand.getPassengers().isEmpty() && sitter.player.isOnline()) {
				continue;
			}

			sitter.armorStand.remove();

			sitters.remove();
			this.chairInUseByPlayer.remove(sitter.player);
		}
	}

	public void runWorldCheck() {
		List<Entity> toRemove = new ArrayList<>();
		Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
			if (entity instanceof ArmorStand) {
				ArmorStand armorStand = (ArmorStand) entity;
				if (armorStand.hasMetadata(METADATA_CHAIR) && armorStand.getPassengers().isEmpty()) {
					toRemove.add(armorStand);
				}
			}
		}));

		if (!toRemove.isEmpty()) {
			Bukkit.getScheduler().runTask(this.core, () -> toRemove.forEach(Entity::remove));
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK ||
				event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock == null || !this.allowedChairs.contains(clickedBlock.getType())) {
			return;
		}

		Player player = event.getPlayer();
		PlayerInventory playerInventory = player.getInventory();
		if ((this.config.allowSneaking && player.isSneaking()) ||
				(!this.config.allowMainHand && playerInventory.getItemInMainHand().getType() != Material.AIR) ||
				(!this.config.allowOffHand && playerInventory.getItemInOffHand().getType() != Material.AIR)) {
			return;
		}


		Location clickedBlockLocation = clickedBlock.getLocation();
		if (!this.isSitable(clickedBlockLocation.clone().add(0, 1, 0).getBlock()) ||
				this.isSitable(clickedBlockLocation.clone().subtract(0, 1, 0).getBlock())) {
			return;
		}

		BlockState blockState = clickedBlock.getState();
		BlockData blockData = blockState.getBlockData();
		if (blockData instanceof Stairs) {
			Stairs stair = (Stairs) blockData;
			if (stair.isWaterlogged() ||
					stair.getHalf() == Half.TOP ||
					!this.isAllowedBlockFace(clickedBlock.getType(), stair.getFacing())) {
				return;
			}
		} else if (blockData instanceof Directional) {
			Directional directional = (Directional) blockState.getData();
			if (!this.isAllowedBlockFace(clickedBlock.getType(), directional.getFacing())) {
				return;
			}
		}

		ChairSitter sitter = this.chairInUseByLocation.get(clickedBlockLocation);
		if (sitter != null) {
			if (sitter.player.getUniqueId().equals(player.getUniqueId())) {
				return;
			}

			player.sendMessage(this.prefix + "Dieses §cSitzplatz §7ist bereits belegt§8.");
			return;
		}

		long couldown = this.cooldown.getOrDefault(player, -1l);
		if (System.currentTimeMillis() < couldown) {
			player.sendMessage(this.prefix + "Bitte warte noch einen augenblick§8.");
			return;
		}
		this.cooldown.put(player, System.currentTimeMillis() + this.config.delay);

		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
		event.setCancelled(true);

		Location armorStandLocation = clickedBlockLocation.clone().add(.5, -this.chairArmorStandDistance.getOrDefault(clickedBlock.getType(), 1d), .5);
		sitter = this.chairInUseByPlayer.get(player);
		if (sitter != null) {
			this.chairInUseByLocation.put(clickedBlockLocation, sitter);
			this.chairInUseByLocation.remove(sitter.location);

			sitter.teleport(clickedBlockLocation, armorStandLocation);
			return;
		}

		ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
		armorStand.setInvisible(true);
		armorStand.setInvulnerable(true);
		armorStand.setCollidable(false);
		armorStand.setBasePlate(false);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setSmall(true);
		armorStand.setCanPickupItems(false);
		armorStand.addPassenger(player);
		armorStand.setMetadata(METADATA_CHAIR, this.chairMetadataValue);

		sitter = new ChairSitter(player, armorStand, clickedBlockLocation);
		this.chairInUseByLocation.put(clickedBlockLocation, sitter);
		this.chairInUseByPlayer.put(player, sitter);
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof ArmorStand && entity.hasMetadata(METADATA_CHAIR)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractAtEvent(PlayerInteractAtEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof ArmorStand && entity.hasMetadata(METADATA_CHAIR)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ArmorStand && entity.hasMetadata(METADATA_CHAIR)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDismount(EntityDismountEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player) || !event.getDismounted().hasMetadata(METADATA_CHAIR)) {
			return;
		}

		Player player = (Player) entity;
		ChairSitter sitter = this.chairInUseByPlayer.get(player);
		if (sitter == null) {
			return;
		}
		this.removeChair(sitter);
		this.cooldown.put(player, System.currentTimeMillis() + this.config.delay);

		Bukkit.getScheduler().runTask(this.core, () -> {
			Location location = sitter.location.clone();
			BlockData blockData = location.getBlock().getBlockData();
			List<BlockFace> blockfaces;

			if (blockData instanceof Directional) {
				Directional directional = (Directional) blockData;
				BlockFace face = directional.getFacing();

				if (this.aroundBlockFaces.contains(face)) {
					blockfaces = new ArrayList<>(this.aroundBlockFaces);
					blockfaces.remove(face);
					blockfaces.add(1, face);
				} else {
					blockfaces = this.aroundBlockFaces;
				}
			} else {
				blockfaces = this.aroundBlockFaces;
			}

			Location freeLocation = null;
			int y = 0;
			while (y < 2 && freeLocation == null) {
				for (BlockFace blockFace : blockfaces) {
					Location checkLocation = location.clone().add(-blockFace.getModX(), blockFace.getModY(), -blockFace.getModZ());
					if (!checkLocation.subtract(0, 1, 0).getBlock().isPassable() &&
							this.isLocationTeleportable(checkLocation.add(0, 1, 0)) &&
							this.isLocationTeleportable(checkLocation.add(0, 1, 0))) {
						freeLocation = checkLocation.add(.5, -(1 - .275), .5);
						break;
					}
				}

				location.add(0, ++y, 0);
			}

			if (freeLocation == null) {
				freeLocation = sitter.location.add(.5, .35, .5);
			}
			freeLocation.setYaw(player.getLocation().getYaw());
			freeLocation.setPitch(player.getLocation().getPitch());
			player.teleport(freeLocation);
		});
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		this.removeChair(this.chairInUseByLocation.get(event.getBlock().getLocation()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.removeChair(this.chairInUseByPlayer.get(event.getPlayer()));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		this.removeChair(this.chairInUseByPlayer.get(event.getEntity()));
	}

	public void kickAllPlayersFromChair() {
		Bukkit.getOnlinePlayers().forEach(player -> this.removeChair(this.chairInUseByPlayer.get(player)));
	}

	private void removeChair(ChairSitter sitter) {
		if (sitter != null) {
			sitter.armorStand.eject();
			sitter.armorStand.removePassenger(sitter.player);
			sitter.armorStand.remove();

			this.chairInUseByPlayer.remove(sitter.player);
			this.chairInUseByLocation.remove(sitter.location);
		}
	}

	private boolean isAllowedBlockFace(Material material, BlockFace blockFace) {
		Set<BlockFace> allowedFaces = this.allowedBlockFaces.getOrDefault(material, Collections.emptySet());
		if (allowedFaces.isEmpty() || allowedFaces.contains(blockFace)) {
			return true;
		}
		return false;
	}

	private void addChairsByMaterialName(String nameEnding, double armorStandDistance, BlockFace... blockFaces) {
		for (Material material : Material.values()) {
			String name = material.name();
			if (!name.startsWith("LEGACY_") && name.endsWith(nameEnding)) {
				this.allowedChairs.add(material);
				this.chairArmorStandDistance.put(material, armorStandDistance);
				this.allowedBlockFaces.put(material, Stream.of(blockFaces).collect(Collectors.toSet()));
			}
		}
	}

	private boolean isLocationTeleportable(Location location) {
		Block block = location.getBlock();
		Material type = block.getType();

		if (block.isPassable() || type == Material.AIR) {
			return true;
		} else if (type.name().endsWith("_CARPET")) {
			return true;
		}
		return false;
	}

	private boolean isSitable(Block block) {
		return block.getType() == Material.AIR || block.isPassable();
	}

	private class ChairSitter {

		public final Player player;

		public ArmorStand armorStand;
		public Location location;

		public ChairSitter(Player player, ArmorStand armorStand, Location location) {
			this.player = player;
			this.armorStand = armorStand;
			this.location = location;
		}

		public void teleport(Location chairLocation, Location armorstandLoaction) {
			this.location = chairLocation;

			(((CraftEntity) this.armorStand)).getHandle().setPosition(armorstandLoaction.getX(), armorstandLoaction.getY(), armorstandLoaction.getZ());
			(((CraftEntity) this.armorStand)).getHandle().positionChanged = true;
		}
	}

	private class ChairProtocolListener extends PacketAdapter {

		private final ChairHandler handler;

		public ChairProtocolListener(ChairHandler handler) {
			super(handler.getCore(), PacketType.Play.Client.LOOK);
			this.handler = handler;

			ProtocolLibrary.getProtocolManager().addPacketListener(this);
		}

		public void unregister() {
			ProtocolLibrary.getProtocolManager().removePacketListener(this);
		}

		@Override
		public void onPacketReceiving(PacketEvent event) {
			ChairSitter chair = this.handler.chairInUseByPlayer.get(event.getPlayer());
			if (chair != null) {
				chair.armorStand.setRotation(event.getPacket().getFloat().read(0), 0);
			}
		}
	}
}