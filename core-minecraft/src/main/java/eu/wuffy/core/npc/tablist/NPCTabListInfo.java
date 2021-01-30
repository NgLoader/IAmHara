package eu.wuffy.core.npc.tablist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eu.wuffy.core.npc.npc.entity.NPCPlayer;

public class NPCTabListInfo {

	protected Set<NPCInfo> toRemove = new HashSet<>();

	public NPCTabListInfo(NPCPlayer npc, int trys) {
		this.add(npc, trys);
	}

	public void add(NPCPlayer npc) {
		this.toRemove.add(new NPCInfo(npc));
	}

	public void add(NPCPlayer npc, int trys) {
		this.toRemove.add(new NPCInfo(npc, trys));
	}

	public void remove(NPCPlayer npc) {
		Iterator<NPCInfo> iterator = this.toRemove.iterator();
		while(iterator.hasNext()) {
			NPCInfo info = iterator.next();
			if (info.npc.equals(npc)) {
				iterator.remove();
				break;
			}
		}
	}

	protected class NPCInfo {

		public final NPCPlayer npc;
		public int trys = NPCTabList.DEFAULT_TRYS;

		public NPCInfo(NPCPlayer npc) {
			this.npc = npc;
		}

		public NPCInfo(NPCPlayer npc, int trys) {
			this(npc);
			this.trys = trys;
		}
	}
}