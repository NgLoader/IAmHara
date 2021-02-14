package eu.wuffy.survival.handler;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.config.ConfigDeathMessage;
import eu.wuffy.survival.config.ConfigDeathMessage.DeathMessageKillerType;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.config.ConfigService;

public class DeathMessageHandler extends IHandler<Survival> implements Listener {

	private final Random random = new Random();
	private ConfigDeathMessage config;

	public DeathMessageHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.config = ConfigService.getConfig(ConfigDeathMessage.class);
		Bukkit.getPluginManager().registerEvents(this, this.core);
	}

	@Override
	public void onDisable() {
		this.config = null;
		//TODO unregister listeners
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (this.config == null) {
			return;
		}
		event.setDeathMessage(null);
		
		Player player = event.getEntity();
		EntityDamageEvent lastDamage = player.getLastDamageCause();
		DeathMessageKillerType killerType = DeathMessageKillerType.UNKNOWN;
		
		String killer = "§cUNKNOWN";
		if (player.getKiller() != null) {
			if (player.getKiller() != player) {
				killerType = DeathMessageKillerType.PLAYER;
				killer = player.getKiller().getDisplayName();
			} else {
				killerType = DeathMessageKillerType.SELF;
			}
		} else if (lastDamage.getEntity() instanceof Player) {
			if (lastDamage.getEntity() != player) {
				killerType = DeathMessageKillerType.PLAYER;
				killer = ((Player) lastDamage.getEntity()).getDisplayName();
			} else {
				killerType = DeathMessageKillerType.SELF;
			}
		} else if (lastDamage.getEntity() instanceof LivingEntity) {
			killerType = DeathMessageKillerType.ENTITY;
			killer = lastDamage.getEntity().getType().name();
			killer = killer.substring(0, 1).toUpperCase() + killer.substring(1).toLowerCase();
		} else if (lastDamage.getEntity() instanceof Projectile) {
			Projectile projectile = (Projectile) lastDamage.getEntity();
			ProjectileSource shooter = projectile.getShooter();
			if (shooter instanceof Player) {
				killerType = DeathMessageKillerType.PLAYER;
				killer = player.getKiller().getDisplayName();
			} else if (shooter instanceof LivingEntity) {
				killerType = DeathMessageKillerType.ENTITY;
				killer = lastDamage.getEntity().getType().name();
				killer = killer.substring(0, 1).toUpperCase() + killer.substring(1).toLowerCase();
			}
		}
		player.setLastDamageCause(null);

		List<String> messages = this.config.getMessages(lastDamage.getCause(), killerType);
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', messages.get(this.random.nextInt(messages.size())))
				.replace("%prefix%", Survival.PREFIX)
				.replace("%player%", player.getDisplayName())
				.replace("%killer%", killer));
	}
}