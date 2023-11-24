package net.bypiramid.nonslipping;

import net.bypiramid.commandmanager.bukkit.BukkitFrame;
import net.bypiramid.nonslipping.engine.manager.TrapManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private BukkitFrame commandFrame;
    private TrapManager trapManager;

    public Main() {
        instance = this;
    }

    @Override
    public void onLoad() {
        commandFrame = new BukkitFrame(this);
        trapManager = new TrapManager(this);
    }

    @Override
    public void onEnable() {
        commandFrame.registerCommands("net.bypiramid.commandmanager.nonslipping.command");
    }

    @Override
    public void onDisable() {
        commandFrame = null;
    }

    public static Main getInstance() {
        return instance;
    }
}
