package lando.systems.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld42.LudumDare42;

public class Button {

    private static final Color DISABLED_COLOR = new Color(120f / 255f, 120f / 255f, 118f / 255f, 1f);
    private static final float DISABLED_ALPHA = 0.8f;

    public TextureRegion buttonTexture;
    public TextureRegion buttonTexturePressed;
    private NinePatch ninePatch;
    public Rectangle bounds;

    protected OrthographicCamera camera;
    private Vector2 touchPosScreen = new Vector2();
    public boolean enabled = true;
    Vector3 tempVec3 = new Vector3();

    public boolean selected;
    public boolean isHover;
    public boolean noHover;

    // Constructors ----------------------------------------------------------------------------------------------------


    public Button(TextureRegion region, Rectangle bounds, OrthographicCamera camera) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.buttonTexture = region;
        this.buttonTexturePressed = null;
        this.ninePatch = null;
    }

    public Button(TextureRegion region, OrthographicCamera camera, float x, float y) {
        this.buttonTexture = region;
        this.buttonTexturePressed = null;
        this.bounds = new Rectangle(x, y, region.getRegionWidth(), region.getRegionHeight());
        this.camera = camera;
        this.ninePatch = null;
    }


    // Update & Render -------------------------------------------------------------------------------------------------

    public void render(SpriteBatch batch) {
        // Button texture
        if (buttonTexture != null) {
            batch.draw(buttonTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        } else if (ninePatch != null) {
            ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        if (selected) {
            if (buttonTexturePressed != null) {
                batch.draw(buttonTexturePressed, bounds.x, bounds.y, bounds.width, bounds.height);
            } else {
                batch.setColor(217f / 255f, 126f / 255f, 0f, 0f);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
        if (!enabled) {
            highlight(batch, DISABLED_COLOR, DISABLED_ALPHA);
        } else {
            if (isHover && !noHover) {
                batch.setColor(217f / 255f, 126f / 255f, 0f, 0f);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    private void highlight(SpriteBatch batch, Color color, float alpha) {
        Color batchColor = batch.getColor();
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(LudumDare42.game.assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(batchColor);
    }

    public void update(float dt) {}

    // -----------------------------------------------------------------------------------------------------------------


    public boolean checkForTouch(int screenX, int screenY) {
        Vector3 touchPosUnproject = camera.unproject(tempVec3.set(screenX, screenY, 0));
        touchPosScreen.set(touchPosUnproject.x, touchPosUnproject.y);
        return  bounds.contains(touchPosScreen.x, touchPosScreen.y);
    }}
