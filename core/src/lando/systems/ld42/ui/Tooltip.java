package lando.systems.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

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

    private float x,y;

    public void render(SpriteBatch batch){
        // Tooltip
        if (text == null || text.equals("")) return;

        tempVec3.set(x, y, 0);

        float tX = tempVec3.x;
        float tY = tempVec3.y;
        float backgroundX;
        float backgroundY;
        float stringTX ;
        float stringTY;

        backgroundX = 5;
        backgroundY = 5;

        if (tY < Config.window_height/2f){
            // Whatever, this gets us there
            backgroundY = World.THE_WORLD.screen.statusUI.boundsPlayerUnits.y - tooltipBackgroundHeight;
        }

        stringTX = backgroundX + TOOLTIP_TEXT_PADDING_X;
        stringTY = backgroundY + tooltipTextOffsetY;

        // DRAW
        batch.setColor(Color.WHITE);
        LudumDare42.game.assets.ninePatchTooltip.draw(batch, backgroundX, backgroundY, tooltipBackgroundWidth, tooltipBackgroundHeight);
        Assets.drawString(batch,
                text,
                stringTX,
                stringTY,
                Color.WHITE,
                TOOLTIP_TEXT_SCALE,
                Assets.font, TOOLTIP_MAX_WIDTH, Align.center);
    }

    public void setText(String text, Tile tile) {
        this.text = text;
        if (text != null) {
            tempVec3.set(tile.position.x, tile.position.y, 0f);
            World.THE_WORLD.screen.worldCamera.project(tempVec3);
            x = tempVec3.x;
            y = tempVec3.y;

            Assets.font.getData().setScale(TOOLTIP_TEXT_SCALE);
            LudumDare42.game.assets.layout.setText(Assets.font, text, Color.WHITE, TOOLTIP_MAX_WIDTH, Align.center, true);
            tooltipBackgroundHeight = LudumDare42.game.assets.layout.height + (TOOLTIP_TEXT_PADDING_Y * 2);
            tooltipBackgroundWidth = TOOLTIP_MAX_WIDTH + (TOOLTIP_TEXT_PADDING_X * 2);
            tooltipTextOffsetY = (LudumDare42.game.assets.layout.height + TOOLTIP_TEXT_PADDING_Y + TOOLTIP_TEXT_OFFSET_Y);
            Assets.font.getData().setScale(1f);
        }
    }
}
