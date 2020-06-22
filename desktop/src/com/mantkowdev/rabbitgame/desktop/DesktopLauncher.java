package com.mantkowdev.rabbitgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mantkowdev.rabbitgame.Main;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int) Main.WIDTH;
        config.height = (int) Main.HEIGHT;
        new LwjglApplication(new Main(), config);
    }
}
