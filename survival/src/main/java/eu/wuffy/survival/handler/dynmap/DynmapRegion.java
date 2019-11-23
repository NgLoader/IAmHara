package eu.wuffy.survival.handler.dynmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import eu.wuffy.survival.Survival;

public class DynmapRegion {

	private static final String DYNMAP_HTML_CODE = "<div class=\"infowindow\"><span style=\"font-size:120%;\">%regionname%</span></div>";

	private final WorldGuard worldGuard = WorldGuard.getInstance();
	private final Survival core;
	private final DynmapAPI dynmap;
	private final MarkerAPI marker;

	private Map<String, MarkerSet> markerSetByName = new HashMap<String, MarkerSet>();
	private Map<String, Map<String, String>> regionDescriptions = new HashMap<String, Map<String, String>>(); //id, name, description => spawn_dorf => Hier ist ein dorf
	private List<String> blacklistRegions = new ArrayList<String>();

	public DynmapRegion(Survival core, DynmapAPI dynmap) {
		this.core = core;
		this.dynmap = dynmap;
		this.marker = this.dynmap.getMarkerAPI();

		this.blacklistRegions.add(ProtectedRegion.GLOBAL_REGION);
		this.regionDescriptions.put("spawn", new HashMap<String, String>());
		this.regionDescriptions.get("spawn").put("dorf", "<div class=\"infowindow\"><span style=\"font-size:120%;\">Hier könnte deine werbung stehen xD</span><br /> Pascal ist <span style=\"font-weight:bold;\">dumm</span></div>");
	}

	public void loadWorldGuardRegions() {
		int regionsFound = 0, addedToDynmap = 0;

		this.markerSetByName.values().forEach(marker -> marker.deleteMarkerSet());
		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Removed §2" + this.markerSetByName.size() + " §7WorldGuard region markers§8.");
		this.markerSetByName.clear();

		RegionContainer regionContainer = this.worldGuard.getPlatform().getRegionContainer();
		for (World bukkitWorld : Bukkit.getWorlds()) {
			com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(bukkitWorld);
			RegionManager regionManager = regionContainer.get(world);

			if (regionManager != null) {
				for (ProtectedRegion region : regionManager.getRegions().values()) {
					regionsFound++;

					boolean skip = false;
					for (String blacklisted : this.blacklistRegions) {
						if (region.getId().equalsIgnoreCase(blacklisted)) {
							skip = true;
							Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Skipping region §a" + region.getId() + " §7region§8.");
							break;
						}
					}

					if (skip) continue;

					String markerName = this.getMarkerName(region.getId());
					String regionName = this.getRegionName(region.getId());
					MarkerSet markerSet = this.getMarkerSet("survival_" + markerName, markerName.substring(0, 1).toUpperCase() + markerName.substring(1));
					int size = region.getPoints().size() / 2;

					AreaMarker areaMarker = markerSet.findAreaMarker("survival_" + regionName);

					if (areaMarker != null) {
						areaMarker.deleteMarker();
					}

					areaMarker = markerSet.createAreaMarker(
							"survival_" + regionName,
							regionName.substring(0, 1).toUpperCase() + regionName.substring(1),
							false,
							world.getName(),
							new double[size], new double[size],
							false);

					int step = 0;
					for (BlockVector2 blockVector2 : region.getPoints()) {
						areaMarker.setCornerLocation(step++, blockVector2.getX(), blockVector2.getZ());
					}

					if (this.regionDescriptions.containsKey(markerName)) {
						areaMarker.setDescription(this.regionDescriptions.get(markerName).getOrDefault(regionName, DYNMAP_HTML_CODE.replace("%regionname%", regionName)));
					} else {
						areaMarker.setDescription(DYNMAP_HTML_CODE.replace("%regionname%", regionName));
					}

					addedToDynmap++;
				}
			}
		}

		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Found §2" + regionsFound + " §7WorldGuard regions§8.");
		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Added §2" + addedToDynmap + " §7regions to dynmap§8.");
	}

	public String getMarkerName(String regionName) {
		if (regionName.contains("_")) {
			return regionName.split("_")[0];
		}
		return regionName;
	}

	public String getRegionName(String regionName) {
		if (regionName.contains("_")) {
			return regionName.substring(regionName.split("_")[0].length() + 1);
		}
		return regionName;
	}

	public MarkerSet getMarkerSet(String collection, String collectionName) {
		collection = collection.toLowerCase();

		if (this.markerSetByName.containsKey(collection))
			return this.markerSetByName.get(collection);

		MarkerSet markerSet = this.marker.getMarkerSet(collection);

		if (markerSet == null) {
			markerSet = this.marker.createMarkerSet(collection, collectionName, null, false);
		} else {
			markerSet.setMarkerSetLabel(collectionName);
		}

		this.markerSetByName.put(collection, markerSet);
		return markerSet;
	}

	public void deleteMarkerSet(String collection) {
		collection = collection.toLowerCase();

		MarkerSet markerSet = this.marker.getMarkerSet(collection);
		markerSet.deleteMarkerSet();

		this.markerSetByName.remove(markerSet.getMarkerSetID());
	}

	public Survival getCore() {
		return this.core;
	}

	public DynmapAPI getDynmap() {
		return this.dynmap;
	}
}