package me.aleesk.parkourcube.commands.impl.subcommands;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.commands.BaseCommand;
import me.aleesk.parkourcube.commands.Command;
import me.aleesk.parkourcube.commands.CommandArgs;
import org.bukkit.entity.Player;

public class ParkourCuboidCommand extends BaseCommand {

    private final ParkourCube plugin;

    public ParkourCuboidCommand(ParkourCube plugin) {
        this.plugin = plugin;
    }

    @Command(name = "parkourcube.setcuboid", permission = "parkourcube.cmd.setcuboid", aliases = {"parkourc.setcuboid", "pc.setcuboid", "pcube.setcuboid", "parkour.setcuboid"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        plugin.getParkourManager().setCuboid(player);
    }
}
