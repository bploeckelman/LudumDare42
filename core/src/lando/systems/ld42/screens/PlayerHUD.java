package lando.systems.ld42.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.ui.HUDIndicator;

import java.util.HashMap;

public class PlayerHUD {
    public GameScreen screen;
    public OrthographicCamera hudCamera;

    public Array<HUDIndicator> indicators;

    public PlayerHUD(GameScreen screen) {
        this.screen = screen;
        hudCamera = screen.hudCamera;
        indicators = new Array<HUDIndicator>();

        // Add indicators for # of enemy tiles, player tiles, unclaimed tiles
        indicators.add(new HUDIndicator(LudumDare42.game.assets.blueCastle, new Vector2(250, hudCamera.viewportHeight - LudumDare42.game.assets.blueCastle.getRegionHeight() - 10)));
        indicators.add(new HUDIndicator(LudumDare42.game.assets.blueCastle, new Vector2(150, hudCamera.viewportHeight - LudumDare42.game.assets.blueCastle.getRegionHeight() - 10)));
        indicators.add(new HUDIndicator(LudumDare42.game.assets.blueCastle, new Vector2(50, hudCamera.viewportHeight - LudumDare42.game.assets.blueCastle.getRegionHeight() - 10)));
    }

    public void render(SpriteBatch batch) {
        for (HUDIndicator indicator : indicators) {
            indicator.render(batch);
        }
    }
}
