package lando.systems.ld42.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.ColorAccessor;
import lando.systems.ld42.accessors.RectangleAccessor;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.particles.Sparkle;
import lando.systems.ld42.screens.GameScreen;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.turns.EnemyAI;
import lando.systems.ld42.turns.Turn;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class StatusUI extends UserInterface {

    private GameScreen gameScreen;
    private Assets assets;
    private GlyphLayout layout;
    private Color color;
    private Color turnTextColor;
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
    private Rectangle boundsRoundCounter;
    private Rectangle boundsTurnText;
    private TextureRegion peasant;
    private TextureRegion soldier;
    private TextureRegion archer;
    private TextureRegion wizard;
    private int claimedCountPlayer;
    private int claimedCountEnemy;
    private int previousRoundNumber;
    private String turnText;
    private Turn previousTurn;
    private EnemyAI.Phase previousEnemyPhase;

    public Vector2 territoryPlayerTarget;
    public Vector2 territoryEnemyTarget;

    private Rectangle boundsTooltip;

    public StatusUI(Assets assets) {
        this.assets = assets;
        this.layout = assets.layout;
        this.color = new Color(1f, 1f, 1f, 0f);
        this.turnTextColor = new Color(255f / 255f, 126f / 255f, 0f / 255f, 0f);
        this.proj = new Vector3();
        this.bounds = new Rectangle();
        this.boundsPlayerUnits = new Rectangle();
        this.boundsEnemyTerritory = new Rectangle();
        this.boundsPlayerTerritory = new Rectangle();
        this.boundsRoundCounter = new Rectangle();
        this.boundsTurnText = new Rectangle();
        this.boundsTooltip = new Rectangle(0, 0, 300, 130);
        this.territoryPlayerTarget = new Vector2();
        this.territoryEnemyTarget = new Vector2();
        this.peasant = assets.unitAnimationPeasant.getKeyFrame(0);
        this.soldier = assets.unitAnimationSoldier.getKeyFrame(0);
        this.archer  = assets.unitAnimationArcher.getKeyFrame(0);
        this.wizard  = assets.unitAnimationWizard.getKeyFrame(0);
        this.claimedCountPlayer = 0;
        this.claimedCountEnemy = 0;
        this.previousRoundNumber = 1;
        this.previousTurn = Turn.PLAYER_ACTION;
        this.previousEnemyPhase = EnemyAI.Phase.Recruit;
        for (int i = 0; i < 256; ++i) {
            sparklePool.free(new Sparkle());
        }
    }

    @Override
    public void update(float dt) {
        // Handle sparkles
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

        // TODO: handle claimed territory loss through either tile removal or opponent capture

        // Bounce round counter if new round
        if (previousRoundNumber != gameScreen.turnNumber) {
            previousRoundNumber = gameScreen.turnNumber;
            Timeline.createSequence()
                    .push(
                            Tween.to(boundsRoundCounter, Vector2Accessor.Y, 0.33f)
                                 .target(boundsRoundCounter.height / 2f)
                    )
                    .push(
                            Tween.to(boundsRoundCounter, Vector2Accessor.Y, 0.8f)
                                 .target(0f).ease(Bounce.OUT)
                    )
                    .start(LudumDare42.game.tween);
        }

        // Handle turn phase transitions
        boolean kickoffPhaseTransition = false;
        Turn currentTurn = gameScreen.turnAction.turn;
        EnemyAI.Phase currentEnemyPhase = gameScreen.enemyAI.phase;
        if ((previousTurn != currentTurn)
         || (currentTurn == Turn.ENEMY && previousEnemyPhase != currentEnemyPhase)) {
            kickoffPhaseTransition = true;

            previousTurn = gameScreen.turnAction.turn;
            previousEnemyPhase = gameScreen.enemyAI.phase;

            if (currentTurn == Turn.PLAYER_RECRUITMENT) turnText = "Player Recruit";
            else if (currentTurn == Turn.PLAYER_ACTION) turnText = "Player Attack";
            else if (currentTurn == Turn.ENEMY) {
                if      (currentEnemyPhase == EnemyAI.Phase.Recruit)    turnText = "Enemy Recruit";
                else if (currentEnemyPhase == EnemyAI.Phase.Move)       turnText = "Enemy Attack";
                else if (currentEnemyPhase == EnemyAI.Phase.RemoveTile) turnText = "Kingdoms Fall";
                else if (currentEnemyPhase == EnemyAI.Phase.Squish)     turnText = "Heal The World";
                else if (currentEnemyPhase == EnemyAI.Phase.Finish)     {
                    kickoffPhaseTransition = false;
                }
            }
        }

        if (kickoffPhaseTransition) {
            startTurnPhaseTransitionTween();
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

        batch.setColor(0.2f, 0.2f, 0.2f, color.a);
        batch.draw(assets.whitePixel, boundsRoundCounter.x, boundsRoundCounter.y, boundsRoundCounter.width, boundsRoundCounter.height);
        batch.setColor(1, 1f, 1f, color.a);
        assets.ninePatchScrews.draw(batch, boundsRoundCounter.x, boundsRoundCounter.y, boundsRoundCounter.width, boundsRoundCounter.height);

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

        // Draw rounds
        String roundsCount = Integer.toString(gameScreen.turnNumber, 10);
        String roundsCountLabel = "Round: " + roundsCount;

        float roundScale = 0.35f;
        Assets.font.getData().setScale(scale);
        {
            layout.setText(Assets.font, roundsCountLabel);
            Assets.drawString(batch, roundsCountLabel,
                              boundsRoundCounter.x + boundsRoundCounter.width / 2f  - layout.width / 2f,
                              boundsRoundCounter.y + boundsRoundCounter.height / 2f + layout.height / 2f,
                              color, roundScale, Assets.font);
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

        // Draw sparkles
        for (Sparkle sparkle : activeSparkles) {
            sparkle.render(batch);
        }

        // Draw tooltip
        renderToolTip(batch);

        // Draw phase transition card
        batch.setColor(0f, 0f, 0f, color.a);
        batch.draw(assets.whitePixel, boundsTurnText.x, boundsTurnText.y, boundsTurnText.width, boundsTurnText.height);
        batch.setColor(1, 1f, 1f, color.a);
        assets.ninePatchScrews.draw(batch, boundsTurnText.x, boundsTurnText.y, boundsTurnText.width, boundsTurnText.height);

        float turnTextScale = 0.4f;
        Assets.font.getData().setScale(scale);
        {
            turnTextColor.a = color.a;
            layout.setText(Assets.font, turnText, Color.WHITE, boundsTurnText.width - 2f * margin, Align.center, true);
            Assets.drawString(batch, turnText,
                              boundsTurnText.x + boundsTurnText.width / 2f - layout.width / 2f,
                              boundsTurnText.y + boundsTurnText.height / 2f + layout.height / 2f,
                              turnTextColor, turnTextScale, Assets.font);
        }
        Assets.font.getData().setScale(originalScaleX, originalScaleY);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private Vector3 toolTipVector = new Vector3();
    private void renderToolTip(SpriteBatch batch){
        OrthographicCamera hudCamera = World.THE_WORLD.screen.hudCamera;
        toolTipVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        hudCamera.unproject(toolTipVector);

        if (boundsPlayerUnits.contains(toolTipVector.x, toolTipVector.y)) {

            float boxWidth = ((boundsPlayerUnits.width - (2f * margin)) - (3f * pad)) * (1 / 4f);
            float boxHeight = height - 2f * pad;
            float bx1 = boundsPlayerUnits.x + margin;
            float bx2 = bx1 + boxWidth + pad;
            float bx3 = bx2 + boxWidth + pad;
            float bx4 = bx3 + boxWidth + pad;
            float by = boundsPlayerUnits.y + boundsPlayerUnits.height / 2f - boxHeight / 2f;

            if (toolTipVector.x > bx1 && toolTipVector.x < bx1 + boxWidth){
                renderToolTipInfo(batch, toolTipVector.x, "Peasant", 1, 1, assets.blankTile, "Claim tiles to build more peasants.");
            }
            if (toolTipVector.x > bx2 && toolTipVector.x < bx2 + boxWidth){
                renderToolTipInfo(batch, toolTipVector.x, "Soldier", 2, 2, assets.mountain, "Claim mountain tiles to build more soldiers.");
            }
            if (toolTipVector.x > bx3 && toolTipVector.x < bx3 + boxWidth){
                renderToolTipInfo(batch, toolTipVector.x - boundsTooltip.width, "Archer", 1, 3, assets.tree, "Claim forest tiles to build more archers.");
            }
            if (toolTipVector.x > bx4 && toolTipVector.x < bx4 + boxWidth){
                renderToolTipInfo(batch, toolTipVector.x - boundsTooltip.width, "Wizard", 3, 1, assets.gem, "Claim crystal tiles to build more wizards.");
            }
        }
    }

    public void renderToolTipInfo(SpriteBatch batch, float x, String name, int attackPower, int defensePower, TextureRegion type, String text){
        boundsTooltip.set(x, boundsPlayerUnits.y - boundsTooltip.height, boundsTooltip.width, boundsTooltip.height);
        assets.ninePatchTooltip.draw(batch, boundsTooltip.x, boundsTooltip.y, boundsTooltip.width, boundsTooltip.height);

        batch.draw(type, boundsTooltip.x + 10, boundsTooltip.y + boundsTooltip.height - 42, 64, 32);
        batch.draw(type, boundsTooltip.x  + boundsTooltip.width - 72, boundsTooltip.y + boundsTooltip.height - 42, 64, 32);

        float originalScaleX = Assets.font.getData().scaleX;
        float originalScaleY = Assets.font.getData().scaleY;
        Assets.font.getData().setScale(.4f);
        layout.setText(Assets.font, name);
        Assets.drawString(batch, name, x, boundsPlayerUnits.y - 10, Color.WHITE, .4f, Assets.font, boundsTooltip.width, Align.center);

        float y = boundsPlayerUnits.y - 10 - layout.height - 5;
        float center = boundsTooltip.x + boundsTooltip.width/2f;
        Assets.font.getData().setScale(.3f);
        layout.setText(Assets.font, ""+attackPower);
        batch.draw(LudumDare42.game.assets.sword, center - layout.width - 52, y - 30, 32, 32);
        Assets.drawString(batch, ""+attackPower, center - 10 - layout.width, y - 16 + layout.height/2f, Color.WHITE, .3f, Assets.font);

        layout.setText(Assets.font, ""+defensePower);
        batch.draw(LudumDare42.game.assets.shield, center + 10, y - 30, 32, 32);
        Assets.drawString(batch, ""+defensePower, center + 52, y - 16 + layout.height/2f, Color.WHITE, .3f, Assets.font);

        y -= 50;
        Assets.font.getData().setScale(.3f);
        Assets.drawString(batch, text, boundsTooltip.x + 10, y, Color.WHITE, .3f, Assets.font, boundsTooltip.width - 20, Align.center);

        Assets.font.getData().setScale(originalScaleX, originalScaleY);
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
        float roundCounterWidth = segmentUnitsWidth / 4f;
        float roundCounterHeight = 30f;
        float turnTextMargin = 100f;
        float turnTextWidth = camera.viewportWidth / 3f;
        float turnTextHeight = 100f;//camera.viewportHeight / 4f;

        float turnTextInitialX = -turnTextWidth - turnTextMargin;
        float turnTextInitialY = camera.viewportHeight / 2f - turnTextHeight / 2f;

        float territoryPlayerInitialX = -segmentTerritoryWidth;
        float territoryPlayerEndingX = 0f;

        float unitsPlayerInitialY = camera.viewportHeight;
        float unitsPlayerEndingY = lowerLeftY - unitsOffsetY;

        float territoryEnemyInitialX = camera.viewportWidth;
        float territoryEnemyEndingX = camera.viewportWidth - segmentTerritoryWidth;

        float roundCounterInitialY = -roundCounterHeight;
        float roundCounterEndingY = 0f;

        bounds.set(0f, lowerLeftY, width, height);
        boundsPlayerTerritory.set(territoryPlayerInitialX, lowerLeftY, segmentTerritoryWidth, height);
        boundsPlayerUnits.set(territoryPlayerEndingX + boundsPlayerTerritory.width, unitsPlayerInitialY, segmentUnitsWidth, height + unitsOffsetY);
        boundsEnemyTerritory.set(territoryEnemyInitialX, lowerLeftY, segmentTerritoryWidth, height);
        boundsRoundCounter.set(boundsPlayerUnits.x + boundsPlayerUnits.width / 2f - roundCounterWidth / 2f, roundCounterInitialY, roundCounterWidth, roundCounterHeight);
        boundsTurnText.set(turnTextInitialX, turnTextInitialY, turnTextWidth, turnTextHeight);

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
                                        Timeline.createParallel()
                                        .push(
                                                Tween.to(boundsPlayerUnits, RectangleAccessor.Y, duration).delay(initialDelay)
                                                     .target(unitsPlayerEndingY).ease(Bounce.OUT)
                                        )
                                        .push(
                                                Tween.to(boundsRoundCounter, RectangleAccessor.Y, duration).delay(initialDelay)
                                                     .target(roundCounterEndingY).ease(Bounce.OUT)
                                        )
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
                        startTurnPhaseTransitionTween();
                    }
                })
                .start(LudumDare42.game.tween);
    }

    private void startTurnPhaseTransitionTween() {
        float expand = 10f;
        float margin = 100f;
        float initialW = camera.viewportWidth / 3f;
        float initialH = camera.viewportHeight / 6f;
        float initialX = -boundsTurnText.width - margin;
        float initialY = camera.viewportHeight / 2f - initialH / 2f;
        float middleX = camera.viewportWidth / 2f - boundsTurnText.width / 2f;
        float middleY = camera.viewportHeight / 2f - boundsTurnText.height / 2f;
        float endingX = camera.viewportWidth + margin;

        Timeline.createSequence()
                .push(
                        Tween.set(boundsTurnText, RectangleAccessor.XYWH)
                             .target(initialX, initialY, initialW, initialH)
                )
                .push(
                        Tween.to(boundsTurnText, RectangleAccessor.X, 0.1f)
                             .target(middleX).ease(Back.OUT)
                )
                .push(
                        Tween.to(boundsTurnText, RectangleAccessor.XYWH, 0.3f)
                             .target(middleX - expand, middleY - expand,
                                     initialW + 2f * expand,
                                     initialH + 2f * expand)
                             .repeatYoyo(2, 0f)
                )
                .push(
                        Tween.to(boundsTurnText, RectangleAccessor.X, 0.1f)
                             .target(endingX).ease(Back.IN)
                )
                .push(
                        Tween.set(boundsTurnText, RectangleAccessor.X).target(initialX)
                )
                .start(LudumDare42.game.tween);
    }

}
