package com.github.osmundf.chess.hub;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;

public enum CastleRevocation {

    REVOKE_BOTH((byte) 0x3),
    REVOKE_KING_SIDE((byte) 0x2),
    REVOKE_NONE((byte) 0x0),
    REVOKE_QUEEN_SIDE((byte) 0x1);

    private final byte index;

    /**
     * Castle Revocation constructor (private).
     *
     * @param index castle revocation index
     */
    CastleRevocation(byte index) {
        this.index = index;
    }

    /** Returns true if revoking king side castling right, false otherwise. */
    public boolean isKingSide() {
        return REVOKE_KING_SIDE == this || REVOKE_BOTH == this;
    }

    /** Returns true if revoking queen side castling right, false otherwise. */
    public boolean isQueenSide() {
        return REVOKE_QUEEN_SIDE == this || REVOKE_BOTH == this;
    }

    /**
     * Returns chess piece caste as representation for castle right revocation.
     *
     * @return chess piece caste for castle right revocation
     */
    public Caste asCaste() {
        switch (this) {
            case REVOKE_BOTH:
                return ROOK;
            case REVOKE_KING_SIDE:
                return KING;
            case REVOKE_QUEEN_SIDE:
                return QUEEN;
            default: {
                return NONE;
            }
        }
    }

    public byte index() {
        return index;
    }
}
