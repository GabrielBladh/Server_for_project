package U4;

/**
 * Representerar en vanlig spelpjäs utan specialeffekter.
 * @author Grupp 22
 */
public class NormalPiece extends GamePiece {
    /**
     * Skapar en vanlig spelpjäs.
     * @param owner Ägaren till pjäsen.
     * @author Grupp 22
     */
    public NormalPiece(Player owner) {
        super(owner);
    }
    /**
     * Returnerar typen av pjäs.
     * @return Strängen "Normal".
     * @author Grupp 22
     */
    @Override
    public String getType() {
        return "Normal";
    }
}