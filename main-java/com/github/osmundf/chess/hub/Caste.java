package com.github.osmundf.chess.hub;

/**
 * Chess piece caste.
 */
public enum Caste {

    BISHOP,
    KING,
    KNIGHT,
    NONE,
    PAWN,
    QUEEN,
    ROOK;

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
    public int index() {
        switch (this) {
            case PAWN:
                return 1;
            case BISHOP:
                return 2;
            case KNIGHT:
                return 3;
            case ROOK:
                return 4;
            case QUEEN:
                return 5;
            case KING:
                return 6;
            default:
                return 0;
        }
    }
}
