package eu.wuffy.survival.handler.dynmap;

import org.dynmap.DynmapAPI;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class DynmapHandler extends IHandler<Survival> {
//
//	private DynmapAPI dynmap;
//	private DynmapRegion dynmapRegion;

	public DynmapHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		try {
			DynmapAPI dynmap = (DynmapAPI) this.getCore().getServer().getPluginManager().getPlugin("dynmap");

//			DynmapRegion dynmapRegion = new DynmapRegion(this.getCore(), dynmap);
//			dynmapRegion.loadWorldGuardRegions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public DynmapAPI getDynmap() {
//		return this.dynmap;
//	}
//
//	public DynmapRegion getDynmapRegion() {
//		return this.dynmapRegion;
//	}
}