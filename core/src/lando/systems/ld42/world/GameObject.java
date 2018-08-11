package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.utils.TileUtils;

public abstract class GameObject {
    public World world;

    public static final float scale = 2f;
    public static float tileWidth = 26 * scale;
    public static float tileHeight = 30 * scale;


    public int row;
    public int col;
    public Vector2 position;

    public GameObject() {
        this(0, 0);
    }

    public GameObject(int col, int row){
        this.world = World.THE_WORLD;
        this.col = col;
        this.row = row;

        this.position = new Vector2(TileUtils.getX(col, tileWidth), TileUtils.getY(row, col, tileHeight));
    }

    protected Assets assets() {
        return LudumDare42.game.assets;
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch) {
        render(batch, position.x, position.y);
    }

    protected abstract void render(SpriteBatch batch, float x, float y);

}
