package xyz.oribuin.eternaltags.command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.eternaltags.EternalTags;
import xyz.oribuin.eternaltags.command.sub.*;
import xyz.oribuin.eternaltags.gui.TagGUI;
import xyz.oribuin.eternaltags.manager.MessageManager;
import xyz.oribuin.eternaltags.manager.TagManager;
import xyz.oribuin.eternaltags.obj.Tag;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command.Info(
        name = "tags",
        description = "Main command for EternalTags",
        permission = "eternaltags.use",
        playerOnly = false,
        usage = "/tags",
        subcommands = {
                SubClear.class,
                SubConvert.class,
                SubCreate.class,
                SubDelete.class,
                SubReload.class,
                SubSearch.class,
                SubSet.class,
                SubSetAll.class
        },
        aliases = {"eternaltags"}
)
public class CmdTags extends Command {

    private final EternalTags plugin = (EternalTags) this.getOriPlugin();

    public CmdTags(EternalTags plugin) {
        super(plugin);
    }

    @Override
    public void runFunction(CommandSender sender, String label, String[] args) {

        if (sender instanceof Player && args.length == 0) {
            new TagGUI(this.plugin, ((Player) sender)).createGUI();
            return;
        }

        final FileConfiguration config = this.plugin.getManager(MessageManager.class).getConfig();
        final String prefix = config.getString("prefix");
        final String unknownCommand = prefix + config.getString("unknown-command");
        final String noPerm = prefix + config.getString("invalid-permission");
        this.runSubCommands(sender, args, unknownCommand, noPerm);
    }

    @Override
    public List<String> completeString(CommandSender sender, String label, String[] args) {

        final TagManager tag = this.plugin.getManager(TagManager.class);
        final List<String> tabComplete = new ArrayList<>();

        if (this.getInfo().permission().length() > 0 && !sender.hasPermission(this.getInfo().permission()))
            return playerList(sender);

        switch (args.length) {
            case 1: {
                tabComplete.addAll(this.getSubCommands().stream().map(SubCommand::getAnnotation)
                        .filter(info -> info.permission().length() != 0 && sender.hasPermission(info.permission()))
                        .map(info -> info.names()[0])
                        .collect(Collectors.toList()));

                break;
            }

            case 2: {
                if (args[0].equalsIgnoreCase("create"))
                    tabComplete.add("<name>");

                if (args[0].equalsIgnoreCase("search") && sender instanceof Player)
                    tabComplete.addAll(tag.getPlayersTag((Player) sender)
                            .stream()
                            .map(Tag::getName)
                            .collect(Collectors.toList()));

                if (Arrays.asList("delete", "setall").contains(args[0]))
                    tabComplete.addAll(tag.getTags().stream().map(Tag::getId).collect(Collectors.toList()));

                if (Arrays.asList("set", "clear").contains(args[0]))
                    return playerList(sender);

                break;
            }

            case 3: {
                if (args[0].equalsIgnoreCase("set"))
                    tabComplete.addAll(tag.getTags().stream().map(Tag::getId).collect(Collectors.toList()));

                if (args[0].equalsIgnoreCase("create"))
                    tabComplete.add("<tag>");

                break;
            }

            default:
                tabComplete.addAll(playerList(sender));
                break;
        }

        return tabComplete;
    }
}
