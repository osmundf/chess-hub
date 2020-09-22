package com.github.osmundf.chess.hub;

/**
 * Chess board square.
 */
public enum Square {

    A1((byte) 0x00),
    A2((byte) 0x08),
    A3((byte) 0x10),
    A4((byte) 0x18),
    A5((byte) 0x20),
    A6((byte) 0x28),
    A7((byte) 0x30),
    A8((byte) 0x38),
    B1((byte) 0x01),
    B2((byte) 0x09),
    B3((byte) 0x11),
    B4((byte) 0x19),
    B5((byte) 0x21),
    B6((byte) 0x29),
    B7((byte) 0x31),
    B8((byte) 0x39),
    C1((byte) 0x02),
    C2((byte) 0x0a),
    C3((byte) 0x12),
    C4((byte) 0x1a),
    C5((byte) 0x22),
    C6((byte) 0x2a),
    C7((byte) 0x32),
    C8((byte) 0x3a),
    D1((byte) 0x03),
    D2((byte) 0x0b),
    D3((byte) 0x13),
    D4((byte) 0x1b),
    D5((byte) 0x23),
    D6((byte) 0x2b),
    D7((byte) 0x33),
    D8((byte) 0x3b),
    E1((byte) 0x04),
    E2((byte) 0x0c),
    E3((byte) 0x14),
    E4((byte) 0x1c),
    E5((byte) 0x24),
    E6((byte) 0x2c),
    E7((byte) 0x34),
    E8((byte) 0x3c),
    F1((byte) 0x05),
    F2((byte) 0x0d),
    F3((byte) 0x15),
    F4((byte) 0x1d),
    F5((byte) 0x25),
    F6((byte) 0x2d),
    F7((byte) 0x35),
    F8((byte) 0x3d),
    G1((byte) 0x06),
    G2((byte) 0x0e),
    G3((byte) 0x16),
    G4((byte) 0x1e),
    G5((byte) 0x26),
    G6((byte) 0x2e),
    G7((byte) 0x36),
    G8((byte) 0x3e),
    H1((byte) 0x07),
    H2((byte) 0x0f),
    H3((byte) 0x17),
    H4((byte) 0x1f),
    H5((byte) 0x27),
    H6((byte) 0x2f),
    H7((byte) 0x37),
    H8((byte) 0x3f);

    private static final Square[] squareArray = new Square[] {
        A1, B1, C1, D1, E1, F1, G1, H1, //
        A2, B2, C2, D2, E2, F2, G2, H2, //
        A3, B3, C3, D3, E3, F3, G3, H3, //
        A4, B4, C4, D4, E4, F4, G4, H4, //
        A5, B5, C5, D5, E5, F5, G5, H5, //
        A6, B6, C6, D6, E6, F6, G6, H6, //
        A7, B7, C7, D7, E7, F7, G7, H7, //
        A8, B8, C8, D8, E8, F8, G8, H8 //
    };

    /**
     * Chess board square utility method.
     *
     * @param index square index
     * @return board square for index
     */
    public static Square squareFromIndex(byte index) {
        if (index < 0 || 63 < index) {
            Exception cause = new Exception("index: " + index);
            throw new ChessException("chess.square.index.invalid", cause);
        }
        return squareArray[index];
    }

    /**
     * Chess board square utility method.
     *
     * @param file chess board file
     * @param rank chess board rank
     * @return board square
     */
    public static Square squareFor(char file, int rank) {
        if (file < 'a' || 'h' < file || rank < 1 || 8 < rank) {
            Exception cause = new Exception("file: " + file + " rank: " + rank);
            throw new ChessException("chess.square.coordinate.invalid", cause);
        }
        int index = ((rank - 1) << 3) | (file - 'a');
        return squareArray[index];
    }

    private final byte index;

    /**
     * Square constructor (package-private).
     *
     * @param index square index
     */
    Square(byte index) {
        this.index = index;
    }

    /** Returns square file. */
    public char file() {
        return (char) ('a' + (index & 0x7));
    }

    /** Returns square rank. */
    public byte rank() {
        return (byte) ((index >> 3) + 1);
    }

    /**
     * Returns the board square at delta distance.
     *
     * @param deltaFile change in file
     * @param deltaRank change in rank
     * @return board square at delta distance
     */
    public Square translate(int deltaFile, int deltaRank) {
        int column = (index & 0x7) + deltaFile;
        int row = (index >> 3) + deltaRank;
        if (0 <= column && column <= 7 && 0 <= row && row <= 7) {
            return squareArray[row << 3 | column];
        }
        var causeMessage = "square: " + this + " deltaFile: " + deltaFile + " deltaRank: " + deltaRank;
        var cause = new ChessException(causeMessage);
        throw new ChessException("chess.square.translate.delta.invalid", cause);
    }

    /**
     * Translate a square up in rank.
     *
     * @param count square translation count
     * @return a square up in rank
     */
    public Square up(int count) {
        return translate(0, count);
    }

    /**
     * Translate a square down in rank.
     *
     * @param count square translation count
     * @return a square down in rank
     */
    public Square down(int count) {
        return translate(0, -count);
    }

    /**
     * Translate a square down in file.
     *
     * @param count square translation count
     * @return a square down in file
     */
    public Square left(int count) {
        return translate(-count, 0);
    }

    /**
     * Translate a square up in file.
     *
     * @param count square translation count
     * @return a square up in file
     */
    public Square right(int count) {
        return translate(count, 0);
    }

    /** Returns square index. */
    public byte index() {
        return index;
    }

    @Override
    public String toString() {
        return file() + "" + rank();
    }
}
