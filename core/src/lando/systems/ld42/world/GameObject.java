package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;

public abstract class GameObject {
    public World world;

    public static float tileWidth = 26;
    public static float tileHeight = 30;

    public static float getX(int row, int col) {
        float x = col * tileWidth;
        if (row % 2 == 0) x += tileWidth / 2f;
        return x;
    }

    public static float getY(int row) {
        return row * tileHeight * .75f;
    }

    public float height;
    public int row;
    public int col;
    public Vector3 position;

    public GameObject() {
        this(0, 0, 0);
    }

    public GameObject(int col, int row, float height){
        this.world = World.THE_WORLD;
        this.col = col;
        this.row = row;

        this.position = new Vector3(getX(row, col), getY(row), height);
    }

    protected Assets assets() {
        return LudumDare42.game.assets;
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch) {
        render(batch, position.x, position.y + position.z);
    }

    protected abstract void render(SpriteBatch batch, float x, float y);

    public Tile getTile() {
        return getTile(row, col);
    }

    public Tile getTile(int row, int col) {
        return world.getTile(row, col);
    }
}
