package eu.wuffy.survival.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class TreeFellerHandler extends IHandler<Survival> {

	private static final Random RANDOM = new Random();
	private static final List<Block> EMPTY_BLOCK_LIST = new ArrayList<Block>();

	private final Map<Runnable, Integer> runnables = new HashMap<Runnable, Integer>();

	private final EnumSet<BlockFace> blockFaceAround = EnumSet.of(
			BlockFace.SELF, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
			BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);

	private final EnumSet<Material> fellerTypes = EnumSet.of(Material.DIAMOND_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.STONE_AXE, Material.WOODEN_AXE);
	private final EnumSet<Material> logTypes = EnumSet.of(Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG);
	private final Map<Material, Material> leaveTypes = new HashMap<Material, Material>();

	private final List<Player> playerEnabled = new ArrayList<Player>();

	public TreeFellerHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.leaveTypes.put(Material.ACACIA_LOG, Material.ACACIA_LEAVES);
		this.leaveTypes.put(Material.BIRCH_LOG, Material.BIRCH_LEAVES);
		this.leaveTypes.put(Material.DARK_OAK_LOG, Material.DARK_OAK_LEAVES);
		this.leaveTypes.put(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES);
		this.leaveTypes.put(Material.OAK_LOG, Material.OAK_LEAVES);
		this.leaveTypes.put(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES);
	}

	@Override
	public void onDisable() {
		this.playerEnabled.clear();
		this.runnables.values().forEach(runnableId -> Bukkit.getScheduler().cancelTask(runnableId));
	}

	public boolean togglePlayerUsage(Player player) {
		if (this.playerEnabled.contains(player)) {
			this.playerEnabled.remove(player);
			return false;
		} else {
			this.playerEnabled.add(player);
			return true;
		}
	}

	public void onPlayerQuit(Player player) {
		this.playerEnabled.remove(player);
	}

	public boolean onBlockBreak(Block block, ItemStack item, int logLimit) {
		if (this.isLogMaterial(block.getType()) && this.hasFellerItem(item.getType())) {
			Damageable itemMeta = (Damageable) item.getItemMeta();
			int itemDamage = itemMeta.getDamage();
			int canBreak = item.getType().getMaxDurability() - itemDamage - 1;

			boolean destroyLeaves = true;

			if (canBreak > 1) {
				List<Block> logs = this.getAllLogs(new HashSet<Block>(), block, logLimit).stream().collect(Collectors.toList());
				int addItemDamage = logs.size();

				int unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
				if (unbreakingLevel > 0) {
					int chance = 100 / (unbreakingLevel + 1);

					for (int i = 0; i < logs.size(); i++) {
						if (TreeFellerHandler.RANDOM.nextInt(100) > chance)
							addItemDamage--;
					}
				}

				if (canBreak > addItemDamage) {
					itemMeta.setDamage(itemDamage + addItemDamage);
				} else {
					int removeFromBreak = addItemDamage - canBreak;

					while (removeFromBreak > 0) {
						removeFromBreak--;
						logs.remove(logs.size() - 1);
					}

					itemMeta.setDamage(itemDamage + canBreak);
					destroyLeaves = false;
				}

				item.setItemMeta((ItemMeta) itemMeta);

				Runnable runnable = this.buildSchedule(block, item, logs.stream().sorted(Comparator.comparingInt(log -> log.getY())).collect(Collectors.toList()), destroyLeaves ? getAllLeaves(logs).stream().collect(Collectors.toList()) : TreeFellerHandler.EMPTY_BLOCK_LIST);
				this.runnables.put(runnable, Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getCore(), runnable, 2, 2));

				return true;
			}
		}
		return false;
	}

	private Runnable buildSchedule(Block block, ItemStack item, List<Block> logs, List<Block> leaves) {
		Collections.shuffle(leaves);

		return new Runnable() {

			@Override
			public void run() {
				try {
					Block block;
					if (logs.size() > 0) {
						block = logs.remove(0);
					} else if (leaves.size() > 0) {
						block = leaves.remove(0);
					} else {
						Bukkit.getScheduler().cancelTask(runnables.get(this));
						return;
					}

					if (isLogMaterial(block.getType()) || isLeaveMaterial(block.getType())) {
						block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(.5, .5, .5), 15, 0.5, 0.5, 0.5, block.getBlockData());
						block.breakNaturally();
					}
				} catch(Exception e) {
					e.printStackTrace();
					Bukkit.getScheduler().cancelTask(runnables.get(this));
				}
			}
		};
	}

	/**
	 * @return A list of all logs with a limit by (logLimit + 1)
	 */
	public Set<Block> getAllLogs(Set<Block> blocks, Block block, int logLimit) {
		if (logLimit < blocks.size())
			return blocks;

		for (int i = 0; i < 2; i++) {
			Block blockUp = block.getRelative(0, i, 0);

			for (BlockFace blockFace : this.blockFaceAround) {
				Block blockCheck = blockUp.getRelative(blockFace);

				if (blockCheck.getType() == block.getType() && !blocks.contains(blockCheck)) {
					blocks.add(blockCheck);
					this.getAllLogs(blocks, blockCheck, logLimit);
				}
			}
		}

		return blocks;
	}

	public Set<Block> getAllLeaves(List<Block> blocks) {
		Set<Block> leaves = new HashSet<Block>();
		Material leaveMaterial = this.leaveTypes.get(blocks.get(0).getType());
		int startLogY = blocks.get(0).getY();
		int highestLogY = blocks.stream().map(block -> block.getY()).sorted(Comparator.reverseOrder()).findFirst().get();

		for (Block block : blocks) {
			int currentlyLogY = block.getY();
			int distance;

			switch (leaveMaterial) {
				case OAK_LEAVES:
					if (currentlyLogY - startLogY > 5)
						distance = 5;
					else
						distance = 3;
					break;

				case BIRCH_LEAVES:
					distance = 3;
				break;

				case SPRUCE_LEAVES:
					distance = 4;
					break;

				case DARK_OAK_LEAVES:
				case ACACIA_LEAVES:
					if (highestLogY - currentlyLogY < 1)
						distance = 4;
					else
						distance = 3;
					break;

				case JUNGLE_LEAVES:
					if (highestLogY - currentlyLogY < 4)
						distance = 6;
					else
						distance = 4;
					break;

				default:
					distance = 3;
					break;
			}

			this.getLeavesForLog(new HashSet<Block>(), leaveMaterial, block.getLocation(), block, distance).stream()
				.filter(leave -> !leaves.contains(leave))
				.forEach(leave -> leaves.add(leave));
		}

		return leaves;
	}

	public Set<Block> getLeavesForLog(Set<Block> leaves, Material leaveMaterial, Location logLocation, Block lastLeave, int distance) {
		for (int i = -1; i < 2; i++) {
			Block blockUp = lastLeave.getRelative(0, i, 0);

			for (BlockFace blockFace : this.blockFaceAround) {
				Block blockCheck = blockUp.getRelative(blockFace);

				if (blockCheck.getType() == leaveMaterial && !leaves.contains(blockCheck) && blockCheck.getLocation().distance(logLocation) < distance) {
					leaves.add(blockCheck);
					this.getLeavesForLog(leaves, leaveMaterial, logLocation, blockCheck, distance);
				}
			}
		}

		return leaves;
	}

	public boolean isLogMaterial(Material material) {
		return this.logTypes.contains(material);
	}

	public boolean isLeaveMaterial(Material material) {
		return this.leaveTypes.values().contains(material);
	}

	public boolean hasFellerItem(Material material) {
		return this.fellerTypes.contains(material);
	}

	public boolean isEnabledForPlayer(Player player) {
		return this.playerEnabled.contains(player);
	}
}