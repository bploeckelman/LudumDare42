package lando.systems.ld42.utils;

import com.badlogic.gdx.graphics.Color;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class TileUtils {

    public static float getX(int col, float tileWidth) {
        float x = tileWidth * col * .75f;
        return x;
    }

    public static float getY(int row, int col, float tileHeight) {
        return tileHeight * (row + .5f * (col&1));
    }


    public static Color getColorFromPosition(int row, int col) {
        return new Color(
                (col * 5f) / 255f,
                (row * 5f) / 255f,
                0f, 1f);
    }

    public static Tile parsePickColorForTileInWorld(Color pickColor, World world) {
        int col = (int) (pickColor.r * (255f / 5f));
        int row = (int) (pickColor.g * (255f / 5f));
        return world.getTile(row, col);
    }
}
