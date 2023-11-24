package net.bypiramid.nonslipping.engine.manager;

import net.bypiramid.nonslipping.Main;
import net.bypiramid.nonslipping.engine.trap.Trap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TrapManager {

    private Main plugin;
    private Map<UUID, Trap> trapMap;

    public TrapManager(Main plugin) {
        this.plugin = plugin;
        this.trapMap = new ConcurrentHashMap<>();
    }

    public Main getPlugin() {
        return plugin;
    }

    public Trap getOrCreateTrap(Player player) {
        return getOrCreateTrap(player, true);
    }

    public Trap getOrCreateTrap(Player player, boolean autoTeleport) {
        Trap found = trapMap.get(player.getUniqueId());

        if (found == null) {

        }

        return found;
    }
}
