package eu.wuffy.core.npc.hologram;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.entity.NPCArmorStand;

public class Hologram {

	public static final double DEFAULT_SPACE = 0.235;

	private final NPCRegistry<Core<?>> registry;

	private Location location;
	private double defaultSpace = 0.235;

	private List<HologramLine> lines = new ArrayList<>();

	public Hologram(NPCRegistry<Core<?>> registry, Location location) {
		this.registry = registry;
		this.location = location;
	}

	public Hologram addLine(String... texts) {
		for (String text : texts) {
			NPCArmorStand npc = new NPCArmorStand(this.registry, this.location);
			npc.setCustonNameVisible(true);
			npc.setHasNoGravity(true);
			npc.setIsInvisible(true);
			npc.create();

			HologramLine line = new HologramLine(this, npc, 0, ChatColor.translateAlternateColorCodes('&', text));
			this.lines.add(line);
		}

		this.rescale();
		return this;
	}

	public Hologram setLine(int line, String text) {
		HologramLine hologramLine = this.getLine(line);
		if (hologramLine != null) {
			hologramLine.setText(ChatColor.translateAlternateColorCodes('&', text));
			return this;
		}

		this.addLine(ChatColor.translateAlternateColorCodes('&', text));
		return this;
	}

	public void removeLine(int line) {
		HologramLine hologramLine = this.getLine(line);
		if (hologramLine != null) {
			this.removeLine(hologramLine);
		}
	}

	public void removeLine(HologramLine line) {
		this.lines.remove(line);
		line.getNPC().destroy();
	}

	public HologramLine getLine(int line) {
		if (line > -1 && this.lines.size() <= line) {
			return null;
		}

		return this.lines.get(line);
	}

	public void rescale() {
		Location location = this.location.clone();

		for (int i = this.lines.size() - 1; i >= 0; i--) {
			HologramLine line = this.lines.get(i);
			NPCArmorStand npc = line.getNPC();

			if (!npc.getLocation().equals(location)) {
				npc.setLocation(location.clone());
			}

			npc.setCustomName(line.getText());
			npc.updateDataWatcher();

			location.add(0, line.getSpace() != 0 ? line.getSpace() : this.defaultSpace, 0);
		}
	}

	public double getDefaultSpace() {
		return this.defaultSpace;
	}

	public void setDefaultSpace(double defaultSpace) {
		this.defaultSpace = defaultSpace;
		this.rescale();
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
		this.rescale();
	}

	public NPCRegistry<Core<?>> getRegistry() {
		return this.registry;
	}
}