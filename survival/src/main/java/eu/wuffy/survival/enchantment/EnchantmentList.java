package eu.wuffy.survival.enchantment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;

import eu.wuffy.synced.util.ReflectionUtil;

public class EnchantmentList {

	public static final CustomEnchantment TREE_FELLER = new TreeFellerEnchantment();
	public static final CustomEnchantment INVISIBLE_ITEM_FRAME = new InvisibleItemFrameEnchantment();

	public static void init() {
		try {
			if (!Enchantment.isAcceptingRegistrations()) {
				Field field = ReflectionUtil.getField(Enchantment.class, "acceptingNew");
				try {
					field.set(null, true);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
			}

			registerEnchantment(TREE_FELLER);
			registerEnchantment(INVISIBLE_ITEM_FRAME);

			Enchantment.stopAcceptingRegistrations();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void registerEnchantment(CustomEnchantment enchantment) {
		if (!Arrays.stream(Enchantment.values()).map(enchant -> enchant.getKey().getKey()).collect(Collectors.toList()).contains(enchantment.getKey().getKey())) {
			try {
				Enchantment.registerEnchantment(enchantment);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}