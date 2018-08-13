package lando.systems.ld42.utils;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.Config;
import lando.systems.ld42.accessors.RectangleAccessor;

public abstract class ModalWindow {

    private BitmapFont font;
    protected final float margin_top = 10f;
    protected final float margin_left = 10f;

    public float accum;
    public boolean isActive;
    public OrthographicCamera camera;

    protected boolean showText;
    protected Rectangle modalRect;
    protected Rectangle modalTarget;
    protected float touchDelay;
//    protected Button exitButton;


    public ModalWindow(OrthographicCamera camera) {
        this.font = LudumDare42.game.assets.font;
        this.accum = 0f;
        this.isActive = false;
        this.showText = false;
        this.camera = camera;

        float modal_width  = camera.viewportWidth - 2f * margin_left;
        float modal_height = (4f / 5f) * camera.viewportHeight;
        this.modalTarget = new Rectangle(
                camera.viewportWidth  / 2f - modal_width  / 2f,
                camera.viewportHeight / 2f - modal_height / 2f,
                modal_width, modal_height
        );
        this.modalRect = new Rectangle(modalTarget);
        this.touchDelay = 0f;
    }

    public boolean contains(Vector3 position) {
        return modalRect.contains(position.x, position.y);
    }

    public void show() {
        if (isActive) return;
        isActive = true;
        touchDelay = 0.5f;
        modalRect.set(
                camera.viewportWidth / 2f,
                camera.viewportHeight / 2f,
                0f, 0f
        );
        Tween.to(modalRect, RectangleAccessor.XYWH, 0.2f)
                .target(modalTarget.x, modalTarget.y, modalTarget.width, modalTarget.height)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        showText = true;
                    }
                })
                .start(LudumDare42.game.tween);
    }

    public void hide() {
        if (!isActive) return;
        showText = false;

        float modal_target_x = camera.viewportWidth / 2f;
        float modal_target_y = camera.viewportHeight / 2f;
        Tween.to(modalRect, RectangleAccessor.XYWH, 0.2f)
                .target(modal_target_x, modal_target_y, 0f, 0f)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        onHide();
                        isActive = false;
                    }
                })
                .start(LudumDare42.game.tween);
    }

    public void onHide() {

    }

    public void update(float dt) {
        // touch is handled in handleTouch
    }

    public void render(SpriteBatch batch) {
//        // Draw background
//        batch.setColor(0f, 0f, 0f, 0.95f);
//        batch.draw(Assets.whitePixel, modalRect.x, modalRect.y, modalRect.width, modalRect.height);
//
//        // Draw outline
//        batch.setColor(Color.WHITE);
//        Assets.defaultNinePatch.draw(batch, modalRect.x, modalRect.y, modalRect.width, modalRect.height);
        batch.setColor(Config.player_color);
        LudumDare42.game.assets.ninePatchTooltip.draw(batch, modalRect.x, modalRect.y, modalRect.width, modalRect.height);
        batch.setColor(Color.WHITE);

        renderWindowContents(batch);
    }

    protected abstract void renderWindowContents(SpriteBatch batch);

    public void handleTouch(float windowX, float windowY) {

    }

}
