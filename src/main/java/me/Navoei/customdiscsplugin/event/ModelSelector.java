package me.Navoei.customdiscsplugin.event;

import me.Navoei.customdiscsplugin.CustomDiscs;
import me.Navoei.customdiscsplugin.ModelSelectorManager;
import me.Navoei.customdiscsplugin.language.Lang;
import me.Navoei.customdiscsplugin.utils.CustomModelDataUtils;
import me.Navoei.customdiscsplugin.utils.TypeChecker;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ModelSelector implements Listener {
    private final CustomDiscs plugin;

    public ModelSelector(CustomDiscs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ModelSelectorManager modelSelector)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (!CustomDiscs.isCustomModelDataEnable()) {
            player.closeInventory();
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.CUSTOM_MODEL_DATA_DISABLED.forPlayer(player)));
            return;
        }

        if (!event.getInventory().equals(event.getClickedInventory())) return;

        int clickedSlot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        boolean hasPages = modelSelector.getAllModelEntries().size() > ModelSelectorManager.MAX_ENTRIES_PER_PAGE;
        if (hasPages) {
            if (clickedSlot == 45 && modelSelector.getPageNumber() > 0) {
                ModelSelectorManager.openPage(player, modelSelector.getItemCategory(), modelSelector.getCustomItemMaterial(), modelSelector.getPageNumber() - 1);
                return;
            }
            int totalPages = ModelSelectorManager.totalPages(modelSelector.getAllModelEntries().size());
            if (clickedSlot == 53 && modelSelector.getPageNumber() < totalPages - 1) {
                ModelSelectorManager.openPage(player, modelSelector.getItemCategory(), modelSelector.getCustomItemMaterial(), modelSelector.getPageNumber() + 1);
                return;
            }
        }

        if (clickedSlot >= ModelSelectorManager.MAX_ENTRIES_PER_PAGE) return;

        int selectedIndex = modelSelector.getPageNumber() * ModelSelectorManager.MAX_ENTRIES_PER_PAGE + clickedSlot;
        if (selectedIndex >= modelSelector.getAllModelEntries().size()) return;

        ModelSelectorManager.ModelEntry selectedModel = modelSelector.getAllModelEntries().get(selectedIndex);

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() != modelSelector.getCustomItemMaterial() || !isStillCustomItem(player, heldItem)) {
            player.closeInventory();
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.NOT_HOLDING_CORRECT_ITEM.forPlayer(player)));
            return;
        }

        CustomModelDataUtils.apply(plugin, player, selectedModel.value());

        player.closeInventory();
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.SET_MODEL_SUCCESS.forPlayer(player)));
    }

    private boolean isStillCustomItem(Player player, ItemStack heldItem) {
        return TypeChecker.isCustomMusicDisc(heldItem) || TypeChecker.isCustomGoatHornPlayer(player) || TypeChecker.isCustomHeadPlayer(player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ModelSelectorManager) {
            event.setCancelled(true);
        }
    }
}
