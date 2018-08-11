package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

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
        Tile tile = getTile(row, col);

        if (tile != null) {
            tileOffset += tile.height;// * Tile.heightScale;
        }
        position.x = getX(row, col);
        position.y = getY(row);
        position.z = tileOffset + (tileHeight * .25f);
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

    public void moveTo(final int row, final int col) {
//        moving = true;
//        float newX = getX(row, col);
//        float newY = getY(row);
//        float tileOffset = (tileHeight * .25f);
//        Tile tile = getTile(row, col);
//
//        if (tile != null) {
//            tileOffset += tile.height * Tile.heightScale;
//        }
//
//        Vector2 from = new Vector2(position.x, position.y + position.z);
//        Vector2 to = new Vector2(newX, newY + tileOffset);
//        int xDir = Float.valueOf(from.x).compareTo(to.x) * -1;
//        float xDiff = from.x > to.x ? from.x - to.x : to.x - from.x;
//        int yDir = Float.valueOf(from.y).compareTo(to.y) * -1;
//        float yDiff = from.y > to.y ? from.y - to.y : to.y - from.y;
//
//        if (yDiff > xDiff && yDir == -1) {
//            animation = type.down;
//        } else if (yDiff > xDiff && yDir == 1) {
//            animation = type.up;
//        } else if (yDiff < xDiff) {
//            animation = type.side;
//            walkRight = xDir == 1;
//        }
//
//        this.row = row;
//        this.col = col;
//        Tween.to(position, Vector3Accessor.XYZ, 1f)
//                .target(newX, newY, tileOffset)
//                .setCallback(new TweenCallback() {
//                    @Override
//                    public void onEvent(int eventType, BaseTween<?> source) {
//                        walkRight = false;
//                        animation = type.down;
//                        moving = false;
//                    }
//                })
//                .start(Assets.tween);

    }

    public void kill(){
        // TODO something fancy here?
        dead = true;
    }
}
