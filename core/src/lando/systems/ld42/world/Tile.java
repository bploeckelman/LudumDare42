package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.utils.TileUtils;

public class Tile {

    public static final float scale = 2f;
    public static float tileWidth = 64 * scale;
    public static float tileHeight = 32 * scale;

    public Color pickColor;
    public int col;
    public int row;
    public World world;
    public Vector2 position;
    public TextureRegion texture;

    public Tile (int col, int row){
        this.world = World.THE_WORLD;
        this.col = col;
        this.row = row;

        this.position = new Vector2(TileUtils.getX(col, tileWidth), TileUtils.getY(row, col, tileHeight));
        pickColor = TileUtils.getColorFromPosition(row, col);
        texture = LudumDare42.game.assets.blueCastle;
    }


    public void render(SpriteBatch batch){
        batch.draw(texture, position.x, position.y, tileWidth, tileHeight);
    }


}
