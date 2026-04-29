package chess;

public class Piece
{
    Player owner;
    PieceType piece;

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
}
