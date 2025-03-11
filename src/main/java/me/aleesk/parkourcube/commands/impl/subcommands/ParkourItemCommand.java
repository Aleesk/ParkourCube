package me.aleesk.parkourcube.commands.impl.subcommands;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.commands.BaseCommand;
import me.aleesk.parkourcube.commands.Command;
import me.aleesk.parkourcube.commands.CommandArgs;
import me.aleesk.parkourcube.utils.ChatUtil;
import org.bukkit.entity.Player;

public class ParkourItemCommand extends BaseCommand {

    private final ParkourCube plugin;

    public ParkourItemCommand(ParkourCube plugin) {
        this.plugin = plugin;
    }

    @Command(name = "parkourcube.item", permission = "parkourcube.cmd.item", aliases = {"parkourc.item", "pc.item", "pcube.item", "parkour.item"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        ChatUtil.sendMessage(player,"&aYou have received the parkour starter item.");
        player.getInventory().addItem(plugin.getParkourManager().getStartItem());
    }
}
