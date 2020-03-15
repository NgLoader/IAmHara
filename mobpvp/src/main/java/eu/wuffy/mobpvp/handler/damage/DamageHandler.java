package eu.wuffy.mobpvp.handler.damage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.database.MobPvPDatabase;
import eu.wuffy.mobpvp.kits.KitType;
import eu.wuffy.synced.IHandler;

public class DamageHandler extends IHandler<MobPvP> {

	private final Map<Player, DamageStatus> playerDamage = new HashMap<Player, DamageStatus>();

	private MobPvPDatabase database;

	public DamageHandler(MobPvP core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.database = this.core.getDatabase();
	}

	public void add(Player player, KitType type) {
		this.playerDamage.put(player, new DamageStatus(this.database, player, type));
	}

	public DamageStatus get(Player player) {
		return this.playerDamage.get(player);
	}

	public DamageStatus remove(Player player) {
		return this.playerDamage.remove(player);
	}
}