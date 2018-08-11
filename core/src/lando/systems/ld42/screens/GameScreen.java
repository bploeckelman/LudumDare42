package lando.systems.ld42.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.units.ArcherUnit;
import lando.systems.ld42.units.PeasantUnit;
import lando.systems.ld42.units.SoldierUnit;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.*;

public class GameScreen extends BaseScreen {

//    public TutorialManager tutorialManager;
    public TextureRegion debugTex;
    public World world;
//    public TurnCounter turnCounter;
    public Array<Tile> adjacentTiles;
    public Array<Tile> adjacentBuildTiles;
//    public EndTurnButton endTurnButton;
//    public PlayerSelectionHud playerSelection;
    public Player selectedPlayer;
    public Array<Unit> testUnits;

    public int turn;
//    public Array<TurnAction> turnActions;
    float time;

    public Vector3 cameraTouchStart;
    public Vector3 touchStart;

    public boolean firstRun = false;

    public MutableFloat overlayAlpha;
    public boolean pauseGame;
    public boolean gameOver;
    public boolean gameLost;
//    EndGameOverlay endGameOverlay;
//    public Screenshake shaker;
    public Vector2 cameraCenter;

    public Pixmap pickPixmap;
    public Color pickColor;
    public PlayerHUD hud;

    public int pickMapScale = 8;
    private FrameBuffer pickBuffer;
    private TextureRegion pickRegion;

    public GameScreen() {
        super();
 //       SoundManager.oceanWaves.play();
        cameraCenter = new Vector2();
        gameOver = false;
        overlayAlpha = new MutableFloat(1);
        pauseGame = true;
        time = 0;
        world = new World(this);
        cameraTargetPos.set(world.bounds.width/2f, world.bounds.height/2f, 0);
        worldCamera.position.set(cameraTargetPos);
        targetZoom.setValue(MAX_ZOOM);
        worldCamera.zoom = targetZoom.floatValue();
        worldCamera.update();
        adjacentTiles = new Array<Tile>();
        adjacentBuildTiles = new Array<Tile>();
        turn = 0;

//        endTurnButton = new EndTurnButton(new Rectangle(hudCamera.viewportWidth - 100 - 10, 10, 100, 30), hudCamera);
//        playerSelection = new PlayerSelectionHud(this);
//        testingButton = new Button(Assets.transparentPixel, new Rectangle(50,50,50,50), hudCamera, "Too much Text!", "Tooltip");
        cameraTouchStart = new Vector3();
        touchStart = new Vector3();
//        shaker = new Screenshake(120, 3);

        testUnits = new Array<Unit>();
        Unit peasant = new PeasantUnit(assets);
        Unit soldier = new SoldierUnit(assets);
        Unit archer  = new ArcherUnit(assets);
        peasant.moveTo(world.getTile(0, 0));
        soldier.moveTo(world.getTile(1, 0));
        archer.moveTo(world.getTile(0, 1));
        testUnits.add(peasant, soldier, archer);

        hud = new PlayerHUD(this);
        pickBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)worldCamera.viewportWidth / pickMapScale, (int)worldCamera.viewportHeight / pickMapScale, false, false);
        pickRegion = new TextureRegion(pickBuffer.getColorBufferTexture());
        pickRegion.flip(false, true);
        pickPixmap = null;
        pickColor = new Color();
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // TODO: removeme, just for testing
        if (Gdx.input.justTouched()) {
            Unit testUnit = testUnits.get(MathUtils.random(0, testUnits.size - 1));
            int currCol = testUnit.tile.col;
            int currRow = testUnit.tile.row;
            int nextCol = currCol;
            int nextRow = currRow;
            boolean moveCol = MathUtils.randomBoolean();
            if (moveCol) {
                nextCol = MathUtils.clamp(currCol + MathUtils.randomSign(), 0, World.WORLD_WIDTH - 1);
            } else {
                nextRow = MathUtils.clamp(currRow + MathUtils.randomSign(), 0, World.WORLD_HEIGHT - 1);
            }
            Gdx.app.log("MOVE", "(" + currCol + ", " + currRow + ") -> (" + nextCol + ", " + nextRow + ")");
            testUnit.moveTo(world.getTile(nextCol, nextRow));
        }


        time += dt;
        world.update(dt);
        for (Unit unit : testUnits) {
            unit.update(dt);
        }

        updateCamera();

//        shaker.update(dt, camera, camera.position.x, camera.position.y);
    }



    @Override
    public void render(SpriteBatch batch) {
        // Draw picking frame buffer
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


        Gdx.gl.glClearColor(Config.background_color.r, Config.background_color.g, Config.background_color.b, Config.background_color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            world.render(batch);
            for (Unit unit : testUnits) {
                unit.render(batch);
            }

            if (pickPixmap != null){
                Tile t = getTileFromScreen(Gdx.input.getX(), Gdx.input.getY());
                if (t != null) {
                    t.renderHightlight(batch, Color.YELLOW);
                    TileUtils.getNeighbors(t, world, adjacentTiles);
                    for (Tile a : adjacentTiles){
                        a.renderHightlight(batch, Color.BLUE);
                    }
                }
            }
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            hud.render(batch);
//            batch.draw(pickRegion, 0, 0, 100, 80);
        }
        batch.end();
    }

    // required Konami code
    int[] sequence = new int [] { Input.Keys.UP, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.B, Input.Keys.A};
    int index = 0;
    public boolean keyUp(int keyCode) {
        if (index >= sequence.length) index = 0;
        if (sequence[index] == keyCode) {
            if (++index == sequence.length) {
                // insert magic here
                index = 0;
            }
        } else {
            index = 0;
        }
        return false;
    }

    Vector3 tempVec3 = new Vector3();
    public Tile getTileFromScreen(int screenX, int screenY) {
        hudCamera.unproject(tempVec3.set(screenX, screenY, 0));
        pickColor.set(pickPixmap.getPixel((int)(tempVec3.x / pickMapScale), (int)(tempVec3.y / pickMapScale)));
        return TileUtils.parsePickColorForTileInWorld(pickColor, world);
    }
}
