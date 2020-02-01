package eu.wuffy.lobby.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.wuffy.core.portal.Portal;
import eu.wuffy.core.portal.PortalAction;
import eu.wuffy.core.portal.PortalHandler;
import eu.wuffy.core.portal.action.PortalActionServerChange;
import eu.wuffy.core.portal.action.PortalActionTeleport;
import eu.wuffy.lobby.Lobby;

public class LobbyPortalHandler extends PortalHandler {

	private final BuildHandler buildHandler;

	public LobbyPortalHandler(Lobby lobby) {
		super(lobby);

		this.buildHandler = lobby.getBuildHandler();
	}

	@Override
	public void onEnable() {
		super.onEnable();

		World world = Bukkit.getWorld("world");

		this.portals.add(new LobbyPortal("world", 36, 68, -42, 43, 65, -38,
				new PortalActionServerChange(this.core, "survival")));

		this.portals.add(new LobbyPortal("world", -19, 67, 3, -15, 64, -1,
				new PortalActionTeleport(new Location(world, -33.5, 89.5, 54.5))));
		this.portals.add(new LobbyPortal("world", -35, 86, 53, -30, 81, 56,
				new PortalActionTeleport(new Location(world, -18.5, 65.5, 5.5, -56, 0))));
	}

	private class LobbyPortal extends Portal {

		public LobbyPortal(String world, double startX, double startY, double startZ, double endX, double endY,
				double endZ, PortalAction action) {
			super(world, startX, startY, startZ, endX, endY, endZ, action);
		}

		@Override
		public boolean canUsePortal(Player player) {
			return !buildHandler.isInBuildMode(player);
		}
	}
}