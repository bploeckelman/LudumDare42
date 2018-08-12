package lando.systems.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;

import static com.badlogic.gdx.Gdx.input;

public class Tooltip {
    private static final float TOOLTIP_TEXT_OFFSET_Y = 3f; // Catches characters with ligatures below the baseline
    private static final float TOOLTIP_TEXT_PADDING_X = 8f;
    private static final float TOOLTIP_TEXT_PADDING_Y = 8f;
    private static final float TOOLTIP_TEXT_SCALE = 0.3f;
    private static final float TOOLTIP_CURSOR_OFFSET_X = 8f;
    private static final float TOOLTIP_MAX_WIDTH = 250;

    public String text;
    private float tooltipBackgroundHeight;
    private float tooltipBackgroundWidth;
    private float tooltipTextOffsetY;
    private Vector3 tempVec3 = new Vector3();

    public void render(SpriteBatch batch, OrthographicCamera hudCamera){
        // Tooltip
        if (text == null || text.equals("")) return;

        tempVec3.set(input.getX(), input.getY(), 0);
        hudCamera.unproject(tempVec3);
        float tX = tempVec3.x;
        float tY = tempVec3.y;
        float backgroundX;
        float backgroundY;
        float stringTX ;
        float stringTY;

        // Screen space
        if (tX < Config.window_width / 2) {
            // left half of the screen: align left edge of tooltip at cursor
            backgroundX = tX;
            if (tY > Config.window_height / 2) {
                // Tooltip will appear under the cursor (bottom-right).  Offset it.
                backgroundX += TOOLTIP_CURSOR_OFFSET_X;
            }
        } else {
            // Right side of screen: align right edge of tooltip at cursor
            backgroundX = tX - tooltipBackgroundWidth;
        }
        stringTX = backgroundX + TOOLTIP_TEXT_PADDING_X;
        if (tY <= Config.window_height / 2) {
            // bottom half of screen: align bottom edge of tooltip with cursor
            backgroundY = tY;
        } else {
            // top half of screen: align top edge of tooltip with cursor
            backgroundY = tY - tooltipBackgroundHeight;
        }
        stringTY = backgroundY + tooltipTextOffsetY;

        // DRAW
        batch.setColor(Color.WHITE);
        Assets.tooltipNinePatch.draw(batch, backgroundX, backgroundY, tooltipBackgroundWidth, tooltipBackgroundHeight);
        Assets.drawString(batch,
                text,
                stringTX,
                stringTY,
                Color.WHITE,
                TOOLTIP_TEXT_SCALE,
                Assets.font, TOOLTIP_MAX_WIDTH, Align.center);
    }

    public void setText(String text) {
        this.text = text;
        if (text != null) {
            Assets.font.getData().setScale(TOOLTIP_TEXT_SCALE);
            LudumDare42.game.assets.layout.setText(Assets.font, text, Color.WHITE, TOOLTIP_MAX_WIDTH, Align.center, true);
            tooltipBackgroundHeight = LudumDare42.game.assets.layout.height + (TOOLTIP_TEXT_PADDING_Y * 2);
            tooltipBackgroundWidth = TOOLTIP_MAX_WIDTH + (TOOLTIP_TEXT_PADDING_X * 2);
            tooltipTextOffsetY = (LudumDare42.game.assets.layout.height + TOOLTIP_TEXT_PADDING_Y + TOOLTIP_TEXT_OFFSET_Y);
            Assets.font.getData().setScale(1f);
        }
    }
}
