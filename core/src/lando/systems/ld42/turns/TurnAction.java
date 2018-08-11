package lando.systems.ld42.turns;

public class TurnAction {

    public Turn turn;
    public int turnNumber;

    public TurnAction() {
        this.turn = turn.PLAYER;
        turnNumber = 1;
    }

    public void doAction() {
        nextTurn();
    }

    public void nextTurn() {
        if (turn == turn.PLAYER) {
            turn = turn.ENEMY;
        } else {
            turn = turn.PLAYER;
            turnNumber++;
        }
    }

}
