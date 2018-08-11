package lando.systems.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;

import java.awt.*;

public class HUDIndicator {
    public int count = 0;
    public TextureRegion texture;
    public Vector2 pos;

    public HUDIndicator(TextureRegion texture, Vector2 pos) {
        this.texture = texture;
        this.pos = pos;
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, pos.x, pos.y);
        Assets.drawString(batch, "x" + count, pos.x + 70, pos.y + texture.getRegionHeight() / 2 + LudumDare42.game.assets.font.getCapHeight() / 2, Color.BLUE, 0.4f, LudumDare42.game.assets.font);
    }
}
