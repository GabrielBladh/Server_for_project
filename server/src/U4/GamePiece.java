package U4;

/**
 * Abstrakt basklass för alla spelpjäser.
 * Definierar gemensamma egenskaper som ägare och typ.
 * @author Grupp 22
 */
public abstract class GamePiece {
    private Player owner;
    /**
     * Skapar en spelpjäs med en angiven ägare.
     * @param owner Spelaren som äger pjäsen.
     * @author Grupp 22
     */
    public GamePiece(Player owner) {
        this.owner = owner;
    }

    /**
     * Hämtar ägaren till pjäsen.
     * @return Ägaren (Player).
     * @author Grupp 22
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sätter en ny ägare till pjäsen (t.ex. vid överraskning).
     * @param owner Den nya ägaren.
     * @author Grupp 22
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Abstrakt metod som subklasser måste implementera för att identifiera pjästyp.
     * @return En strängrepresentation av typen.
     * @author Grupp 22
     */
    public abstract String getType();
}