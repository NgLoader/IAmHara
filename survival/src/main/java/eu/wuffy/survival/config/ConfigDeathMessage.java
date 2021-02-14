package eu.wuffy.survival.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.google.gson.annotations.Expose;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.config.Config;

@Config(path = Survival.CONFIG_FOLDER, name = "deathMessage")
public class ConfigDeathMessage {

	private static final List<String> NO_MESSAGE_FOUND = Arrays.asList("%prefix%&7Der Spieler &b%player% &7ist &cgestorben&8.");

	@Expose(deserialize = false, serialize = false)
	private Map<DamageCause, DeathMessageEntry> cached;

	public DeathMessageEntry defaultMessages = new DeathMessageEntry(
			Arrays.asList("%prefix%&7Der Spieler &b%player% &7ist &cgestorben&8."),
			Arrays.asList("%prefix%&7Der Spieler &b%player% &7wurde von &b%killer% &cgetötet&8."),
			Arrays.asList("%prefix%&7Der Spieler &b%player% &7wurde von einen &b%killer% &cgetötet&8."),
			Arrays.asList("%prefix%&7Der Spieler &b%player% &7hat sich selbst &cgetötet&8."));

	public List<DeathMessageEntry> definedMessages = Arrays.asList(
			new DeathMessageEntry(
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7ist &cexplodiert&8."),
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7wurde von &b%killer% &cgetötet&8."),
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7wurde von einen &b%entity% &cgetötet&8."),
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7hat sich selbst &cgetötet&8."),
					DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_SWEEP_ATTACK),
			new DeathMessageEntry(
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7ist &cexplodiert&8."),
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7wurde von &b%killer% &7in die Luft gejagt&8."),
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7wurde von &b%killer% &7in die Luft gejagt&8."),
					Arrays.asList("%prefix%&7Der Spieler &b%player% &7hat sich selbst in die Luft gejagt&8."),
					DamageCause.ENTITY_EXPLOSION, DamageCause.BLOCK_EXPLOSION));

	public class DeathMessageEntry {
		
		public DamageCause[] causes = new DamageCause[0];

		public Map<DeathMessageKillerType, List<String>> messages = new HashMap<>();

		public DeathMessageEntry(List<String> unknownKiller, List<String> playerKiller, List<String> entityKiller, List<String> selfKiller, DamageCause... causes) {
			this.messages.put(DeathMessageKillerType.UNKNOWN, unknownKiller);
			this.messages.put(DeathMessageKillerType.PLAYER, playerKiller);
			this.messages.put(DeathMessageKillerType.ENTITY, entityKiller);
			this.messages.put(DeathMessageKillerType.SELF, selfKiller);
			this.causes = causes;
		}

		public DeathMessageEntry(Map<DeathMessageKillerType, List<String>> messages, DamageCause... causes) {
			this.messages = messages;
			this.causes = causes;
		}
	}

	public enum DeathMessageKillerType {
		UNKNOWN,
		PLAYER,
		ENTITY,
		SELF
	}

	public Map<DamageCause, DeathMessageEntry> getMessages() {
		Map<DamageCause, DeathMessageEntry> map = new HashMap<>();
//		for (DeathMessageEntry entry : this.definedMessages) {
//			for (DamageCause cause : entry.causes) {
//				if (map.containsKey(cause)) {
//					DeathMessageEntry existing = map.get(cause);
//					for (Entry<DeathMessageKillerType, List<String>> killer : existing.messages.entrySet()) {
//						List<String> messages = killer.getValue();
//						if (messages != null && !messages.isEmpty()) {
//							existing.messages.get(killer.getKey()).addAll(messages);
//						}
//					}
//					continue;
//				}
//
//				DeathMessageEntry copy = new DeathMessageEntry(new HashMap<DeathMessageKillerType, List<String>>(), entry.causes);
//				for (Entry<DeathMessageKillerType, List<String>> killer : entry.messages.entrySet()) {
//					List<String> messages = killer.getValue();
//					if (messages != null && !messages.isEmpty()) {
//						copy.messages.put(killer.getKey(), new ArrayList<>(messages));
//					}
//			}
//		}
		return map;
	}

	public List<String> getMessages(DamageCause cause, DeathMessageKillerType killer) {
		if (this.cached == null) {
			this.cached = this.getMessages();
		}

		DeathMessageEntry entry = this.cached.get(cause);
		if (entry == null) {
			return this.getDefaultMessages(killer);
		}

		List<String> messages = entry.messages.get(killer);
		return messages != null && !messages.isEmpty() ? messages : this.getDefaultMessages(killer);
	}

	public List<String> getDefaultMessages(DeathMessageKillerType killer) {
		if (this.defaultMessages == null || this.defaultMessages.messages == null) {
			return NO_MESSAGE_FOUND;
		}

		return this.defaultMessages.messages.getOrDefault(killer, NO_MESSAGE_FOUND);
	}
}
