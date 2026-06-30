package me.Navoei.customdiscsplugin;

import me.Navoei.customdiscsplugin.language.Lang;
import me.Navoei.customdiscsplugin.utils.TypeChecker;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ModelSelectorManager implements InventoryHolder {
    public record ModelEntry(int value, String name) {}

    public static final int MAX_ENTRIES_PER_PAGE = 45;

    private static Map<Material, List<ModelEntry>> modelsByMaterial = Map.of();

    private final Inventory modelSelectorInventory;
    private final List<ModelEntry> allModelEntries;
    private final int currentPage;
    private final String itemCategory;
    private final Material customItemMaterial;

    private ModelSelectorManager(Player player, String itemCategoryGroup, Material customItemMaterial, List<ModelEntry> allModelEntries, int pageNumber) {
        this.itemCategory = itemCategoryGroup;
        this.customItemMaterial = customItemMaterial;
        this.currentPage = pageNumber;
        this.allModelEntries = allModelEntries;

        boolean hasPages = allModelEntries.size() > MAX_ENTRIES_PER_PAGE;
        int inventoryRows = Math.min(6, Math.max(1, (allModelEntries.size() + 8) / 9));

        this.modelSelectorInventory = Bukkit.createInventory(this, inventoryRows * 9, LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.MODEL_SELECTOR_TITLE.forPlayer(player)));

        int pageStart = currentPage * MAX_ENTRIES_PER_PAGE;
        int pageEnd = Math.min(pageStart + MAX_ENTRIES_PER_PAGE, allModelEntries.size());

        for (int i = pageStart; i < pageEnd; i++) {
            ModelEntry modelEntry = allModelEntries.get(i);
            ItemStack modelItem = new ItemStack(customItemMaterial);
            ItemMeta itemMeta = modelItem.getItemMeta();
            itemMeta.displayName(Component.text(modelEntry.name()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            modelItem.setItemMeta(itemMeta);
            modelItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat((float) modelEntry.value()).build());
            modelSelectorInventory.setItem(i - pageStart, modelItem);
        }

        if (hasPages) {
            if (currentPage > 0) {
                modelSelectorInventory.setItem(45, createNavButton(Lang.MODEL_SELECTOR_PREVIOUS.forPlayer(player), Material.RED_CONCRETE));
            }
            if (currentPage < totalPages(allModelEntries.size()) - 1) {
                modelSelectorInventory.setItem(53, createNavButton(Lang.MODEL_SELECTOR_NEXT.forPlayer(player), Material.LIME_CONCRETE));
            }
        }
    }

    public static void loadModels(File dataFolder, Logger logger) {
        Map<Material, List<ModelEntry>> loadedModels = new HashMap<>();
        File modelsFile = new File(dataFolder, "models.yml");
        if (modelsFile.exists()) {
            YamlConfiguration modelsConfig = YamlConfiguration.loadConfiguration(modelsFile);
            loadModelGroup(modelsConfig, "disc", logger, loadedModels);
            loadModelGroup(modelsConfig, "horn", logger, loadedModels);
            loadModelGroup(modelsConfig, "head", logger, loadedModels);
        }
        modelsByMaterial = Map.copyOf(loadedModels);
    }

    private static void loadModelGroup(YamlConfiguration modelsConfig, String groupItemType, Logger logger, Map<Material, List<ModelEntry>> modelEntryMap) {
        ConfigurationSection groupSection = modelsConfig.getConfigurationSection(groupItemType);
        if (groupSection == null) return;

        for (String materialKey : groupSection.getKeys(false)) {
            Material material = Material.matchMaterial(materialKey.toUpperCase());
            if (material == null) {
                logger.warning("Unknown material '" + materialKey + "' in models.yml ('" + groupItemType + "'). Skipping.");
                continue;
            }
            List<?> rawList = groupSection.getList(materialKey);
            if (rawList == null) continue;

            String materialPath = groupItemType + "/" + materialKey;
            List<ModelEntry> modelEntries = new ArrayList<>();
            for (Object rawEntry : rawList) {
                if (!(rawEntry instanceof Map<?, ?> entryMap)) {
                    logger.warning("Ignoring malformed entry in models.yml ('" + materialPath + "'): " + rawEntry);
                    continue;
                }
                Object rawValue = entryMap.get("value");
                Object rawName = entryMap.get("name");
                if (!(rawValue instanceof Number parsedValue) || !(rawName instanceof String parsedName)) {
                    logger.warning("Ignoring entry in models.yml ('" + materialPath + "'): 'value' must be an integer and 'name' a string. Entry: " + entryMap);
                    continue;
                }
                if (parsedValue.intValue() < 1) {
                    logger.warning("Ignoring entry '" + parsedName + "' in models.yml ('" + materialPath + "'): 'value' must be a positive integer (got " + parsedValue.intValue() + ").");
                    continue;
                }
                modelEntries.add(new ModelEntry(parsedValue.intValue(), parsedName));
            }
            if (!modelEntries.isEmpty())
                modelEntryMap.put(material, List.copyOf(modelEntries));
        }
    }

    public static void open(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        String itemCategoryGroup;
        if (TypeChecker.isCustomMusicDisc(heldItem)) {
            itemCategoryGroup = "disc";
        } else if (TypeChecker.isCustomGoatHornPlayer(player)) {
            itemCategoryGroup = "horn";
        } else if (TypeChecker.isCustomHeadPlayer(player)) {
            itemCategoryGroup = "head";
        } else {
            return;
        }

        List<ModelEntry> modelEntries = getModelEntries(heldItem.getType());
        if (modelEntries.isEmpty()) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.MODEL_SELECTOR_EMPTY.forPlayer(player)));
            return;
        }

        player.openInventory(new ModelSelectorManager(player, itemCategoryGroup, heldItem.getType(), modelEntries, 0).modelSelectorInventory);
    }

    public static void openPage(Player player, String itemCategoryGroup, Material customItemMaterial, int pageNumber) {
        player.openInventory(new ModelSelectorManager(player, itemCategoryGroup, customItemMaterial, getModelEntries(customItemMaterial), pageNumber).modelSelectorInventory);
    }

    @Override
    public Inventory getInventory() { return modelSelectorInventory; }

    public List<ModelEntry> getAllModelEntries() { return allModelEntries; }
    public int getPageNumber() { return currentPage; }
    public String getItemCategory() { return itemCategory; }
    public Material getCustomItemMaterial() { return customItemMaterial; }

    public static List<ModelEntry> getModelEntries(Material lookupMaterial) {
        return modelsByMaterial.getOrDefault(lookupMaterial, List.of());
    }

    public static int totalPages(int entryCount) {
        return (entryCount + MAX_ENTRIES_PER_PAGE - 1) / MAX_ENTRIES_PER_PAGE;
    }

    private ItemStack createNavButton(String itemNavLabel, Material navItemMaterial) {
        ItemStack navItem = new ItemStack(navItemMaterial);
        ItemMeta itemMeta = navItem.getItemMeta();
        itemMeta.displayName(Component.text(itemNavLabel).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        navItem.setItemMeta(itemMeta);
        return navItem;
    }
}
