package me.aleesk.parkourcube.models.parkour.generator.placement;

import me.aleesk.parkourcube.models.parkour.generator.GeneratorPlacement;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

public class PlacementSounds {
    private final GeneratorPlacement placement;

    public PlacementSounds(GeneratorPlacement placement) {
        this.placement = placement;
    }

    public void playSound(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        String[] soundParts = placement.getSoundConfig().split(";");
        String soundName = soundParts[0];
        float volume = soundParts.length > 1 ? Float.parseFloat(soundParts[1]) : 0.5F;
        float pitch = soundParts.length > 2 ? Float.parseFloat(soundParts[2]) : 1.0F;

        try {
            Sound sound = Sound.valueOf(soundName);
            world.playSound(location, sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            String defaultSound = placement.getGenerator().isSupportsVersion9() ? "ENTITY_CHICKEN_EGG" : "CHICKEN_EGG_POP";
            world.playSound(location, Sound.valueOf(defaultSound), volume, pitch);
        }
    }
}
