package eu.wuffy.mobpvp.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.util.PlayerUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;

public abstract class Kit implements Listener {

	private final List<UUID> players = new ArrayList<UUID>();

	protected final MobPvP core;
	protected final KitType type;

	protected abstract void join(Player player);
	protected abstract void leave(Player player);

	public Kit(MobPvP core, KitType type) {
		this.core = core;
		this.type = type;
	}

	public void add(Player player) {
		this.players.add(player.getUniqueId());

		PlayerUtil.resetPlayer(player);
		this.join(player);
		this.disguise(player);
	}

	public void remove(Player player) {
		this.players.remove(player.getUniqueId());

		this.leave(player);
	}

	public void disguise(Player player) {
		MobDisguise disguise = new MobDisguise(DisguiseType.BLAZE);
		LivingWatcher watcher = disguise.getWatcher();

		watcher.setCustomName(player.getDisplayName());
		watcher.setCustomNameVisible(true);

		DisguiseAPI.disguiseToAll(player, disguise);
	}

	public boolean isPlayerKit(Player player) {
		return this.players.contains(player.getUniqueId());
	}

	public KitType getType() {
		return this.type;
	}

	public MobPvP getCore() {
		return this.core;
	}
}