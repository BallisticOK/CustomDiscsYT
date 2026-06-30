package me.Navoei.customdiscsplugin.command.SubCommands;

import me.Navoei.customdiscsplugin.CustomDiscs;
import me.Navoei.customdiscsplugin.ModelSelectorManager;
import me.Navoei.customdiscsplugin.language.Lang;
import me.Navoei.customdiscsplugin.utils.CustomModelDataUtils;
import me.Navoei.customdiscsplugin.utils.TypeChecker;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetCustomModelDataSubCommand extends CommandAPICommand {
    private final CustomDiscs plugin;

    public SetCustomModelDataSubCommand(CustomDiscs plugin) {
        super("setmodel");
        this.plugin = plugin;

        this.withFullDescription(NamedTextColor.GRAY + "Apply a Custom Model Data to a custom item, or open the graphical model selector (if no model specified).");
        this.withUsage("/cd setmodel [model]");
        this.withPermission("customdiscs.setmodel");

        this.withOptionalArguments(new TextArgument("model")
                .replaceSuggestions(ArgumentSuggestions.strings(suggestion -> {
                    if (!(suggestion.sender() instanceof Player player)) return new String[0];
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (!TypeChecker.isCustomMusicDisc(heldItem) && !TypeChecker.isCustomGoatHornPlayer(player) && !TypeChecker.isCustomHeadPlayer(player)) {
                        return new String[0];
                    }
                    return ModelSelectorManager.getModelEntries(heldItem.getType()).stream().map(e -> "\"" + e.name() + "\"").toArray(String[]::new);
                })));

        this.executesPlayer(this::onCommandPlayer);
        this.executesConsole(this::onCommandConsole);
    }

    private int onCommandPlayer(Player player, CommandArguments arguments) {
        if (!CustomDiscs.isCustomModelDataEnable()) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.CUSTOM_MODEL_DATA_DISABLED.forPlayer(player)));
            return 1;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        boolean isCustomDisc = TypeChecker.isCustomMusicDisc(heldItem);
        boolean isCustomHorn = TypeChecker.isCustomGoatHornPlayer(player);
        boolean isCustomHead = TypeChecker.isCustomHeadPlayer(player);

        if (!isCustomDisc && !isCustomHorn && !isCustomHead) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.NOT_HOLDING_CORRECT_ITEM.forPlayer(player)));
            return 1;
        }

        String modelInput = (String) arguments.get("model");

        if (modelInput == null) {
            ModelSelectorManager.open(player);
            return 1;
        }

        int customModelDataValue;
        try {
            customModelDataValue = Integer.parseInt(modelInput);
            if (customModelDataValue < 1) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.SET_MODEL_INVALID.forPlayer(player)));
                return 1;
            }
        } catch (NumberFormatException e) {
            customModelDataValue = ModelSelectorManager.getModelEntries(heldItem.getType()).stream()
                    .filter(entry -> entry.name().equalsIgnoreCase(modelInput))
                    .mapToInt(ModelSelectorManager.ModelEntry::value)
                    .findFirst()
                    .orElse(-1);
            if (customModelDataValue == -1) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.SET_MODEL_INVALID.forPlayer(player)));
                return 1;
            }
        }

        CustomModelDataUtils.apply(plugin, player, customModelDataValue);
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.SET_MODEL_SUCCESS.forPlayer(player)));
        return 1;
    }

    private int onCommandConsole(ConsoleCommandSender executor, CommandArguments arguments) {
        executor.sendMessage(NamedTextColor.RED + "Only players can use this command : '"+arguments+"'!");
        return 1;
    }
}
