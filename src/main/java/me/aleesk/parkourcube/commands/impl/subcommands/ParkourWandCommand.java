package me.aleesk.parkourcube.commands.impl.subcommands;

import me.aleesk.parkourcube.commands.BaseCommand;
import me.aleesk.parkourcube.commands.Command;
import me.aleesk.parkourcube.commands.CommandArgs;
import me.aleesk.parkourcube.models.parkour.ParkourSelection;
import me.aleesk.parkourcube.utils.ChatUtil;
import org.bukkit.entity.Player;

public class ParkourWandCommand extends BaseCommand {

    @Command(name = "parkourcube.wand", permission = "parkourcube.cmd.wand", aliases = {"parkourc.wand", "pc.wand", "pcube.wand", "parkour.wand"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        ChatUtil.sendMessage(player,"&aYou have received the Parkour Wand.");
        player.getInventory().addItem(ParkourSelection.SELECTION_WAND);
    }
}
