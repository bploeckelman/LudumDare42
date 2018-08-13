package lando.systems.ld42.units;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.ColorAccessor;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Tile;

public abstract class Unit {

    protected static float scale = 4f;
    public static float moveDuration = .5f;

    public Vector2 pos;
    public Vector2 size;
    public MutableFloat rotation;
    public Color color;
    public Color shadowColor;
    public Tile tile;
    public Team.Type team;
    public float animTime;
    public TextureRegion keyframe;
    public TextureRegion dropShadow;
    public Animation<TextureRegion> animation;
    public int attackPower;
    public int defensePower;
    public int actionPoint;
    public int actionAvailable;
    public boolean dead;
    public Interpolation bounceInterpoation;

    public Unit(Animation<TextureRegion> animation) {
        bounceInterpoation = Interpolation.bounceOut;
        this.tile = null;
        this.pos = new Vector2();
        this.size = new Vector2();
        this.rotation = new MutableFloat(0f);
        this.color = new Color(1f, 1f, 1f, 1f);
        this.shadowColor = new Color(0f, 0f, 0f, 0.75f);
        this.animTime = 0f;
        this.animation = animation;
        this.keyframe = animation.getKeyFrame(animTime);
        this.dropShadow = LudumDare42.game.assets.whiteCircle; // brian frowny faces at self
        this.size.set(keyframe.getRegionWidth() * scale, keyframe.getRegionHeight() * scale);
        this.actionAvailable = 0;
        this.team = null;
        this.attackPower = 0;
        this.defensePower = 0;
        this.dead = false;
    }

    public void update(float dt) {
        animTime += dt;
        keyframe = animation.getKeyFrame(animTime);
    }

    public void moveTo(final Tile tile) {
        if (tile == null) return;

        actionAvailable--;
        if (this.tile != null) {
            this.tile.occupant = null;
        }
        float tx = TileUtils.getX(tile.col, Tile.tileWidth) + Tile.tileWidth / 2f - size.x / 2f;
        float ty = TileUtils.getY(tile.row, tile.col, Tile.tileHeight)+ Tile.tileHeight - size.y;
        Tween.to(pos, Vector2Accessor.XY, moveDuration)
             .target(tx, ty)
             .setCallback(new TweenCallback() {
                 @Override
                 public void onEvent(int i, BaseTween<?> baseTween) {
                     Unit.this.tile = tile;
                     Unit.this.tile.occupant = Unit.this;
                     if (Unit.this.team != null) {
                         takeOverTile(Unit.this.tile, Unit.this.team);
                     }
                 }
             })
             .start(LudumDare42.game.tween);
    }


    private float jumpDuration = 1f;
    private float jumpDelay = 3f;
    public void render(SpriteBatch batch) {
//        batch.setColor(shadowColor);
//        batch.draw(dropShadow, pos.x, pos.y, size.x, size.y);
        float yOffset = 0;
        if (actionAvailable > 0){
            yOffset = animTime % jumpDelay;
            yOffset = Math.max(0, yOffset - (jumpDelay-jumpDuration)) / jumpDuration;
            if (yOffset > 0) {
                yOffset = 1f - bounceInterpoation.apply(yOffset);
                yOffset *= 30;
            }
        }
        batch.setColor(color);
        batch.draw(keyframe, pos.x, pos.y + yOffset, size.x / 2f, size.y / 2f, size.x, size.y, 1f, 1f, rotation.floatValue());

        batch.setColor(1f, 1f, 1f, 1f);
        if (actionAvailable > 0) {
            batch.draw(LudumDare42.game.assets.bootTexture, pos.x, pos.y + size.y - 20 + yOffset, 20, 20);
        }
    }

    public void takeOverTile(Tile tile, Team.Type team) {
        tile.owner = team;
    }

    public void tileGotSquanched() {
        // TODO: kick off a particle effect
        float peak = pos.y + 160f;
        float trough = pos.y - 50f;
        Timeline.createSequence()
                .push(Tween.to(pos, Vector2Accessor.Y, 1.0f).target(peak).ease(Back.IN))
                .pushPause(0.25f)
                .push(
                        Timeline.createParallel()
                                .push(Tween.to(pos, Vector2Accessor.Y, 0.75f).target(trough))
                                .push(Tween.to(color, ColorAccessor.A, 0.75f).target(0f))
                                .push(Tween.to(shadowColor, ColorAccessor.A, 0.75f).target(0f))
                                .push(Tween.to(rotation, -1, 0.75f).target(-360f * 4f).ease(Cubic.IN))
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        dead = true;
                    }
                })
                .start(LudumDare42.game.tween);
    }

}
