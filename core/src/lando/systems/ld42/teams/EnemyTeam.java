package lando.systems.ld42.teams;

import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class EnemyTeam extends Team {

    public EnemyTeam(World world, Assets assets) {
        super(world);
        this.owner = Type.enemy;
        this.color = Config.enemy_color;
        this.castleTile = world.getTile(World.WORLD_WIDTH - 1, World.WORLD_HEIGHT / 2);
        this.castleTile.owner = Type.enemy;
        this.castleTile.type = Tile.Type.enemyBase;
        TileUtils.getNeighbors(castleTile, world, neighbors);
        for (Tile t : neighbors){
            t.owner = Type.enemy;
        }
    }

}
