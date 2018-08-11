package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import lando.systems.ld42.screens.*;

public class World {

    public static World THE_WORLD;
    public static final int WORLD_WIDTH = 15;
    public static final int WORLD_HEIGHT = 10;

    public Array<Tile> adjacentTiles;

    public Array<Tile> tiles;
    public Array<Player> players;

//    public final Array<ResourceIndicator> resIndicators = new Array<ResourceIndicator>(10);

    public Rectangle bounds;
    public GameScreen screen;

    public World(GameScreen screen){
        this.screen = screen;
        THE_WORLD = this;

        generateWorldTiles();
        bounds = new Rectangle(0, 0,(Tile.tileWidth) * WORLD_WIDTH * .75f, Tile.tileHeight * WORLD_HEIGHT);

        players = new Array<Player>();

        adjacentTiles = new Array<Tile>();
    }

    public void update(float dt){
        for (int i = players.size - 1; i >= 0; i--){
            Player p = players.get(i);
            if (p.dead){
                players.removeValue(p, true);
            } else {
                p.update(dt);
            }
        }


    }

    public void render(SpriteBatch batch){
        for (int i = tiles.size-1; i >= 0; i--){
            Tile t = tiles.get(i);
            t.render(batch);
        }

        for (Player player : players) {
            player.render(batch);
        }

        batch.setColor(Color.WHITE);
    }

    public void renderPickBuffer(SpriteBatch batch){
        for (int i = tiles.size-1; i >= 0; i--){
            Tile t = tiles.get(i);
            t.renderPickBuffer(batch);
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
//        // Now, assign biomes.
//        float relativeHeightAboveSeaLevel; // 0 - 1, one being the highest.  Height of 0 is bound to 0.
//        float typeStep = 1 / 6f;  // Number of biomes
//        for (OldTile tile : tiles) {
//            relativeHeightAboveSeaLevel = tile.height <= 0 ? 0 : tile.height / maxTileHeight;
//            // Clay, Dirt, Grass, Sand, Snow, Stone
//            TileType type;
//            if (tile.height <= -1){
//                type = TileType.Ocean;
//            } else if (relativeHeightAboveSeaLevel <= typeStep * 1) {
//                type = TileType.Sand;
//            } else if (relativeHeightAboveSeaLevel <= typeStep * 2) {
//                type = TileType.Clay;
//            } else if (relativeHeightAboveSeaLevel <= typeStep * 3) {
//                type = TileType.Grass;
//            } else if (relativeHeightAboveSeaLevel <= typeStep * 4) {
//                type = TileType.Dirt;
//            } else if (relativeHeightAboveSeaLevel <= typeStep * 5) {
//                type = TileType.Stone;
//            } else {
//                type = TileType.Snow;
//            }
//            tile.setType(type);
//            OldTile ul = getUpperLeftTile(tile.row, tile.col);
//            OldTile ur = getUpperRightTile(tile.row, tile.col);
//            int shadowmap = 0;
//            if (ul != null && ul.height > tile.height) shadowmap += 1;
//            if (ur != null && ur.height > tile.height) shadowmap += 2;
//            tile.addShadow(shadowmap);
//        }
    }

//    private float getTileHeight(int col, int row) {
//        float adjustedRow = (col % 2 == 0) ? row + 0.5f : row;
//        float depth = Math.min(adjustedRow / World.WORLD_WIDTH, 1f); // 0 to 1, 1 being furthest from the front
//        float basicHeight = ISLAND_FRONT_HEIGHT + (ISLAND_RISE * depth);
//        float noisePercent = (float)(osn.eval(col * HEIGHT_NOISE_SCALE, adjustedRow * HEIGHT_NOISE_SCALE) + 1) / 2; // -1 to 1
//        float noiseHeight = HEIGHT_NOISE_HEIGHT * noisePercent;
//        int quantHeight = (int) ((basicHeight + noiseHeight));
//        return quantHeight;
//    }

    public Array<Player> getPlayers(GridPoint2 location) {
        Array<Player> selectedPlayers = new Array<Player>(5);
        for(Player p : players) {
            if (p.row == location.y && p.col == location.x) {
                selectedPlayers.add(p);
            }
        }
        return selectedPlayers;
    }

    public Tile getTile(GridPoint2 location) {
        return getTile(location.x, location.y);
    }

    public Tile getTile(int col, int row){
        if (col < 0 || col >= WORLD_WIDTH) return null;
        if (row < 0 || row >= WORLD_HEIGHT) return null;
        int index = col + row * WORLD_WIDTH;
        if (index < 0 || index >= tiles.size) return null;
        return tiles.get(index);

    }
//
//    public void addStartPlayers(){
//        players.clear();
//        for (int i = 0; i < 4; i ++){
//            int row = 1;
//            int col = MathUtils.random(WORLD_WIDTH);
//            boolean alreadyOccupied = false;
//            for (Player player : players) {
//                if (player.row == row && player.col == col) {
//                    alreadyOccupied = true;
//                }
//            }
//            while ((getTile(row, col) == null || getTile(row, col).type == TileType.Ocean) || alreadyOccupied) {
//                alreadyOccupied = false;
//                row = 1;
//                col = MathUtils.random(WORLD_WIDTH);
//                for (Player player : players) {
//                    if (player.row == row && player.col == col) {
//                        alreadyOccupied = true;
//                    }
//                }
//            }
//            players.add(new Player(this, row, col));
//        }
//    }
}
