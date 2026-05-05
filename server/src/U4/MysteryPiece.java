package U4;

/**
 * Representerar ett Mysterium på spelplanen.
 * Kan aktiveras en gång när det blir överraskat.
 * @author Grupp 22
 */
public class MysteryPiece extends GamePiece {

    private boolean isActivated;
    private String mysteryType; // T.ex. "Tidshopp", "Narcissus"
    /**
     * Skapar ett mysterium av en viss typ.
     * @param owner Ägaren (oftast NONE vid start).
     * @param type Typen av mysterium (t.ex. "Tidshopp").
     * @author Grupp 22
     */
    public MysteryPiece(Player owner, String type) {
        super(owner);
        this.mysteryType = type;
        this.isActivated = false;
    }
    /**
     * Kollar om mysteriet redan har aktiverats.
     * @return true om det är aktiverat, annars false.
     * @author Grupp 22
     */
    public boolean isActivated() {
        return isActivated;
    }
    /**
     * Sätter status för om mysteriet är aktiverat.
     * @param activated true för att markera som aktiverad.
     * @author Grupp 22
     */
    public void setActivated(boolean activated) {
        this.isActivated = activated;
    }
    /**
     * Hämtar vilken typ av effekt detta mysterium har.
     * @return Typnamnet som sträng.
     * @author Grupp 22
     */
    public String getMysteryType() {
        return mysteryType;
    }
    /**
     * Returnerar typen av pjäs för identifiering.
     * @return Strängen "Mystery".
     * @author Grupp 22
     */
    @Override
    public String getType() {
        return "Mystery";
    }
}