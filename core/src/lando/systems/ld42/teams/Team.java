package lando.systems.ld42.teams;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.world.Castle;
import lando.systems.ld42.world.World;

public abstract class Team {

    public enum Type { none, player, enemy }

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
        for (int i = units.size - 1; i >= 0; --i) {
            Unit unit = units.get(i);
            unit.update(dt);
            if (unit.dead) {
                units.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        castle.render(batch);
        for (Unit unit : units) {
            unit.render(batch);
        }
    }

    public boolean isActionLeft() {
        for (Unit unit : units) {
            if (unit.actionAvailable > 0) {
                return true;
            }
        }
        return false;
    }

    public void replenishAction() {
        for (Unit unit : units) {
            unit.actionAvailable = unit.actionPoint;
        }
    }

}
