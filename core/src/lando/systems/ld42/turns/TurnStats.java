package lando.systems.ld42.turns;

import com.badlogic.gdx.utils.IntArray;

import java.util.HashMap;

public class TurnStats {
    private static TurnStats turnStats;

    public static TurnStats getTurnStats(){
        if (turnStats == null){
            turnStats = new TurnStats();
        }
        return turnStats;
    }

    public HashMap<Integer, Integer> playerOwnedTilesByTurn;
    public HashMap<Integer, Integer> enemyOwnedTilesByTurn;
    public HashMap<Integer, Integer> unownedTilesByTurn;

    private TurnStats(){
        playerOwnedTilesByTurn = new HashMap<Integer, Integer>();
        enemyOwnedTilesByTurn = new HashMap<Integer, Integer>();
        unownedTilesByTurn = new HashMap<Integer, Integer>();

    }

    public void addTileStats(int turnNumber, int playerTiles, int enemyTiles, int unownedTiles){
        playerOwnedTilesByTurn.put(turnNumber, playerTiles);
        enemyOwnedTilesByTurn.put(turnNumber, enemyTiles);
        unownedTilesByTurn.put(turnNumber, unownedTiles);
    }
}
