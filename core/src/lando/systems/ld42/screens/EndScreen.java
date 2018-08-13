package lando.systems.ld42.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.Config;
import lando.systems.ld42.turns.TurnStats;

public class EndScreen extends BaseScreen{

    private static int TURN_HASH = 5;
    private static float ANIMATION_TIME = 2f;

    private String heading = "LD42";
    private String theme = "Made for Ludum Dare 42:\nTheme: Running out of Space";
    private String thanks = "Thanks for playing our game!";
    private String developers = "Developed by:\nDoug Graham\nBrian Ploeckelman\nBrian Rossman\nJeffrey Hwang\nBrandon Humboldt";
    private String artists = "Art by:\nMatt Neumann\nLuke Bain\nTroy Sullivan";
    private String emotionalSupport = "Emotional Support:\nAsuka the Shiba";
    //TODO add song title
    private String music = "Music by:\nLuke Bain";
    private String libgdx = "Made with <3 and LibGDX";

    private float animTimer;
    private Rectangle graphBounds;

    public EndScreen(){
        animTimer = 0;
        graphBounds = new Rectangle(100, 10, hudCamera.viewportWidth - 200, 100);
    }


    @Override
    public void update(float dt) {
        animTimer += dt;

        if (Gdx.input.justTouched()) {
            //enable this for testing only
//            game.setScreen(new TitleScreen());
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

        if (inTransition){
            animTimer = 0;
        } else {
            renderGraph(batch);
        }

    }

    private void renderGraph(SpriteBatch batch){

        TurnStats stats = TurnStats.getTurnStats();
        int numberOfTurns = stats.enemyOwnedTilesByTurn.keySet().size();
        if (numberOfTurns <= 1) return;

        ShapeRenderer sr = LudumDare42.game.assets.shapes;
        sr.setProjectionMatrix(hudCamera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        float dx = graphBounds.width / (numberOfTurns - 1);

        sr.setColor(Color.GRAY);
        sr.rect(graphBounds.x - 5, graphBounds.y - 5, graphBounds.width + 10, graphBounds.height+ 10);

        // Turn hashes
        sr.setColor(177/255f, 185/255f, 166/255f, 1f);
        for (int i = TURN_HASH; i < numberOfTurns; i += TURN_HASH){
            drawDashedLine(sr, graphBounds.x + (i * dx), graphBounds.y - 5,
                    graphBounds.x + (i * dx), graphBounds.y + graphBounds.height + 10,
                    20, .2f);
        }

        float graphPercent = animTimer / ANIMATION_TIME;
        int lastIndex = (int)(numberOfTurns * graphPercent) + 1;
        for (int i = 1; i <= lastIndex; i++) {
            if (i == numberOfTurns) break;
            float lerpPercent = 1f;
            if (i == lastIndex){
                float dt = 1f / numberOfTurns;
                lerpPercent = (graphPercent % dt) / dt;
            }

            int lastTurnPlayer = stats.playerOwnedTilesByTurn.get(i);
            int thisTurnPlayer = stats.playerOwnedTilesByTurn.get(i+1);
            int lastTurnEnemy = stats.enemyOwnedTilesByTurn.get(i);
            int thisTurnEnemy = stats.enemyOwnedTilesByTurn.get(i+1);
            int lastUnowned = stats.unownedTilesByTurn.get(i);
            int thisUnowned = stats.unownedTilesByTurn.get(i+1);
            float lastTotal = lastTurnEnemy + lastTurnPlayer + lastUnowned;
            float thisTotal = thisTurnEnemy + thisTurnPlayer + thisUnowned;

            float x1 = graphBounds.x + (dx * (i - 1));
            float x2 = x1 + dx;
            float y1 = 0;
            float y2 = 0;

            { // Player
                y1 = graphBounds.y + lastTurnPlayer / lastTotal * graphBounds.height;
                y2 = graphBounds.y + thisTurnPlayer / thisTotal * graphBounds.height;

                x2 = MathUtils.lerp(x1, x2, lerpPercent);
                y2 = MathUtils.lerp(y1, y2, lerpPercent);

                sr.setColor(Config.player_color);
                sr.line(x1, y1, x2, y2);
            }

            { // Enemy
                y1 = graphBounds.y + graphBounds.height - (lastTurnEnemy / lastTotal * graphBounds.height);
                y2 = graphBounds.y + graphBounds.height - (thisTurnEnemy / thisTotal * graphBounds.height);

                x2 = MathUtils.lerp(x1, x2, lerpPercent);
                y2 = MathUtils.lerp(y1, y2, lerpPercent);

                sr.setColor(Config.enemy_color);
                sr.line(x1, y1, x2, y2);
            }
        }


        sr.end();
    }

    public void drawDashedLine(ShapeRenderer shapes, float x1, float y1, float x2, float y2, int numDashes, float dashSize) {
        for (int i = 0; i < numDashes; i++) {
            float start = (float)i / (float)numDashes;
            float end = (i + dashSize) / (float)numDashes;
            shapes.line(x1 + (x2 - x1) * start, y1 + (y2 - y1) * start,
                    x1 + (x2 - x1) * end, y1 + (y2 - y1) * end);
        }
    }
}
