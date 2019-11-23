package eu.wuffy.survival.handler;

import java.util.Optional;

import org.bukkit.Bukkit;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;

public class LuckPermsHandler extends IHandler<Survival> {

	private LuckPermsApi luckPermsApi;

	public LuckPermsHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		Optional<LuckPermsApi> optional = LuckPerms.getApiSafe();

		if (optional.isPresent()) {
			this.luckPermsApi = optional.get();
		} else
			Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§4LuckPermsApi §ckonnte nicht gefunden werden§8.");
	}

	public LuckPermsApi getApi() {
		return this.luckPermsApi;
	}
}