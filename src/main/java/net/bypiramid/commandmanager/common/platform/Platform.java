package net.bypiramid.commandmanager.common.platform;

import java.io.File;

public interface Platform {

    String getName();

    File getFile();

    void runAsync(Runnable command);
}
