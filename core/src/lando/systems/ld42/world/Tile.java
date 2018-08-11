package lando.systems.ld42.world;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld42.Config;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.utils.TileUtils;

public class Tile {
    public enum TileType {
        None,
        forest,
        mountain,
        playerBase,
        enemyBase
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
    public MutableFloat alpha;
    public Color renderColor;
    public TileType type;

    /**
     * Owner of the tile 0 - unclaimed, 1 - player, 2 - computer
     */
    public int owner;

    public Tile (int col, int row){
        this.world = World.THE_WORLD;
        this.col = col;
        this.row = row;
        renderColor = new Color(1,1,1,1);
        alpha = new MutableFloat(0);
        this.position = new Vector2(TileUtils.getX(col, tileWidth), TileUtils.getY(row, col, tileHeight) - 120);
        pickColor = TileUtils.getColorFromPosition(row, col);
        texture = LudumDare42.game.assets.blankTile;
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

        type = TileType.None;
    }


    public void render(SpriteBatch batch){
        renderColor.a = alpha.floatValue();
        batch.setColor(renderColor);
        batch.draw(texture, position.x, position.y, tileWidth, tileHeight);

        if (alpha.floatValue() >= 1f) {
            if (owner == 1) {
                batch.setColor(new Color(Config.player_color.r, Config.player_color.g, Config.player_color.b, .3f));
                batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
            }
            if (owner == 2) {
                batch.setColor(new Color(Config.enemy_color.r, Config.enemy_color.g, Config.enemy_color.b, .3f));
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
            default: break;
        }

        if (typeTexture != null) {
            int typeXTexOffset = texture.getRegionWidth() / 2;
            int typeYTextOffset = texture.getRegionHeight() / 2;
            batch.draw(typeTexture, position.x + typeXTexOffset, position.y + typeYTextOffset);
        }
    }

    public void renderPickBuffer(SpriteBatch batch){
        batch.setColor(pickColor);
        batch.draw(LudumDare42.game.assets.whiteHex, position.x, position.y, tileWidth, tileHeight);
    }

    public void renderHightlight(SpriteBatch batch, Color c){
        batch.setColor(c);
        batch.draw(LudumDare42.game.assets.hightlightHex, position.x, position.y, tileWidth, tileHeight);
    }

}
