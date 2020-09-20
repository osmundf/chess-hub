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
     * @return Returns chess piece caste based on index value.
     */
    public static Caste casteFromIndex(int index) {
        switch (index) {
            case 6:
                return KING;
            case 5:
                return QUEEN;
            case 4:
                return ROOK;
            case 3:
                return KNIGHT;
            case 2:
                return BISHOP;
            case 1:
                return PAWN;
            case 0:
                return NONE;
            default: {
                var cause = new ChessException("index: " + index);
                throw new ChessException("chess.caste.invalid.index", cause);
            }
        }
    }

    private final byte index;

    Caste(byte index) {
        this.index = index;
    }

    /** Returns piece material value. */
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

    /** Returns piece index. */
    public byte index() {
        return index;
    }
}
