package lando.systems.ld42.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.units.Unit;
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
    public Unit testUnit;

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

        testUnit = new Unit(LudumDare42.game.assets);
        testUnit.moveTo(world.getTile(0, 0));
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // TODO: removeme, just for testing
        if (Gdx.input.justTouched()) {
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
        testUnit.update(dt);

        updateCamera();

//        shaker.update(dt, camera, camera.position.x, camera.position.y);
    }



    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.background_color.r, Config.background_color.g, Config.background_color.b, Config.background_color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            world.render(batch);
            testUnit.render(batch);
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
}
