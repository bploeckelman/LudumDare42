package lando.systems.ld42.ui;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.ColorAccessor;
import lando.systems.ld42.screens.GameScreen;
import lando.systems.ld42.world.Tile;

public class StatusUI extends UserInterface {

    private GameScreen gameScreen;
    private Assets assets;
    private GlyphLayout layout;
    private Color color;

    private float margin = 10f;
    private float pad = 5f;
    private float width;
    private float height;

    // Fuck Scene2D on this, we'll do it live
    private Rectangle bounds;
    private Rectangle boundsPlayerUnits;
    private Rectangle boundsEnemyTerritory;
    private Rectangle boundsPlayerTerritory;
    private TextureRegion peasant;
    private TextureRegion soldier;
    private TextureRegion archer;
    private TextureRegion wizard;

    public StatusUI(Assets assets) {
        this.assets = assets;
        this.layout = assets.layout;
        this.color = new Color(1f, 1f, 1f, 0f);
        this.bounds = new Rectangle();
        this.boundsPlayerUnits = new Rectangle();
        this.boundsEnemyTerritory = new Rectangle();
        this.boundsPlayerTerritory = new Rectangle();
        this.peasant = assets.unitAnimationPeasant.getKeyFrame(0);
        this.soldier = assets.unitAnimationSoldier.getKeyFrame(0);
        this.archer  = assets.unitAnimationArcher.getKeyFrame(0);
        this.wizard  = assets.unitAnimationWizard.getKeyFrame(0);
    }

    @Override
    public void update(float dt) {
        // ...
    }

