package me.aleesk.parkourcube.models.user;

import lombok.Getter;
import lombok.Setter;
import me.aleesk.parkourcube.ParkourCube;
import me.aleesk.parkourcube.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class User {

    private final FileConfig usersFile;
    private final UUID uuid;
    private final String name;
    private int score;

    public User(ParkourCube plugin, UUID uuid, String name) {
        this.usersFile = plugin.getUsersFile();
        this.uuid = uuid;
        this.name = name;
    }

    public void save () {
        ConfigurationSection section = this.usersFile.getConfiguration().getConfigurationSection("users");
        if (section == null)
            section = this.usersFile.getConfiguration().createSection("users");
        section.set(this.uuid + ".name", this.name);
        section.set(this.uuid + ".score", this.score);
        this.usersFile.save();
        this.usersFile.reload();
    }

    public boolean isHighScore(int score) {
        return score > this.score;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
