package com.github.osmundf.chess.hub;

/**
 * Chess side.
 */
public enum Side {

    WHITE,
    BLACK,
    NO_SIDE;

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
    public int index() {
        switch (this) {
            case WHITE:
                return 0x2;
            case BLACK:
                return 0x1;
            case NO_SIDE:
                return 0x0;
            default: {
                throw new ChessException("chess.side.index.unmapped");
            }
        }
    }
}
