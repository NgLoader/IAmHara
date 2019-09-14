package eu.wuffy.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemFactory extends ItemStack {

	public static final List<Material> MATERIAL_ITEMS = Arrays.stream(Material.values()).filter(material -> material.isItem()).collect(Collectors.toList());
	public static final List<Material> MATERIAL_BLOCKS = Arrays.stream(Material.values()).filter(material -> material.isBlock()).collect(Collectors.toList());

	public static String[] playerInventoryToBase64(PlayerInventory playerInventory) {
		return new String[] { toBase64(playerInventory), itemStackArrayToBase64(playerInventory.getArmorContents()) };
	}

	public static String itemStackArrayToBase64(ItemStack[] items) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeInt(items.length);
			for (int i = 0; i < items.length; i++)
				dataOutput.writeObject(items[i]);
			dataOutput.close();
			
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	public static String toBase64(Inventory inventory) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeInt(inventory.getSize());
			for (int i = 0; i < inventory.getSize(); i++)
				dataOutput.writeObject(inventory.getItem(i));
			dataOutput.close();
			
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	public static Inventory fromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());
			
			for (int i = 0; i < inventory.getSize(); i++)
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			dataInput.close();
			
			return inventory;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

	public static ItemStack[] itemStackArrayFromBase64(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];
			
			for (int i = 0; i < items.length; i++)
				items[i] = (ItemStack) dataInput.readObject();
			dataInput.close();
			
			return items;
		} catch (Exception e) {
			return null;
		}
	}

	private ItemMeta meta;

	public ItemFactory() { }

	public ItemFactory(ItemStack item) {
		this(item.getType(), item.getAmount(), item.getDurability());
		this.meta = item.getItemMeta();
	}
	
	public ItemFactory(Material material, ItemMeta meta) {
		this(material);
		this.meta = meta;
	}
	
	public ItemFactory(Material material) {
		this(material, 1);
	}
	
	public ItemFactory(Material material, int amount) {
		this(material, amount, (byte) 0);
	}
	
	public ItemFactory(Material material, int amount, String name, byte b) {
		this(material, amount, b);
		this.setDisplayName(name);
	}
	
	public ItemFactory(Material material, String name, byte b) {
		this(material, b);
		this.setDisplayName(name);
	}
	
	public ItemFactory(Material material, byte b) {
		this(material, 1, b);
	}
	
	public ItemFactory(Material material, int amount, int i) {
		this.setType(material);
		this.setAmount(amount);
		this.setDamage((short) i);
		this.meta = this.getItemMeta();
	}
	
	public ItemFactory setMaterial(Material material) {
		this.setType(material);
		return this;
	}
	
	public ItemFactory setNumber(int amount) {
		this.setAmount(amount);
		return this;
	}
	
	public ItemFactory setDamage(short damage) {
		this.setDurability(damage);
		return this;
	}

	public ItemFactory setDisplayName(String name) {
		this.meta.setDisplayName(name);
		return this;
	}
	
	public ItemFactory setLore(int index, String lore) {
		List<String> lores = this.meta.getLore();
		if (lores.size() > index)
			lores.set(index, lore);
		this.setLore(lores);
		return this;
	}
	
	public ItemFactory setLore(List<String> lore) {
		this.meta.setLore(lore);
		return this;
	}
	
	public ItemFactory setLore(String... lore) {
		this.meta.setLore(Arrays.asList(lore));
		return this;
	}
	
	public ItemFactory addLore(String lore) {
		List<String> temp = this.meta.getLore() != null ? this.meta.getLore() : new ArrayList<String>();
		temp.add(lore);
		this.meta.setLore(temp);
		return this;
	}
	
	public ItemFactory addLore(String... lores) {
		List<String> temp = this.meta.getLore() != null ? this.meta.getLore() : new ArrayList<String>();
		for(String lore : lores)
			temp.add(lore);
		this.meta.setLore(temp);
		return this;
	}
	
	public ItemFactory clearEnchantments() {
		this.meta.getEnchants().forEach((en, lvl) -> this.meta.removeEnchant(en));
		return this;
	}
	
	public ItemFactory addSingleEnchantment(Enchantment enchantment, int level) {
		this.meta.addEnchant(enchantment, level, true);
		return this;
	}
	
	public ItemFactory addEnchantments(HashMap<Enchantment, Integer> enchantments) {
		for (Entry<Enchantment, Integer> e : enchantments.entrySet())
			this.meta.addEnchant(e.getKey(), e.getValue(), true);
		return this;
	}
	
	public ItemFactory addItemFlag(ItemFlag... flag) {
		this.meta.addItemFlags(flag);
		return this;
	}
	
	public ItemFactory setColor(Color color) {
		try {
			LeatherArmorMeta meta = (LeatherArmorMeta) this.meta;
			meta.setColor(color);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a leather armor");
		}
		return this;
	}
	
	public ItemFactory addPotionEffect(PotionEffect effect) {
		try {
			PotionMeta meta = (PotionMeta) this.meta;
			meta.addCustomEffect(effect, true);
		} catch (Exception e) {
			throw new ClassCastException("The given item wasn't a potion");
		}
		return this;
	}
	
	public ItemFactory setPotionType(PotionData type) {
		try {
			PotionMeta meta = (PotionMeta) this.meta;
			meta.setBasePotionData(type);
		} catch (Exception e) {
			throw new ClassCastException("The given item wasn't a potion");
		}
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public ItemFactory setSkullOwner(String owner) {
		try {
			SkullMeta meta = (SkullMeta) this.meta;
			meta.setOwner(owner);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a skull");
		}
		return this;
	}
	
	public ItemFactory addFireworkEffect(FireworkEffect effect) {
		try {
			FireworkMeta meta = (FireworkMeta) this.meta;
			meta.addEffect(effect);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a firework");
		}
		return this;
	}
	
	public ItemFactory setFireworkPower(int power) {
		try {
			FireworkMeta meta = (FireworkMeta) this.meta;
			meta.setPower(power);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a firework");
		}
		return this;
	}
	
	public ItemFactory setBookTitle(String title) {
		try {
			BookMeta meta = (BookMeta) this.meta;
			meta.setTitle(title);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a book");
		}
		return this;
	}
	
	public ItemFactory setBookAuthor(String author) {
		try {
			BookMeta meta = (BookMeta) this.meta;
			meta.setAuthor(author);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a book");
		}
		return this;
	}
	
	public ItemFactory addBookPage(String page) {
		try {
			BookMeta meta = (BookMeta) this.meta;
			meta.addPage(page);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a book");
		}
		return this;
	}
	
	public ItemFactory setBookPage(int id, String page) {
		try {
			BookMeta meta = (BookMeta) this.meta;
			meta.setPage(id, page);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a book");
		}
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public ItemFactory setBackgroundColor(DyeColor color) {
		try {
			BannerMeta meta = (BannerMeta) this.meta;
			meta.setBaseColor(color);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a banner");
		}
		return this;
	}
	
	public ItemFactory addPattern(Pattern pattern) {
		try {
			BannerMeta meta = (BannerMeta) this.meta;
			meta.addPattern(pattern);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a banner");
		}
		return this;
	}
	
	public ItemFactory setPattern(Pattern pattern, int id) {
		try {
			BannerMeta meta = (BannerMeta) this.meta;
			meta.setPattern(id, pattern);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a banner");
		}
		return this;
	}
	
	public ItemFactory setPatterns(List<Pattern> patterns) {
		try {
			BannerMeta meta = (BannerMeta) this.meta;
			meta.setPatterns(patterns);
		} catch (ClassCastException e) {
			throw new ClassCastException("The given item wasn't a banner");
		}
		return this;
	}
	
	public ItemFactory addEnchant(Enchantment enchantment, int level, boolean see) {
		ItemMeta meta = this.meta;
		meta.addEnchant(enchantment, level, see);
		return this;
	}
	
	public ItemFactory addFlag(ItemFlag flag) {
		ItemMeta meta = this.meta;
		meta.addItemFlags(flag);
		return this;
	}
	
	public ItemFactory removeFlag(ItemFlag flag) {
		ItemMeta meta = this.meta;
		meta.addItemFlags(flag);
		return this;
	}
	
	public ItemFactory addAllFlag() {
		ItemMeta meta = this.meta;
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		return this;
	}
	
	public ItemFactory clone() {
		return new ItemFactory(this.build());
	}
	
	public ItemStack build() {
		this.setItemMeta(this.meta);
		return this;
	}
}
