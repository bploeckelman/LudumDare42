package lando.systems.ld42.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.ui.TileHUDIndicator;

public class PlayerHUD {
    public GameScreen screen;
    public OrthographicCamera hudCamera;

    public Array<TileHUDIndicator> indicators;

    public PlayerHUD(GameScreen screen) {
        this.screen = screen;
        hudCamera = screen.hudCamera;
        indicators = new Array<TileHUDIndicator>();

        // Add indicators for # of enemy tiles, player tiles, unclaimed tiles
        indicators.add(new TileHUDIndicator(LudumDare42.game.assets.blankTile, new Vector2(300, hudCamera.viewportHeight - LudumDare42.game.assets.mountain.getRegionHeight() - 10), 0));
        indicators.add(new TileHUDIndicator(LudumDare42.game.assets.blankTile, new Vector2(175, hudCamera.viewportHeight - LudumDare42.game.assets.mountain.getRegionHeight() - 10), 1));
        indicators.add(new TileHUDIndicator(LudumDare42.game.assets.blankTile, new Vector2(50, hudCamera.viewportHeight - LudumDare42.game.assets.mountain.getRegionHeight() - 10), 2));
    }

    public void update(float dt) {
        for (TileHUDIndicator indicator : indicators) {
            int tileCount = 0;
            switch (indicator.owner) {
                case 0: tileCount = screen.world.unclaimedTileCount; break;
                case 1: tileCount = screen.world.playerTileCount; break;
                case 2: tileCount = screen.world.enemyTileCount; break;
                default: break;
            }
            indicator.count = tileCount;
        }
    }

    public void render(SpriteBatch batch) {
        for (TileHUDIndicator indicator : indicators) {
            indicator.render(batch);
        }
    }
}
