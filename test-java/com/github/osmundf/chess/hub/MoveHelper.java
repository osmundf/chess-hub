package com.github.osmundf.chess.hub;

class MoveHelper extends MoveHash {

    /**
     * Return hash for move.
     *
     * @param m move type
     * @param s side
     * @param p promotion caste
     * @param c capture caste
     * @param b base caste
     * @param f from square
     * @param t to square
     * @return hash for move
     */
    static int hashFor(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
        return new MoveHelper(m, s, p, c, b, f, t).hash;
    }

    /** Private constructor. */
    private MoveHelper(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
        super(m, s, p, c, b, f, t);
    }
}
