package lando.systems.ld42.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Vector2Accessor;
import lando.systems.ld42.screens.*;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.utils.TileUtils;

public class World {

    public static World THE_WORLD;
    public static final int WORLD_WIDTH = 11;
    public static final int WORLD_HEIGHT = 7;

    public Array<Tile> adjacentTiles;
    public Array<Tile> tiles;
    public Rectangle bounds;
    public GameScreen screen;

    public int enemyTileCount;
    public int playerTileCount;
    public int unclaimedTileCount;

    public World(GameScreen screen){
        this.screen = screen;
        THE_WORLD = this;

        generateWorldTiles();
        bounds = new Rectangle(0, 0,(Tile.tileWidth) * WORLD_WIDTH * .75f, Tile.tileHeight * WORLD_HEIGHT);

        adjacentTiles = new Array<Tile>();
        tiles.get(0).owner = Team.Type.player;
        tiles.get(tiles.size-1).owner = Team.Type.enemy;

        unclaimedTileCount = tiles.size - 2;
        enemyTileCount = 1;
        playerTileCount = 1;
    }

    public void update(float dt){
        int maxCol = 0;
        int maxRow = 0;
        for (int i = 0; i < tiles.size; i++){
            Tile t = tiles.get(i);
            if (t != null) {
                if (t.dead) {
                    tiles.set(i, null);
                }
                maxCol = Math.max(maxCol, t.col);
                maxRow = Math.max(maxRow, t.row);
            }
        }
        bounds.setWidth(Tile.tileWidth * (maxCol+1.5f) * .75f);
        bounds.setHeight(Tile.tileHeight * (maxRow+2));
    }

    public void render(SpriteBatch batch){
        for (int i = tiles.size-1; i >= 0; i--){
            Tile t = tiles.get(i);
            if (t != null){
                t.render(batch);
            }
        }

        batch.setColor(Color.WHITE);

    }

    public void renderPickBuffer(SpriteBatch batch){
        for (int i = tiles.size-1; i >= 0; i--){
            Tile t = tiles.get(i);
            if (t != null) {
                t.renderPickBuffer(batch);
            }
        }
        batch.setColor(Color.WHITE);
    }


    public Tile getUpperLeftTile(int row, int col){
        int offset = row % 2 == 1 ? -1 : 0;
        return getTile(col + 1, row + offset);
    }

    public Tile getUpperRightTile(int row, int col){
        int offset = row % 2 == 1 ? 0 : 1;
        return getTile(col + 1, row + offset);
    }

    private void generateWorldTiles() {
        tiles = new Array<Tile>(WORLD_WIDTH * WORLD_HEIGHT );
        // Create the tiles.
        for (int row = 0; row < WORLD_HEIGHT; row++){
            for (int col = 0; col < WORLD_WIDTH; col++){
                tiles.add(new Tile(col, row));
            }
        }

        randomAssignTileType();
    }

    public void randomAssignTileType() {
        for (Tile tile : tiles) {
            int randIndex = MathUtils.random(0, 2);
            tile.type = getTileTypeFromInt(randIndex);
        }
    }

    public Tile.TileType getTileTypeFromInt(int num) {
        Tile.TileType type = Tile.TileType.None;
        switch (num) {
            case 0: type = Tile.TileType.forest; break;
            case 1: type = Tile.TileType.mountain; break;
            default: break;
        }

        return type;
    }

    public Tile getTile(GridPoint2 location) {
        return getTile(location.x, location.y);
    }

    public int getTileIndex(Tile t){
        if (t == null) return -1;
        return t.col + t.row * WORLD_WIDTH;
    }

    public Tile getTile(int col, int row){
        if (col < 0 || col >= WORLD_WIDTH) return null;
        if (row < 0 || row >= WORLD_HEIGHT) return null;
        int index = col + row * WORLD_WIDTH;
        if (index < 0 || index >= tiles.size) return null;
        return tiles.get(index);

    }

    public void removeTile(Tile t){
        if (t.occupant != null) {
            t.occupant.tileGotSquanched();
        }
        t.killTile();
    }

    Array<Tile> unclaimedTiles = new Array<Tile>();
    public void pickRemoveTile(){
        Tile removeTile = null;
        unclaimedTiles.clear();
        for (Tile t : tiles){
            if (t != null && t.owner == Team.Type.none){
                unclaimedTiles.add(t);
            }
        }

        Array<Tile> tilesToRemove = tiles;
        if (unclaimedTiles.size > 0) {
            tilesToRemove = unclaimedTiles;
        }

        if (tilesToRemove.size < 2) return;
        while (removeTile == null) {
            int index = MathUtils.random(tilesToRemove.size - 1);
            removeTile = tilesToRemove.get(index);
        }
        removeTile(removeTile);
    }

    public void moveTile(Tile t, int col, int row){
        float x = TileUtils.getX(col, Tile.tileWidth);
        float y = TileUtils.getY(row, col, Tile.tileHeight);
        int oldIndex = getTileIndex(t);
        Tween.to(t.position, Vector2Accessor.XY, 1f)
                .target(x, y)
                .start(LudumDare42.game.tween);
        t.row = row;
        t.col = col;
        t.pickColor = TileUtils.getColorFromPosition(row, col);
        tiles.set(col + row * WORLD_WIDTH, t);
        tiles.set(oldIndex, null);
        if (t.occupant != null) {
            t.occupant.moveTo(t);
        }
    }

    public void squishHoles(){
        for (int fixthis = 0; fixthis < 5; fixthis++){
            for (int row = 0; row < WORLD_HEIGHT; row ++) {
                for (int col = 0; col < WORLD_WIDTH; col++) {
                    Tile t = getTile(col, row);
                    if (t == null) {
                        boolean emptyCol = row == 0;
                        for (int y = row + 1; y < WORLD_HEIGHT; y++) {
                            Tile next = getTile(col, y);
                            if (next != null) {
                                moveTile(next, col, row);
                                emptyCol = false;
                                break;
                            }
                        }
                        if (emptyCol) {
                            for (int y = 0; y < WORLD_HEIGHT; y++) {
                                Tile rightTile = getTile(col + 1, y);
                                if (rightTile != null) {
                                    moveTile(rightTile, col, y);
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}
