package com.github.osmundf.chess.hub;

/**
 * Chess board square.
 */
public class Square {

    /**
     * Chess board square factory method.
     *
     * @param file chess board file
     * @param rank chess board rank
     * @return new square.
     */
    public static Square newSquare(char file, int rank) {
        if (file < 'a' || 'h' < file || rank < 1 || 8 < rank) {
            Exception cause = new Exception("file: " + file + " rank: " + rank);
            throw new ChessException("chess.square.invalid.coordinate", cause);
        }
        return new Square((byte) ((rank - 1) << 3 | (file - 'a')));
    }

    private final byte index;

    /**
     * Chess square constructor (package-private).
     *
     * @param index square index
     */
    Square(byte index) {
        this.index = index;
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
    public int index() {
        return index;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Square)) {
            return false;
        }
        return this == object || this.index == ((Square) object).index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return file() + "" + rank();
    }
}
