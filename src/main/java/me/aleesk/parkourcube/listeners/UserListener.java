package me.aleesk.parkourcube.listeners;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.models.user.User;
import me.aleesk.parkourcube.models.user.UserManager;
import me.aleesk.parkourcube.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class UserListener implements Listener {

    private final UserManager userManager;

    public UserListener(ParkourCube plugin) {
        this.userManager = plugin.getUserManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        userManager.create(event.getUniqueId(), event.getName());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onPlayerLogin(PlayerLoginEvent event) {
        User user = userManager.getUser(event.getPlayer().getUniqueId());
        if (user == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatUtil.translate("&cFailed in load your user, please join again."));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = userManager.getUser(player.getUniqueId());
        if (user != null) userManager.load(user);
    }

    @EventHandler
    private void onPlayerSaveProfile(PlayerQuitEvent event) {
        User user = userManager.getUser(event.getPlayer().getUniqueId());

        if (user != null) {
            CompletableFuture.runAsync(() -> userManager.destroyUser(user));
        }
    }
}
