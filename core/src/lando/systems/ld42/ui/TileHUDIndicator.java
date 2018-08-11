package lando.systems.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;

import java.awt.*;

public class TileHUDIndicator {
    public int count = 0;
    public TextureRegion texture;
    public Vector2 pos;
    public int owner;

    public TileHUDIndicator(TextureRegion texture, Vector2 pos, int owner) {
        this.texture = texture;
        this.pos = pos;
        this.owner = owner;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, pos.x, pos.y);
        if (owner == 1) {
            batch.setColor(new Color(Config.player_color.r, Config.player_color.g, Config.player_color.b, .3f));
            batch.draw(LudumDare42.game.assets.whiteHex, pos.x, pos.y, texture.getRegionWidth(), texture.getRegionHeight());
        }
        if (owner == 2) {
            batch.setColor(new Color(Config.enemy_color.r, Config.enemy_color.g, Config.enemy_color.b, .3f));
            batch.draw(LudumDare42.game.assets.whiteHex, pos.x, pos.y, texture.getRegionWidth(), texture.getRegionHeight());
        }

        batch.setColor(Color.WHITE);
        Assets.drawString(batch, "x" + count, pos.x + 70, pos.y + texture.getRegionHeight() / 2 + LudumDare42.game.assets.font.getCapHeight() / 2, Color.BLUE, 0.4f, LudumDare42.game.assets.font);
    }
}
