package lando.systems.ld42.teams;

import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class PlayerTeam extends Team {

    public PlayerTeam(World world, Assets assets) {
        super(world);
        this.owner = Type.player;
        this.color = Config.player_color;
        this.castleTile = world.getTile(0, World.WORLD_HEIGHT / 2);
        this.castleTile.owner = Type.player;
        this.castleTile.type = Tile.Type.playerBase;
        TileUtils.getNeighbors(castleTile, world, neighbors);
        for (Tile t : neighbors){
            t.owner = Type.player;
            // prevents these tiles from being assigned a resource
            t.type = Tile.Type.empty;
        }
    }


}
