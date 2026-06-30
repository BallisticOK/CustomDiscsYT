package me.Navoei.customdiscsplugin.command.SubCommands;

import me.Navoei.customdiscsplugin.CustomDiscs;
import me.Navoei.customdiscsplugin.language.Lang;
import me.Navoei.customdiscsplugin.utils.CustomModelDataUtils;
import me.Navoei.customdiscsplugin.utils.TypeChecker;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;

import io.papermc.paper.datacomponent.DataComponentTypes;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RevertCustomModelDataSubCommand extends CommandAPICommand {
    private final CustomDiscs plugin;

    public RevertCustomModelDataSubCommand(CustomDiscs plugin) {
        super("revertmodel");
        this.plugin = plugin;

        this.withFullDescription(NamedTextColor.GRAY + "Remove the Custom Model Data from the custom item in hand.");
        this.withUsage("/cd revertmodel");
        this.withPermission("customdiscs.revertmodel");

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

        if (!heldItem.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.REVERT_MODEL_NOT_SET.forPlayer(player)));
            return 1;
        }

        CustomModelDataUtils.reset(plugin, player);

        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.PREFIX.forPlayer(player) + Lang.REVERT_MODEL_SUCCESS.forPlayer(player)));
        return 1;
    }

    private int onCommandConsole(ConsoleCommandSender executor, CommandArguments arguments) {
        executor.sendMessage(NamedTextColor.RED + "Only players can use this command : '"+arguments+"'!");
        return 1;
    }
}
