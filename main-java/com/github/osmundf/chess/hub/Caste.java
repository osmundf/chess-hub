package com.github.osmundf.chess.hub;

/**
 * Chess piece caste.
 */
public enum Caste {

    BISHOP((byte) 3),
    KING((byte) 6),
    KNIGHT((byte) 2),
    NONE((byte) 0),
    PAWN(((byte) 1)),
    QUEEN((byte) 5),
    ROOK((byte) 4);

    /**
     * Chess piece caste utility method.
     *
     * @param index caste index
     * @return piece caste for index
     */
    public static Caste casteFromIndex(int index) {
        if (index < 0 || 6 < index) {
            Exception cause = new Exception("index: " + index);
            throw new ChessException("chess.caste.invalid.index", cause);
        }
        return array[index];
    }

    private static final Caste[] array = new Caste[] {
        NONE, PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    };

    private final byte index;

    /**
     * Caste constructor (private).
     *
     * @param index caste index
     */
    Caste(byte index) {
        this.index = index;
    }

    /** Returns caste material value. */
    public int value() {
        switch (this) {
            case KING:
                return 300;
            case QUEEN:
                return 9;
            case ROOK:
                return 5;
            case KNIGHT:
            case BISHOP:
                return 3;
            case PAWN:
                return 1;
            default:
                return 0;
        }
    }

    /** Returns caste index. */
    public byte index() {
        return index;
    }
}
