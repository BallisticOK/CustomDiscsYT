package me.Navoei.customdiscsplugin.utils;

import me.Navoei.customdiscsplugin.CustomDiscs;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomModelDataUtils {

    private CustomModelDataUtils() {}

    /**
     * Apply a Custom Model Data value to the item in the player's main hand.
     */
    public static void apply(CustomDiscs plugin, Player player, int value) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = heldItem.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(new NamespacedKey(plugin, "custommodeldata"), PersistentDataType.INTEGER, value);
        heldItem.setItemMeta(itemMeta);
        heldItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat((float) value).build());
    }

    /**
     * Remove the Custom Model Data from the item in the player's main hand.
     */
    public static void reset(CustomDiscs plugin, Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = heldItem.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.remove(new NamespacedKey(plugin, "custommodeldata"));
        heldItem.setItemMeta(itemMeta);
        heldItem.resetData(DataComponentTypes.CUSTOM_MODEL_DATA);
    }
}
