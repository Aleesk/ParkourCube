package me.aleesk.parkourcube.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class BukkitUtil {

    public String getLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + ", "
                    + location.getX() + ", "
                    + location.getY() + ", "
                    + location.getZ();
    }

    public String serializeLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + ", " +
                location.getX() + ", " +
                location.getY() + ", " +
                location.getZ() + ", " +
                location.getYaw() + ", " +
                location.getPitch();
    }

    public Location deserializeLocation(String data) {
        if (data == null || data.isEmpty()) return null;

        String[] splittedData = data.split(", ");

        if (splittedData.length < 6) return null;

        World world = Bukkit.getWorld(splittedData[0]);
        double x = Double.parseDouble(splittedData[1]);
        double y = Double.parseDouble(splittedData[2]);
        double z = Double.parseDouble(splittedData[3]);
        float yaw = Float.parseFloat(splittedData[4]);
        float pitch = Float.parseFloat(splittedData[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public String serializeBlockLocation(Location location) {
        if (location == null || location.getWorld() == null) return null;
        return location.getWorld().getName() + ", " +
                location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ();
    }

    public Location deserializeBlockLocation(String data) {
        if (data == null || data.isEmpty()) return null;

        String[] splittedData = data.split(", ");

        if (splittedData.length < 4) return null;

        World world = Bukkit.getWorld(splittedData[0]);
        double x = Double.parseDouble(splittedData[1]);
        double y = Double.parseDouble(splittedData[2]);
        double z = Double.parseDouble(splittedData[3]);

        return new Location(world, x, y, z);
    }

    public List<String> serializeLocations(List<Location> locations) {
        if (locations == null) return null;

        List<String> serializedLocations = new ArrayList<>();

        for (Location location : locations) {
            serializedLocations.add(serializeBlockLocation(location));
        }

        return serializedLocations;
    }

    public List<Location> deserializeLocations(List<String> data) {
        if (data == null) return null;

        List<Location> locations = new ArrayList<>();

        for (String location : data) {
            locations.add(deserializeBlockLocation(location));
        }

        return locations;
    }

    public String serializeItemStackArray(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public ItemStack[] deserializeItemStackArray(String data) {
        if (data == null) return new ItemStack[0];
        if (data.equals("")) return new ItemStack[0];

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }
    public String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(itemStack);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize ItemStack.", e);
        }
    }

    public ItemStack deserializeItemStack(String data) {
        if (data == null || data.isEmpty()) return null;

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack itemStack = (ItemStack) dataInput.readObject();

            dataInput.close();
            return itemStack;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize ItemStack.", e);
        }
    }

    public String serializeInventory(Inventory inventory) {
        if (inventory == null) return null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(inventory.getSize());

            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize inventory.", e);
        }
    }

    public Inventory deserializeInventory(String data) {
        if (data == null || data.isEmpty()) return null;

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.createInventory(null, dataInput.readInt());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize inventory.", e);
        }
    }

    public static boolean supports(int version) {
        return VERSION >= version;
    }

    public static final int VERSION;

    static { // This needs to be right below VERSION because of initialization order.
        String version = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("MC: \\d\\.(\\d+)").matcher(version);

        if (matcher.find()) VERSION = Integer.parseInt(matcher.group(1));
        else throw new IllegalArgumentException("Failed to parse server version from: " + version);
    }
}
