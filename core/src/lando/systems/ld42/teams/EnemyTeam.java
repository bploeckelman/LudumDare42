package lando.systems.ld42.teams;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.units.*;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Castle;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class EnemyTeam extends Team {

    Array<Tile> neighbors;


    public EnemyTeam(World world, Assets assets) {
        super(world);

        this.owner = Type.enemy;

        this.color = Config.enemy_color;
        this.neighbors = new Array<Tile>();


        Tile castleTile = world.getTile(World.WORLD_WIDTH - 1, World.WORLD_HEIGHT / 2);
        castleTile.owner = Type.enemy;
        TileUtils.getNeighbors(castleTile, world, neighbors);
        for (Tile t : neighbors){
            t.owner = Type.enemy;
        }
        this.castle = new Castle(assets, Type.enemy, castleTile);
    }
}
