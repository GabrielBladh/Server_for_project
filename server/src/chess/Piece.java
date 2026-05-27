package chess;
/**
 * Representerar en enskild schackpjäs med dess typ och färg (ägare).
 * Håller även koll på om pjäsen har flyttats tidigare, vilket är viktigt för specialdrag.
 * * @author Gabriel Bladh
 */
public class Piece
{
    Player owner;
    PieceType piece;
    private boolean isMoved = false;

    public Piece(Player owner, PieceType piece)
    {
        this.owner = owner;
        this.piece = piece;
    }

    public Player getOwner()
    {
        return owner;
    }

    public PieceType getPiece()
    {
        return piece;
    }

    public boolean getisMoved()
    {
        return isMoved;
    }

    public void setMoved(){
        isMoved = true;
    }
}
