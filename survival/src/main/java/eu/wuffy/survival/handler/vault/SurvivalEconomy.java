package eu.wuffy.survival.handler.vault;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.OfflinePlayer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.database.SurvivalDatabase;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class SurvivalEconomy implements Economy {

	private final DecimalFormat currencyFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.GERMAN));
	private final Survival core;
	private final SurvivalDatabase database;

	public SurvivalEconomy(Survival core) {
		this.core = core;
		this.database = this.core.getDatabase();
		this.currencyFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	@Override
	public boolean isEnabled() {
		return this.core.isEnabled();
	}

	@Override
	public String getName() {
		return "Survival Economy";
	}

	@Override
	public boolean hasBankSupport() {
		return true;
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public String format(double amount) {
		try {
			String value = this.currencyFormat.format(amount);

			if (value.endsWith(".00")) {
				value = value.substring(0, value.length() - 3);
			}

			return value;
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return "NaN";
		}
	}

	@Override
	public String currencyNamePlural() {
		return "";
	}

	@Override
	public String currencyNameSingular() {
		return "";
	}

	@Override
	public boolean hasAccount(String playerName) {
		try {
			return this.database.economyPlayerExist(playerName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return this.hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		try {
			return this.database.economyPlayerMoneyGet(playerName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public double getBalance(String playerName, String world) {
		return this.getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		try {
			if (this.database.economyBankExist(playerName)) {
				double balance = this.database.economyBankMoneyGet(playerName);

				if (balance >= amount) {
					return true;
				}
			}
			return false;
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return this.has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (amount < 0) return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Amount is negative");

		try {
			if (!this.database.economyBankExist(playerName)) {
				double balance = 0 - amount;
				this.database.economyPlayerCreate(playerName, balance);
				return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
			}

			double balance = this.database.economyPlayerMoneyGet(playerName) - amount;
			this.database.economyPlayerMoneySet(playerName, balance);
			return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
		} catch(SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Withdraw error by player: " + playerName);
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return this.withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		if (amount < 0) return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Amount is negative");

		try {
			if (!this.database.economyBankExist(playerName)) {
				this.database.economyPlayerCreate(playerName, amount);
				return new EconomyResponse(amount, amount, ResponseType.SUCCESS, "");
			}

			double balance = this.database.economyPlayerMoneyGet(playerName) + amount;
			this.database.economyPlayerMoneySet(playerName, balance);
			return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
		} catch(SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Deposit error by player: " + playerName);
		}
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return this.depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		try {
			this.database.economyBankCreate(name, player, 0);
			this.database.economyBankAddPlayer(name, player);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create bank");
		}
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		try {
			this.database.economyBankDelete(name);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to delete bank");
		}
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		try {
			return new EconomyResponse(0, this.database.economyBankMoneyGet(name), ResponseType.SUCCESS, null);
		} catch (SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to get money from bank: " + name);
		}
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		try {
			if (this.database.economyBankExist(name)) {
				double balance = this.database.economyBankMoneyGet(name);

				if (balance >= amount) {
					return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
				}
				return new EconomyResponse(amount, balance, ResponseType.FAILURE, "Bank has not enough money");
			}
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Bank not exist");
		} catch(SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Deposit error by bank: " + name);
		}
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		if (amount < 0) return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Amount is negative");

		try {
			if (this.database.economyBankExist(name)) {
				double balance = this.database.economyBankMoneyGet(name) - amount;
				this.database.economyBankMoneySet(name, balance);
				return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
			}
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Bank not exist");
		} catch(SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Withdraw error by bank: " + name);
		}
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		if (amount < 0) return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Amount is negative");

		try {
			if (this.database.economyBankExist(name)) {
				double balance = this.database.economyBankMoneyGet(name) + amount;
				this.database.economyBankMoneySet(name, balance);
				return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
			}
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Bank not exist");
		} catch(SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(amount, 0, ResponseType.FAILURE, "Deposit error by bank: " + name);
		}
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		try {
			if (this.database.economyBankOwnerGet(name) == playerName) {
				return this.bankBalance(name);
			}
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "The give player is not the owner of the bank");
		} catch (SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to get the bank owner");
		}
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		try {
			if (this.database.economyBankIsPlayerIn(name, playerName)) {
				return this.bankBalance(name);
			}
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "The give player is not in the bank");
		} catch (SQLException e) {
			e.printStackTrace();
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to check if player in bank");
		}
	}

	@Override
	public List<String> getBanks() {
		try {
			return this.database.economyBanks();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		try {
			this.database.economyPlayerCreate(playerName, 0);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return this.createPlayerAccount(playerName);
	}

	public Survival getCore() {
		return this.core;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return this.hasAccount(player.getUniqueId().toString());
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return this.hasAccount(player.getUniqueId().toString());
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return this.getBalance(player.getUniqueId().toString());
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return this.getBalance(player.getUniqueId().toString());
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return this.has(player.getUniqueId().toString(), amount);
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		return this.has(player.getUniqueId().toString(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		return this.withdrawPlayer(player.getUniqueId().toString(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return this.withdrawPlayer(player.getUniqueId().toString(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		return this.depositPlayer(player.getUniqueId().toString(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return this.depositPlayer(player.getUniqueId().toString(), amount);
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return this.createBank(player.getUniqueId().toString(), player.getUniqueId().toString());
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return this.isBankOwner(name, player.getUniqueId().toString());
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return this.isBankMember(name, player.getUniqueId().toString());
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		return this.createPlayerAccount(player.getUniqueId().toString());
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return this.createPlayerAccount(player.getUniqueId().toString());
	}
}