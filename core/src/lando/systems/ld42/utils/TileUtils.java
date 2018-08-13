package lando.systems.ld42.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class TileUtils {

    public static float pickColorScale = 5f;
    public static Array<Tile> neighbors = new Array<Tile>();

    public static float getX(int col, float tileWidth) {
        float x = tileWidth * col * .75f;
        return x;
    }

    public static float getY(int row, int col, float tileHeight) {
        return tileHeight * (row + .5f * (col&1));
    }


    public static Color getColorFromPosition(int row, int col) {
        return new Color(
                (col * pickColorScale) / 255f,
                (row * pickColorScale) / 255f,
                0f, 1f);
    }

    public static Tile parsePickColorForTileInWorld(Color pickColor, World world) {
        if (pickColor.b > 0) return null;
        int col = (int) (pickColor.r * (255f / pickColorScale));
        int row = (int) (pickColor.g * (255f / pickColorScale));
        return world.getTile(col, row);
    }

    public static void getNeighbors(Tile t, World world, Array<Tile> neighbors){
        if (neighbors == null){
            throw new GdxRuntimeException("Need to pass in a valid neighbors object");
        }

        neighbors.clear();
        if (t == null) return;

        int colOffset = t.col % 2;
        Tile top = world.getTile(t.col, t.row -1);
        Tile bottom = world.getTile(t.col, t.row + 1);
        Tile topLeft = world.getTile(t.col - 1, t.row + -1 + colOffset);
        Tile bottomLeft = world.getTile(t.col - 1, t.row + colOffset);
        Tile topRight = world.getTile(t.col + 1, t.row + -1 + colOffset);
        Tile bottomRight= world.getTile(t.col + 1, t.row + colOffset);

        if (top != null) neighbors.add(top);
        if (bottom != null) neighbors.add(bottom);
        if (topLeft != null) neighbors.add(topLeft);
        if (bottomLeft != null) neighbors.add(bottomLeft);
        if (topRight != null) neighbors.add(topRight);
        if (bottomRight != null) neighbors.add(bottomRight);
    }

    public static Tile getTopNeighbor(int col, int row, World world){
        return  world.getTile(col, row -1);
    }

    public static Tile getTopRightNeighbor(int col, int row, World world){
        int colOffset = col %2;
        return  world.getTile(col + 1, row + -1 + colOffset);
    }

    public static int calculateDefense(Tile currentTile, Team.Type team, World world) {
        int defense = 0;
        neighbors.clear();
        TileUtils.getNeighbors(currentTile, world, neighbors);
        if (currentTile.occupant != null && currentTile.occupant.team == team) {
            defense += currentTile.occupant.defensePower;

            for (Tile tile : neighbors) {
                if (tile.occupant != null && tile.occupant.team == team) {
                    defense += tile.occupant.defensePower;
                }
            }
        }
        return defense;
    }

    public static int calculateAttack(Tile currentTile, Team.Type team, World world) {
        int attack = 0;
        neighbors.clear();
        TileUtils.getNeighbors(currentTile, world, neighbors);
        for (Tile tile : neighbors) {
            if (tile.occupant != null && tile.occupant.team == team) {
                attack += tile.occupant.attackPower;
            }
        }
        return attack;
    }
}
