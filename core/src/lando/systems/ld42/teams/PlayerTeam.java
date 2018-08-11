package lando.systems.ld42.teams;

import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.units.ArcherUnit;
import lando.systems.ld42.units.PeasantUnit;
import lando.systems.ld42.units.SoldierUnit;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.world.World;

public class PlayerTeam extends Team {

    public PlayerTeam(World world, Assets assets) {
        super(world);

        this.color = Config.player_color;

        Unit peasant = new PeasantUnit(assets);
        Unit soldier = new SoldierUnit(assets);
        Unit archer  = new ArcherUnit(assets);
        peasant.moveTo(world.getTile(0, World.WORLD_HEIGHT / 2 - 1));
        soldier.moveTo(world.getTile(1, World.WORLD_HEIGHT / 2 - 1));
        archer.moveTo(world.getTile(0, World.WORLD_HEIGHT / 2 - 2));

        this.units.add(peasant, soldier, archer);
        for (Unit unit : units) {
            unit.shadowColor.set(this.color.cpy());
            unit.shadowColor.a = 0.75f;
        }

        // TODO: set base tile
    }

}
