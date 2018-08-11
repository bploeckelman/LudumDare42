package lando.systems.ld42.teams;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.world.Castle;
import lando.systems.ld42.world.World;

public abstract class Team {

    public enum Type { player, enemy }

    public Color color;
    public World world;
    public Castle castle;
    public Array<Unit> units;

    public Team(World world) {
        this.world = world;
        this.units = new Array<Unit>();
    }

    public void update(float dt) {
        castle.update(dt);
        for (Unit unit : units) {
            unit.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        castle.render(batch);
        for (Unit unit : units) {
            unit.render(batch);
        }
    }

}
