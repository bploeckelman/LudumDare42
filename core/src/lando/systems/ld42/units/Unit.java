package lando.systems.ld42.units;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.world.Tile;

public class Unit {

    private static float scale = 3f;

    public Vector2 pos;
    public Vector2 size;
    public Color color;
    public Tile tile;
    public float moveDuration;
    public float animTime;
    public TextureRegion keyframe;
    public Animation<TextureRegion> animation;

    public Unit(Assets assets) {
        this.tile = null;
        this.pos = new Vector2();
        this.size = new Vector2();
        this.color = new Color(1f, 1f, 1f, 1f);
        this.moveDuration = 0.5f;
        this.animTime = 0f;
        this.animation = assets.unitAnimation;
        this.keyframe = animation.getKeyFrame(animTime);
        this.size.set(keyframe.getRegionWidth() * scale, keyframe.getRegionHeight() * scale);
    }

    public void update(float dt) {
        animTime += dt;
        keyframe = animation.getKeyFrame(animTime);
    }

    public void moveTo(final Tile tile) {
        float tx = tile.position.x + Tile.tileWidth / 2f - size.x / 2f;
        float ty = tile.position.y + Tile.tileHeight - size.y;
        Tween.to(pos, Vector2Accessor.XY, moveDuration)
             .target(tx, ty)
             .setCallback(new TweenCallback() {
                 @Override
                 public void onEvent(int i, BaseTween<?> baseTween) {
                     Unit.this.tile = tile;
                 }
             })
             .start(LudumDare42.game.tween);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(keyframe, pos.x, pos.y, size.x, size.y);
        batch.setColor(1f, 1f, 1f, 1f);
    }

}
