package lando.systems.ld42.turns;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.screens.GameScreen;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class EnemyAI {

    public enum Phase {Recruit, Move, RemoveTile, Squish, Finish}

    private Array<Tile> neighbors;
    public World world;
    public GameScreen screen;
    private float delay = 1f;
    public Phase phase;

    public EnemyAI(World world, GameScreen screen){
        this.neighbors = new Array<Tile>();
        this.world = world;
        this.screen = screen;
        phase = Phase.Recruit;

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

    }

    private void doMove(){
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
