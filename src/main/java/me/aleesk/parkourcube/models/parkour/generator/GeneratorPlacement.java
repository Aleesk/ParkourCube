package me.aleesk.parkourcube.models.parkour.generator;

import lombok.Getter;
import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.models.parkour.generator.placement.PlacementBlocks;
import me.aleesk.parkourcube.models.parkour.generator.placement.PlacementAnimations;
import me.aleesk.parkourcube.models.parkour.generator.placement.PlacementSounds;
import me.aleesk.parkourcube.utils.BukkitUtil;
import me.aleesk.parkourcube.utils.ChatUtil;
import me.aleesk.parkourcube.utils.file.FileConfig;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GeneratorPlacement {
    private final GeneratorManager generator;
    private final PlacementBlocks placementBlocks;
    private final PlacementAnimations placementEffects;
    private final PlacementSounds placementSounds;
    private List<Material> blockMaterials;
    private String particleEffectType;
    private String particleType;
    private int particleCount;
    private boolean effectsEnabled;
    private String soundConfig;
    private boolean soundEnabled;
    private static final String DEFAULT_SOUND_1_8 = "CHICKEN_EGG_POP;0.5;1.0";
    private static final String DEFAULT_SOUND_1_9_PLUS = "ENTITY_CHICKEN_EGG;0.5;1.0";

    public GeneratorPlacement(ParkourCube plugin, GeneratorManager generator) {
        this.generator = generator;
        loadConfig(plugin.getConfigFile());
        this.placementBlocks = new PlacementBlocks(this);
        this.placementEffects = new PlacementAnimations(this);
        this.placementSounds = new PlacementSounds(this);
    }

    public void placeBlockAt(Location location) {
        placementBlocks.placeBlock(location);
        if (effectsEnabled) placementEffects.spawnEffect(location);
        if (soundEnabled) placementSounds.playSound(location);
    }

    private void loadConfig(FileConfig config) {
        String path = "parkour.generator.";
        blockMaterials = new ArrayList<>();
        List<String> materialList = config.getConfiguration().getStringList(path + "block-materials");
        for (String entry : materialList) {
            Material material = parseMaterial(entry);
            if (material != null) blockMaterials.add(material);
        }
        if (blockMaterials.isEmpty()) blockMaterials.add(Material.STONE);

        effectsEnabled = config.getConfiguration().getBoolean(path + "animation.enabled", true);
        particleEffectType = config.getConfiguration().getString(path + "animation.type", "explosion").toLowerCase();
        particleType = config.getConfiguration().getString(path + "animation.particle-type", "FLAME").toUpperCase();
        particleCount = config.getConfiguration().getInt(path + "animation.particle-intensity", 1);

        soundConfig = config.getConfiguration().getString(path + "sound.type", generator.isSupportsVersion9() ? DEFAULT_SOUND_1_9_PLUS : DEFAULT_SOUND_1_8);
        soundEnabled = config.getConfiguration().getBoolean(path + "sound.enabled", true);
    }

    private Material parseMaterial(String entry) {
        try {
            if (entry.contains(";")) { // Format legacy: <id>;<data>
                String[] parts = entry.split(";");
                String id = parts[0];
                byte data = Byte.parseByte(parts[1]);
                Material material = Material.getMaterial(id);
                if (material != null && BukkitUtil.supports(13)) {
                    return Material.valueOf(material.name() + "_" + data);
                }
                return material;
            } else {
                return Material.valueOf(entry.toUpperCase());
            }
        } catch (Exception e) {
            ChatUtil.logger("&c[ParkourCube] Invalid material: " + entry);
            return null;
        }
    }
}
