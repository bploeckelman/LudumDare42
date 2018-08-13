package lando.systems.ld42.particles;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.teams.Team;

public class Sparkle implements Pool.Poolable {

    public Vector2 pos;
    public Vector2 target;
    public MutableFloat alpha;
    public MutableFloat rotation;
    public Team.Type teamType;
    public int revolutions;
    public boolean dead;

    public Sparkle() {
        this.pos = new Vector2();
        this.target = new Vector2();
        this.alpha = new MutableFloat(0f);
        this.rotation = new MutableFloat(0f);
        this.revolutions = 1;
        this.teamType = Team.Type.none;
        this.dead = true;
    }

    @Override
    public void reset() {
        dead = true;
    }

    public void init(float px, float py, float tx, float ty, Team.Type teamType, float max_transit_duration) {
        this.pos.set(px, py);
        this.target.set(tx, ty);
        this.alpha.setValue(0f);
        this.rotation.setValue(0f);
        this.revolutions = MathUtils.random(2, 8);
        this.teamType = teamType;
        this.dead = false;

        float min_transit_duration = 1f;
        float duration = MathUtils.random(min_transit_duration, max_transit_duration);

        int sign = MathUtils.randomSign();
        float offset = MathUtils.random(25f, 75f);
        float wx = sign * offset + pos.x + (target.x - pos.x) / 2f;
        float wy = sign * offset + pos.y + (target.y - pos.y) / 2f;

        Timeline.createSequence()
                .push(Tween.set(alpha, -1).target(0f))
                .push(Tween.to(alpha, -1, 0.2f).target(1f))
                .push(
                        Timeline.createParallel()
                                .push(
                                        Tween.to(pos, Vector2Accessor.XY, duration)
                                             .waypoint(wx, wy)
                                             .target(target.x, target.y)
                                             .path(TweenPaths.catmullRom)
                                             .ease(Sine.INOUT)
                                )
                                .push(
                                        Tween.to(alpha, -1, duration)
                                             .target(0f).ease(Expo.IN)
                                )
                                .push(
                                        Tween.to(rotation, -1, duration)
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
        batch.setColor(1f, 1f, 1f, alpha.floatValue());
        TextureRegion texture = LudumDare42.game.assets.sparkle;
        switch (teamType){

            case none:
                texture = LudumDare42.game.assets.sparkle;
                break;
            case enemy:
                texture = LudumDare42.game.assets.sparkleEnemy;
                break;
            case player:
                texture = LudumDare42.game.assets.sparklePlayer;
                break;
        }

        batch.draw(texture,
                   pos.x, pos.y, 16f, 16f,
                   32f, 32f, 1f, 1f,
                   rotation.floatValue());
        batch.setColor(Color.WHITE);
    }

}
