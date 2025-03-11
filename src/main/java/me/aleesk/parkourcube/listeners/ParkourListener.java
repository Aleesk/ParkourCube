package me.aleesk.parkourcube.listeners;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.models.parkour.ParkourManager;
import me.aleesk.parkourcube.models.parkour.ParkourSelection;
import me.aleesk.parkourcube.models.parkour.ParkourSession;
import me.aleesk.parkourcube.utils.ChatUtil;
import me.aleesk.parkourcube.utils.TaskUtil;
import me.aleesk.parkourcube.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ParkourListener implements Listener {

    private final ParkourCube plugin;
    private final ParkourManager parkourManager;
    private final FileConfig languageFile;

    public ParkourListener(ParkourCube plugin) {
        this.plugin = plugin;
        this.parkourManager = plugin.getParkourManager();
        this.languageFile = plugin.getLanguageFile();
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlaceParkour(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (item.isSimilar(plugin.getParkourManager().getStartItem())) {
            parkourManager.setLocation(event.getBlock().getLocation());
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null && clickedBlock.getType() == parkourManager.getStartItem().getType()) {
            if (clickedBlock.getLocation().equals(parkourManager.getLocation()) && event.getAction().equals(Action.PHYSICAL)) {
                if (parkourManager.getCuboid() != null) {
                    parkourManager.startParkour(player.getUniqueId());
                } else {
                    player.sendMessage(ChatUtil.translate(languageFile.getString("area-not-defined")));
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParkourSession session = parkourManager.getActiveSessions().get(player.getUniqueId());

        if (session != null) parkourManager.stopParkour(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ParkourSession session = parkourManager.getActiveSessions().get(player.getUniqueId());
        if (session == null) return;

        Location nextBlock = session.getNextTargetBlock();
        Location playerLocation = player.getLocation();

        double playerY = playerLocation.getY();
        double lowestBlockY = session.getLowestBlockHeight();

        if (playerY < lowestBlockY - 1) {
            ChatUtil.sendMessage(player, languageFile.getString("fall").replace("<score>", String.valueOf(session.getScore())));

            if (parkourManager.getRespawn() != null) TaskUtil.runLater(() -> player.teleport(parkourManager.getRespawn()), 2L);

            if (session.isNewHighScore(plugin.getUserManager().getUser(player.getUniqueId()))) {
                ChatUtil.sendMessage(player, languageFile.getString("new-high-score").replace("<score>", String.valueOf(session.getScore())));
                plugin.getUserManager().getUser(player.getUniqueId()).setScore(session.getScore());
            }

            plugin.getParkourManager().stopParkour(player.getUniqueId());

            return;
        }

        if (Math.abs(playerY - nextBlock.getY()) < 1.5) {
            double blockMinX = nextBlock.getX() - 0.2;
            double blockMaxX = nextBlock.getX() + 1.2;
            double blockMinZ = nextBlock.getZ() - 0.2;
            double blockMaxZ = nextBlock.getZ() + 1.2;

            double playerX = playerLocation.getX();
            double playerZ = playerLocation.getZ();

            if (playerX >= blockMinX && playerX <= blockMaxX &&
                    playerZ >= blockMinZ && playerZ <= blockMaxZ) {
                session.advance(player.getUniqueId());
                if (session.getScore() % parkourManager.getStreak() == 0) {
                    ChatUtil.sendMessage(player, languageFile.getString("streak").replace("<score>", String.valueOf( session.getScore())));
                    if (!parkourManager.getCommands().isEmpty())
                        parkourManager.getCommands().forEach(string -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChatUtil.format(string, player.getName())));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWandInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK))
            return;

        if (event.getItem() != null && event.getItem().isSimilar(ParkourSelection.SELECTION_WAND) && event.getClickedBlock() != null) {
            Block clicked = event.getClickedBlock();
            Player player = event.getPlayer();
            int location = 0;

            ParkourSelection selection = ParkourSelection.createOrGetSelection(player);

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                selection.setPoint2(clicked.getLocation());
                location = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                selection.setPoint1(clicked.getLocation());
                location = 1;
            }

            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            String message = "&3" + (location == 1 ? "First" : "Second") +
                    " location " + "&7(&f" +
                    clicked.getX() + "&7,&f" +
                    clicked.getY() + "&7,&f" +
                    clicked.getZ() + "&7)" + "&b has been set!";

            if (selection.isFullObject()) {
                message += "&7 (&f" + selection.getCuboid().volume() + " &3blocks" +
                        "&7)";
            }
            ChatUtil.sendMessage(player, message);
        }
    }
}
