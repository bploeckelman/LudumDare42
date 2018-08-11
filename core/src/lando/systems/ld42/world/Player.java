package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import lando.systems.ld42.utils.TileUtils;

public class Player extends GameObject {
    public TextureRegion tex;
    public float timer = 0f;

    //public Animation<TextureRegion> animation;
    public boolean dead;

    public Player(int row, int col) {
        dead = false;
        this.row = row;
        this.col = col;
        float tileOffset = 0f;

        position.x = TileUtils.getX(col, tileWidth);
        position.y = TileUtils.getY(row, col, tileHeight);
    }

    // Renders and Updates ---------------------------------------------------------------------------------------------

    @Override
    public void update(float dt) {
        super.update(dt);
        timer += dt;
//        tex = animation.getKeyFrame(timer);
    }

    public void render(SpriteBatch batch, float x, float y) {

    }

    // -----------------------------------------------------------------------------------------------------------------

    public GridPoint2 getLocation() {
        return new GridPoint2(col, row);
    }


    public void kill(){
        // TODO something fancy here?
        dead = true;
    }
}
