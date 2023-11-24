package net.bypiramid.commandmanager.common;

import net.bypiramid.commandmanager.common.argument.Argument;
import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.holder.CommandHolder;
import net.bypiramid.commandmanager.common.parameter.Adapter;
import net.bypiramid.commandmanager.common.parameter.AdapterMap;
import net.bypiramid.commandmanager.common.parameter.ParamInfo;
import net.bypiramid.reflectionapi.resolver.ClassGetter;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface CommandFrame {

    Object getPlugin();

    void runAsync(Runnable command);

    void registerRawCommand(Command info);

    AdapterMap getAdapterMap();

    Map<String, CommandHolder> getCommandMap();

    default CommandHolder getCommandHolder(String alias) {
        return getCommandMap().get(alias.toLowerCase());
    }

    default void registerCommands(String path) {
        ClassGetter.getClassesForPackageByPlugin(getPlugin(), path).stream()
                .forEach(this::registerCommands);
    }

    default void registerCommands(Class<?> clazz) {
        try {
            registerCommands(clazz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void registerCommands(Object commandClass) {
        Stream.of(commandClass.getClass().getDeclaredMethods()).forEach(method -> {
            Command command = method.getAnnotation(Command.class);

            if(command == null) {
                return;
            }

            CommandHolder commandHolder = new CommandHolder(this, commandClass, method, command);
            Stream.of(command.names()).forEach(name -> getCommandMap().put(name.toLowerCase(), commandHolder));

            registerRawCommand(command);
        });
    }

    default void registerAdapters(String path) {
        ClassGetter.getClassesForPackageByPlugin(getPlugin(), path).stream()
                .filter(clazz -> Adapter.class.isAssignableFrom(clazz))
                .forEach(clazz -> {
                    try {
                        getAdapterMap().registerAdapter((Adapter<?>) clazz.newInstance());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    default boolean dispatchCommand(Context<?> context, String label, String[] args) {
        CommandHolder commandHolder = getCommandHolder(label);

        if (commandHolder == null) {
            System.out.println("Command '" + label + "' not handled!");
            return true;
        }

        try {
            commandHolder.execute(context, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    default List<String> tabComplete(Context<?> context, String label, String[] args) throws IllegalArgumentException {
        try {
            CommandHolder commandHolder = getCommandHolder(label);
            int arg = args.length - 1;

            if(arg < 0 || commandHolder.getParameters().size() < arg + 1) {
                return null;
            }

            ParamInfo paramInfo = commandHolder.getParameters().get(arg);
            return new Argument(this, paramInfo, args[args.length - 1], context).getTabComplete();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
