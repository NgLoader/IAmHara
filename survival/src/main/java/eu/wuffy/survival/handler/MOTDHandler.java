package eu.wuffy.survival.handler;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.config.ConfigMOTD;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.config.ConfigService;

public class MOTDHandler extends IHandler<Survival> {

	public MOTDHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		ConfigMOTD config = ConfigService.getConfig(ConfigMOTD.class);
		((CraftServer) Bukkit.getServer()).getServer().setMotd(config.getMotd());
		ConfigService.removeConfig(config);
	}
}