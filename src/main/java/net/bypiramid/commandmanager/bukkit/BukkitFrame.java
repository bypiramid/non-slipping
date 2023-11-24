package net.bypiramid.commandmanager.bukkit;

import net.bypiramid.commandmanager.common.Command;
import net.bypiramid.commandmanager.common.CommandFrame;
import net.bypiramid.commandmanager.common.holder.CommandHolder;
import net.bypiramid.commandmanager.common.parameter.AdapterMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BukkitFrame implements CommandFrame {

    private final JavaPlugin plugin;
    private final AdapterMap adapterMap;
    private final Map<String, CommandHolder> commandMap = new HashMap<>();

    public BukkitFrame(JavaPlugin plugin, AdapterMap adapterMap) {
        this.plugin = plugin;
        this.adapterMap = adapterMap;
    }

    public BukkitFrame(JavaPlugin plugin) {
        this(plugin, new AdapterMap(true));
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void runAsync(Runnable command) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), command);
    }

    @Override
    public void registerRawCommand(Command info) {
        String[] aliases = info.names();
        String name = aliases[0];

        BukkitCommand command = new BukkitCommand(name, info.description(), "/<command>",
                Stream.of(aliases).filter(alias -> !alias.equalsIgnoreCase(name))
                        .collect(Collectors.toList()));

        if (!info.permission().isEmpty()) {
            command.setPermission(info.permission());
        }

        ((CraftServer) getPlugin().getServer()).getCommandMap().register(getPlugin().getName().toLowerCase(), command);
    }

    @Override
    public AdapterMap getAdapterMap() {
        return adapterMap;
    }

    @Override
    public Map<String, CommandHolder> getCommandMap() {
        return commandMap;
    }

    class BukkitCommand extends org.bukkit.command.Command {

        public BukkitCommand(String name, String description, String usageMessage,
                             List<String> aliases) {
            super(name, description, usageMessage, aliases);
            super.setPermissionMessage("§cVocê não tem permissão para usar este comando.");
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            return BukkitFrame.this.dispatchCommand(
                    new BukkitContext(BukkitFrame.this, BukkitFrame.this.getCommandHolder(label),
                            sender, label, args), label, args);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
            List<String> completions = BukkitFrame.this.tabComplete(
                    new BukkitContext(BukkitFrame.this, BukkitFrame.this.getCommandHolder(alias),
                            sender, alias, args), alias, args);

            if (completions == null) {
                return super.tabComplete(sender, alias, args);
            }

            return completions;
        }
    }
}
