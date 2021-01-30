package eu.wuffy.core.portal;

import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Portal {

	private final String world;

	private final DoubleRange x;
	private final DoubleRange y;
	private final DoubleRange z;

	private final PortalAction action;

	public Portal(String world, double startX, double startY, double startZ, double endX, double endY, double endZ, PortalAction action) {
		this.world = world;
		this.action = action;

		if (startX > endX) {
			this.x = new DoubleRange(startX, endX);
		} else {
			this.x = new DoubleRange(endX, startX);
		}
		if (startY > endY) {
			this.y = new DoubleRange(startY, endY);
		} else {
			this.y = new DoubleRange(endY, startY);
		}
		if (startZ > endZ) {
			this.z = new DoubleRange(startZ, endZ);
		} else {
			this.z = new DoubleRange(endZ, startZ);
		}
	}

	public boolean isInPortal(Location location) {
		if (!location.getWorld().getName().contentEquals(this.world)) {
			return false;
		}

		if (this.x.containsDouble(location.getX()) && this.y.containsDouble(location.getY())
				&& this.z.containsDouble(location.getZ())) {
			return true;
		}

		return false;
	}

	public boolean canUsePortal(Player player) {
		return true;
	}

	public PortalAction getAction() {
		return this.action;
	}
}