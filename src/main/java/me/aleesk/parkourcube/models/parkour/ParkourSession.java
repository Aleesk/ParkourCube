package me.aleesk.parkourcube.models.parkour;

import lombok.Getter;
import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.models.parkour.generator.GeneratorManager;
import me.aleesk.parkourcube.models.parkour.generator.GeneratorType;
import me.aleesk.parkourcube.models.user.User;
import me.aleesk.parkourcube.models.user.UserManager;
import me.aleesk.parkourcube.utils.ChatUtil;
import me.aleesk.parkourcube.utils.TaskUtil;
import me.aleesk.parkourcube.utils.cuboid.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ParkourSession {
    private final ParkourCube plugin;
    private final UserManager userManager;
    private final ParkourManager parkourManager;
    private final List<Location> activeBlocks = new ArrayList<>();
    private final GeneratorManager generatorManager;
    private boolean isActive = true;
    private int score = 0;

    public ParkourSession(ParkourCube plugin, Cuboid cuboid) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.parkourManager = plugin.getParkourManager();
        this.generatorManager = new GeneratorManager(plugin, cuboid);
    }

    public void start(UUID uuid) {
        activeBlocks.clear();
        Location startLocation = generatorManager.findValidStartingPoint();

        if (startLocation == null) {
            isActive = false;
            ChatUtil.logger("&c[ParkourPlugin] No valid starting location found in the area...");
            return;
        }

        initializeFirstBlocks(startLocation, uuid);
        teleportPlayerToStart(uuid);
        isActive = true;
    }

    private void initializeFirstBlocks(Location startLocation, UUID uuid) {
        activeBlocks.add(startLocation);
        generatorManager.placeBlockAt(startLocation);

        for (int i = 1; i < 3; i++) {
            Location nextBlock = generateNextBlock(activeBlocks.get(i - 1));
            if (nextBlock.equals(activeBlocks.get(i - 1))) {
                handleBlockGenerationFailure(uuid);
                return;
            }
            nextBlock = adjustBlockHeight(nextBlock);
            activeBlocks.add(nextBlock);
            generatorManager.placeBlockAt(nextBlock);
        }
    }

    private void teleportPlayerToStart(UUID uuid) {
        Location teleportLocation = activeBlocks.get(0).clone().add(0.5, 1, 0.5);
        TaskUtil.runLater(() -> userManager.getUser(uuid).getPlayer().teleport(teleportLocation), 2L);
    }

    public void advance(UUID uuid) {
        incrementScore();
        removeOldestBlock();
        Location lastBlock = activeBlocks.get(activeBlocks.size() - 1);
        Location nextBlock = generateNextBlock(lastBlock);

        if (nextBlock.equals(lastBlock)) {
            handleBlockGenerationFailure(uuid);
            return;
        }

        nextBlock = adjustBlockHeight(nextBlock);
        activeBlocks.add(nextBlock);
        generatorManager.placeBlockAt(nextBlock);
    }

    private Location generateNextBlock(Location previousBlock) {
        return generatorManager.getGeneratorType() == GeneratorType.RANDOM
                ? generatorManager.generateRandomNextBlock(previousBlock)
                : generatorManager.generateDirectionalNextBlock(previousBlock);
    }

    private Location adjustBlockHeight(Location nextBlock) {
        if (nextBlock.getY() - activeBlocks.get(0).getY() > 1) {
            Location first = activeBlocks.get(0);
            Location second = activeBlocks.get(1);

            boolean isBetweenX = nextBlock.getBlockX() >= Math.min(first.getBlockX(), second.getBlockX()) &&
                    nextBlock.getBlockX() <= Math.max(first.getBlockX(), second.getBlockX());
            boolean isBetweenZ = nextBlock.getBlockZ() >= Math.min(first.getBlockZ(), second.getBlockZ()) &&
                    nextBlock.getBlockZ() <= Math.max(first.getBlockZ(), second.getBlockZ());

            if (isBetweenX || isBetweenZ) {
                return nextBlock.subtract(0, 1, 0);
            }
        }
        return nextBlock;
    }

    private void incrementScore() {
        score++;
    }

    public boolean isNewHighScore(User user) {
        return user.isHighScore(this.score);
    }

    public Location getNextTargetBlock() {
        return activeBlocks.get(1);
    }

    public double getLowestBlockHeight() {
        return activeBlocks.stream()
                .mapToDouble(Location::getY)
                .min()
                .orElse(activeBlocks.get(1).getY());
    }

    private void handleBlockGenerationFailure(UUID uuid) {
        parkourManager.stopParkour(uuid);
        ChatUtil.sendMessage(userManager.getUser(uuid).getPlayer(), "&cError generating next parkour block...");
    }

    public void stop() {
        isActive = false;
        activeBlocks.forEach(location -> location.getBlock().setType(Material.AIR));
        activeBlocks.clear();
    }

    private void removeOldestBlock() {
        activeBlocks.get(0).getBlock().setType(Material.AIR);
        activeBlocks.remove(0);
    }
}
