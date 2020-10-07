package com.github.osmundf.chess.hub;

/**
 * Chess move castle revocation.
 *
 * @author Osmund
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Revocation {

    REVOKE_BOTH((byte) 0x3),
    REVOKE_KING_SIDE((byte) 0x2),
    REVOKE_NONE((byte) 0x0),
    REVOKE_QUEEN_SIDE((byte) 0x1);

    public static Revocation revocationFromIndex(int index) {
        if (index == 0) {
            return REVOKE_NONE;
        }
        else if (index == 1) {
            return REVOKE_QUEEN_SIDE;
        }
        else if (index == 2) {
            return REVOKE_KING_SIDE;
        }
        else if (index == 3) {
            return REVOKE_BOTH;
        }
        else {
            ChessException cause = new ChessException("index: " + index);
            throw new ChessException("chess.revocation.index.invalid", cause);
        }
    }

    private final byte index;

    /**
     * Castle Revocation constructor (private).
     *
     * @param index castle revocation index
     */
    Revocation(byte index) {
        this.index = index;
    }

    /**
     * Returns true if revoking king side castling right, false otherwise.
     *
     * @return a boolean.
     */
    public boolean isKingSide() {
        return REVOKE_KING_SIDE == this || REVOKE_BOTH == this;
    }

    /**
     * Returns true if revoking queen side castling right, false otherwise.
     *
     * @return a boolean.
     */
    public boolean isQueenSide() {
        return REVOKE_QUEEN_SIDE == this || REVOKE_BOTH == this;
    }

    /**
     * Returns castle revocation index.
     *
     * @return castle revocation index
     */
    public byte index() {
        return index;
    }
}