    public void render(SpriteBatch batch) {
        // Draw backgrounds
        batch.setColor(Config.player_color.r, Config.player_color.g, Config.player_color.b, color.a);
        batch.draw(assets.whitePixel, boundsPlayerTerritory.x, boundsPlayerTerritory.y, boundsPlayerTerritory.width, boundsPlayerTerritory.height);
        batch.setColor(1, 1f, 1f, color.a);
        assets.ninePatchScrews.draw(batch, boundsPlayerTerritory.x, boundsPlayerTerritory.y, boundsPlayerTerritory.width, boundsPlayerTerritory.height);

        batch.setColor(0f, 0f, 0f, color.a);
        batch.draw(assets.whitePixel, boundsPlayerUnits.x, boundsPlayerUnits.y, boundsPlayerUnits.width, boundsPlayerUnits.height);
        batch.setColor(1, 1f, 1f, color.a);
        assets.ninePatchScrews.draw(batch, boundsPlayerUnits.x, boundsPlayerUnits.y, boundsPlayerUnits.width, boundsPlayerUnits.height);

        batch.setColor(Config.enemy_color.r, Config.enemy_color.g, Config.enemy_color.b, color.a);
        batch.draw(assets.whitePixel, boundsEnemyTerritory.x, boundsEnemyTerritory.y, boundsEnemyTerritory.width, boundsEnemyTerritory.height);
        batch.setColor(1, 1f, 1f, color.a);
        assets.ninePatchScrews.draw(batch, boundsEnemyTerritory.x, boundsEnemyTerritory.y, boundsEnemyTerritory.width, boundsEnemyTerritory.height);

        // Draw claimed territory
        String playerTerritoryCount = Integer.toString(gameScreen.playerTeam.getTileTotalCount(), 10);
        String enemyTerritoryCount = Integer.toString(gameScreen.playerTeam.getTileTotalCount(), 10);
        String territoryLabelPlayer = "Claimed: " + playerTerritoryCount;
        String territoryLabelEnemy = "Claimed: " + enemyTerritoryCount;

        float originalScaleX = Assets.font.getData().scaleX;
        float originalScaleY = Assets.font.getData().scaleY;
        float scale = 0.35f;
        Assets.font.getData().setScale(scale);
        {
            layout.setText(Assets.font, territoryLabelPlayer);
            Assets.drawString(batch, territoryLabelPlayer,
                              boundsPlayerTerritory.x + boundsPlayerTerritory.width / 2f - layout.width / 2f,
                              boundsPlayerTerritory.y + boundsPlayerTerritory.height / 2f + layout.height / 2f,
                              color, scale, Assets.font);

            layout.setText(Assets.font, territoryLabelEnemy);
            Assets.drawString(batch, territoryLabelEnemy,
                              boundsEnemyTerritory.x + boundsEnemyTerritory.width / 2f - layout.width / 2f,
                              boundsEnemyTerritory.y + boundsEnemyTerritory.height / 2f + layout.height / 2f,
                              color, scale, Assets.font);
        }
        Assets.font.getData().setScale(originalScaleX, originalScaleY);
        batch.setColor(1f, 1f, 1f, 1f);

        // Draw player unit counts
        float boxWidth = ((boundsPlayerUnits.width - (2f * margin)) - (3f * pad)) * (1 / 4f);
        float boxHeight = height - 2f * pad;
        float bx1 = boundsPlayerUnits.x + margin;
        float bx2 = bx1 + boxWidth + pad;
        float bx3 = bx2 + boxWidth + pad;
        float bx4 = bx3 + boxWidth + pad;
        float by = boundsPlayerUnits.y + boundsPlayerUnits.height / 2f - boxHeight / 2f;

        batch.setColor(color);
        batch.draw(peasant, bx1, by, boxWidth / 2f, boxHeight);
        batch.draw(soldier, bx2, by, boxWidth / 2f, boxHeight);
        batch.draw(archer,  bx3, by, boxWidth / 2f, boxHeight);
        batch.draw(wizard,  bx4, by, boxWidth / 2f, boxHeight);

        // TODO: draw numerator and denom with diff colors?

        String peasantCount = gameScreen.playerTeam.getUnitCountPeasant() + "/" + gameScreen.playerTeam.getMaxPeasant();
        String soldierCount = gameScreen.playerTeam.getUnitCountSoldier() + "/" + gameScreen.playerTeam.getTileTypeCount(Tile.Type.mountain);
        String archerCount  = gameScreen.playerTeam.getUnitCountArcher()  + "/" + gameScreen.playerTeam.getTileTypeCount(Tile.Type.forest);
        String wizardCount  = gameScreen.playerTeam.getUnitCountWizard()  + "/" + gameScreen.playerTeam.getTileTypeCount(Tile.Type.crystal);

        float countScale = 0.3f;
        Assets.font.getData().setScale(scale);
        {
            layout.setText(Assets.font, peasantCount);
            Assets.drawString(batch, peasantCount,
                              bx1 + boxWidth / 2f + boxWidth / 4f - layout.width / 2f,
                              by + boxHeight / 2f + layout.height / 2f,
                              color, countScale, Assets.font);

            layout.setText(Assets.font, soldierCount);
            Assets.drawString(batch, soldierCount,
                              bx2 + boxWidth / 2f + boxWidth / 4f - layout.width / 2f,
                              by + boxHeight / 2f + layout.height / 2f,
                              color, countScale, Assets.font);

            layout.setText(Assets.font, archerCount);
            Assets.drawString(batch, archerCount,
                              bx3 + boxWidth / 2f + boxWidth / 4f - layout.width / 2f,
                              by + boxHeight / 2f + layout.height / 2f,
                              color, countScale, Assets.font);

            layout.setText(Assets.font, wizardCount);
            Assets.drawString(batch, wizardCount,
                              bx4 + boxWidth / 2f + boxWidth / 4f - layout.width / 2f,
                              by + boxHeight / 2f + layout.height / 2f,
                              color, countScale, Assets.font);
        }
        Assets.font.getData().setScale(originalScaleX, originalScaleY);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void rebuild(GameScreen gameScreen, Camera camera) {
        this.gameScreen = gameScreen;
        this.width = camera.viewportWidth;
        this.height = 40f;

        float lowerLeftY = camera.viewportHeight - height;
        float territorySegmentWidth = width / 6f;

        bounds.set(0f, lowerLeftY, width, height);
        boundsPlayerTerritory.set(0f, lowerLeftY, territorySegmentWidth, height);
        boundsPlayerUnits.set(boundsPlayerTerritory.x + boundsPlayerTerritory.width, lowerLeftY, width - 2f * territorySegmentWidth, height);
        boundsEnemyTerritory.set(boundsPlayerUnits.x + boundsPlayerUnits.width, lowerLeftY, territorySegmentWidth, height);

        Tween.to(color, ColorAccessor.A, 2f)
             .delay(2f)
             .target(0.9f)
             .start(LudumDare42.game.tween);
    }

}
