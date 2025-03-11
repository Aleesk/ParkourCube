package me.aleesk.parkourcube.commands.impl;

import me.aleesk.parkourcube.commands.BaseCommand;
import me.aleesk.parkourcube.commands.Command;
import me.aleesk.parkourcube.commands.CommandArgs;
import me.aleesk.parkourcube.utils.ChatUtil;

public class ParkourCommand extends BaseCommand {

    @Command(name = "parkourcube", permission = "parkourcube.cmd", aliases = {"parkour", "pc", "parkourc", "pcube"})
    @Override
    public void onCommand(CommandArgs command) {
        String label = command.getLabel();
        ChatUtil.sendMessage(command.getSender(), new String[]{
                "&3&lParkourCube &b&lCommands",
                "",
                " &b/&3" + label + " &bwand &7- &fGet corners selection tool.",
                " &b/&3" + label + " &bsetcuboid &7- &fSet parkour cuboid.",
                " &b/&3" + label + " &bitem &7- &fGet the parkour start item.",
                " &b/&3" + label + " &bsetrespawn &7- &fSet parkour respawn location.",
                " &b/&3" + label + " &breload &7- &fReload plugin."
        });
    }
}
