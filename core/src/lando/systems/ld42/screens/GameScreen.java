package lando.systems.ld42.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.particles.ParticleSystem;
import lando.systems.ld42.teams.EnemyTeam;
import lando.systems.ld42.teams.PlayerTeam;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.turns.EnemyAI;
import lando.systems.ld42.turns.Turn;
import lando.systems.ld42.turns.TurnAction;
import lando.systems.ld42.turns.TurnStats;
import lando.systems.ld42.ui.RecruitmentUI;
import lando.systems.ld42.ui.StatusUI;
import lando.systems.ld42.ui.Tooltip;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.utils.Audio;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.utils.screenshake.ScreenShakeCameraController;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class GameScreen extends BaseScreen {
    public World world;
    public Array<Tile> adjacentTiles;
    public Array<Tile> adjacentBuildTiles;
    public PlayerTeam playerTeam;
    public EnemyTeam enemyTeam;
    public Tile selectedUnitTile;
    public float time;
    public TurnAction turnAction;
    public int turnNumber;
    public Vector3 cameraTouchStart;
    public Vector3 touchStart;
    public MutableFloat overlayAlpha;
    public boolean pauseGame;
    public boolean gameOver;
    public boolean transitioning;
    public Vector2 cameraCenter;
    public Pixmap pickPixmap;
    public Color pickColor;

    public ScreenShakeCameraController screenShakeCamera;
    public Stage ui;
    public StatusUI statusUI;
    public RecruitmentUI recruitmentUI;
    public int pickMapScale = 8;
    private FrameBuffer pickBuffer;
    private TextureRegion pickRegion;
    private float accum;
    private String endGameText;

    public EnemyAI enemyAI;
    public Tooltip tooltip;

    public ParticleSystem particleSystem;

    public Vector2 helpHandPos;

    public GameScreen() {
        super();
 //       SoundManager.oceanWaves.play();
        this.particleSystem = new ParticleSystem();
        this.cameraCenter = new Vector2();
        this.gameOver = false;
        this.overlayAlpha = new MutableFloat(1);
        this.pauseGame = true;
        this.transitioning = true;
        this.time = 0;
        this.world = new World(this);
        this.cameraTargetPos.set(world.bounds.width/2f, world.bounds.height/2f, 0);
        this.worldCamera.position.set(cameraTargetPos);
        this.targetZoom.setValue(MAX_ZOOM);
        this.worldCamera.zoom = targetZoom.floatValue();
        this.worldCamera.update();
        this.adjacentTiles = new Array<Tile>();
        this.adjacentBuildTiles = new Array<Tile>();
        this.turnNumber = 1;

        this.screenShakeCamera = new ScreenShakeCameraController(worldCamera);

        this.cameraTouchStart = new Vector3();
        this.touchStart = new Vector3();
        this.playerTeam = new PlayerTeam(world);
        this.enemyTeam = new EnemyTeam(world);
        this.turnAction = new TurnAction(playerTeam, enemyTeam);
        this.pickBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                                          (int) worldCamera.viewportWidth / pickMapScale,
                                          (int) worldCamera.viewportHeight / pickMapScale,
                                          false, false);
        this.pickRegion = new TextureRegion(pickBuffer.getColorBufferTexture());
        this.pickRegion.flip(false, true);
        this.pickPixmap = null;
        this.pickColor = new Color();
        this.selectedUnitTile = playerTeam.castleTile;

        enemyAI = new EnemyAI(world, this);
        tooltip = new Tooltip();

        initializeUserInterface();

        int unownedTile = 0;
        for (Tile t : world.tiles){
            if (t != null && t.owner == Team.Type.none) unownedTile++;
        }
        TurnStats.getTurnStats().addTileStats(turnNumber, playerTeam.getTileTotalCount(), enemyTeam.getTileTotalCount(), unownedTile);
        helpHandPos = new Vector2(148, 320);
    }

    @Override
    public void setInputProcessors() {
        Gdx.input.setInputProcessor(new InputMultiplexer(ui, this));
    }

    @Override
    public void update(float dt) {
        // Don't judge me there is only 30 min left in the Jam
        if (turnNumber == 1 && turnAction.turn == Turn.PLAYER_ACTION){
            if (selectedUnitTile == null) {
                Unit u = playerTeam.units.get(0);
                if (u != null) {
                    helpHandPos.set(u.pos.x + u.size.x/2f, u.pos.y + (u.size.y *2));
                }
            } else {
                Tile t = world.getTile(selectedUnitTile.col, selectedUnitTile.row + 1);
                helpHandPos.set(t.position.x + Tile.tileWidth/2f, t.position.y + Tile.tileHeight * 1.5f);
            }
        }

        if (turnNumber != 1 || turnAction.turn == Turn.ENEMY){
            helpHandPos.set(-1000, -1000);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (checkVictory()) {
            endGameText = "You Win!";
        }

        if (checkLoss()) {
            endGameText = "You Lost :(";
        }

        if ((Gdx.input.justTouched() && gameOver) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            game.setScreen(new EndScreen());
        }

        accum += dt;
        ui.act(Math.min(dt, 1 / 30f));

        particleSystem.update(dt);

        if (turnAction.turn == Turn.ENEMY && !gameOver) {
            enemyAI.update(dt);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)){
            world.pickRemoveTileCleverly();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)){
            world.squishHoles();
        }

        time += dt;

        world.update(dt);
        playerTeam.update(dt);
        enemyTeam.update(dt);

        recruitmentUI.update(dt);
        statusUI.update(dt);

        updateCamera();
        targetZoom.setValue(Math.max(world.bounds.width / worldCamera.viewportWidth, world.bounds.height / worldCamera.viewportHeight));
        cameraTargetPos.set(world.bounds.x + world.bounds.width/2, world.bounds.y + world.bounds.height/2, 0);
        screenShakeCamera.update(dt);
    }

    private void UpdateTileDetails(Tile hoverTile) {
        if (hoverTile != null && hoverTile.occupant != null) {
            TileUtils.getNeighbors(hoverTile, world, adjacentTiles);

            if (hoverTile.occupant.team == Team.Type.player) {
                int playerDefense = calculateDefense(hoverTile, Team.Type.player);
                int enemyAttack = calculateAttack(null, Team.Type.enemy);

                if (enemyAttack == 0) {
                    tooltip.setText("Player Defense: " + playerDefense, hoverTile);
                } else {
                    tooltip.setText("Player Defense: " + playerDefense + "\nEnemy attack: " + enemyAttack, hoverTile);
                }
            } else {
                int enemyDefense = calculateDefense(hoverTile, Team.Type.enemy);
                int playerAttack = calculateAttack(null, Team.Type.player);
                if (playerAttack == 0) {
                    tooltip.setText("Enemy Defense: " + enemyDefense, hoverTile);
                } else {
                    tooltip.setText("Enemy Defense: " + enemyDefense + "\nPlayer Attack: " + playerAttack, hoverTile);
                }
            }
        } else {
            tooltip.text = null;
        }
    }

    @Override
    public void render(SpriteBatch batch, boolean inTransition) {
        transitioning = inTransition;
        // Draw picking frame buffer
        if (!transitioning) {
            batch.setProjectionMatrix(worldCamera.combined);
            pickBuffer.begin();
            {
                Gdx.gl.glClearColor(0f, 0f, 1f, 1f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                world.renderPickBuffer(batch);
                batch.end();
            }

            pickPixmap = ScreenUtils.getFrameBufferPixmap(0, 0, pickBuffer.getWidth(), pickBuffer.getHeight());
            pickBuffer.end();
        }


        Gdx.gl.glClearColor(Config.background_color.r, Config.background_color.g, Config.background_color.b, Config.background_color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.setShader(assets.cloudShader);
        batch.begin();
        assets.cloudShader.setUniformf("u_time", accum);
        assets.cloudShader.setUniformf("u_resolution", hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.draw(assets.titleTexture, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.end();
        batch.setShader(null);

        // Draw world
        batch.setProjectionMatrix(screenShakeCamera.getCombinedMatrix());
        batch.begin();
        {
            world.render(batch);

            if (!transitioning) {
                Tile touchedTile = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                UpdateTileDetails(touchedTile);
            }

            if (selectedUnitTile != null) {
                selectedUnitTile.renderHighlight(batch, Config.selected_color);
                TileUtils.getNeighbors(selectedUnitTile, world, adjacentTiles);
                for (Tile adjacentTile : adjacentTiles) {
                    adjacentTile.renderHighlight(batch, Config.highlight_color);
                }

            }

            playerTeam.render(batch);
            enemyTeam.render(batch);

            if (selectedUnitTile != null && turnAction.turn  == Turn.PLAYER_ACTION){
                Tile touchedTile = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                if (touchedTile != null && adjacentTiles.contains(touchedTile, true)){
                    touchedTile.renderAttackStats(batch);
                }
            }

            particleSystem.render(batch);
            if (accum > 5f && turnNumber == 1){
                batch.setColor(Color.WHITE);
                batch.draw(assets.pointer, helpHandPos.x, helpHandPos.y + Math.abs(MathUtils.sin(accum*4)) * 30, -64, -64);
            }
        }
        batch.end();

        // Draw HUD
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);

            if (!inTransition) {
                statusUI.render(batch);
            }

            if (tooltip != null) {
                tooltip.render(batch);
            }

            if (Config.debug) {
                batch.draw(pickRegion, 0, 0, 100, 80);
            }

            if (gameOver) {
                batch.setColor(0f, 0f, 0f, 0.4f);
                batch.draw(assets.whitePixel, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);

                float w = hudCamera.viewportWidth / 2f;
                float h = hudCamera.viewportHeight / 5f;
                batch.setColor(0.2f, 0.2f, 0.2f, 0.9f);
                batch.draw(assets.whitePixel, hudCamera.viewportWidth / 2f - w / 2f, hudCamera.viewportHeight / 2f - h / 2f, w, h);
                batch.setColor(1, 1f, 1f, 1f);
                assets.ninePatchScrews.draw(batch, hudCamera.viewportWidth / 2f - w / 2f, hudCamera.viewportHeight / 2f - h / 2f, w, h);
                Color col = checkVictory() ? Color.GREEN : Color.RED;
                Assets.drawString(batch, endGameText, hudCamera.viewportWidth / 2f - w / 2f, hudCamera.viewportHeight / 2f + 10f, col, .5f, Assets.font, w, Align.center);
            }
        }
        batch.end();

        // Draw Scene2D ui components
        ui.draw();
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (!transitioning) {
            if (turnAction.turn == Turn.PLAYER_RECRUITMENT) {
                Tile t = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                if (t != null && t.occupant == null && adjacentTiles.contains(t, true) && playerTeam.buildsLeft()){
                    if (turnNumber == 1) helpHandPos.set(900, 150);
                    recruitmentUI.rebuild(playerTeam, t, hudCamera);
                    recruitmentUI.show();
                    LudumDare42.game.audio.playSound(Audio.Sounds.click);
                }
                if (!playerTeam.buildsLeft()) {
                    selectedUnitTile = null;
                    turnAction.nextTurn();
                }
            }
            else if (turnAction.turn == Turn.PLAYER_ACTION && pickPixmap != null){
                Tile t = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                if (t != null) {
                    if (selectedUnitTile != null) {
                        if (t == selectedUnitTile) {
                            // reset
                            LudumDare42.game.audio.playSound(Audio.Sounds.click);
                            selectedUnitTile = null;
                        } else if (adjacentTiles.contains(t, true)) {
                            Unit playerUnit = selectedUnitTile.occupant;
                            if (playerUnit.actionAvailable > 0) {
                                if (t.occupant == null) {
                                    // move to this tile
                                    playerUnit.moveTo(t);
                                } else if (t.occupant.team == Team.Type.enemy) {
                                    // attack
                                    resolveAttack(playerUnit, t);
                                } else if (t.occupant.actionAvailable > 0) {
                                    // swap
                                    Unit swap = t.occupant;
                                    playerUnit.moveTo(t);
                                    swap.moveTo(selectedUnitTile);
                                }
                                selectedUnitTile = null;
                            }
                        }
                    } else if (t.occupant != null && t.occupant.team == Team.Type.player && t.occupant.actionAvailable > 0) {
                        // select unit
                        selectedUnitTile = t;
                        LudumDare42.game.audio.playSound(Audio.Sounds.click);

                    }
                }
                else {
                    selectedUnitTile = null;
                }

                // if no action left, next
                if (!playerTeam.isActionLeft()) { //TODO also able to early out with a button and leave movement on the field for the turn
                    turnAction.nextTurn();
                    playerTeam.removeLeftoverActions();
                }
            }
        }

        return false;
    }

    private int calculateDefense(Tile currentTile, Team.Type team) {
        int defense = 0;
        TileUtils.getNeighbors(currentTile, world, adjacentTiles);
        if (currentTile.occupant != null && currentTile.occupant.team == team) {
            defense += currentTile.occupant.defensePower;

            for (Tile tile : adjacentTiles) {
                if (tile.occupant != null && tile.occupant.team == team) {
                    defense += tile.occupant.defensePower;
                }
            }
        }
        return defense;
    }

    private int calculateAttack(Unit attackingUnit, Team.Type team) {
        int attack = 0;
        for (Tile tile : adjacentTiles) {
            if (tile.occupant != null && tile.occupant.team == team) {
                attack += tile.occupant.attackPower;
            }
        }
        return attack;
    }


    public int willAttackSucceed(Unit attackingUnit, Tile attackingTile) {
        TileUtils.getNeighbors(attackingTile, world, adjacentTiles);
        return calculateAttack(attackingUnit, attackingUnit.team) - calculateDefense(attackingTile, attackingTile.occupant.team);

    }

    public void resolveAttack(Unit attackingUnit, Tile attackingTile){
        particleSystem.addBattleCloud(attackingTile, attackingUnit.tile);
        LudumDare42.game.audio.playSound(Audio.Sounds.fight);
        int result = willAttackSucceed(attackingUnit, attackingTile);
        if (result > 0) {
            //attack succeeded
            attackingTile.occupant.dead = true;
            attackingTile.occupant = null;
            attackingUnit.moveTo(attackingTile);
            selectedUnitTile = null;
        } else if (result == 0) { // Both die
            attackingTile.occupant.dead = true;
            attackingTile.occupant = null;
            attackingUnit.dead = true;
            attackingUnit.tile.occupant = null;
            selectedUnitTile = null;
        } else {
            //killed
            attackingUnit.dead = true;
            attackingUnit.tile.occupant = null;
            selectedUnitTile = null;
        }
    }

    private Vector3 tempVec3 = new Vector3();
    private Tile getTileFromScreen(int screenX, int screenY) {
        if (pickPixmap == null) return null;
        hudCamera.unproject(tempVec3.set(screenX, screenY, 0));
        if (tempVec3.x < 0 || tempVec3.x > hudCamera.viewportWidth ||
                tempVec3.y < 0 || tempVec3.y > hudCamera.viewportHeight) return null;

        pickColor.set(pickPixmap.getPixel((int)(tempVec3.x / pickMapScale), (int)(tempVec3.y / pickMapScale)));
        return TileUtils.parsePickColorForTileInWorld(pickColor, world);
    }


    private void initializeUserInterface() {
        ui = new Stage(new ExtendViewport(Config.window_width, Config.window_height));
        ui.getViewport().setCamera(hudCamera);
        ui.setDebugUnderMouse(Config.debug);

        recruitmentUI = new RecruitmentUI(assets);

        statusUI = new StatusUI(assets);
        statusUI.rebuild(this, hudCamera);

        ui.addActor(recruitmentUI.root);
    }

    private boolean checkVictory() {

        if (enemyTeam.castleTile.occupant != null && enemyTeam.castleTile.occupant.team == Team.Type.player) {
            this.gameOver = true;
            return true;
        }
        else if (enemyTeam.getTileTotalCount() < 2 && world.unclaimedTileCount == 0) {
            this.gameOver = true;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean checkLoss() {

        if (playerTeam.castleTile.occupant != null && playerTeam.castleTile.occupant.team == Team.Type.enemy) {
            this.gameOver = true;
            return true;
        }
        else if (world.playerTileCount < 1 && world.unclaimedTileCount == 0) {
            this.gameOver = true;
            return true;
        }
        else {
            return false;
        }
    }

    public void endRecruitment(boolean force) {
        if (turnAction.turn != Turn.PLAYER_RECRUITMENT) return;

        if (!playerTeam.buildsLeft() || force) {
            turnAction.nextTurn();
        }
    }

//    // required Konami code
//    private int[] sequence = new int [] { Input.Keys.UP, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.B, Input.Keys.A};
//    private int index = 0;
//    @Override
//    public boolean keyUp(int keyCode) {
//        if (index >= sequence.length) index = 0;
//        if (sequence[index] == keyCode) {
//            if (++index == sequence.length) {
//                // insert magic here
//                index = 0;
//            }
//        } else {
//            index = 0;
//        }
//        return false;
//    }

}
