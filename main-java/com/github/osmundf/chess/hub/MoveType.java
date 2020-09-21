package com.github.osmundf.chess.hub;

/**
 * Chess move type.
 */
public enum MoveType {

    BASIC((byte) 0x0),
    CAPTURE((byte) 0x1),
    CAPTURE_PROMOTION((byte) 0x5),
    CASTLE_LONG((byte) 0x7),
    CASTLE_SHORT((byte) 0x6),
    DOUBLE_PUSH((byte) 0x2),
    EN_PASSANT((byte) 0x3),
    PROMOTION((byte) 0x4);

    /**
     * Chess move type utility method.
     *
     * @param index move type index
     * @return move type for index
     */
    public static MoveType moveTypeFromIndex(int index) {
        if (index < 0 || 7 < index) {
            Exception cause = new Exception("index: " + index);
            throw new ChessException("chess.move.type.invalid.index", cause);
        }

        return moveTypeArray[index];
    }

    private static final MoveType[] moveTypeArray = {
        BASIC, CAPTURE, DOUBLE_PUSH, EN_PASSANT, PROMOTION, CAPTURE_PROMOTION, CASTLE_SHORT, CASTLE_LONG
    };

    private final byte index;

    /**
     * Move type constructor (private).
     *
     * @param index move type index
     */
    MoveType(byte index) {
        this.index = index;
    }

    /** Returns true if move type is basic (non-capture), false otherwise. */
    public boolean isBasic() {
        return (BASIC == this || DOUBLE_PUSH == this);
    }

    /** Returns true if move type captures a piece, false otherwise. */
    public boolean isCapture() {
        return (CAPTURE == this || CAPTURE_PROMOTION == this || EN_PASSANT == this);
    }

    /** Returns true if move type is for castling, false otherwise. */
    public boolean isCastling() {
        return (CASTLE_SHORT == this || CASTLE_LONG == this);
    }

    /** Returns true if move type is for promotion, false otherwise. */
    public boolean isPromotion() {
        return (PROMOTION == this || CAPTURE_PROMOTION == this);
    }

    /** Return move type index. */
    public byte index() {
        return index;
    }
}
