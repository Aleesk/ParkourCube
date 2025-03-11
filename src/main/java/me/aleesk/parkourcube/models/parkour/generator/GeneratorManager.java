package me.aleesk.parkourcube.models.parkour.generator;

import lombok.Getter;
import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.utils.BukkitUtil;
import me.aleesk.parkourcube.utils.cuboid.Cuboid;
import org.bukkit.*;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class GeneratorManager {
    private final int minDistance;
    private final int maxDistance;
    private final int maxPlacementAttempts;
    private final GeneratorType generatorType;
    private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
    private final Cuboid cuboid;
    private final boolean supportsVersion9 = BukkitUtil.supports(9);
    private final GeneratorLocation genLocation;
    private final GeneratorPlacement genPlacer;

    public GeneratorManager(ParkourCube plugin, Cuboid cuboid) {
        this.cuboid = cuboid;
        this.maxPlacementAttempts = plugin.getConfigFile().getInt("parkour.generator.attempts");
        this.minDistance = plugin.getConfigFile().getInt("parkour.generator.min-distance");
        this.maxDistance = plugin.getConfigFile().getInt("parkour.generator.max-distance");
        this.generatorType = GeneratorType.valueOf(plugin.getConfigFile().getString("parkour.generator.type").toUpperCase());
        this.genLocation = new GeneratorLocation(this);
        this.genPlacer = new GeneratorPlacement(plugin, this);
    }

    public Location generateDirectionalNextBlock(Location previousBlock) {
        return genLocation.generateDirectionalNextBlock(previousBlock);
    }

    public Location generateRandomNextBlock(Location previousBlock) {
        return genLocation.generateRandomNextBlock(previousBlock);
    }

    public Location findValidStartingPoint() {
        return genLocation.findValidStartingPoint();
    }

    public void placeBlockAt(Location location) {
        genPlacer.placeBlockAt(location);
    }
}