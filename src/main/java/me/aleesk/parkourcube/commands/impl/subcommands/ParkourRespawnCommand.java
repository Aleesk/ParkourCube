package me.aleesk.parkourcube.commands.impl.subcommands;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.commands.BaseCommand;
import me.aleesk.parkourcube.commands.Command;
import me.aleesk.parkourcube.commands.CommandArgs;
import me.aleesk.parkourcube.utils.ChatUtil;
import org.bukkit.entity.Player;

public class ParkourRespawnCommand extends BaseCommand {
    private final ParkourCube plugin;

    public ParkourRespawnCommand(ParkourCube plugin) {
        this.plugin = plugin;
    }

    @Command(name = "parkourcube.setrespawn", permission = "parkourcube.cmd.setrespawn", aliases = {"parkourc.setrespawn", "pc.setrespawn", "pcube.setrespawn", "parkour.setrespawn"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        plugin.getParkourManager().setRespawn(player.getLocation());
        ChatUtil.sendMessage(player, "&aParkour respawn location has been updated.");
    }
}
