package eu.wuffy.mobpvp.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.util.PlayerUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;

public abstract class Kit implements Listener {

	private final List<Player> players = new ArrayList<>();

	protected final MobPvP core;
	protected final KitType type;
	protected final KitInventory inventory;

	protected final Map<Player, KitCouldown> playerCouldown = new HashMap<>();

	protected abstract void join(Player player);
	protected abstract void leave(Player player);

	public Kit(MobPvP core, KitType type, KitInventory inventory) {
		this.core = core;
		this.type = type;
		this.inventory = inventory;
	}

	public void add(Player player) {
		this.players.add(player);
		this.playerCouldown.put(player, new KitCouldown());

		PlayerUtil.resetPlayer(player);
		this.join(player);
		this.inventory.apply(player.getInventory());
		this.disguise(player);
	}

	public void remove(Player player) {
		this.players.remove(player);
		this.playerCouldown.remove(player);

		this.leave(player);
	}

	public void disguise(Player player) {
		MobDisguise disguise = new MobDisguise(DisguiseType.BLAZE);
		LivingWatcher watcher = disguise.getWatcher();

		watcher.setCustomName(player.getDisplayName());
		watcher.setCustomNameVisible(false);

		DisguiseAPI.disguiseIgnorePlayers(player, disguise, player);
	}

	public boolean isPlayerKit(Player player) {
		return this.players.contains(player);
	}

	public boolean canUseItem(PlayerInteractEvent event, KitItems item) {
		Player player = event.getPlayer();

		if (!this.isPlayerKit(player)) {
			return false;
		}

		if (event.getItem() == null || event.getItem().getType() != item.getMaterial()) {
			return false;
		}

		if (this.getCouldown(player).hasCouldown(item)) {
			return false;
		}

		return true;
	}

	public boolean isRightClick(Action action) {
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}

	public boolean isLeftClick(Action action) {
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}

	public KitCouldown getCouldown(Player player) {
		KitCouldown couldown = this.playerCouldown.get(player);

		if (couldown == null) {
			couldown = new KitCouldown();
			this.playerCouldown.put(player, couldown);
		}

		return couldown;
	}

	public KitType getType() {
		return this.type;
	}

	public MobPvP getCore() {
		return this.core;
	}
}