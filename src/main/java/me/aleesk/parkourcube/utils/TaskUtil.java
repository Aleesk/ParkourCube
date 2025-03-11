package me.aleesk.parkourcube.utils;

import me.aleesk.parkourcube.ParkourCube;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class TaskUtil {
    private TaskUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final JavaPlugin plugin = ParkourCube.get();

    public static void run(Runnable runnable) {
        Bukkit.getServer().getScheduler().runTask(plugin, runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(plugin, delay, timer);
    }

    public static void runTimerAsync(Runnable runnable, long delay, long timer) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, timer);
    }

    public static void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimerAsynchronously(plugin, delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public static void runLaterSync(Runnable runnable, long delay) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    public static void runTaskTimerAsynchronously(Runnable runnable, int delay) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, 20L * delay, 20L * delay);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }
}