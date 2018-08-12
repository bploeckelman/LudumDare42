package lando.systems.ld42.teams;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld42.units.*;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Castle;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public abstract class Team {

    public enum Type { none, enemy, player }

    public Color color;
    public World world;
    public Castle castle;
    public Array<Unit> units;
    public Type owner;
    public Array<Tile> buildSpots;

    public Team(World world) {
        this.buildSpots = new Array<Tile>();
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

    public boolean canBuildPeasant(){
        return getUnitCount(PeasantUnit.class) < getMaxPeasant();
    }
    public boolean canBuildSoldier(){
        return getUnitCount(SoldierUnit.class) < getTileTypeCount(Tile.Type.mountain);
    }
    public boolean canBuildArcher(){
        return getUnitCount(ArcherUnit.class) < getTileTypeCount(Tile.Type.forest);
    }
    public boolean canBuildWizard(){
        return getUnitCount(WizardUnit.class) < getTileTypeCount(Tile.Type.crystal);
    }


    public boolean buildsLeft(){
        TileUtils.getNeighbors(castle.tile, world, buildSpots);
        boolean freeSpace = false;
        for (Tile t : buildSpots){
            if (t.occupant == null) freeSpace = true;
        }
        return freeSpace && (canBuildPeasant() || canBuildSoldier() || canBuildArcher() || canBuildWizard());
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
    public void removeLeftoverActions() {
        for (Unit unit : units) {
            unit.actionAvailable = 0;
        }
    }

    public void addUnit(Unit unit, Tile t){
        unit.shadowColor.set(this.color.cpy());
        unit.shadowColor.a = 0.75f;
        unit.team = owner;
        unit.pos.set(t.position);
        unit.moveTo(t);
        units.add(unit);

    }

    public int getUnitCount(Class unitType){
        int count = 0;
        for (Unit u : units){
            if (unitType.isInstance(u)){
                count++;
            }
        }
        return count;
    }

    public int getTileTypeCount(Tile.Type type){
        int count = 0;
        for (Tile tile : world.tiles){
            if (tile != null && tile.owner == this.owner && tile.type == type){
                count++;
            }
        }
        return count;
    }

    public int getTileTotalCount(){
        int count = 0;
        for (Tile tile : world.tiles){
            if (tile != null && tile.owner == this.owner){
                count++;
            }
        }
        return count;
    }

    public int getMaxPeasant(){
        return getTileTotalCount() /4;
    }

}
