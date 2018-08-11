package lando.systems.ld42;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import lando.systems.ld42.accessors.*;
import lando.systems.ld42.screens.BaseScreen;
import lando.systems.ld42.screens.TitleScreen;

public class LudumDare42 extends ApplicationAdapter {

    public static LudumDare42 game;

    public Audio audio;
    public Assets assets;
    public TweenManager tween;

    private BaseScreen screen;
    private BaseScreen nextScreen;
    private MutableFloat transitionPercent;
    private FrameBuffer transitionFBO;
    private FrameBuffer originalFBO;
    Texture originalTexture;
    Texture transitionTexture;
    ShaderProgram transitionShader;
    OrthographicCamera camera;

    @Override
    public void create () {
        camera = new OrthographicCamera(Config.window_width, Config.window_height);
        camera.translate(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
        camera.update();

        transitionPercent = new MutableFloat(0);
        transitionFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        transitionTexture = transitionFBO.getColorBufferTexture();

        originalFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        originalTexture = originalFBO.getColorBufferTexture();

        LudumDare42.game = this;

        if (tween == null) {
            tween = new TweenManager();
            Tween.setWaypointsLimit(4);
            Tween.setCombinedAttributesLimit(4);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        if (assets == null) {
            assets = new Assets();
        }

        setScreen(new TitleScreen());
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(Config.background_color.r, Config.background_color.g, Config.background_color.b, Config.background_color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
//        audio.update(dt);
        tween.update(dt);
        screen.update(dt);

        if (nextScreen != null) {
            nextScreen.update(dt);

            transitionFBO.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            nextScreen.render(assets.batch, true);
            transitionFBO.end();

            originalFBO.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            screen.render(assets.batch, true);
            originalFBO.end();

            assets.batch.setShader(transitionShader);
            assets.batch.setProjectionMatrix(camera.combined);
            assets.batch.begin();
            originalTexture.bind(1);
            transitionShader.setUniformi("u_texture1", 1);
            transitionTexture.bind(0);
            transitionShader.setUniformf("u_percent", transitionPercent.floatValue());
            assets.batch.setColor(Color.WHITE);
            assets.batch.draw(transitionTexture, 0, 0, Config.window_width, Config.window_height);
            assets.batch.end();
            assets.batch.setShader(null);
        } else {
            screen.render(assets.batch, false);
        }
    }

    public void setScreen(final BaseScreen newScreen){
        setScreen(newScreen, null, 1.4f);
    }

    public void setScreen(final BaseScreen newScreen, ShaderProgram transitionType, float transitionSpeed){
        if (nextScreen != null) return;
        if (screen == null) { // First time i hope
            screen = newScreen;
            Gdx.input.setInputProcessor(screen);
        } else { // transition
//            audio.playSound(Audio.Sounds.transition);
            Gdx.input.setInputProcessor(null);
            if (transitionType == null) {
                transitionShader = assets.randomTransitions.get(MathUtils.random(assets.randomTransitions.size-1));
            } else {
                transitionShader = transitionType;
            }
            screen.allowInput = false;
            transitionPercent.setValue(0);
            Timeline.createSequence()
                    .pushPause(.1f)
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            nextScreen = newScreen;
                        }
                    }))
                    .push(Tween.to(transitionPercent, 1, transitionSpeed)
                                  .target(1f))
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            screen = nextScreen;
                            nextScreen = null;
                            screen.allowInput = true;
                            Gdx.input.setInputProcessor(screen);
                        }
                    }))
                    .start(tween);
        }
    }

    @Override
    public void dispose () {
        tween = null;
        screen = null;
        assets.dispose();
    }

}
