package lando.systems.ld42.world;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld42.Assets;

public class Tile extends GameObject {

    public static MutableFloat renderShift = new MutableFloat(-120);

    //public TileType type;
    public TextureRegion top_tex;
    public TextureRegion bottom_tex;

    //public TextureRegion decoration_tex;
    //public Decoration decoration;
    public TextureRegion shadow_tex;
    public Color pickColor;
    public TextureRegion overlayObjectTex = null;
    public TextureRegion item;
    public boolean isHighlighted = false;
    public boolean isInaccessible = false;
    public boolean isMoveTarget = false;
    public boolean isBuildTarget = false;
    public boolean isWaitTarget = false;

    public Tile(int col, int row, float height) {
        super(col, row, height);
        pickColor = Tile.getColorFromPosition(row, col);
        //decoration = Decoration.None;
        //decoration_tex = null;
        top_tex = assets().defaultHex;
        addShadow(1);
    }

//    public void setType(TileType type){
//        this.type = type;
//        this.top_tex = type.top_tex;
//        this.bottom_tex = type.bottom_tex;
//        this.decoration = type.availableDecorations.random();
//        this.decoration_tex = this.decoration.tex;
//        if (this.decoration == Decoration.Tree) {
//            int treeType = MathUtils.random(2);
//            switch (treeType) {
//                case 0: this.decoration_tex = Assets.palmtree1; break;
//                case 1: this.decoration_tex = Assets.palmtree2; break;
//                case 2: this.decoration_tex = Assets.palmtree3; break;
//            }
//        }
//    }

    public void addShadow(int type){
        shadow_tex = null;
        switch (type){
            case 1:
                shadow_tex = assets().shadowUL;
                break;
            case 2:
                shadow_tex = assets().shadowUR;
                break;
            case 3:
                shadow_tex = assets().shadowU;
                break;
        }
    }

    public void render(SpriteBatch batch, float x, float y) {
        render(batch, x, y, false);
    }

    public void render(SpriteBatch batch, float x, float y, boolean asPickBuffer){
        float selectAlpha = .75f + (float)Math.sin((x) * 6f) * .25f;
        TextureRegion bottomTex = bottom_tex;
        TextureRegion topTex = top_tex;
        Color texColor = Color.WHITE;
        if (asPickBuffer) {
            texColor = pickColor;
            bottomTex = assets().whiteHex;
            topTex = assets().whiteHex;
        }

        batch.setColor(texColor);

//        if (asPickBuffer){
//            for (int yOffset = (int)waterHeight; yOffset < heightOffset; yOffset += 10) {
//                batch.draw(bottomTex, x, y + yOffset, tileWidth, tileHeight);
//            }
//        }

        batch.draw(topTex, x, y, tileWidth, tileHeight);
        batch.setColor(Color.WHITE);

        if (asPickBuffer) {
            return;
        }

        if (isHighlighted) {
            batch.setColor((isInaccessible) ? Color.RED : Color.CYAN);
            batch.draw(assets().selectHex, x, y, tileWidth, tileHeight);
            batch.setColor(Color.WHITE);
        }
        if (isMoveTarget) {
            batch.setColor(0,1,0,selectAlpha);
            batch.draw(assets().selectHex, x, y, tileWidth, tileHeight);
            batch.setColor(Color.WHITE);
        }
        if (isBuildTarget) {
            batch.setColor(1, .75f, 0, selectAlpha);
            batch.draw(assets().selectHex, x, y, tileWidth, tileHeight);
            batch.setColor(Color.WHITE);
        }
        if (isWaitTarget){
            batch.setColor(1,0,1,selectAlpha);
            batch.draw(assets().selectHex, x, y, tileWidth, tileHeight);
            batch.setColor(Color.WHITE);
        }
//        if (!decoration.equals(Decoration.None) && (aboveWater && heightOffset > waterHeight)) {
//            if (decoration_tex != null) {
//                batch.draw(decoration_tex, x, y + heightOffset + (tileHeight * .35f), tileWidth, tileHeight);
//            }
//        }
        if (isInaccessible && overlayObjectTex != null) {
            batch.draw(overlayObjectTex, x, y, tileWidth, tileHeight);
        }

        for (Player p : world.players){
            if (p.row == row && p.col == col) { // && !p.moving) {
                p.render(batch);
            }
        }
        batch.setColor(Color.WHITE);

    }

    public void renderPickBuffer(SpriteBatch batch) {
        float x = col * tileWidth;
        float y = row * tileHeight * .75f;
        if (row % 2 == 0) x += tileWidth / 2f;
        render(batch, x, y, true);
    }

    public static Color getColorFromPosition(int row, int col) {
        return new Color(
                (col * 5f) / 255f,
                (row * 5f) / 255f,
                0f, 1f);
    }

    public static Tile parsePickColorForTileInWorld(Color pickColor, World world) {
        int col = (int) (pickColor.r * (255f / 5f));
        int row = (int) (pickColor.g * (255f / 5f));
        return world.getTile(row, col);
    }
}
