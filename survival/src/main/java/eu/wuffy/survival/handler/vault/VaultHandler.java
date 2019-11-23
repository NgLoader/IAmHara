package eu.wuffy.survival.handler.vault;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHandler extends IHandler<Survival> {

	private Permission permission;
	private Economy economy;
	private Chat chat;

	public VaultHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.setupPermission();
		this.setupEconomy();
		this.setupChat();
	}

	private void setupPermission() {
		RegisteredServiceProvider<Permission> registeredServiceProvider = this.getCore().getServer().getServicesManager().getRegistration(Permission.class);

		if (registeredServiceProvider != null)
			this.permission = registeredServiceProvider.getProvider();
	}

	private void setupEconomy() {
		RegisteredServiceProvider<Economy> registeredServiceProvider = this.getCore().getServer().getServicesManager().getRegistration(Economy.class);

		if (registeredServiceProvider != null) {
			Economy economyOld = registeredServiceProvider.getProvider();
			Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Removed Vault Economy system: §a" + economyOld.getName());
			Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7And replaced it with §aSurvival Economy");
			this.getCore().getServer().getServicesManager().unregister(economyOld);
		}

		this.economy = new SurvivalEconomy(this.getCore());
		this.getCore().getServer().getServicesManager().register(Economy.class, economy, this.getCore(), ServicePriority.High);
	}

	private void setupChat() {
		RegisteredServiceProvider<Chat> registeredServiceProvider = this.getCore().getServer().getServicesManager().getRegistration(Chat.class);

		if (registeredServiceProvider != null)
			this.chat = registeredServiceProvider.getProvider();
	}

	public Permission getPermission() {
		return this.permission;
	}

	public Economy getEconomy() {
		return this.economy;
	}

	public Chat getChat() {
		return this.chat;
	}
}