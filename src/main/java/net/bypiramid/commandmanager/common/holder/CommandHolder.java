package net.bypiramid.commandmanager.common.holder;


import net.bypiramid.commandmanager.common.Command;
import net.bypiramid.commandmanager.common.CommandFrame;
import net.bypiramid.commandmanager.common.argument.Argument;
import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.parameter.Param;
import net.bypiramid.commandmanager.common.parameter.ParamInfo;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class CommandHolder {

    private final CommandFrame commandFrame;
    private final Object parentClass;
    private final Method method;
    private List<String> names = new ArrayList<>();
    private String permission;
    private String description;
    private boolean async;
    private boolean playerOnly;
    private List<ParamInfo> parameters = new ArrayList<>();

    public CommandHolder(CommandFrame commandFrame, Object parentClass, Method method,
                         Command command) {
        this.commandFrame = commandFrame;

        Stream.of(command.names()).forEach(name -> names.add(name.toLowerCase()));

        this.permission = command.permission();
        this.description = command.description();
        this.async = command.async();
        this.playerOnly = command.playerOnly();

        this.parentClass = parentClass;
        this.method = method;

        Arrays.stream(method.getParameters()).forEach(parameter -> {
            Param param = parameter.getAnnotation(Param.class);

            if (param == null) {
                return;
            }

            parameters.add(new ParamInfo(param.name(), param.concated(), parameter));
        });
    }

    public CommandFrame getCommandManager() {
        return commandFrame;
    }

    public List<String> getNames() {
        return names;
    }

    public boolean isAsync() {
        return async;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public List<ParamInfo> getParameters() {
        return parameters;
    }

    public String getUsageMessage() {
        StringBuilder builder = new StringBuilder(ChatColor.RED + "Usage: /" + names.get(0) + " ");
        parameters.forEach(param -> {
            builder.append("<").append(param.getName()).append(param.isConcated() ? ".." : "").append(">");
            builder.append(" ");
        });
        return builder.toString();
    }

    /**
     * Executes the command
     *
     * @param context Command context
     * @param args    Arguments
     */
    public void execute(Context<?> context, String[] args) {
        if (!permission.equals("") && !context.testPermission(permission)) {
            context.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
            return;
        }

        if (!context.isPlayer() && playerOnly) {
            context.sendMessage(ChatColor.RED + "Comando exclusivo para jogadores.");
            return;
        }

        List<Object> objects = new ArrayList<>(Collections.singletonList(context));
        for (int i = 0; i < args.length; i++) {
            if (parameters.size() < i + 1) break;
            ParamInfo paramInfo = parameters.get(i);

            if (paramInfo.isConcated()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int x = i; x < args.length; x++) {
                    if (args.length - 1 < x) continue;
                    stringBuilder.append(args[x]).append(" ");
                }
                objects.add(stringBuilder.substring(0, stringBuilder.toString().length() - 1));
                break;
            }

            String suppliedArgument = args[i];
            objects.add(new Argument(commandFrame, paramInfo, suppliedArgument,
                    context).get());
        }

        int difference = (parameters.size() - objects.size()) - (args.length - objects.size());
        for (int i = 0; i < difference; i++) objects.add(null);

        if (async) {
            commandFrame.runAsync(() -> {
                try {
                    method.invoke(parentClass, objects.toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        try {
            method.invoke(parentClass, objects.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
