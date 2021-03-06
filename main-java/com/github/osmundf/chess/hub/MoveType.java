package com.github.osmundf.chess.hub;

/**
 * Chess move type.
 *
 * @author Osmund
 * @version 1.0.0
 * @since 1.0.0
 */
public enum MoveType {

    BASE((byte) 0x0),
    CAPTURE((byte) 0x1),
    CAPTURE_PROMOTION((byte) 0x5),
    CASTLE_LONG((byte) 0x7),
    CASTLE_SHORT((byte) 0x6),
    DOUBLE_PUSH((byte) 0x2),
    EN_PASSANT((byte) 0x3),
    PROMOTION((byte) 0x4);

    /** Constant <code>moveTypeArray</code> */
    private static final MoveType[] moveTypeArray = {
        BASE, CAPTURE, DOUBLE_PUSH, EN_PASSANT, PROMOTION, CAPTURE_PROMOTION, CASTLE_SHORT, CASTLE_LONG
    };

    /**
     * Chess move type utility method.
     *
     * @param index move type index
     * @return move type for index
     */
    public static MoveType moveTypeFromIndex(int index) {
        if (index < 0 || 7 < index) {
            Exception cause = new Exception("index: " + index);
            throw new ChessException("chess.move.type.index.invalid", cause);
        }

        return moveTypeArray[index];
    }

    private final byte index;

    /**
     * Move type constructor (private).
     *
     * @param index move type index
     */
    MoveType(byte index) {
        this.index = index;
    }

    /**
     * Returns true if move type is basic (non-capture/non-promoting), false otherwise.
     *
     * @return a boolean.
     */
    public boolean isBasic() {
        return (BASE == this || DOUBLE_PUSH == this || CASTLE_SHORT == this || CASTLE_LONG == this);
    }

    /**
     * Returns true if move type captures a piece, false otherwise.
     *
     * @return a boolean.
     */
    public boolean isCapture() {
        return (CAPTURE == this || CAPTURE_PROMOTION == this || EN_PASSANT == this);
    }

    /**
     * Returns true if move type is for castling, false otherwise.
     *
     * @return a boolean.
     */
    public boolean isCastling() {
        return (CASTLE_SHORT == this || CASTLE_LONG == this);
    }

    /**
     * Returns true if move type is for promotion, false otherwise.
     *
     * @return a boolean.
     */
    public boolean isPromotion() {
        return (PROMOTION == this || CAPTURE_PROMOTION == this);
    }

    /**
     * Return move type index.
     *
     * @return move type index
     */
    public byte index() {
        return index;
    }
}
