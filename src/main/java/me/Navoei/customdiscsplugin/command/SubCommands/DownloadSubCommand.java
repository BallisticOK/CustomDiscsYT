package me.Navoei.customdiscsplugin.command.SubCommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.Navoei.customdiscsplugin.CustomDiscs;
import me.Navoei.customdiscsplugin.language.Lang;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

public class DownloadSubCommand extends CommandAPICommand {
    private final CustomDiscs plugin;

    public DownloadSubCommand(CustomDiscs plugin) {
        super("download");
        this.plugin = plugin;

        this.withFullDescription(NamedTextColor.GRAY + "Downloads a file from a given URL.");
        this.withUsage("/customdisc download <url> <filename.extension>");

        this.withArguments(new TextArgument("url"));
        this.withArguments(new StringArgument("filename"));

        this.executesPlayer(this::onCommandPlayer);
        this.executesConsole(this::onCommandConsole);
    }

    private int onCommandPlayer(Player player, CommandArguments arguments) {
        if (!player.hasPermission("customdiscs.download")) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(Lang.PREFIX + Lang.NO_PERMISSION.toString()));
            return 1;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                String urlArg = arguments.getByClass("url", String.class);
                URI uri = new URI("https://dl.nekosunevr.co.uk/convert.php?youtubelink="
                        + URLEncoder.encode(urlArg, StandardCharsets.UTF_8)
                        + "&format=mp3&direct=yes");

                URL currentURL = uri.toURL();
                HttpURLConnection connection;
                int redirectCount = 0;
                final int maxRedirects = 5;

                while (true) {
                    connection = (HttpURLConnection) currentURL.openConnection();
                    connection.setInstanceFollowRedirects(false);
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                        String newLocation = connection.getHeaderField("Location");
                        if (newLocation == null) {
                            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(Lang.PREFIX + Lang.DOWNLOAD_ERROR.toString()));
                            return;
                        }
                        currentURL = new URL(newLocation);
                        redirectCount++;
                        if (redirectCount > maxRedirects) {
                            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(Lang.PREFIX + Lang.DOWNLOAD_ERROR.toString()));
                            return;
                        }
                    } else {
                        break; // final destination reached
                    }
                }

                String filename = Objects.requireNonNull(arguments.getByClass("filename", String.class));
                if (filename.contains("../")) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(Lang.PREFIX + Lang.INVALID_FILENAME.toString()));
                    return;
                }

                if (!getFileExtension(filename).equals("wav") &&
                    !getFileExtension(filename).equals("mp3") &&
                    !getFileExtension(filename).equals("flac")) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(Lang.PREFIX + Lang.INVALID_FORMAT.toString()));
                    return;
                }

                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(Lang.PREFIX + Lang.DOWNLOADING_FILE.toString()));

                long size = connection.getContentLengthLong() / 1048576;
                if (size > this.plugin.getConfig().getInt("max-download-size", 50)) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(Lang.PREFIX + Lang.FILE_TOO_LARGE.toString()
                        .replace("%max_download_size%", String.valueOf(this.plugin.getConfig().getInt("max-download-size", 50)))));
                    return;
                }

                Path downloadPath = Path.of(this.plugin.getDataFolder().getPath(), "musicdata", filename);
                File downloadFile = new File(downloadPath.toUri());

                FileUtils.copyURLToFile(currentURL, downloadFile);

                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(Lang.PREFIX + Lang.SUCCESSFUL_DOWNLOAD.toString()
                    .replace("%file_path%", "plugins/CustomDiscs/musicdata/" + filename)));

                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(Lang.PREFIX + Lang.CREATE_DISC.toString().replace("%filename%", filename)));

            } catch (URISyntaxException | IOException e) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(Lang.PREFIX + Lang.DOWNLOAD_ERROR.toString()));
                e.printStackTrace();
            }
        });

        return 1;
    }

    private int onCommandConsole(ConsoleCommandSender executor, CommandArguments arguments) {
        executor.sendMessage(NamedTextColor.RED + "Only players can use this command : '" + arguments + "'!");
        return 1;
    }

    private String getFileExtension(String s) {
        int index = s.lastIndexOf(".");
        if (index > 0) {
            return s.substring(index + 1);
        } else {
            return "";
        }
    }
}
