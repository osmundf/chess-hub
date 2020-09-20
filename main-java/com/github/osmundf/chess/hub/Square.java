package com.github.osmundf.chess.hub;

/**
 * Chess board square.
 */
public enum Square {

    A1(0x00),
    A2(0x08),
    A3(0x10),
    A4(0x18),
    A5(0x20),
    A6(0x28),
    A7(0x30),
    A8(0x38),
    B1(0x01),
    B2(0x09),
    B3(0x11),
    B4(0x19),
    B5(0x21),
    B6(0x29),
    B7(0x31),
    B8(0x39),
    C1(0x02),
    C2(0x0a),
    C3(0x12),
    C4(0x1a),
    C5(0x22),
    C6(0x2a),
    C7(0x32),
    C8(0x3a),
    D1(0x03),
    D2(0x0b),
    D3(0x13),
    D4(0x1b),
    D5(0x23),
    D6(0x2b),
    D7(0x33),
    D8(0x3b),
    E1(0x04),
    E2(0x0c),
    E3(0x14),
    E4(0x1c),
    E5(0x24),
    E6(0x2c),
    E7(0x34),
    E8(0x3c),
    F1(0x05),
    F2(0x0d),
    F3(0x15),
    F4(0x1d),
    F5(0x25),
    F6(0x2d),
    F7(0x35),
    F8(0x3d),
    G1(0x06),
    G2(0x0e),
    G3(0x16),
    G4(0x1e),
    G5(0x26),
    G6(0x2e),
    G7(0x36),
    G8(0x3e),
    H1(0x07),
    H2(0x0f),
    H3(0x17),
    H4(0x1f),
    H5(0x27),
    H6(0x2f),
    H7(0x37),
    H8(0x3f);

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
            throw new ChessException("chess.square.invalid.coordinate", cause);
        }
        int index = ((rank - 1) << 3) | (file - 'a');
        return squareArray[index];
    }

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

    private final byte index;

    /**
     * Chess square constructor (package-private).
     *
     * @param index square index
     */
    Square(int index) {
        this.index = (byte) index;
    }

    /** Returns the file. */
    public char file() {
        return (char) ('a' + (index & 0x7));
    }

    /** Returns the rank. */
    public byte rank() {
        return (byte) ((index >> 3) + 1);
    }

    /** Returns the index. */
    public byte index() {
        return index;
    }

    @Override
    public String toString() {
        return file() + "" + rank();
    }
}
