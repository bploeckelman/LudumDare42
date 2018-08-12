package lando.systems.ld42.teams;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.units.*;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Castle;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class PlayerTeam extends Team {

    Array<Tile> neighbors;

    public PlayerTeam(World world, Assets assets) {
        super(world);
        this.owner = Type.player;
        this.color = Config.player_color;
        this.neighbors = new Array<Tile>();


        Tile castleTile = world.getTile(0, World.WORLD_HEIGHT / 2);
        castleTile.owner = Type.player;
        TileUtils.getNeighbors(castleTile, world, neighbors);
        for (Tile t : neighbors){
            t.owner = Type.player;
        }
        this.castle = new Castle(assets, Type.player, castleTile);
    }

}
