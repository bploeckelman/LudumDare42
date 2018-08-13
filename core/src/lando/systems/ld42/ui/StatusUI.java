package lando.systems.ld42.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.screens.GameScreen;

public class StatusUI extends UserInterface {

    private GameScreen gameScreen;
    private Assets assets;
    private GlyphLayout layout;

    private float width;
    private float height;

    // Fuck Scene2D on this, we'll do it live
    private Rectangle bounds;
    private Rectangle boundsPlayerUnits;
    private Rectangle boundsEnemyTerritory;
    private Rectangle boundsPlayerTerritory;

    private String territoryLabelPlayer;
    private String territoryLabelEnemy;

    public StatusUI(Assets assets) {
        this.assets = assets;
        this.layout = assets.layout;
        this.bounds = new Rectangle();
        this.boundsPlayerUnits = new Rectangle();
        this.boundsEnemyTerritory = new Rectangle();
        this.boundsPlayerTerritory = new Rectangle();
    }

    @Override
    public void update(float dt) {
        String playerTerritoryCount = Integer.toString(gameScreen.playerTeam.getTileTotalCount(), 10);
        String enemyTerritoryCount = Integer.toString(gameScreen.playerTeam.getTileTotalCount(), 10);
        territoryLabelPlayer = "Player Territory: " + playerTerritoryCount;
        territoryLabelEnemy = "Enemy Territory: " + enemyTerritoryCount;
    }

    public void render(SpriteBatch batch) {
        // Draw backgrounds
        batch.setColor(1f, 1f, 1f, 0.8f);
        batch.draw(assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Config.player_color);
        batch.draw(assets.whitePixel, boundsPlayerTerritory.x, boundsPlayerTerritory.y, boundsPlayerTerritory.width, boundsPlayerTerritory.height);
        batch.setColor(1f, 1f, 1f, 0.8f);
        batch.draw(assets.whitePixel, boundsPlayerUnits.x, boundsPlayerUnits.y, boundsPlayerUnits.width, boundsPlayerUnits.height);
        batch.setColor(Config.enemy_color);
        batch.draw(assets.whitePixel, boundsEnemyTerritory.x, boundsEnemyTerritory.y, boundsEnemyTerritory.width, boundsEnemyTerritory.height);

        // Draw texts
        layout.setText(Assets.font, territoryLabelPlayer);
        Assets.drawString(batch, territoryLabelPlayer,
                          boundsPlayerTerritory.x + boundsPlayerTerritory.width / 2f - layout.width / 2f,
                          boundsPlayerTerritory.y + boundsPlayerTerritory.height / 2f, // - layout.height / 2f,
                          Color.WHITE, 0.3f, Assets.font);

        layout.setText(Assets.font, territoryLabelEnemy);
        Assets.drawString(batch, territoryLabelEnemy,
                          boundsEnemyTerritory.x + boundsEnemyTerritory.width / 2f - layout.width / 2f,
                          boundsEnemyTerritory.y + boundsEnemyTerritory.height / 2f, // - layout.height / 2f,
                          Color.WHITE, 0.3f, Assets.font);

        // TODO: draw strings and things...

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void rebuild(GameScreen gameScreen, Camera camera) {
        this.gameScreen = gameScreen;
        this.width = camera.viewportWidth;
        this.height = 40f;

        float lowerLeftY = camera.viewportHeight - height;
        float territorySegmentWidth = width / 4f;

        bounds.set(0f, lowerLeftY, width, height);
        boundsPlayerTerritory.set(0f, lowerLeftY, territorySegmentWidth, height);
        boundsPlayerUnits.set(boundsPlayerTerritory.x + boundsPlayerTerritory.width, lowerLeftY, width - 2f * territorySegmentWidth, height);
        boundsEnemyTerritory.set(boundsPlayerUnits.x + boundsPlayerUnits.width, lowerLeftY, territorySegmentWidth, height);


    }

}
