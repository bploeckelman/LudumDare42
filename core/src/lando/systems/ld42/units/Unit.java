package lando.systems.ld42.units;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Tile;

public abstract class Unit {

    protected static float scale = 4f;

    public Vector2 pos;
    public Vector2 size;
    public Color color;
    public Color shadowColor;
    public Tile tile;
    public Team team;
    public float moveDuration;
    public float animTime;
    public TextureRegion keyframe;
    public TextureRegion dropShadow;
    public Animation<TextureRegion> animation;
    public int attackPower;
    public int defensePower;

    public Unit(Animation<TextureRegion> animation) {
        this.tile = null;
        this.pos = new Vector2();
        this.size = new Vector2();
        this.color = new Color(1f, 1f, 1f, 1f);
        this.shadowColor = new Color(0f, 0f, 0f, 0.75f);
        this.moveDuration = 0.5f;
        this.animTime = 0f;
        this.animation = animation;
        this.keyframe = animation.getKeyFrame(animTime);
        this.dropShadow = LudumDare42.game.assets.whiteCircle; // brian frowny faces at self
        this.size.set(keyframe.getRegionWidth() * scale, keyframe.getRegionHeight() * scale);
        attackPower = 0;
        defensePower = 0;
    }

    public void update(float dt) {
        animTime += dt;
        keyframe = animation.getKeyFrame(animTime);
    }

    public void moveTo(final Tile tile) {
        if (tile == null) return;
        float tx = TileUtils.getX(tile.col, Tile.tileWidth) + Tile.tileWidth / 2f - size.x / 2f;
        float ty = TileUtils.getY(tile.row, tile.col, Tile.tileHeight)+ Tile.tileHeight - size.y;
        Tween.to(pos, Vector2Accessor.XY, moveDuration)
             .target(tx, ty)
             .setCallback(new TweenCallback() {
                 @Override
                 public void onEvent(int i, BaseTween<?> baseTween) {
                     if (Unit.this.tile != null) {
                         Unit.this.tile.occupant = null;
                     }
                     Unit.this.tile = tile;
                     Unit.this.tile.occupant = Unit.this;
                 }
             })
             .start(LudumDare42.game.tween);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(shadowColor);
        batch.draw(dropShadow, pos.x, pos.y, size.x, size.y);

        float offset = 1.5f * scale;
        batch.setColor(color);
        batch.draw(keyframe, pos.x, pos.y + offset, size.x, size.y);

        batch.setColor(1f, 1f, 1f, 1f);
    }

}
