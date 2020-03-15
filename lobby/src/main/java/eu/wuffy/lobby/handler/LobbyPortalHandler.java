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

		this.portals.add(new LobbyPortal("world", -366, 68, -301, -359, 65, -297,
				new PortalActionServerChange(this.core, "survival"))); // to survival

		this.portals.add(new LobbyPortal("world", -423, 68, -254, -419, 64, -259,
				new PortalActionTeleport(new Location(world, -435.5, 89.5, -202.5)))); // to outpost position
		this.portals.add(new LobbyPortal("world", -433, 86, -202, -438, 81, -205,
				new PortalActionTeleport(new Location(world, -419.5, 65.5, -251.5, -56, 0)))); // back from outpost position
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