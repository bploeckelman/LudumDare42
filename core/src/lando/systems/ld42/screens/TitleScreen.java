package lando.systems.ld42.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.ColorAccessor;
import lando.systems.ld42.utils.HelpModalWindow;
import lando.systems.ld42.utils.Utils;

class Letter {
    String letter;
    float x;
    float y;
    float w;
    float h;
    float fy;
    public Letter(String letter, float x, float y, float w, float h, float fy) {
        this.letter = letter;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.fy = fy;
    }
}

public class TitleScreen extends BaseScreen {

    private final String title = "KINGDOMS FALL";
    private Rectangle titleBounds;
    private Assets assets;
    private BitmapFont titleFont;
    private GlyphLayout glyphLayout = new GlyphLayout();

    private float speed = 200;
    private float time;

    private Letter[] letters;

    private boolean falling = true;

    enum State {intro, outro, tutorial, end, done}
    private State state = State.intro;

    public boolean showTutorial;
    private HelpModalWindow helpModalWindow;

    public TitleScreen() {
        assets = LudumDare42.game.assets;
        titleFont = assets.titleFont;
        titleBounds = new Rectangle(0, hudCamera.viewportHeight,
                hudCamera.viewportWidth / 2, hudCamera.viewportHeight / 2);

        Gdx.input.setInputProcessor(this);

        glyphLayout.setText(titleFont, title, Color.WHITE, hudCamera.viewportWidth / 2, 1, true);

        float finalY = hudCamera.viewportHeight - (titleBounds.height - glyphLayout.height)/2;

        letters = new Letter[title.length()];
        int index = 0;
        float y = hudCamera.viewportHeight + glyphLayout.height + 20;
        for (int i = 0; i < glyphLayout.runs.size; i++) {
            GlyphLayout.GlyphRun run = glyphLayout.runs.get(i);
            float x = titleBounds.x + (titleBounds.width - run.width) / 2;
            for (BitmapFont.Glyph g: run.glyphs) {
                letters[index++] = new Letter(g.toString(), x, y, g.width, g.height, finalY);
                x += g.width;
            }
            y -= 60;
            finalY -= 60;
        }


        this.helpModalWindow = new HelpModalWindow(hudCamera);
        this.showTutorial = true;
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.justTouched()) {
            transition();
        }

        if (falling) {
            time += dt;
            int glyphCountUpdate = (int) (time / 0.2f);
            for (int i = 0; i < letters.length; i++) {
                if (glyphCountUpdate-- < 0) break;
                float dy = speed * dt;
                if ((letters[i].y - dy) > letters[i].fy) {
                    letters[i].y -= dy;
                } else {
                    letters[i].y = letters[i].fy;
                }
            }
            if (letters.length > 0) {
                Letter last = letters[letters.length - 1];
                falling = (last.y != last.fy);
                if (!falling) {
                    transition();
                }
            }
        } else if (state == State.outro) {
            time += dt;
            if (time >= 2) {
                transition();
            }
        } else if (state == State.tutorial) {
            transition();
        }
    }

    private void transition() {
        switch (state) {
            case intro:
                time = 0;
                if (falling) {
                    for (Letter l: letters) {
                        l.y = l.fy;
                    }
                }
                falling = false;
                state = State.outro;
                break;
            case outro:
                time = 0;
                for (Letter l: letters) {
                    l.fy = -100;
                    speed = 600;
                }
                falling = true;
                state = State.tutorial;
                break;
            case tutorial:
                letters = new Letter[]{};
                helpModalWindow.show();
                state = State.end;
                break;
            case end:
                game.setScreen(new GameScreen());
                state = State.done;
                break;

        }
    }

    @Override
    public void render(SpriteBatch batch, boolean inTransition) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.draw(game.assets.titleTexture, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);

            for (Letter l : letters) {
                Assets.drawString(batch, l.letter, l.x, l.y, Color.WHITE, 1, titleFont);
            }

            if (helpModalWindow.isActive) {
                helpModalWindow.render(batch);
            }
        }
        batch.end();
    }

}
