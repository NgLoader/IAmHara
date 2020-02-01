package eu.wuffy.survival.handler.dynmap;

import org.dynmap.DynmapAPI;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class DynmapHandler extends IHandler<Survival> {

	private DynmapAPI dynmap;
	private DynmapRegion dynmapRegion;

	public DynmapHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.dynmap = (DynmapAPI) this.getCore().getServer().getPluginManager().getPlugin("dynmap");

		this.dynmapRegion = new DynmapRegion(this.getCore(), this.dynmap);

		this.dynmapRegion.loadWorldGuardRegions();
		this.dynmapRegion.loadGriefProventionClaims();
	}

	public DynmapAPI getDynmap() {
		return this.dynmap;
	}

	public DynmapRegion getDynmapRegion() {
		return this.dynmapRegion;
	}
}