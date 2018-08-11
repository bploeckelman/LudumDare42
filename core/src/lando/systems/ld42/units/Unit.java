package lando.systems.ld42.units;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.Assets;

public class Unit {

    private static float scale = 2f;

    public Vector2 pos;
    public Vector2 size;
    public Color color;
    public float animTime;
    public TextureRegion keyframe;
    public Animation<TextureRegion> animation;

    public Unit(Assets assets) {
        this.pos = new Vector2();
        this.size = new Vector2();
        this.color = new Color(1f, 1f, 1f, 1f);
        this.animTime = 0f;
        this.animation = assets.unitAnimation;
        this.keyframe = animation.getKeyFrame(animTime);
        this.size.set(keyframe.getRegionWidth() * scale, keyframe.getRegionHeight() * scale);
    }

    public void update(float dt) {
        animTime += dt;
        keyframe = animation.getKeyFrame(animTime);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(keyframe, pos.x, pos.y, size.x, size.y);
        batch.setColor(1f, 1f, 1f, 1f);
    }

}
