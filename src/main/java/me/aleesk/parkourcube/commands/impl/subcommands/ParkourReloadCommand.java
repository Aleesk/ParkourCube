package me.aleesk.parkourcube.commands.impl.subcommands;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.commands.BaseCommand;
import me.aleesk.parkourcube.commands.Command;
import me.aleesk.parkourcube.commands.CommandArgs;
import me.aleesk.parkourcube.utils.ChatUtil;

public class ParkourReloadCommand extends BaseCommand {

    private final ParkourCube plugin;

    public ParkourReloadCommand(ParkourCube plugin) {
        this.plugin = plugin;
    }

    @Command(name = "parkourcube.reload", permission = "parkourcube.cmd.reload", aliases = {"parkourc.reload", "pc.reload", "pcube.reload", "parkour.reload"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        plugin.onReload();
        ChatUtil.sendMessage(commandArgs.getPlayer(), "&3&lPARKOUR &b&lCube &8| &aReload complete.");
    }
}
