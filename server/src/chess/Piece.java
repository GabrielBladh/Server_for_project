package chess;

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

    public boolean getMoved(){
        return isMoved;
    }

    public void setMoved(){
        isMoved = true;
    }
}
