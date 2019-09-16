package eu.wuffy.survival.help;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.synced.IHandler;

public class HelpHandler extends IHandler<Survival> {

	private final List<HelpLine> lines = new ArrayList<HelpLine>();
	private final SurvivalDatabase database;

	public HelpHandler(Survival core) {
		super(core);
		this.database = this.getCore().getDatabase();
	}

	@Override
	public void onEnable() {
		try {
			this.lines.addAll(this.database.loadHelpLines());
			Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Loaded §a" + this.lines.size() + " §7help message lines§8.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String build(Permissible permissible) {
		return ChatColor.translateAlternateColorCodes(
				'&',
				String.join(
						"\n",
						this.lines.stream()
							.filter(line -> line.permission.isEmpty() || permissible.hasPermission(line.permission))
							.sorted(Comparator.comparing(HelpLine::getLine))
							.map(line -> line.message)
							.collect(Collectors.toList()))
				.replace("%p", Survival.PREFIX));
	}

	public void removeLine(int line) throws SQLException {
		HelpLine helpLine = this.getLine(line);

		this.database.deleteHelpLine(helpLine);
		this.lines.remove(helpLine);
	}

	public void setLine(int line, String message) throws SQLException {
		this.setLine(line, message, this.getLine(line).permission);
	}

	public void setLine(int line, String message, String permission) throws SQLException {
		this.database.updateHelpLine(this.getLine(line), line, message, permission);
	}

	public void addLine(String message) throws SQLException {
		this.addLine(message, "");
	}

	public void addLine(String message, String permission) throws SQLException {
		this.lines.add(this.database.createHelpLine(this.lines.size(), message, permission));
	}

	public void addLine(int line, String message) throws SQLException {
		this.addLine(line, message, "");
	}

	public void addLine(int line, String message, String permission) throws SQLException {
		this.lines.add(this.database.createHelpLine(line, message, permission));
	}

	public HelpLine getLine(int id) {
		return this.lines.stream().filter(line -> line.line == id).findFirst().orElseGet(() -> null);
	}

	public List<HelpLine> getLines() {
		return this.lines;
	}
}