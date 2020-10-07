package com.github.osmundf.chess.hub;

import static com.github.osmundf.chess.hub.Caste.casteFromIndex;
import static com.github.osmundf.chess.hub.MoveType.moveTypeFromIndex;
import static com.github.osmundf.chess.hub.Revocation.revocationFromIndex;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFromIndex;
import static java.lang.String.format;

/**
 * <p>Chess move hash.
 * </p>
 *
 * @author Osmund
 * @version 1.0.0
 * @since 1.0.0
 */
public class MoveHash {

    /**
     * Chess move hash factory method.
     *
     * @param hash move hash
     * @return new move instance
     */
    public static MoveHash moveHashFor(int hash) {
        // type[ttt] side[s] revocation[kk] promotion[ppp] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]
        if ((hash & 0xf8000000) != 0x0) {
            ChessException cause = new ChessException(format("hash: 0x%08x", hash));
            throw new ChessException("chess.move.input.hash.invalid", cause);
        }

        return new MoveHash(hash);
    }

    protected final int hash;

    /**
     * Constructor for MoveHash (protected).
     *
     * @param hash move hash
     */
    protected MoveHash(int hash) {
        this.hash = hash;
    }

    /**
     * <p>Constructor for MoveHash (protected).
     * </p>
     * <p>type[ttt] side[s] revocation[kq] promotion[ppp] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]
     * </p>
     *
     * @param m move type
     * @param s move side
     * @param r castle right revocation
     * @param p promotion
     * @param c capture
     * @param b piece caste
     * @param f move source square
     * @param t move target square
     */
    protected MoveHash(MoveType m, Side s, Revocation r, Caste p, Caste c, Caste b, Square f, Square t) {
        int hash = m.index() << 24;
        hash |= s.isWhite() ? 1 << 23 : 0;
        hash |= r.index() << 21;
        hash |= p.index() << 18;
        hash |= c.index() << 15;
        hash |= b.index() << 12;
        hash |= f.index() << 6;
        hash |= t.index();
        this.hash = hash;
    }

    /**
     * Returns move type.
     *
     * @return move type
     */
    protected MoveType type() {
        return moveTypeFromIndex((hash >> 24) & 0x7);
    }

    /**
     * Returns move side.
     *
     * @return move side
     */
    protected Side side() {
        return (hash & 0x800000) != 0x0 ? WHITE : BLACK;
    }

    /**
     * Returns revocation detail.
     *
     * @return revocation detail
     */
    protected Revocation revocation() {
        return revocationFromIndex((hash >> 21) & 0x3);
    }

    /**
     * Returns promotion/revocation detail.
     *
     * @return promotion/revocation detail
     */
    protected Caste promotion() {
        return casteFromIndex((hash >> 18) & 0x7);
    }

    /**
     * Returns capture/castle detail.
     *
     * @return capture/castle detail
     */
    protected Caste capture() {
        return casteFromIndex((hash >> 15) & 0x7);
    }

    /**
     * Returns caste of moving piece.
     *
     * @return caste of moving piece
     */
    protected Caste base() {
        return casteFromIndex((hash >> 12) & 0x7);
    }

    /**
     * Returns source/king square.
     *
     * @return source/king square
     */
    protected Square from() {
        return squareFromIndex((byte) ((hash >> 6) & 0x3f));
    }

    /**
     * Returns destination/rook square.
     *
     * @return destination/rook square
     */
    protected Square to() {
        return squareFromIndex((byte) (hash & 0x3f));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return hash;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MoveHash)) {
            return false;
        }
        MoveHash other = (MoveHash) object;
        return this == object || this.hash == other.hash;
    }

    /**
     * Returns the string representation for the {@link com.github.osmundf.chess.hub.MoveHash} by its hash code.
     *
     * @return representational string
     */
    @Override
    public String toString() {
        return format("MoveHash(0x%08x)", hash);
    }
}
