package lando.systems.ld42.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld42.Assets;
import lando.systems.ld42.accessors.ColorAccessor;

public class TitleScreen extends BaseScreen {

    public MutableFloat clickTextScale;
    public Color clickTextColor;

    public TitleScreen() {
        alpha = new MutableFloat(0);

        float scaleMax = 0.52f;
        float scaleMin = 0.48f;
        clickTextScale = new MutableFloat(scaleMin);
        clickTextColor = new Color(0xffa50044);
        Tween.to(clickTextColor, ColorAccessor.A, 0.33f)
             .target(1f)
             .repeatYoyo(-1, 0f)
             .start(game.tween);
        Tween.to(clickTextScale, -1, 0.33f)
             .target(scaleMax)
             .repeatYoyo(-1, 0f)
             .start(game.tween);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

//        if (Gdx.input.justTouched()) {
//            game.setScreen(new LevelSelectScreen());
//        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            batch.draw(game.assets.titleTexture, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);

            float width = hudCamera.viewportWidth * (2f / 3f);
            Assets.drawString(batch, "Click to start!", width, 30,
                              clickTextColor, clickTextScale.floatValue(),
                              game.assets.font, width, Align.left);
        }
        batch.end();
    }

}
