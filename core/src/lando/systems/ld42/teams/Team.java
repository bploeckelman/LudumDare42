package lando.systems.ld42.teams;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.units.*;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.units.PeasantUnit;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public abstract class Team {

    public enum Type { none, enemy, player }

    public Color color;
    public World world;
    public Tile castleTile;
    public Array<Unit> units;
    public Type owner;
    public Array<Tile> buildSpots;
    protected Array<Tile> neighbors;

    public Team(World world) {
        this.buildSpots = new Array<Tile>();
        this.world = world;
        this.units = new Array<Unit>();
        this.neighbors = new Array<Tile>();
    }

    public void update(float dt) {
        for (int i = units.size - 1; i >= 0; --i) {
            Unit unit = units.get(i);
            unit.update(dt);
            if (unit.dead) {
                units.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Unit unit : units) {
            unit.render(batch);
        }
    }

    public boolean canBuildPeasant(){
        return getUnitCountPeasant() < getMaxPeasant();
    }
    public boolean canBuildSoldier(){
        return getUnitCountSoldier() < getTileTypeCount(Tile.Type.mountain);
    }
    public boolean canBuildArcher(){
        return getUnitCountArcher() < getTileTypeCount(Tile.Type.forest);
    }
    public boolean canBuildWizard(){
        return getUnitCountWizard() < getTileTypeCount(Tile.Type.crystal);
    }

    public boolean buildsLeft(){
        TileUtils.getNeighbors(castleTile, world, buildSpots);
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
        float tx = TileUtils.getX(t.col, Tile.tileWidth) + Tile.tileWidth / 2f - unit.size.x / 2f;
        float ty = TileUtils.getY(t.row, t.col, Tile.tileHeight)+ Tile.tileHeight - unit.size.y;
        unit.pos.set(tx, ty);
        unit.moveTo(t);
        units.add(unit);

    }

    public int getUnitCountPeasant(){
        int count = 0;
        for (Unit u : units){
            if (u instanceof PeasantUnit) {
                count++;
            }
        }
        return count;
    }
    public int getUnitCountSoldier(){
        int count = 0;
        for (Unit u : units){
            if (u instanceof SoldierUnit) {
                count++;
            }
        }
        return count;
    }
    public int getUnitCountArcher(){
        int count = 0;
        for (Unit u : units){
            if (u instanceof ArcherUnit) {
                count++;
            }
        }
        return count;
    }
    public int getUnitCountWizard(){
        int count = 0;
        for (Unit u : units){
            if (u instanceof WizardUnit) {
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
