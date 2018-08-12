package lando.systems.ld42.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;

/**
 * Created by Brian on 7/25/2017
 */
public abstract class BaseScreen extends InputAdapter {

    public final LudumDare42 game;
    public final Assets assets;

    public boolean allowInput;
    public MutableFloat alpha;
    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;

    protected static final float ZOOM_SCALE = 0.1f;
    protected static final float MAX_ZOOM = 2f;
    protected static final float MIN_ZOOM = 1.1f;
    private static final float DRAG_DELTA = 10f;
    private static final float ZOOM_LERP = .01f;
    private static final float PAN_LERP = .01f;
    protected boolean cancelTouchUp = false;
    protected Vector3 cameraTouchStart = new Vector3();
    protected Vector3 touchStart = new Vector3();

    public Vector3 cameraTargetPos = new Vector3();
    public MutableFloat targetZoom = new MutableFloat(1f);



    public BaseScreen() {
        super();
        this.game = LudumDare42.game;
        this.assets = LudumDare42.game.assets;

        this.allowInput = false;
        this.alpha = new MutableFloat(0f);

        float aspect = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.worldCamera = new OrthographicCamera(Config.window_width, Config.window_height / aspect);
        this.worldCamera.translate(this.worldCamera.viewportWidth / 2f, this.worldCamera.viewportHeight / 2f, 0f);
        this.worldCamera.update();

        this.hudCamera = new OrthographicCamera(Config.window_width, Config.window_height / aspect);
        this.hudCamera.translate(this.hudCamera.viewportWidth / 2f, this.hudCamera.viewportHeight / 2f, 0f);
        this.hudCamera.update();
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch, boolean inTransition);

    protected void updateCamera() {
        worldCamera.zoom = MathUtils.lerp(worldCamera.zoom, targetZoom.floatValue(), ZOOM_LERP);
        worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, MIN_ZOOM, MAX_ZOOM);

        worldCamera.position.x = MathUtils.lerp(worldCamera.position.x, cameraTargetPos.x, PAN_LERP);
        worldCamera.position.y = MathUtils.lerp(worldCamera.position.y, cameraTargetPos.y, PAN_LERP);
        worldCamera.update();
    }

//

}
