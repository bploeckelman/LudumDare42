package lando.systems.ld42.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = Config.title;
        config.width = Config.window_width;
        config.height = Config.window_height;
        config.resizable = Config.resizable;
        config.samples = 4;
        new LwjglApplication(new LudumDare42(), config);
    }
}
