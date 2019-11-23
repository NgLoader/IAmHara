package eu.wuffy.survival.handler.help;

public class HelpLine {

	public int line;

	public String message;
	public String permission;

	public HelpLine(int line, String message, String permission) {
		this.line = line;
		this.message = message;
		this.permission = permission;
	}

	public int getLine() {
		return this.line;
	}
}
