package eu.wuffy.mobpvp.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.LocationHandler;
import eu.wuffy.mobpvp.handler.damage.DamageHandler;
import eu.wuffy.mobpvp.handler.damage.DamageStatus;
import eu.wuffy.mobpvp.kits.blaze.KitBlaze;
import eu.wuffy.mobpvp.util.PlayerUtil;
import eu.wuffy.synced.IHandler;
import me.libraryaddict.disguise.DisguiseAPI;

public class KitHandler extends IHandler<MobPvP> {

	private final Map<UUID, Kit> kitByPlayers = new HashMap<UUID, Kit>();
	private final Map<String, Kit> kitByNames = new HashMap<String, Kit>();

	private DamageHandler damageHandler;
	private LocationHandler locationHandler;

	public KitHandler(MobPvP core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.damageHandler = this.core.getDamageHandler();
		this.locationHandler = this.core.getLocationHandler();

		this.addKit(new KitBlaze(this.core));
	}

	public Kit searchKit(String name) {
		return this.kitByNames.get(name.toLowerCase());
	}

	public void select(Player player, Kit kit) {
		if (this.kitByPlayers.getOrDefault(player.getUniqueId(), null) != null) {
			player.sendMessage(MobPvP.PREFIX + "§7Du hast bereits ein §cKit §7gewählt.");
			return;
		}

		this.kitByPlayers.put(player.getUniqueId(), kit);
		this.damageHandler.add(player, kit.getType());

		kit.add(player);

		this.locationHandler.teleport(player, "map");
		player.sendMessage(MobPvP.PREFIX + "§7Du hast das Kit §8'§a" + kit.getType().name + "§8' §7gewählt.");
	}

	public void remove(Player player) {
		Kit oldKit = this.kitByPlayers.get(player.getUniqueId());

		if (oldKit != null) {
			oldKit.remove(player);
			this.kitByPlayers.remove(player.getUniqueId());
		}

		DisguiseAPI.undisguiseToAll(player);
		PlayerUtil.resetPlayer(player);

		DamageStatus damageStatus = this.damageHandler.remove(player);

		if (damageStatus != null) {
			damageStatus.finish();
		}

		this.locationHandler.teleport(player, "spawn");
	}

	public Kit getPlayerKit(Player player) {
		return this.kitByPlayers.getOrDefault(player.getUniqueId(), null);
	}

	private void addKit(Kit kit) {
		this.kitByNames.put(kit.getType().name.toLowerCase(), kit);
		this.kitByNames.put(ChatColor.stripColor(kit.getType().name).toLowerCase(), kit);
	}
}