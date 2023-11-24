package net.bypiramid.commandmanager.common.context;

import net.bypiramid.commandmanager.common.CommandFrame;
import net.bypiramid.commandmanager.common.parameter.Adapter;
import net.bypiramid.commandmanager.common.holder.CommandHolder;

import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class Context<S> {

    private final CommandFrame commandFrame;
    private final CommandHolder commandHolder;
    private final S sender;
    private final String label;
    private final String[] args;

    public Context(CommandFrame commandFrame, CommandHolder commandHolder, S sender, String label,
                   String[] args) {
        this.commandFrame = commandFrame;
        this.commandHolder = commandHolder;
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    public abstract boolean isPlayer();

    public CommandFrame getCommandManager() {
        return commandFrame;
    }

    public CommandHolder getCommandHolder() {
        return commandHolder;
    }

    public S getSender() {
        return sender;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }

    /**
     * @return the number of arguments
     */
    public int argsCount() {
        return getArgs().length;
    }

    /**
     * @param index the index of the argument
     * @return the argument - null if the index is out of bounds
     */
    public String getArg(int index) {
        try {
            return getArgs()[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(int index, Class<T> type) {
        return (T) getCommandManager().getAdapterMap().get(type).adapt(this, getArg(index));
    }

    /**
     * Gets all args between indexes from and to
     *
     * @param from defines the start of the array relative to the arguments, inclusive
     * @param to   defines the end of the array relative to the arguments, exclusive
     * @return the arguments array - null if the indexes are out of bounds
     */
    public String[] getArgs(int from, int to) {
        try {
            return Arrays.copyOfRange(getArgs(), from, to);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArgs(int from, int to, Class<T> type) {
        try {
            final Adapter<?> adapter = getCommandManager().getAdapterMap().get(type);
            final T[] instance = (T[]) Array.newInstance(type,  to - from);

            for (int i = from; i <= to; i++) {
                instance[i - from] = (T) adapter.adapt(this, getArg(i));
            }

            return instance;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Sends a message to the executor
     *
     * @param message the message to be sent
     */
    public abstract void sendMessage(String message);

    /**
     * Sends multiple messages to the executor
     *
     * @param messages the messages to be sent
     */
    public abstract void sendMessage(String[] messages);

    /**
     * Tests whether the executor has a permission
     *
     * @param permission the permission to be tested
     */
    public abstract boolean testPermission(String permission);

    /**
     * Sends a message formatting it with the String#format() method
     *
     * @param message the message to be sent
     * @param objects the objects to be inserted
     */
    public void sendMessage(String message, Object... objects) {
        sendMessage(String.format(message, objects));
    }
}
