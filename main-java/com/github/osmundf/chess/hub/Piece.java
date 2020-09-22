package com.github.osmundf.chess.hub;

/**
 * Chess piece.
 */
public class Piece {

    /**
     * Chess piece factory method.
     *
     * @param side   board side
     * @param caste  piece caste
     * @param square piece board square
     * @return new instance of chess piece with side, caste, and square
     */
    public static Piece pieceFor(Side side, Caste caste, Square square) {
        if (side == null) {
            var cause = new ChessException("chess.side.null");
            throw new ChessException("chess.piece.new.piece.null.argument", cause);
        }
        if (Side.NO_SIDE == side) {
            var cause = new ChessException("chess.side.none");
            throw new ChessException("chess.piece.new.piece.no.side", cause);
        }
        if (caste == null) {
            var cause = new ChessException("chess.caste.null");
            throw new ChessException("chess.piece.new.piece.null.argument", cause);
        }
        if (Caste.NONE == caste) {
            var cause = new ChessException("chess.caste.none");
            throw new ChessException("chess.piece.new.piece.no.caste", cause);
        }
        if (square == null) {
            var cause = new ChessException("chess.square.null");
            throw new ChessException("chess.piece.new.piece.null.argument", cause);
        }
        return new Piece(side, caste, square);
    }

    private final Side side;

    private final Caste caste;

    private final Square square;

    /**
     * Chess piece constructor (protected).
     *
     * @param side   piece side
     * @param caste  piece caste
     * @param square piece square
     */
    protected Piece(Side side, Caste caste, Square square) {
        this.side = side;
        this.caste = caste;
        this.square = square;
    }

    /** Returns the chess piece side. */
    public Side side() {
        return side;
    }

    /** Returns the chess piece caste. */
    public Caste caste() {
        return caste;
    }

    /** Returns the chess piece square. */
    public Square square() {
        return square;
    }

    /** Returns chess piece hash. */
    @Override
    public int hashCode() {
        // side[ss] caste[ccc] rank[rrr] file[fff]
        int result = side.index() << 9;
        result |= (caste.index() << 6);
        result |= square.index();
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Piece)) {
            return false;
        }
        var other = (Piece) object;
        return this == object || this.side == other.side && this.caste == other.caste && this.square == other.square;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", side, caste, square);
    }
}
