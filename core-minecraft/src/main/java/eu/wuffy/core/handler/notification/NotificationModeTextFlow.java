package eu.wuffy.core.handler.notification;

import eu.wuffy.core.Core;

public abstract class NotificationModeTextFlow <T extends Core<?>> extends NotificationMode<T> {

	protected abstract void onTick();
	protected abstract void displayTileText(String text);
	protected abstract void startDisplay();
	protected abstract void stopDisplay();

	protected boolean finished = false;
	protected String finalText;

	protected long duration = 0;
	protected int step = 0;

	public NotificationModeTextFlow(T core) {
		super(core);
	}

	@Override
	public void onDisable() {
		this.stopDisplay();
	}

	@Override
	public void display(String text, long duration) {
		this.finalText = text;
		this.duration = System.currentTimeMillis() + duration;
		this.finished = false;
		this.step = 0;

		this.startDisplay();
	}

	@Override
	public boolean tick() {
		try {
			if (this.hasNextStep()) {
				this.displayTileText(getNextStepMessage());
			}

			this.onTick();

			if (this.duration < System.currentTimeMillis()) {
				this.stopDisplay();
				return false;
			}
		} catch(Exception e) {
			e.printStackTrace();
			this.stopDisplay();
			return false;
		}
		return true;
	}

	protected String getNextStepMessage() {
		if (!this.hasNextStep()) {
			return this.finalText;
		}
		this.step++;

		String splittedMessage = this.finalText.substring(0, this.step);

		while (splittedMessage.endsWith("§")) {
			this.step += 2;

			if (!this.hasNextStep()) {
				return this.finalText;
			}

			splittedMessage = this.finalText.substring(0, this.step);
		}

		return splittedMessage;
	}

	protected boolean hasNextStep() {
		if (this.finalText.length() - 1 < this.step) {
			return false;
		}
		return true;
	}
}
