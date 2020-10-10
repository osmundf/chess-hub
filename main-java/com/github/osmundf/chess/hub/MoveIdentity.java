package com.github.osmundf.chess.hub;

import static com.github.osmundf.chess.hub.Caste.casteFromIndex;
import static com.github.osmundf.chess.hub.MoveType.moveTypeFromIndex;
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
public class MoveIdentity {

    /**
     * Chess move identity factory method.
     *
     * @param hash move hash
     * @return new move identity instance
     */
    public static MoveIdentity moveIdentityFor(int hash) {
        return new MoveIdentity(hash);
    }

    protected final int hash;

    /**
     * Constructor for {@link com.github.osmundf.chess.hub.MoveIdentity} (protected).
     *
     * @param hash move hash
     */
    protected MoveIdentity(int hash) {
        this.hash = hash;
    }

    /**
     * Constructor for {@link com.github.osmundf.chess.hub.MoveIdentity} (protected).
     * </p>
     * <p>type[ttt] side[s] promotion[ppp] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]
     * </p>
     *
     * @param m move type
     * @param s move side
     * @param p promotion
     * @param c capture
     * @param b piece caste
     * @param f move source square
     * @param t move target square
     */
    protected MoveIdentity(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
        int hash = m.index() << 22;
        hash |= s.isWhite() ? 1 << 21 : 0;
        hash |= p.index() << 18;
        hash |= c.index() << 15;
        hash |= b.index() << 12;
        hash |= f.index() << 6;
        hash |= t.index();
        this.hash = hash;
    }

    /**
     * <p>Returns if the hash is valid.
     * </p>
     * <p>This is a simple bit pattern check.
     * </p>
     *
     * @return true if the hash is valid, false otherwise.
     */
    public boolean valid() {
        // Check promotion caste.
        if ((hash & 0x1c0000) == 0x1c0000) {
            return false;
        }
        // Check capture caste.
        if ((hash & 0x38000) == 0x38000) {
            return false;
        }
        // Check base caste.
        if ((hash & 0x7000) == 0x7000) {
            return false;
        }
        // Check pad.
        return (hash & 0xfe000000) == 0x0;
    }

    /**
     * Returns move type.
     *
     * @return move type
     */
    protected MoveType type() {
        return moveTypeFromIndex((hash >> 22) & 0x7);
    }

    /**
     * Returns move side.
     *
     * @return move side
     */
    protected Side side() {
        return (hash & 0x200000) != 0x0 ? WHITE : BLACK;
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
        if (!(object instanceof MoveIdentity)) {
            return false;
        }
        MoveIdentity other = (MoveIdentity) object;
        return this == object || this.hash == other.hash;
    }

    /**
     * Returns the string representation for the {@link MoveIdentity} by its hash code.
     *
     * @return representational string
     */
    @Override
    public String toString() {
        return format("MoveHash(0x%08x)", hash);
    }
}
