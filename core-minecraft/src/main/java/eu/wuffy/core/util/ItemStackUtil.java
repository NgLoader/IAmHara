package eu.wuffy.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTList;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;

public class ItemStackUtil {

	@Nullable
	public static List<ItemStack> toItemStack(String nbtString) {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(nbtString.getBytes())) {
			List<ItemStack> items = new ArrayList<>();
			NBTTagCompound nbt = NBTCompressedStreamTools.a(inputStream);
			if (nbt.hasKeyOfType("i", 9)) {
				NBTList<NBTBase> list = nbt.getList("i", 10);
				for (NBTBase base : list) {
					if (base instanceof NBTTagCompound) {
						items.add(CraftItemStack.asBukkitCopy(net.minecraft.server.v1_16_R3.ItemStack.a((NBTTagCompound) base)));
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ItemStackUtil.class.getSimpleName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Nullable
	public static String toString(ItemStack[] items) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			NBTTagCompound inventory = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for (ItemStack itemStack : items) {
				net.minecraft.server.v1_16_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(itemStack);
				list.add(craftItem.save(new NBTTagCompound()));
			}
			inventory.set("i", list);
			NBTCompressedStreamTools.a(inventory, outputStream);
			return new String(outputStream.toByteArray());
		} catch (Exception e) {
			Logger.getLogger(ItemStackUtil.class.getSimpleName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
}
