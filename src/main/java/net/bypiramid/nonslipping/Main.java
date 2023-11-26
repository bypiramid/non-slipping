package net.bypiramid.nonslipping;

import net.bypiramid.nonslipping.engine.manager.Manager;
import net.bypiramid.nonslipping.engine.trap.Trap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private Manager manager;

    public Main() {
        instance = this;
    }

    @Override
    public void onEnable() {
        manager = new Manager(this);
    }

    @Override
    public void onDisable() {
        for (Player o : getServer().getOnlinePlayers()) {
            Trap trap = manager.removeTrap(o);
            if (trap != null) {
                trap.undo();
            }
        }

        manager = null;
    }

    public Manager getManager() {
        return manager;
    }

    public static Main getInstance() {
        return instance;
    }
}
