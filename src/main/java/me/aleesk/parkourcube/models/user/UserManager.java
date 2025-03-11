package me.aleesk.parkourcube.models.user;

import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.utils.file.FileConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final ParkourCube plugin;
    private final FileConfig usersFile;
    private final Map<UUID, User> users;

    public UserManager(ParkourCube plugin) {
        this.plugin = plugin;
        this.usersFile = plugin.getUsersFile();
        this.users = new HashMap<>();
    }

    public void load(User user) {
        String path = "users." + user.getUuid().toString();
        if (usersFile.getConfiguration().contains(path)) {
            user.setScore(usersFile.getConfiguration().getInt(path + ".score"));
        }
    }

    public void create(UUID uuid, String name) {
        User user = new User(plugin, uuid, name);
        user.setScore(0);
        users.put(uuid, user);
    }

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public void destroyUser(User user) {
        user.save();
    }

    public void onDisable() {
        for (User user : users.values()) {
            destroyUser(user);
        }
    }
}
