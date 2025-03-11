package me.aleesk.parkourcube;

import lombok.Getter;
import me.aleesk.parkourcube.commands.CommandManager;
import me.aleesk.parkourcube.commands.impl.ParkourCommand;
import me.aleesk.parkourcube.commands.impl.subcommands.*;
import me.aleesk.parkourcube.listeners.ParkourListener;
import me.aleesk.parkourcube.listeners.UserListener;
import me.aleesk.parkourcube.models.parkour.ParkourManager;
import me.aleesk.parkourcube.models.parkour.generator.GeneratorManager;
import me.aleesk.parkourcube.models.user.UserManager;
import me.aleesk.parkourcube.utils.ChatUtil;
import me.aleesk.parkourcube.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ParkourCube extends JavaPlugin {

    private FileConfig configFile, usersFile, languageFile;

    private ParkourManager parkourManager;
    private UserManager userManager;

    @Override
    public void onEnable() {
        this.registerConfigs();
        this.registerManagers();
        this.registerListeners();
        this.registerCommands();
        this.log(ChatUtil.LONG_LINE);
        this.log("");
        this.log("     &3Name&7: &b" + this.getName());
        this.log("     &3Version&7: &b" + this.getDescription().getVersion());
        this.log("     &3Author&7: &b" + this.getDescription().getAuthors().toString().replace("]", "").replace("[", ""));
        this.log("");
        this.log(ChatUtil.LONG_LINE);
    }

    @Override
    public void onDisable() {
        parkourManager.onDisable();
        userManager.onDisable();
    }

    public void onReload() {
        this.configFile.reload();
        this.usersFile.reload();
        this.languageFile.reload();
        loadManagers();
    }

    public void registerConfigs() {
        this.configFile = new FileConfig(this, "config.yml");
        this.usersFile = new FileConfig(this, "users.yml");
        this.languageFile = new FileConfig(this, "language.yml");
    }

    public void registerManagers() {
        this.userManager = new UserManager(this);
        this.parkourManager = new ParkourManager(this);
        this.loadManagers();
    }

    public void loadManagers(){
        parkourManager.loadOrRefresh();
    }

    public void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ParkourListener(this), this);
        pluginManager.registerEvents(new UserListener(this), this);
    }

    public void registerCommands() {
        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommands(new ParkourCommand());
        commandManager.registerCommands(new ParkourWandCommand());
        commandManager.registerCommands(new ParkourItemCommand(this));
        commandManager.registerCommands(new ParkourCuboidCommand(this));
        commandManager.registerCommands(new ParkourRespawnCommand(this));
        commandManager.registerCommands(new ParkourReloadCommand(this));
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatUtil.translate(message));
    }

    public static ParkourCube get() {
        return ParkourCube.getPlugin(ParkourCube.class);
    }
}
