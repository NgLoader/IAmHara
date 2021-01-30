package eu.wuffy.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import eu.wuffy.synced.util.ReflectionUtil;

/**
 * @author Ingrim4
 * @author NgLoader
 */
public class ItemFactory extends ItemStack {

	private static final Method CRAFTMETASKULL_SET_PROFILE = ReflectionUtil.getMethod(MCReflectionUtil.getCraftBukkitClass("inventory.CraftMetaSkull"), "setProfile", GameProfile.class);

	public static final List<Material> MATERIAL_ITEMS = Collections.unmodifiableList(Arrays.stream(Material.values()).filter(Material::isItem).collect(Collectors.toList()));
	public static final List<Material> MATERIAL_BLOCKS = Collections.unmodifiableList(Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList()));

	private ItemMeta meta;

	public ItemFactory() { }

	public ItemFactory(ItemStack item) {
		this(item.getType(), item.getAmount(), ((Damageable) item.getItemMeta()).getDamage());
		this.meta = item.getItemMeta();
	}
	
	public ItemFactory(Material material, ItemMeta meta) {
		this(material);
		this.meta = meta;
	}
	
	public ItemFactory(Material material) {
		this(material, 1, 0);
	}
	
	public ItemFactory(Material material, String name) {
		this(material);
		this.setDisplayName(name);
	}

	public ItemFactory(Material material, int amount, String name, int damage) {
		this(material, amount, damage);
		this.setDisplayName(name);
	}

	public ItemFactory(Material material, String name, int damage) {
		this(material, damage);
		this.setDisplayName(name);
	}

	public ItemFactory(Material material, int damage) {
		this(material, 1, damage);
	}

	public ItemFactory(Material material, int amount, int damage) {
		this.setType(material);
		this.setAmount(amount);
		this.setDamage(damage);
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

	public ItemFactory setDamage(int damage) {
		if (this.meta != null)
			((Damageable) this.meta).setDamage(damage);
		else {
			ItemMeta itemMeta = this.getItemMeta();

			((Damageable) itemMeta).setDamage(damage);

			this.setItemMeta(itemMeta);
		}
		return this;
	}
	
	public ItemFactory setDisplayName(String name) {
		this.meta.setDisplayName(name);
		return this;
	}

	public String getDisplayName() {
		return this.meta.getDisplayName();
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
	
	public ItemFactory setUnbreakable(Boolean unbreakable) {
		this.meta.setUnbreakable(unbreakable);
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

	public ItemFactory setSkullProfile(String texture) {
		try {
			SkullMeta meta = (SkullMeta) this.meta;
			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
			gameProfile.getProperties().put("textures", new Property("textures", texture));
			CRAFTMETASKULL_SET_PROFILE.invoke(meta, gameProfile);
		} catch (Exception e) {
			throw new ClassCastException("Error by setting skull profile");
		}

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
