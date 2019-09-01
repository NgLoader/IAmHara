package eu.wuffy.synced;

public enum Rank {

	ADMINISTRATOR("", "", "Admin", "Administrator", "§4", 100),
	DEVELOPER("", "", "Dev", "Developer", "§3", 60),
	MODERATOR("", "", "Mod", "Moderator", "§c", 50),
	SUPPORTER("", "", "Sup", "Supporter", "§b", 40),
	YOUTUBER("", "", "YT", "YouTuber", "§5", 30),
	VIP("", "", "VIP", "VIP", "§6", 20),
	PREMIUM("", "", "Premium", "Premium", "§e", 10),
	PLAYER("", "", "Spieler", "Spieler", "§a", 0);

	private String prefix, suffix;
	private String displayName, fullDisplayName;
	private String colorCode;
	private int power;

	private Rank(String prefix, String suffix, String displayName, String fullDisplayName, String colorCode, int power) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.displayName = displayName;
		this.fullDisplayName = fullDisplayName;
		this.colorCode = colorCode;
		this.power = power;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFullDisplayName() {
		return fullDisplayName;
	}

	public String getColorCode() {
		return colorCode;
	}

	public int getPower() {
		return power;
	}

	public static boolean isRankHigher(Rank rank, Rank higherRank, boolean equals) {
		if (equals)
			return rank.getPower() >= higherRank.getPower();
		return rank.getPower() > higherRank.getPower();
	}
}
