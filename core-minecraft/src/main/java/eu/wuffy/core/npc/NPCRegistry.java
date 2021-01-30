package eu.wuffy.core.npc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import eu.wuffy.core.Core;

public class NPCRegistry<T extends Core<?>> implements Runnable {

	private static final int MAX_NPC_CHECK = 4;

	private final NPCSystem<T> npcSystem;
	private final T core;

	protected final List<NPC> npcs = new ArrayList<>();

	protected BukkitTask task;
	protected int taskIndex = 0;

	public NPCRegistry(NPCSystem<T> npcSystem, T core) {
		this.npcSystem = npcSystem;
		this.core = core;
	}

	@Override
	public void run() {
		if (this.npcs.isEmpty()) {
			return;
		}

		int npcSize = this.npcs.size();
		for (int i = 0; i < MAX_NPC_CHECK; i++) {
			if (this.taskIndex > npcSize - 1) {
				this.taskIndex = 0;
				break;
			}

			NPC npc = this.npcs.get(this.taskIndex);
			npc.checkInRange();

			if (npcSize > 1) {
				this.taskIndex++;
			}
		}
	}

	public void startNPCDistanceCheck() {
		if (this.task == null) {
			this.task = Bukkit.getScheduler().runTaskTimer(this.core, this, 0, 10);
		}
	}

	public void stopNPCDistanceCheck() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}

	public void showAll(Player player) {
		this.npcs.forEach(npc -> npc.show(player));
	}

	public void hideAll(Player player) {
		this.npcs.forEach(npc -> npc.hide(player));
	}

	public void respawnAll(Player player) {
		this.npcs.forEach(npc -> npc.respawn(player));
	}

	public void destroy() {
		this.npcSystem.registries.remove(this);
		this.stopNPCDistanceCheck();
		this.npcs.forEach(NPC::destroy);
		this.npcs.clear();
	}

	public List<NPC> getNpcs() {
		return this.npcs;
	}

	public Core<?> getCore() {
		return this.core;
	}

	public NPCSystem<?> getNpcSystem() {
		return this.npcSystem;
	}
}