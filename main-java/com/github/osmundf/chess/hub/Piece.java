package com.github.osmundf.chess.hub;

import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Side.NO_SIDE;

/**
 * Chess piece.
 *
 * @author Osmund
 * @version 1.0.0
 * @since 1.0.0
 */
public class Piece {

    /**
     * Chess piece factory method.
     *
     * @param side   piece side
     * @param caste  piece caste
     * @param square piece square
     * @return new instance of chess piece with side, caste, and square
     */
    public static Piece pieceFor(Side side, Caste caste, Square square) {
        if (side == null) {
            ChessException cause = new ChessException("chess.side.null");
            throw new ChessException("chess.piece.new.piece.null.argument", cause);
        }
        if (NO_SIDE == side) {
            ChessException cause = new ChessException("chess.side.none");
            throw new ChessException("chess.piece.new.piece.no.side", cause);
        }
        if (caste == null) {
            ChessException cause = new ChessException("chess.caste.null");
            throw new ChessException("chess.piece.new.piece.null.argument", cause);
        }
        if (NONE == caste) {
            ChessException cause = new ChessException("chess.caste.none");
            throw new ChessException("chess.piece.new.piece.no.caste", cause);
        }
        if (square == null) {
            ChessException cause = new ChessException("chess.square.null");
            throw new ChessException("chess.piece.new.piece.null.argument", cause);
        }
        return new Piece(side, caste, square);
    }

    private final Side side;

    private final Caste caste;

    private final Square square;

    /**
     * Piece constructor (protected).
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

    /**
     * Returns the piece side.
     *
     * @return the piece side
     */
    public Side side() {
        return side;
    }

    /**
     * Returns the piece caste.
     *
     * @return the piece caste
     */
    public Caste caste() {
        return caste;
    }

    /**
     * Returns the piece square.
     *
     * @return the piece square
     */
    public Square square() {
        return square;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        // side[ss] caste[ccc] rank[rrr] file[fff]
        int result = side.index() << 9;
        result |= (caste.index() << 6);
        result |= square.index();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Piece)) {
            return false;
        }
        Piece other = (Piece) object;
        return this == object || this.side == other.side && this.caste == other.caste && this.square == other.square;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("%s.%s.%s", side, caste, square);
    }
}
