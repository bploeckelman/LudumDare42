package lando.systems.ld42.turns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.screens.GameScreen;
import lando.systems.ld42.teams.EnemyTeam;
import lando.systems.ld42.units.ArcherUnit;
import lando.systems.ld42.units.PeasantUnit;
import lando.systems.ld42.units.SoldierUnit;
import lando.systems.ld42.units.WizardUnit;
import lando.systems.ld42.utils.TileUtils;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class EnemyAI {

    public enum Phase {Recruit, Move, RemoveTile, Squish, Finish}

    private Array<Tile> neighbors;
    public World world;
    public GameScreen screen;
    private float delay = 1f;
    public Phase phase;
    public EnemyTeam enemyTeam;

    public EnemyAI(World world, GameScreen screen){
        this.neighbors = new Array<Tile>();
        this.world = world;
        this.screen = screen;
        phase = Phase.Recruit;
        enemyTeam = screen.enemyTeam;

    }

    public void update(float dt){
        delay -= dt;

        if (delay <= 0){
            delay = 2f;
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
            if (enemyTeam.canBuildWizard()){
                enemyTeam.addUnit(new WizardUnit(LudumDare42.game.assets), neighbors.get(MathUtils.random(neighbors.size-1)));
            }
            if (enemyTeam.canBuildArcher()){
                enemyTeam.addUnit(new ArcherUnit(LudumDare42.game.assets), neighbors.get(MathUtils.random(neighbors.size-1)));
            }
            if (enemyTeam.canBuildSoldier()){
                enemyTeam.addUnit(new SoldierUnit(LudumDare42.game.assets), neighbors.get(MathUtils.random(neighbors.size-1)));
            }
            if (enemyTeam.canBuildPeasant()){
                enemyTeam.addUnit(new PeasantUnit(LudumDare42.game.assets), neighbors.get(MathUtils.random(neighbors.size-1)));
            }
        }

    }

    private void doMove(){
        // Try to attack smartly

        // Try to gain resources

        // Try to gain more land

        phase = Phase.RemoveTile;

    }

    private void doRemoveTile(){
        screen.turnNumber++;
        world.pickRemoveTileCleverly();
        if (screen.turnNumber % 8 == 0){
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

    public void endTurn(){
        screen.turnAction.nextTurn();
        screen.selectedUnitTile = screen.playerTeam.castleTile;
        screen.turnNumber++;
        world.pickRemoveTileCleverly();
        if (screen.turnNumber % 8 == 0) {
            world.squishHoles();
            screen.shaker.shakeDuration = 25f;
            screen.shaker.shake(2f);
        }
    }
}
