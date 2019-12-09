package eu.wuffy.mobpvp.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.kits.blaze.KitBlaze;
import eu.wuffy.mobpvp.util.PlayerUtil;
import eu.wuffy.synced.IHandler;
import me.libraryaddict.disguise.DisguiseAPI;

public class KitHandler extends IHandler<MobPvP> {

	private final Map<UUID, Kit> kitByPlayers = new HashMap<UUID, Kit>();
	private final Map<String, Kit> kitByNames = new HashMap<String, Kit>();

	public KitHandler(MobPvP core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.addKit(new KitBlaze(this.core));
	}

	public Kit searchKit(String name) {
		return this.kitByNames.get(name.toLowerCase());
	}

	public void select(Player player, Kit kit) {
		Kit oldKit = this.kitByPlayers.put(player.getUniqueId(), kit);

		if (oldKit != null) {
			oldKit.leave(player);
		}

		if (kit == null) {
			DisguiseAPI.undisguiseToAll(player);
			PlayerUtil.resetPlayer(player);

			// TODO teleport to lobby back
		} else {
			kit.join(player);
			// TODO teleport to random point in map
		}
	}

	public void remove(Player player) {
		Kit oldKit = this.kitByPlayers.get(player.getUniqueId());

		if (oldKit != null) {
			oldKit.remove(player);
			DisguiseAPI.undisguiseToAll(player);
		}

		this.kitByPlayers.remove(player.getUniqueId());
	}

	private void addKit(Kit kit) {
		this.kitByNames.put(ChatColor.stripColor(kit.getName()), kit);
	}
}