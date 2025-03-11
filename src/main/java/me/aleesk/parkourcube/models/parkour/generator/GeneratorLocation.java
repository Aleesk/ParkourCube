package me.aleesk.parkourcube.models.parkour.generator;

import me.aleesk.parkourcube.utils.ChatUtil;
import me.aleesk.parkourcube.utils.cuboid.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorLocation {
    private final GeneratorManager generator;
    private static final char[] DIRECTIONS = {'N', 'S', 'E', 'W'};
    private static final int[] HEIGHT_VARIATIONS = {-1, 0, 1};

    public GeneratorLocation(GeneratorManager generator) {
        this.generator = generator;
    }

    public Location generateDirectionalNextBlock(Location previousBlock) {
        return generateNextBlock(previousBlock, false);
    }

    public Location generateRandomNextBlock(Location previousBlock) {
        return generateNextBlock(previousBlock, true);
    }

    private Location generateNextBlock(Location previousBlock, boolean isRandom) {
        World world = previousBlock.getWorld();
        int x = previousBlock.getBlockX();
        int y = previousBlock.getBlockY();
        int z = previousBlock.getBlockZ();

        Cuboid cuboid = generator.getCuboid();
        int minX = cuboid.getLowerX(), maxX = cuboid.getUpperX();
        int minZ = cuboid.getLowerZ(), maxZ = cuboid.getUpperZ();
        int minY = cuboid.getLowerY(), maxY = cuboid.getUpperY();
        double minDistSquared = Math.pow(generator.getMinDistance(), 2);

        ThreadLocalRandom random = generator.getThreadLocalRandom();
        int maxAttempts = generator.getMaxPlacementAttempts();
        int distance = getRandomDistance(random);
        int newX = x, newZ = z;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {

            if (isRandom) {
                newX += random.nextBoolean() ? distance : -distance;
                newZ += random.nextBoolean() ? distance : -distance;
            } else {
                switch (DIRECTIONS[random.nextInt(DIRECTIONS.length)]) {
                    case 'N': newZ -= distance; break;
                    case 'S': newZ += distance; break;
                    case 'E': newX += distance; break;
                    case 'W': newX -= distance; break;
                }
            }

            newX = clamp(newX, minX, maxX);
            newZ = clamp(newZ, minZ, maxZ);
            int newY = clamp(y + HEIGHT_VARIATIONS[random.nextInt(HEIGHT_VARIATIONS.length)], minY, maxY);

            if (isValidPlacement(world, newX, newY, newZ) && distanceSquared(x, z, newX, newZ) >= minDistSquared) {
                return new Location(world, newX, newY, newZ);
            }
        }

        ChatUtil.logger("&c[ParkourCube] No valid location found after multiple attempts...");
        return previousBlock;
    }

    public Location findValidStartingPoint() {
        Cuboid cuboid = generator.getCuboid();
        World world = cuboid.getWorld();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int maxAttempts = generator.getMaxPlacementAttempts();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = random.nextInt(cuboid.getLowerX(), cuboid.getUpperX() + 1);
            int y = random.nextInt(cuboid.getLowerY(), cuboid.getUpperY() + 1);
            int z = random.nextInt(cuboid.getLowerZ(), cuboid.getUpperZ() + 1);

            if (isValidPlacement(world, x, y, z)) {
                return new Location(world, x, y, z);
            }
        }
        return null;
    }

    private int getRandomDistance(ThreadLocalRandom random) {
        return generator.getMinDistance() + random.nextInt(generator.getMaxDistance() - generator.getMinDistance() + 1);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private boolean isValidPlacement(World world, int x, int y, int z) {
        return world != null && world.getChunkAt(x >> 4, z >> 4).isLoaded() &&
                world.getBlockAt(x, y, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 2, z).getType() == Material.AIR &&
                world.getBlockAt(x, y - 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y - 2, z).getType() == Material.AIR;
    }

    private double distanceSquared(int x1, int z1, int x2, int z2) {
        return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2);
    }
}