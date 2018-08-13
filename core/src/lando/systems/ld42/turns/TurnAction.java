package lando.systems.ld42.turns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld42.teams.EnemyTeam;
import lando.systems.ld42.teams.PlayerTeam;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.units.Unit;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class TurnAction {

    public Turn turn;
    public Unit unit;
    public Team playerTeam;
    public Team enemyTeam;
    public TurnAction(Team playerTeam, Team enemyTeam) {

        this.turn = turn.PLAYER_RECRUITMENT;
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
    }

    public void nextTurn() {
        Tile selectedTile = null;
        switch (turn) {
            case PLAYER_RECRUITMENT:
                this.turn = Turn.PLAYER_ACTION;
                playerTeam.replenishAction();
                break;
            case PLAYER_ACTION:
                playerTeam.removeLeftoverActions();
                this.turn = Turn.ENEMY;
                break;
            case ENEMY:
                if (playerTeam.buildsLeft()) {
                    selectedTile = playerTeam.castleTile;
                    this.turn = Turn.PLAYER_RECRUITMENT;
                } else {
                    playerTeam.replenishAction();
                    this.turn = Turn.PLAYER_ACTION;
                }
                break;
        }
        World.THE_WORLD.screen.selectedUnitTile = selectedTile;
    }

}
