package com.github.osmundf.chess.hub;

/**
 * Chess side.
 */
public enum Side {

    WHITE((byte) 0x2),
    BLACK((byte) 0x1),
    NO_SIDE((byte) 0x0);

    private final byte index;

    Side(byte index) {
        this.index = index;
    }

    /** Returns true if side is white. */
    public boolean isWhite() {
        return WHITE == this;
    }

    /** Returns is side is black. */
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

    /** Returns index. */
    public byte index() {
        return index;
    }
}
