package com.github.osmundf.chess.hub;

/**
 * Chess side.
 */
public enum Side {

    WHITE((byte) 0x2),
    BLACK((byte) 0x1),
    NO_SIDE((byte) 0x0);

    private final byte index;

    /**
     * Side constructor (private).
     *
     * @param index side index
     */
    Side(byte index) {
        this.index = index;
    }

    /** Returns true if side is white, false otherwise. */
    public boolean isWhite() {
        return WHITE == this;
    }

    /** Returns true if side is black, false otherwise. */
    public boolean isBlack() {
        return BLACK == this;
    }

    /** Returns opposite side. */
    public Side opposite() {
        if (NO_SIDE == this) {
            return NO_SIDE;
        }
        return WHITE == this ? BLACK : WHITE;
    }

    /** Returns side index. */
    public byte index() {
        return index;
    }
}
