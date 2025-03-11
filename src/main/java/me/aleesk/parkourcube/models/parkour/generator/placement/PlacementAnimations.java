package me.aleesk.parkourcube.models.parkour.generator.placement;

import me.aleesk.parkourcube.models.parkour.generator.GeneratorPlacement;
import me.aleesk.parkourcube.utils.ChatUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PlacementAnimations {
    private final GeneratorPlacement placement;
    private static final double RING_RADIUS = 0.8;
    private static final double SPIRAL_RADIUS = 1.2;
    private static final double SPIRAL_HEIGHT = 1.5;
    private static final double EXPLOSION_RADIUS = 0.7;

    private static final Map<String, Particle> PARTICLE_CACHE = new HashMap<>();
    private static final Map<String, Effect> EFFECT_CACHE = new HashMap<>();

    public PlacementAnimations(GeneratorPlacement placement) {
        this.placement = placement;
    }

    public void spawnEffect(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        String effectType = placement.getParticleEffectType().toLowerCase();
        int particleCount = placement.getParticleCount();
        String particleType = placement.getParticleType().toUpperCase();

        switch (effectType) {
            case "explosion":
                spawnExplosionEffect(location, particleCount, particleType);
                break;
            case "ring":
                spawnRingEffect(location, particleCount, particleType);
                break;
            case "spiral":
                spawnSpiralEffect(location, particleCount, particleType);
                break;
            default:
                ChatUtil.logger("&c[ParkourCube] Unknown particle effect type: " + effectType);
                spawnExplosionEffect(location, 1, "FLAME");
        }
    }

    private void spawnExplosionEffect(Location location, int count, String particleType) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int effectiveCount = Math.max(6, count);
        Location particleLoc = location.clone().add(0.5, 0.5, 0.5);

        for (int i = 0; i < effectiveCount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double x = EXPLOSION_RADIUS * Math.sin(phi) * Math.cos(theta);
            double y = EXPLOSION_RADIUS * Math.sin(phi) * Math.sin(theta);
            double z = EXPLOSION_RADIUS * Math.cos(phi);

            particleLoc.add(x, y, z);
            spawnParticle(particleLoc, particleType, 3, 0.15, 0.3);
            particleLoc.subtract(x, y, z);
        }
    }

    private void spawnRingEffect(Location location, int count, String particleType) {
        Location particleLoc = location.clone().add(0.5, 0.5, 0.5);

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = RING_RADIUS * Math.cos(angle);
            double z = RING_RADIUS * Math.sin(angle);

            particleLoc.add(x, 0, z);
            spawnParticle(particleLoc, particleType, 2, 0.05, 0.01);
            particleLoc.subtract(x, 0, z);
        }
    }

    private void spawnSpiralEffect(Location location, int count, String particleType) {
        Location particleLoc = location.clone().add(0.5, 0.5, 0.5);

        for (int i = 0; i < count; i++) {
            double t = (double) i / count;
            double angle = 4 * Math.PI * t;
            double radius = SPIRAL_RADIUS * (1 - t);
            double height = SPIRAL_HEIGHT * t;

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            particleLoc.add(x, height, z);
            spawnParticle(particleLoc, particleType, 2, 0.1, 0.05);
            particleLoc.subtract(x, height, z);
        }
    }

    private void spawnParticle(Location location, String particleType, int amount, double offset, double speed) {
        World world = location.getWorld();
        if (world == null) return;

        if (placement.getGenerator().isSupportsVersion9()) {
            Particle particle = getParticle(particleType);
            if (particle != null) {
                if (requiresData(particle)) {
                    ChatUtil.logger("&c[ParkourCube] Particle " + particle + " requires data, using FLAME instead.");
                    particle = Particle.FLAME;
                }
                world.spawnParticle(particle, location, amount, offset, offset, offset, speed);
            }
        } else {
            Effect effect = getEffect(particleType);
            if (effect != null) {
                world.playEffect(location, effect, 1);
            }
        }
    }

    private Particle getParticle(String particleType) {
        return PARTICLE_CACHE.computeIfAbsent(particleType, key -> {
            try {
                return Particle.valueOf(key);
            } catch (IllegalArgumentException e) {
                ChatUtil.logger("&c[ParkourCube] Invalid particle type: " + key + ", using FLAME");
                return Particle.FLAME;
            }
        });
    }

    private Effect getEffect(String effectType) {
        return EFFECT_CACHE.computeIfAbsent(effectType, key -> {
            try {
                return Effect.valueOf(key);
            } catch (IllegalArgumentException e) {
                ChatUtil.logger("&c[ParkourCube] Invalid effect type: " + key + ", using MOBSPAWNER_FLAMES");
                return Effect.MOBSPAWNER_FLAMES;
            }
        });
    }

    private boolean requiresData(Particle particle) {
        String particleName = particle.name().toLowerCase();

        switch (particleName) {
            case "block":
            case "block_crumble":
            case "block_marker":
            case "dust":
            case "dust_color_transition":
            case "dust_pillar":
            case "entity_effect":
            case "falling_dust":
            case "item":
            case "sculk_charge":
            case "trail":
            case "vibration":
                // ALIAS
            case "block_crack":
            case "block_dust":
            case "item_crack":
                return true;
            default:
                return false;
        }
    }
}