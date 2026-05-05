package U4;

/**
 * Enum som representerar de två spelarna i spelet samt ett värde för "ingen ägare".
 * @author Grupp 22
 */
public enum Player {
    NONE,
    PLAYER1,
    PLAYER2;

    /**
     * Hämtar motståndaren till den nuvarande spelaren.
     * @return Den andra spelarens Enum-värde (PLAYER1 ger PLAYER2 och vice versa).
     * @author Grupp 22
     */
    public Player getOpponent() {
        if (this == PLAYER1) return PLAYER2;
        if (this == PLAYER2) return PLAYER1;
        return NONE;
    }
}