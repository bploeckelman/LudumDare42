package lando.systems.ld42.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.Config;

public class EndScreen extends BaseScreen{

    private String heading = "LD42";
    private String theme = "Made for Ludum Dare 42:\nTheme: ";
    private String thanks = "Thanks for playing our game!";
    private String developers = "Developed by:\nDoug Graham\nBrian Ploeckelman\nBrian Rossman\nJeffrey Hwang\nBrandon Humboldt";
    private String artists = "Art by:\nMatt Neumann\nLuke Bain\nTroy Sullivan";
    private String emotionalSupport = "Emotional Support:\nAsuka the Shiba";
    //TODO add song title
    private String music = "Music by:\nLuke Bain";
    private String libgdx = "Made with <3 and LibGDX";

    public EndScreen(){

    }


    @Override
    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new TitleScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch, boolean inTransition) {
        Gdx.gl.glClearColor(Config.background_color.r, Config.background_color.g, Config.background_color.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(LudumDare42.game.assets.titleTexture, 0, 0,hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.setColor(new Color(0f, 0f, 0f, .9f));
        batch.draw(LudumDare42.game.assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

        LudumDare42.game.assets.drawString(batch, heading, 0, hudCamera.viewportHeight - 10, Config.player_color, .8f, LudumDare42.game.assets.font, hudCamera.viewportWidth, Align.center);
        LudumDare42.game.assets.drawString(batch, theme, 0, hudCamera.viewportHeight - 60, Config.player_color, .35f, LudumDare42.game.assets.font, hudCamera.viewportWidth, Align.center);
        LudumDare42.game.assets.drawString(batch, developers, 0, hudCamera.viewportHeight - 120, Config.player_color, .3f, LudumDare42.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare42.game.assets.drawString(batch, emotionalSupport, 0, hudCamera.viewportHeight - 280, Config.player_color, .3f, LudumDare42.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare42.game.assets.drawString(batch, artists, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 120, Config.player_color, .3f, LudumDare42.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare42.game.assets.drawString(batch, music, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 220, Config.player_color, .3f, LudumDare42.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare42.game.assets.drawString(batch, libgdx, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 300, Config.player_color, .4f, LudumDare42.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare42.game.assets.drawString(batch, thanks, 0, 200, Config.player_color, .3f, LudumDare42.game.assets.font, hudCamera.viewportWidth, Align.center);

        batch.end();
    }
}
