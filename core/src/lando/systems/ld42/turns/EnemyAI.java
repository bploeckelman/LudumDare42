package lando.systems.ld42.turns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.screens.GameScreen;
import lando.systems.ld42.teams.EnemyTeam;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.units.*;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

import java.util.Collections;

public class EnemyAI {

    public enum Phase {Recruit, Move, RemoveTile, Squish, Finish}

    public static int turnsPerSquanch = 6;

    private Array<Tile> neighbors;
    private Array<Tile> tempTileArray;
    public World world;
    public GameScreen screen;
    private float delay = 1f;
    public Phase phase;
    public EnemyTeam enemyTeam;

    public EnemyAI(World world, GameScreen screen){
        this.neighbors = new Array<Tile>();
        this.tempTileArray = new Array<Tile>();
        this.world = world;
        this.screen = screen;
        phase = Phase.Recruit;
        enemyTeam = screen.enemyTeam;

    }

    public void update(float dt){
        delay -= dt;

        if (delay <= 0){
            delay = 1f;
            switch (phase){

                case Recruit:
                    doRecruit();
                    break;
                case Move:
                    doMove();
                    break;
                case RemoveTile:
                    doRemoveTile();
                    break;
                case Squish:
                    doSquish();
                    break;
                case Finish:
                    doFinish();
                    break;
            }

        }
    }

    private void doRecruit(){
        phase = Phase.Move;
        TileUtils.getNeighbors(enemyTeam.castleTile, world, neighbors);
        for (int i = neighbors.size-1; i >= 0; i--){
            Tile t = neighbors.get(i);
            if (t.owner != enemyTeam.owner || t.occupant != null){
                neighbors.removeIndex(i);
            }
        }

        while (neighbors.size > 0 && (enemyTeam.canBuildPeasant() || enemyTeam.canBuildSoldier() || enemyTeam.canBuildArcher() || enemyTeam.canBuildWizard())){
            Tile buildTile = neighbors.random();
            if (enemyTeam.canBuildWizard()){
                enemyTeam.addUnit(new WizardUnit(LudumDare42.game.assets), buildTile);
                neighbors.removeValue(buildTile, true);
                continue;
            }
            if (enemyTeam.canBuildArcher()){
                enemyTeam.addUnit(new ArcherUnit(LudumDare42.game.assets), buildTile);
                neighbors.removeValue(buildTile, true);
                continue;
            }
            if (enemyTeam.canBuildSoldier()){
                enemyTeam.addUnit(new SoldierUnit(LudumDare42.game.assets), buildTile);
                neighbors.removeValue(buildTile, true);
                continue;
            }
            if (enemyTeam.canBuildPeasant()){
                enemyTeam.addUnit(new PeasantUnit(LudumDare42.game.assets), buildTile);
                neighbors.removeValue(buildTile, true);
                continue;
            }
        }
        enemyTeam.replenishAction();

    }

    private void doMove(){
        for (Unit unit : enemyTeam.units){
            if (unit.actionAvailable <= 0) continue;
            TileUtils.getNeighbors(unit.tile, world, neighbors);

            // Try to attack smartly
            if(tryToAttack(unit)) return;

            // Try to gain resources
            if(tryToGetResource(unit)) return;

            // Try to gain more land
            if (tryToGetMoreLand(unit)) return;

            // Move towards the enemy
            if (tryToMoveTowardsCastle(unit)) return;

            // RandomWander
            if (tryRandomWander(unit)) return;
        }

        delay = Unit.moveDuration + .5f;
        enemyTeam.removeLeftoverActions();
        phase = Phase.RemoveTile;

    }

    private boolean tryToAttack(Unit unit){
        tempTileArray.clear();
        for (Tile t : neighbors){
            if (t.occupant != null && t.occupant.team == Team.Type.player){ // find tile with enemy on it
                if (screen.willAttackSucceed(unit, t) >= 0) {
                    tempTileArray.add(t);
                }
            }
        }
        if (tempTileArray.size > 0){
            unit.actionAvailable--;
            screen.resolveAttack(unit, tempTileArray.random());
            delay = 1f;
            return true;
        }
        return false;
    }

    private boolean tryToGetResource(Unit unit){
        tempTileArray.clear();
        for (Tile t : neighbors){
            if (t.type != Tile.Type.none && t.owner != Team.Type.enemy && t.occupant == null){ // find empty unowned resource tile
                tempTileArray.add(t);
            }
        }
        if (tempTileArray.size > 0){
            moveUnit(unit, tempTileArray.random());
            return true;
        }
        return false;
    }

    private boolean tryToGetMoreLand(Unit unit) {
        tempTileArray.clear();
        for (Tile t : neighbors){
            if (t.owner != Team.Type.enemy && t.occupant == null){ // find empty tile that is not owned by enemy
                tempTileArray.add(t);
            }
        }
        if (tempTileArray.size > 0){
            moveUnit(unit, tempTileArray.random());
            return true;
        }
        return false;
    }
    private boolean tryToMoveTowardsCastle(Unit unit){
        tempTileArray.clear();
        for (Tile t : neighbors){
            if (t.col < unit.tile.col && t.occupant == null){ // find empty tile to the left
                tempTileArray.add(t);
            }
        }
        if (tempTileArray.size > 0){
            moveUnit(unit, tempTileArray.random());
            return true;
        }
        return false;
    }

    private boolean tryRandomWander(Unit unit) {
        tempTileArray.clear();
        for (Tile t : neighbors){
            if (t.occupant == null){ // Find empty tile to wander
                tempTileArray.add(t);
            }
        }
        if (tempTileArray.size > 0){
            moveUnit(unit, tempTileArray.random());
            return true;
        }
        return false;
    }

    private void moveUnit(Unit u, Tile t){
        u.moveTo(t);
        u.actionAvailable--;
        delay = Unit.moveDuration + .1f; // This needs to be longer than the tween, so the new tile is occupied

    }

    private void doRemoveTile(){
        screen.turnNumber++;
        delay = 4f;
        if (screen.turnNumber < 5 || screen.turnNumber > 20){
            world.pickRemoveTileCleverly();
        } else if (screen.turnNumber < 10 || screen.turnNumber > 15){
            world.pickRemoveTileCleverly();
            world.pickRemoveTileCleverly();
        } else {
            world.pickRemoveTileCleverly();
            world.pickRemoveTileCleverly();
            world.pickRemoveTileCleverly();
        }
        if (screen.turnNumber % turnsPerSquanch == 0){
            phase = Phase.Squish;
        } else {
            phase = Phase.Finish;
        }
    }

    private void doSquish() {
        world.squishHoles();
        screen.shaker.shakeDuration = 25f;
        screen.shaker.shake(2f);
        phase = Phase.Finish;
    }

    private void doFinish() {
        screen.turnAction.nextTurn();
        screen.selectedUnitTile = screen.playerTeam.castleTile;
        phase = Phase.Recruit;
    }

}
