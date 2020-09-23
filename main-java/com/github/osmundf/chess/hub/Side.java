package com.github.osmundf.chess.hub;

/**
 * Chess side.
 *
 * @author Osmund
 * @version 1.0.0
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

    /**
     * Checks if side is for the white pieces.
     *
     * @return true if side is white, false otherwise
     */
    public boolean isWhite() {
        return WHITE == this;
    }

    /**
     * Checks if side is for the black pieces.
     *
     * @return true if side is black, false otherwise
     */
    public boolean isBlack() {
        return BLACK == this;
    }

    /**
     * Returns opposite side.
     *
     * @return white for black, black for white, no-side otherwise
     */
    public Side opposite() {
        if (NO_SIDE == this) {
            return NO_SIDE;
        }
        return WHITE == this ? BLACK : WHITE;
    }

    /**
     * Returns side index.
     *
     * @return side index
     */
    public byte index() {
        return index;
    }
}
