package eu.wuffy.survival.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;

public class DatabasePlayerInfo {

	public final int inventory_id;

	public int gamemode;

	public float exp;
	public int level;
	public int totalExperience;

	public float saturation;
	public int foodLevel;

	public double healthScale;
	public boolean healthScaled;

	public float flySpeed, walkSpeed;

	public Map<NamespacedKey, Integer> statistic;
	public Map<NamespacedKey, Map<Material, Integer>> statisticBlock;
	public Map<NamespacedKey, Map<Material, Integer>> statisticItem;

	public Map<NamespacedKey, Map<String, Date>> advancement;

	public ItemStack[] content;
	public ItemStack[] armorContent;
	public ItemStack[] storageContent;
	public ItemStack[] extraContent;
	public ItemStack[] enderchestContent;
	public ItemStack[] enderchestStorageContent;

	public DatabasePlayerInfo(ResultSet resultSet) throws SQLException {
		this.inventory_id = resultSet.getInt("inventory_id");
		this.gamemode = resultSet.getInt("gamemode");
	}

	public void loadInventory(ResultSet resultSet) throws SQLException {
		this.content = ItemFactory.itemStackArrayFromBase64(resultSet.getString("content"));
		this.armorContent = ItemFactory.itemStackArrayFromBase64(resultSet.getString("armorContent"));
		this.storageContent = ItemFactory.itemStackArrayFromBase64(resultSet.getString("storageContent"));
		this.extraContent = ItemFactory.itemStackArrayFromBase64(resultSet.getString("extraContent"));
		this.enderchestContent = ItemFactory.itemStackArrayFromBase64(resultSet.getString("enderchestContent"));
		this.enderchestStorageContent = ItemFactory.itemStackArrayFromBase64(resultSet.getString("enderchestStorageContent"));
	}

	public void loadStatistic(ResultSet resultSet) {
	}

	public void loadAdvancement(ResultSet resultSet) {
	}
}