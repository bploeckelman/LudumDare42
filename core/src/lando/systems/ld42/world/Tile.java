package lando.systems.ld42.world;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

    public Tile (int col, int row){
        this.world = World.THE_WORLD;
        this.col = col;
        this.row = row;
        this.renderColor = new Color(1,1,1,1);
        this.alpha = new MutableFloat(0);
        this.animState = 0f;
        this.position = new Vector2(TileUtils.getX(col, tileWidth), TileUtils.getY(row, col, tileHeight) - 120);
        this.pickColor = TileUtils.getColorFromPosition(row, col);
        this.texture = LudumDare42.game.assets.blankTile;
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
    }

    public void render(SpriteBatch batch){
        renderColor.a = alpha.floatValue();
        batch.setColor(renderColor);
        batch.draw(texture, position.x, position.y, tileWidth, tileHeight);

        if (alpha.floatValue() >= 1f) {
            if (owner == Team.Type.player) {
                batch.setColor(new Color(Config.player_color.r, Config.player_color.g, Config.player_color.b, .5f));
                batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
            }
            if (owner == Team.Type.enemy) {
                batch.setColor(new Color(Config.enemy_color.r, Config.enemy_color.g, Config.enemy_color.b, .5f));
                batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
            }
        }

        batch.setColor(renderColor);
        renderType(batch);
    }

    public void renderType(SpriteBatch batch) {
        TextureRegion typeTexture = null;
        switch (type) {
            case forest: typeTexture = LudumDare42.game.assets.tree; break;
            case mountain: typeTexture = LudumDare42.game.assets.mountain; break;
            case crystal: typeTexture = LudumDare42.game.assets.gem; break;
            case playerBase: {
                typeTexture = LudumDare42.game.assets.castleRaiseAnimationPlayer.getKeyFrame(animState);
            } break;
            case enemyBase: {
                typeTexture = LudumDare42.game.assets.castleRaiseAnimationEnemy.getKeyFrame(animState);
            } break;
            default: break;
        }

        if (typeTexture != null) {
            if (type == Type.playerBase || type == Type.enemyBase) {
                Color color = (type == Type.playerBase) ? Config.player_color : Config.enemy_color;
                color.a = alpha.floatValue();

                batch.setColor(color);
                batch.draw(hexBase, position.x, position.y, Tile.tileWidth, Tile.tileHeight);

                batch.setColor(1f, 1f, 1f, alpha.floatValue());
                batch.draw(hexOverlay, position.x, position.y, Tile.tileWidth, Tile.tileHeight);

                batch.draw(typeTexture, position.x, position.y, Tile.tileWidth, Tile.tileHeight);
            } else {
                int typeXTexOffset = texture.getRegionWidth() / 2;
                int typeYTextOffset = texture.getRegionHeight() / 2;
                batch.draw(typeTexture, position.x + typeXTexOffset, position.y + typeYTextOffset);
            }
        }
    }

    public void renderPickBuffer(SpriteBatch batch){
        batch.setColor(pickColor);
        batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
    }

    public void renderHighlight(SpriteBatch batch, Color c){
        batch.setColor(c.r, c.g, c.b, alpha.floatValue());
        batch.draw(LudumDare42.game.assets.highlightHex, position.x, position.y, tileWidth, tileHeight);
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
