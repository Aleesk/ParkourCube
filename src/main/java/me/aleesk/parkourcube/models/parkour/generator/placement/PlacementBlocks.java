package me.aleesk.parkourcube.models.parkour.generator.placement;

import me.aleesk.parkourcube.models.parkour.generator.GeneratorPlacement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlacementBlocks {
    private final GeneratorPlacement generatorPlacement;

    public PlacementBlocks(GeneratorPlacement generatorPlacement) {
        this.generatorPlacement = generatorPlacement;
    }

    public void placeBlock(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Material> blockMaterials = generatorPlacement.getBlockMaterials();
        Material blockType = blockMaterials.get(random.nextInt(blockMaterials.size()));
        world.getBlockAt(location).setType(blockType);
    }
}
