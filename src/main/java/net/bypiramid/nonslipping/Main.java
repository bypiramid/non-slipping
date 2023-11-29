package net.bypiramid.nonslipping;

import net.bypiramid.nonslipping.tracking.PlayerMoveTracking;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private PlayerMoveTracking trackingListener;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(trackingListener = new PlayerMoveTracking(this),
                this);
    }

    @Override
    public void onDisable() {
        if (trackingListener != null) {
            for (Player o : getServer().getOnlinePlayers())
                trackingListener.untracked(o);
            trackingListener = null;
        }
    }
}
