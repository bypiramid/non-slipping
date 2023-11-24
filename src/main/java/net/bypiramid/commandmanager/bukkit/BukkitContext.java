package net.bypiramid.commandmanager.bukkit;

import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.holder.CommandHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitContext extends Context<CommandSender> {

    public BukkitContext(BukkitFrame commandFrame, CommandHolder commandHolder, CommandSender sender,
                         String label, String[] args) {
        super(commandFrame, commandHolder, sender, label, args);
    }

    @Override
    public boolean isPlayer() {
        return getSender() instanceof Player;
    }

    @Override
    public void sendMessage(String message) {
        getSender().sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        getSender().sendMessage(messages);
    }

    @Override
    public boolean testPermission(String permission) {
        return getSender().hasPermission(permission);
    }
}
