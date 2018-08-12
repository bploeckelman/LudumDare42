package lando.systems.ld42.turns;

import lando.systems.ld42.units.Unit;

public class TurnAction {

    public Turn turn;
    public Unit unit;
    public TurnAction() {
        this.turn = turn.PLAYER;
    }

    public void doAction() {
        if (turn == turn.PLAYER) {
            //TODO: recruitment Action
            //TODO: move Character Action
            //TODO: resolution

        }
        else if (turn == turn.ENEMY) {
            //TODO: handle AI action
        }
        nextTurn();
    }

    private void nextTurn() {
        if (turn == turn.PLAYER) {
            turn = turn.ENEMY;
        } else {
            turn = turn.PLAYER;
        }
    }

}
