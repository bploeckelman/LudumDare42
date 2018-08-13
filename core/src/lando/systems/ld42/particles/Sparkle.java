package lando.systems.ld42.particles;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.ColorAccessor;
import lando.systems.ld42.accessors.Vector2Accessor;

public class Sparkle implements Pool.Poolable {

    public Vector2 pos;
    public Vector2 target;
    public Color color;
    public MutableFloat rotation;
    public int revolutions;
    public boolean dead;

    public Sparkle() {
        this.pos = new Vector2();
        this.target = new Vector2();
        this.color = new Color(1f, 1f, 1f, 1f);
        this.rotation = new MutableFloat(0f);
        this.revolutions = 1;
        this.dead = true;
    }

    @Override
    public void reset() {
        dead = true;
    }

    public void init(float px, float py, float tx, float ty, Color color) {
        this.pos.set(px, py);
        this.target.set(tx, ty);
        this.color.set(color.r, color.g, color.b, 1f);
        this.rotation.setValue(0f);
        this.revolutions = MathUtils.random(2, 8);
        this.dead = false;

        float min_transit_duration = 1f;
        float max_transit_duration = 4f;
        float duration = MathUtils.random(min_transit_duration, max_transit_duration);

        int sign = MathUtils.randomSign();
        float offset = MathUtils.random(25f, 75f);
        float wx = sign * offset + pos.x + (target.x - pos.x) / 2f;
        float wy = sign * offset + pos.y + (target.y - pos.y) / 2f;

        this.color.a = 0f;
        Timeline.createSequence()
                .push(Tween.set(this.color, ColorAccessor.A).target(0f))
                .push(Tween.to(this.color, ColorAccessor.A, 0.2f).target(1f))
                .push(
                        Timeline.createParallel()
                                .push(
                                        Tween.to(this.pos, Vector2Accessor.XY, duration)
                                             .waypoint(wx, wy)
                                             .target(target.x, target.y)
                                             .path(TweenPaths.catmullRom)
                                             .ease(Sine.INOUT)
                                )
                                .push(
                                        Tween.to(this.color, ColorAccessor.A, duration)
                                             .target(0f).ease(Expo.IN)
                                )
                                .push(
                                        Tween.to(this.rotation, -1, duration)
                                             .target(revolutions * 360f)
                                )
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        Sparkle.this.dead = true;
                    }
                })
                .start(LudumDare42.game.tween);
    }

    public void update(float dt) {
        // ...
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(LudumDare42.game.assets.sparkle,
                   pos.x, pos.y, 16f, 16f,
                   32f, 32f, 1f, 1f,
                   rotation.floatValue());
        batch.setColor(Color.WHITE);
    }

}
