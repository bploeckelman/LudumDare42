package lando.systems.ld42.world;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.utils.TileUtils;

public class Tile {
    public enum Type {
        none,
        forest,
        mountain,
        crystal,
        playerBase,
        enemyBase,
        empty
    }

    public static final float scale = 2f;
    public static float tileWidth = 64 * scale;
    public static float tileHeight = 32 * scale;

    public Color pickColor;
    public int col;
    public int row;
    public World world;
    public Vector2 position;
    public TextureRegion texture;
    public TextureRegion hexBase;
    public TextureRegion hexOverlay;
    public MutableFloat alpha;
    public Color renderColor;
    public Type type;
    public float animState;
    public boolean dead;
    public boolean animating;
    public Team.Type owner;
    public Unit occupant;
    public Array<Tile> tempNeighbors;
    private TextureRegion highlightKeyFrame;

    public Tile (int col, int row){
        this.world = World.THE_WORLD;
        this.col = col;
        this.row = row;
        this.renderColor = new Color(1,1,1,1);
        this.alpha = new MutableFloat(0);
        this.animState = 0f;
        this.position = new Vector2(TileUtils.getX(col, tileWidth), TileUtils.getY(row, col, tileHeight) - 120);
        this.pickColor = TileUtils.getColorFromPosition(row, col);
        int rand = MathUtils.random(4);
        switch (rand){
            case 0:
                this.texture = LudumDare42.game.assets.blankTile;
                break;
            case 1:
                this.texture = LudumDare42.game.assets.blankTile1;
                break;
            case 2:
                this.texture = LudumDare42.game.assets.blankTile2;
                break;
            case 3:
                this.texture = LudumDare42.game.assets.blankTile3;
                break;
            case 4:
                this.texture = LudumDare42.game.assets.blankTile4;
                break;
            default:
                this.texture = LudumDare42.game.assets.blankTile;

        }
        this.hexBase = LudumDare42.game.assets.whiteHex;
        this.hexOverlay = LudumDare42.game.assets.emptyHex;
        Timeline.createSequence()
                .pushPause((35 - (row + col))/15f)
                .beginParallel()
                .push(Tween.to(position, Vector2Accessor.Y, 1f)
                        .target(TileUtils.getY(row, col, tileHeight))
                        .ease(Back.OUT))
                .push(Tween.to(alpha, 1, 1f)
                        .target(1))
                .end()
                .start(LudumDare42.game.tween);
        this.type = Type.none;
        this.owner = Team.Type.none;
        this.occupant = null;
        this.dead = false;
        this.animating = false;
    }

    public void update(float dt) {
        animState += dt;
        highlightKeyFrame = LudumDare42.game.assets.tileHighlightAnimation.getKeyFrame(animState);
    }

    public void render(SpriteBatch batch){
        renderColor.a = alpha.floatValue();
        batch.setColor(renderColor);
        batch.draw(texture, position.x, position.y, tileWidth, tileHeight);

        // draw owner color
        if (owner != Team.Type.none) {
            Color teamColor = (owner == Team.Type.player) ? Config.player_color : Config.enemy_color;
            batch.setColor(new Color(teamColor.r, teamColor.g, teamColor.b, 0.4f * alpha.floatValue()));
            batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
        }

        batch.setColor(renderColor);
        renderType(batch);
    }

    public void renderType(SpriteBatch batch) {
        switch (type) {
            case forest:
                renderResource(batch, LudumDare42.game.assets.tree);
                break;
            case mountain:
                renderResource(batch, LudumDare42.game.assets.mountain);
                break;
            case crystal:
                renderResource(batch, LudumDare42.game.assets.gem);
                break;
            case playerBase:
            case enemyBase:
                Team team = this.world.getTeam(type);
                if (team != null) {
                    TextureRegion castle = team.getImage();
                    if (castle != null) {
                        batch.draw(castle, position.x, position.y, Tile.tileWidth, Tile.tileHeight);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void renderResource(SpriteBatch batch, TextureRegion image) {
        batch.draw(image, position.x, position.y, Tile.tileWidth, Tile.tileHeight);
    }

    public void renderAttackStats(SpriteBatch batch){
        batch.setColor(Color.WHITE);
        float center = position.x + tileWidth/2;
        float y = position.y + tileHeight + 42;
        LudumDare42.game.assets.ninePatchTooltip.draw(batch, position.x, position.y + tileHeight, Tile.tileWidth, 52);
        GlyphLayout layout = LudumDare42.game.assets.layout;

        int defensePower = TileUtils.calculateDefense(this, owner, world);
        int attackPower = TileUtils.calculateAttack(this, Team.Type.player, world);

        Assets.font.getData().setScale(.3f);
        layout.setText(Assets.font, ""+attackPower);
        batch.draw(LudumDare42.game.assets.sword, center - layout.width - 47, y - 30, 32, 32);
        Assets.drawString(batch, ""+attackPower, center - 5 - layout.width, y - 16 + layout.height/2f, Color.WHITE, .3f, Assets.font);

        layout.setText(Assets.font, ""+defensePower);
        batch.draw(LudumDare42.game.assets.shield, center + 5, y - 30, 32, 32);
        Assets.drawString(batch, ""+defensePower, center + 47, y - 16 + layout.height/2f, Color.WHITE, .3f, Assets.font);

    }

//    private void stuff() {
//        if (typeTexture != null) {
//            if (type == Type.playerBase || type == Type.enemyBase) {
//                Color color = (type == Type.playerBase) ? Config.player_color : Config.enemy_color;
//                color.a = alpha.floatValue();
//
//                batch.setColor(color);
//                batch.draw(hexBase, position.x, position.y, Tile.tileWidth, Tile.tileHeight);
//
//                batch.setColor(1f, 1f, 1f, alpha.floatValue());
//                batch.draw(hexOverlay, position.x, position.y, Tile.tileWidth, Tile.tileHeight);
//
//                batch.draw(typeTexture, position.x, position.y, Tile.tileWidth, Tile.tileHeight);
//            } else {
//
//            }
//        }
//    }

    public void renderPickBuffer(SpriteBatch batch){
        batch.setColor(pickColor);
        batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
    }

    public void renderHighlight(SpriteBatch batch, Color c){
        batch.setColor(c.r, c.g, c.b, alpha.floatValue());
        batch.draw(highlightKeyFrame, position.x, position.y, tileWidth, tileHeight);
    }

    public void killTile(){
        animating = true;
        Timeline.createSequence()
                .push(Tween.to(position, Vector2Accessor.Y, 1f)
                        .target(TileUtils.getY(row, col, tileHeight) + 80)
                        .ease(Back.IN))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        world.screen.particleSystem.addTileDestroyParticles(Tile.this);
                    }}))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        dead = true;
                    }
                }))
                .start(LudumDare42.game.tween);

    }

    // kind of temp
    public void resetResource() {
        switch (type) {
            case crystal:
            case forest:
            case mountain:
                type = Type.none;
                break;

        }
    }

}
