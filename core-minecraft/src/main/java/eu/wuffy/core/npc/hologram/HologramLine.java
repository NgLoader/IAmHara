package eu.wuffy.core.npc.hologram;

import eu.wuffy.core.npc.npc.entity.NPCArmorStand;

public class HologramLine {

	private final Hologram hologram;
	private final NPCArmorStand npc;

	private double space;
	private String text;

	/**
	 * 
	 * @param npc hologram npc
	 * @param space space between parent hologram line
	 * @param text hologram text
	 */
	public HologramLine(Hologram hologram, NPCArmorStand npc, double space, String text) {
		this.hologram = hologram;
		this.npc = npc;
		this.space = space;
		this.text = text;
	}

	public double getSpace() {
		return this.space;
	}

	public void setSpace(double space) {
		this.space = space;
		this.hologram.rescale();
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;

		this.npc.setCustomName(this.text);
		this.npc.updateDataWatcher();
	}

	public NPCArmorStand getNPC() {
		return this.npc;
	}
}