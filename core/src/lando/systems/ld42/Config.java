package lando.systems.ld42;

import com.badlogic.gdx.graphics.Color;

public class Config {
    public static final String title = "LudumDare42";
    public static final int window_width = 800;
    public static final int window_height = 600;
    public static final boolean resizable = false;
    public static final Color background_color = new Color(0f, .25f, .7f, 1f);
    public static final Color player_color = new Color(0f / 255f, 183f / 255f, 239f / 255f, 1f);
    public static final Color enemy_color = new Color(227f / 255f, 28f / 255f, 36f / 255f, 1f);
    public static final Color turn_text_color = new Color(255f / 255f, 126f / 255f, 0f / 255f, 0f);
    public static final Color selected_color = new Color(Color.FOREST.cpy());//new Color(34f / 255f, 177f / 255f, 76f / 255f, 1f);
    public static final Color highlight_color = new Color(255f / 255f, 242f / 255f, 0f / 255f, 1f);
    public static final boolean debug = false;
}
