package lando.systems.ld42.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld42.Config;
import lando.systems.ld42.teams.EnemyTeam;
import lando.systems.ld42.teams.PlayerTeam;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.turns.Turn;
import lando.systems.ld42.turns.TurnAction;
import lando.systems.ld42.ui.Tooltip;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.utils.Screenshake;
import lando.systems.ld42.utils.TileUtils;
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
    public PlayerHUD hud;
    public Tooltip tooltip;
    public Screenshake shaker;
    public int pickMapScale = 8;
    private FrameBuffer pickBuffer;
    private TextureRegion pickRegion;
    private float accum;

    public GameScreen() {
        super();
 //       SoundManager.oceanWaves.play();
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
        this.turnAction = new TurnAction();
        this.tooltip = new Tooltip();
        this.shaker = new Screenshake(120, 3);
        this.cameraTouchStart = new Vector3();
        this.touchStart = new Vector3();
        this.playerTeam = new PlayerTeam(world, assets);
        this.enemyTeam = new EnemyTeam(world, assets);
        this.hud = new PlayerHUD(this);
        this.pickBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                                          (int) worldCamera.viewportWidth / pickMapScale,
                                          (int) worldCamera.viewportHeight / pickMapScale,
                                          false, false);
        this.pickRegion = new TextureRegion(pickBuffer.getColorBufferTexture());
        this.pickRegion.flip(false, true);
        this.pickPixmap = null;
        this.pickColor = new Color();
    }

    @Override
    public void update(float dt) {
        accum += dt;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (turnAction.turn == Turn.ENEMY) {
            dumbAIMovement();
            turnAction.nextTurn();
            turnNumber++;
        }

        if (Gdx.input.justTouched() && !transitioning) {
            if (turnAction.turn == Turn.PLAYER_RECRUITMENT) {
                playerTeam.replenishAction();
                enemyTeam.replenishAction();
                turnAction.nextTurn();
            }
            else if (pickPixmap != null && turnAction.turn == Turn.PLAYER_ACTION){
                Tile t = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                if (t != null && t.occupant == null && selectedUnitTile != null && adjacentTiles.contains(t, true)) {
                    selectedUnitTile.occupant.moveTo(t);
                    selectedUnitTile.occupant.actionAvailable--;
                    selectedUnitTile = null;
                }
                else if (t != null && t.occupant != null && t.occupant.team.getClass() == PlayerTeam.class) {
                    selectedUnitTile = t;
                } else {
                    selectedUnitTile = null;
                }
                // if no action left, next
                if (!playerTeam.isActionLeft()) {
                    turnAction.nextTurn();
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)){
            world.pickRemoveTile();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            shaker.shakeDuration = 25f;
            shaker.shake(2f);
            world.squishHoles();
        }

        time += dt;

        world.update(dt);
        playerTeam.update(dt);
        enemyTeam.update(dt);
        hud.update(dt);

        updateCamera();
        targetZoom.setValue(Math.max(world.bounds.width / worldCamera.viewportWidth, world.bounds.height / worldCamera.viewportHeight));
        cameraTargetPos.set(world.bounds.width/2, world.bounds.height/2, 0);
        shaker.update(dt, worldCamera, worldCamera.position.x, worldCamera.position.y);
    }

    @Override
    public void render(SpriteBatch batch, boolean inTransition) {
        transitioning = inTransition;

        // Draw picking frame buffer
        if (!inTransition) {
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
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            world.render(batch);

            playerTeam.render(batch);
            enemyTeam.render(batch);

            if (!transitioning) {
                Tile touchedTile = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                if (touchedTile != null) {
                    if (touchedTile.owner == Team.Type.player) {
                        calculateInformationForPlayerTile(adjacentTiles, touchedTile);
                    } else if (touchedTile.owner == Team.Type.enemy) {
                        calculateInformationForEnemyTile(adjacentTiles, touchedTile);
                    } else {
                        tooltip.text = null;
                    }
                } else {
                    tooltip.text = null;
                }
            }

            if (selectedUnitTile != null) {
                selectedUnitTile.renderHighlight(batch, Color.YELLOW);
                TileUtils.getNeighbors(selectedUnitTile, world, adjacentTiles);
                for (Tile adjacentTile : adjacentTiles){
                    adjacentTile.renderHighlight(batch, Color.BLUE);
                }
            }
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            hud.render(batch, inTransition);

            if (tooltip != null) {
                tooltip.render(batch, hudCamera);
            }

            if (Config.debug) {
                batch.draw(pickRegion, 0, 0, 100, 80);
            }
        }

        String turnText;

        if (turnAction.turn == Turn.PLAYER_RECRUITMENT) {
            turnText = "Player's Recruitment Turn " + turnNumber;
        }
        else if (turnAction.turn == Turn.PLAYER_ACTION) {
            turnText = "Player's Action Turn " + turnNumber;
        } else {
            turnText = "Enemy's Turn " + turnNumber;
        }
        lando.systems.ld42.Assets.drawString(batch, turnText, 0, 30, Color.BLACK, .5f, lando.systems.ld42.Assets.font, Config.window_width, Align.center);

        batch.end();
    }

    private void calculateInformationForPlayerTile(Array<Tile> neighbors, Tile currentTile) {
        if (neighbors == null || currentTile == null) return;

        int playerDefense = calculateDefense(neighbors, currentTile, playerTeam.units);
        int enemyAttack = calculateAttack(neighbors, enemyTeam.units);

        String str = "Player Defense: " + playerDefense + "\nEnemy Attack: " + enemyAttack;
        tooltip.setText(str);
    }

    private void calculateInformationForEnemyTile(Array<Tile> neighbors, Tile currentTile) {
        if (neighbors == null || currentTile == null) return;

        int enemyDefense = calculateDefense(neighbors, currentTile, enemyTeam.units);
        int playerAttack = calculateAttack(neighbors, playerTeam.units);

        String str = "Enemy Defense: " + enemyDefense + "\nPlayer Attack: " + playerAttack;
        tooltip.setText(str);
    }

    private int calculateDefense(Array<Tile> neighbors, Tile currentTile, Array<Unit> units) {
        int defense = 0;
        for (Unit unit : units) {
            if (unit.tile != null) {
                for (Tile tile : neighbors) {
                    if (tile.position == unit.tile.position) {
                        defense += unit.defensePower;
                    }
                }
                if (unit.tile.position == currentTile.position) {
                    defense += unit.defensePower;
                }
            }
        }
        return defense;
    }

    private int calculateAttack(Array<Tile> neighbors, Array<Unit> units) {
        int attack = 0;
        for (Unit unit : units) {
            if (unit.tile != null) {
                for (Tile tile : neighbors) {
                    if (tile.position == unit.tile.position) {
                        attack += unit.attackPower;
                    }
                }
            }
        }
        return attack;
    }

    private Vector3 tempVec3 = new Vector3();
    private Tile getTileFromScreen(int screenX, int screenY) {
        hudCamera.unproject(tempVec3.set(screenX, screenY, 0));
        pickColor.set(pickPixmap.getPixel((int)(tempVec3.x / pickMapScale), (int)(tempVec3.y / pickMapScale)));
        return TileUtils.parsePickColorForTileInWorld(pickColor, world);
    }

    public void dumbAIMovement() {
        Array<Unit> units = enemyTeam.units;
        for (Unit unit : units) {
            if (unit.actionAvailable > 0) {
                int currCol = unit.tile.col;
                int currRow = unit.tile.row;
                int nextCol = currCol;
                int nextRow = currRow;
                boolean moveCol = MathUtils.randomBoolean();
                if (moveCol) {
                    nextCol = MathUtils.clamp(currCol + MathUtils.randomSign(), 0, World.WORLD_WIDTH - 1);
                } else {
                    nextRow = MathUtils.clamp(currRow + MathUtils.randomSign(), 0, World.WORLD_HEIGHT - 1);
                }
                Gdx.app.log("MOVE", "(" + currCol + ", " + currRow + ") -> (" + nextCol + ", " + nextRow + ")");
                unit.moveTo(world.getTile(nextCol, nextRow));
                unit.actionAvailable--;
            }
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
