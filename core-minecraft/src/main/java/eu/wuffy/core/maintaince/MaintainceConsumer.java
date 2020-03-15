package eu.wuffy.core.maintaince;

import eu.wuffy.synced.MaintainceStatus;

public interface MaintainceConsumer {

	void recive(MaintainceHandler handler, MaintainceStatus mode, String server, Long responseTime);
}
