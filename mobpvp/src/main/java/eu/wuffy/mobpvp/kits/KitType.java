package eu.wuffy.mobpvp.kits;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public enum KitType {

	BLAZE("§4Blaze", DisguiseType.BLAZE);

	public final String name;
	public final DisguiseType disguiseType;

	private KitType(String name, DisguiseType disguiseType) {
		this.name = name;
		this.disguiseType = disguiseType;
	}
}
