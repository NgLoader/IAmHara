package eu.wuffy.lobby.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import eu.wuffy.core.hologram.HologramHandler;
import eu.wuffy.lobby.Lobby;

public class LobbyHologramHandler extends HologramHandler {

	public LobbyHologramHandler(Lobby core) {
		super(core);
	}

	@Override
	public void onEnable() {
		super.onEnable();

		World world = Bukkit.getWorld("world");
		this.addHologram(new Location(world, 35.5, 65.7, -40.5), "§7Hier geht's zu");
		this.addHologram(new Location(world, 35.5, 65.4, -40.5), "§2S§aurvival");

		this.addHologram(new Location(world, -18, 65.3, 3.5), "§7Hier geht's zu");
		this.addHologram(new Location(world, -18, 65, 3.5), "§9Leuchtturm");

		this.addHologram(new Location(world, 1.5, 65.8, -14.5), "§7!!! §cNetzwerk Regeln §7!!!");
		this.addHologram(new Location(world, 1.5, 65.3, -14.5), "§71§8. §cGriefen §7ist verboten");
		this.addHologram(new Location(world, 1.5, 65, -14.5), "§72§8. §cFallen §7sind verboten");
		this.addHologram(new Location(world, 1.5, 64.7, -14.5), "§73§8. §7Das ausnutzen von §cBugs §7ist verboten");
		this.addHologram(new Location(world, 1.5, 64.4, -14.5), "§74§8. §7Nicht erlaubte §cModifikationen §7sind verboten");
		this.addHologram(new Location(world, 1.5, 64.1, -14.5), "§75§8. §7Keine §cBeleidigungen §7seid §aRespektvoll");

		this.addHologram(new Location(world, -4.5, 68.5, 70.5), "§cAbonniert §eIamHaRa §7mit §5TWITCH §bPRIME§8!");

		this.spawnAll();
	}
}