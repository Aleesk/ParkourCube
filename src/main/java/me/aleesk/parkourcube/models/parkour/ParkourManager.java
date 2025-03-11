package me.aleesk.parkourcube.models.parkour;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.models.user.UserManager;
import me.aleesk.parkourcube.utils.BukkitUtil;
import me.aleesk.parkourcube.utils.ChatUtil;
import me.aleesk.parkourcube.utils.ItemBuilder;
import me.aleesk.parkourcube.utils.TaskUtil;
import me.aleesk.parkourcube.utils.cuboid.Cuboid;
import me.aleesk.parkourcube.utils.file.FileConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class ParkourManager {

    private final ParkourCube plugin;
    private final ItemStack startItem;
    private final Map<UUID, ParkourSession> activeSessions;
    private final UserManager userManager;
    private final FileConfig languageFile;
    private final int streak;
    private final List<String> commands;
    private ParkourSession parkourSession;
    private Cuboid cuboid;
    private Location location, respawn;

    public ParkourManager(ParkourCube plugin) {
        this.plugin = plugin;
        this.startItem = new ItemBuilder(XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.parseMaterial())
                .setName("&3Parkour &bItem")
                .setLore("&7Place this to create a parkour!")
                .build();
        this.activeSessions = Maps.newHashMap();
        this.languageFile = plugin.getLanguageFile();
        this.userManager = plugin.getUserManager();
        this.streak = plugin.getConfigFile().getConfiguration().getInt("parkour.streak.points", 10);
        this.commands = plugin.getConfigFile().getConfiguration().getStringList("parkour.streak.commands");
    }

    public void loadOrRefresh() {
        this.respawn = BukkitUtil.deserializeLocation(plugin.getConfigFile().getString("parkour.respawn"));
        this.location = BukkitUtil.deserializeLocation(plugin.getConfigFile().getString("parkour.location"));
        Location l1 = BukkitUtil.deserializeLocation(plugin.getConfigFile().getString("parkour.cuboid.higher"));
        Location l2 = BukkitUtil.deserializeLocation(plugin.getConfigFile().getString("parkour.cuboid.lower"));
        if (l1 != null && l2 != null) this.cuboid = new Cuboid(l1, l2);
        if (this.cuboid != null) this.parkourSession = new ParkourSession(plugin, cuboid);
    }

    public void setLocation(Location location) {
        this.location = location;
        plugin.getConfigFile().getConfiguration().set("parkour.location", BukkitUtil.serializeLocation(location));
        plugin.getConfigFile().save();
        plugin.getConfigFile().reload();
    }

    public void setRespawn(Location respawn) {
        this.respawn = respawn;
        plugin.getConfigFile().getConfiguration().set("parkour.respawn", BukkitUtil.serializeLocation(respawn));
        plugin.getConfigFile().save();
        plugin.getConfigFile().reload();
    }

    public void setCuboid(Player player) {
        ParkourSelection selection = ParkourSelection.createOrGetSelection(player);
        if (selection.isFullObject()) {
            this.cuboid = selection.getCuboid();
            player.sendMessage(ChatUtil.translate("&aParkour cuboid has been updated!"));
            plugin.getConfigFile().getConfiguration().set("parkour.cuboid.higher", BukkitUtil.serializeLocation(cuboid.getUpperCorner()));
            plugin.getConfigFile().getConfiguration().set("parkour.cuboid.lower", BukkitUtil.serializeLocation(cuboid.getLowerCorner()));
            plugin.getConfigFile().save();
            plugin.getConfigFile().reload();
            selection.clear();
                this.parkourSession = new ParkourSession(plugin, cuboid);
        } else {
            ChatUtil.sendMessage(player, "&cPlease select a valid higher and lower locations.");
        }
    }

    public void startParkour(UUID uuid) {
        Player player = userManager.getUser(uuid).getPlayer();
        if (activeSessions.containsKey(uuid)) {
            ChatUtil.sendMessage(player, languageFile.getString("already"));
            return;
        }
        if (parkourSession == null) return;

        parkourSession.start(uuid);
        if (parkourSession.isActive()) {
            TaskUtil.runLater(() -> activeSessions.put(uuid, parkourSession), 2L);
            ChatUtil.sendMessage(player, languageFile.getString("start").replace("<score>", String.valueOf(userManager.getUser(uuid).getScore())));
        }
    }

    public void stopParkour(UUID uuid) {
        ParkourSession session = activeSessions.remove(uuid);
        if (session != null) session.stop();
    }

    public void onDisable() {
        activeSessions.values().forEach(ParkourSession::stop);
        activeSessions.clear();
    }
}
