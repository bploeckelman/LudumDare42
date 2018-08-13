package lando.systems.ld42.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.ColorAccessor;
import lando.systems.ld42.accessors.RectangleAccessor;
import lando.systems.ld42.particles.Sparkle;
import lando.systems.ld42.screens.GameScreen;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class StatusUI extends UserInterface {

    private GameScreen gameScreen;
    private Assets assets;
    private GlyphLayout layout;
    private Color color;
    private Camera camera;
    private Vector3 proj;

    private final Array<Sparkle> activeSparkles = new Array<Sparkle>(false, 128);
    private final Pool<Sparkle> sparklePool = Pools.get(Sparkle.class, 256);

    private float margin = 10f;
    private float pad = 5f;
    private float width;
    private float height;

    // Fuck Scene2D on this, we'll do it live
    private Rectangle bounds;
    public Rectangle boundsPlayerUnits;
    private Rectangle boundsEnemyTerritory;
    private Rectangle boundsPlayerTerritory;
    private TextureRegion peasant;
    private TextureRegion soldier;
    private TextureRegion archer;
    private TextureRegion wizard;
    private int claimedCountPlayer;
    private int claimedCountEnemy;

    public Vector2 territoryPlayerTarget;
    public Vector2 territoryEnemyTarget;

    public StatusUI(Assets assets) {
        this.assets = assets;
        this.layout = assets.layout;
        this.color = new Color(1f, 1f, 1f, 0f);
        this.proj = new Vector3();
        this.bounds = new Rectangle();
        this.boundsPlayerUnits = new Rectangle();
        this.boundsEnemyTerritory = new Rectangle();
        this.boundsPlayerTerritory = new Rectangle();
        this.territoryPlayerTarget = new Vector2();
        this.territoryEnemyTarget = new Vector2();
        this.peasant = assets.unitAnimationPeasant.getKeyFrame(0);
        this.soldier = assets.unitAnimationSoldier.getKeyFrame(0);
        this.archer  = assets.unitAnimationArcher.getKeyFrame(0);
        this.wizard  = assets.unitAnimationWizard.getKeyFrame(0);
        this.claimedCountPlayer = 0;
        this.claimedCountEnemy = 0;
        for (int i = 0; i < 256; ++i) {
            sparklePool.free(new Sparkle());
        }
    }

    @Override
    public void update(float dt) {
        for (int i = activeSparkles.size - 1; i >= 0; --i) {
            Sparkle sparkle = activeSparkles.get(i);
            sparkle.update(dt);
            if (sparkle.dead) {
                // Increase claimed count
                if (sparkle.teamType == Team.Type.player) {
                    claimedCountPlayer++;
                } else if (sparkle.teamType == Team.Type.enemy) {
                    claimedCountEnemy++;
                }

                activeSparkles.removeIndex(i);
                sparklePool.free(sparkle);
            }
        }
    }

    public void addClaimedTerritorySparkle(Tile tile, Team.Type teamType) {
        float sparkleWidth = LudumDare42.game.assets.sparkle.getRegionWidth();
        float sparkleHeight = LudumDare42.game.assets.sparkle.getRegionHeight();

        // NOTE: this converts world -> hud coords properly

        // Calculate the middle of the tile
        proj.set(tile.position.x + Tile.tileWidth / 2f,
                 tile.position.y + Tile.tileHeight / 2f, 0f);
        // Convert world -> screen coords
        World.THE_WORLD.screen.worldCamera.project(proj);
        // Screen coords has an inverted Y axis, so re-invert to get Y-up
        proj.y = Config.window_height - proj.y;
        // Convert screen -> hud coords
        World.THE_WORLD.screen.hudCamera.unproject(proj);

        Sparkle sparkle = sparklePool.obtain();
        sparkle.init(
                proj.x - sparkleWidth / 2f,
                proj.y - sparkleHeight / 2f,
                (teamType == Team.Type.player) ? territoryPlayerTarget.x : territoryEnemyTarget.x,
                (teamType == Team.Type.player) ? territoryPlayerTarget.y : territoryEnemyTarget.y,
                (teamType == Team.Type.player) ? Config.player_color   : Config.enemy_color,
                teamType);
        activeSparkles.add(sparkle);
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
        String playerTerritoryCount = Integer.toString(claimedCountPlayer, 10);
        String enemyTerritoryCount = Integer.toString(claimedCountEnemy, 10);
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
        // TODO: maybe grey if full (num == denom), yellow if not

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

        for (Sparkle sparkle : activeSparkles) {
            sparkle.render(batch);
        }
    }

    public void rebuild(final GameScreen gameScreen, final Camera camera) {
        this.gameScreen = gameScreen;
        this.camera = camera;
        this.width = camera.viewportWidth;
        this.height = 40f;

        float lowerLeftY = camera.viewportHeight - height;
        float unitsOffsetY = 10f;
        float segmentTerritoryWidth = (1 / 6f) * width;
        float segmentUnitsWidth = (4 / 6f) * width;

        float territoryPlayerInitialX = -segmentTerritoryWidth;
        float territoryPlayerEndingX = 0f;

        float unitsPlayerInitialY = camera.viewportHeight;
        float unitsPlayerEndingY = lowerLeftY - unitsOffsetY;

        float territoryEnemyInitialX = camera.viewportWidth;
        float territoryEnemyEndingX = camera.viewportWidth - segmentTerritoryWidth;

        bounds.set(0f, lowerLeftY, width, height);
        boundsPlayerTerritory.set(territoryPlayerInitialX, lowerLeftY, segmentTerritoryWidth, height);
        boundsPlayerUnits.set(territoryPlayerEndingX + boundsPlayerTerritory.width, unitsPlayerInitialY, segmentUnitsWidth, height + unitsOffsetY);
        boundsEnemyTerritory.set(territoryEnemyInitialX, lowerLeftY, segmentTerritoryWidth, height);

        float textOffsetX = (1 / 4f) * segmentTerritoryWidth;
        territoryPlayerTarget.set(territoryPlayerEndingX + boundsPlayerTerritory.width / 2f + textOffsetX,
                                  boundsPlayerTerritory.y + boundsPlayerTerritory.height / 2f - assets.sparkle.getRegionHeight() / 2f);
        territoryEnemyTarget.set(territoryEnemyEndingX + boundsEnemyTerritory.width / 2f + textOffsetX,
                                 boundsEnemyTerritory.y + boundsEnemyTerritory.height / 2f - assets.sparkle.getRegionHeight() / 2f);

        float duration = 1.25f;
        float initialDelay = 3.5f; // don't know why pushPause() isn't working
        Timeline.createParallel()
                .push(
                        Tween.to(color, ColorAccessor.A, duration).delay(initialDelay)
                             .target(0.9f).ease(Quint.OUT)
                )
                .push(
                        // units panel drops, then territories panels come in from sides
                        Timeline.createSequence()
                                .push(
                                        Tween.to(boundsPlayerUnits, RectangleAccessor.Y, duration).delay(initialDelay)
                                             .target(unitsPlayerEndingY).ease(Bounce.OUT)
                                )
                                .push(
                                        Timeline.createParallel()
                                        .push(
                                                Tween.to(boundsPlayerTerritory, RectangleAccessor.X, duration / 2f)
                                                     .target(territoryPlayerEndingX).ease(Bounce.OUT)
                                        )
                                        .push(
                                                Tween.to(boundsEnemyTerritory, RectangleAccessor.X, duration / 2f)
                                                     .target(territoryEnemyEndingX).ease(Bounce.OUT)
                                        )
                                )
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        // Do some sparkles
                        for (Tile tile : gameScreen.world.tiles) {
                            if (tile == null) continue;
                            if (tile.owner == Team.Type.none) continue;
                            addClaimedTerritorySparkle(tile, tile.owner);
                        }
                    }
                })
                .start(LudumDare42.game.tween);
    }

}
