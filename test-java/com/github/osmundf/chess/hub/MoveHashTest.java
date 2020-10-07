package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.MoveHash.moveHashFor;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class MoveHashTest {

    @Test
    void testRange() {
        final var boardSide = new Side[] {WHITE, BLACK};
        final var anyRevocation = Revocation.values();
        final var anyCaste = Caste.values();

        for (var type : MoveType.values()) {
            for (var side : boardSide) {
                for (var revocation : anyRevocation) {
                    for (var promotion : anyCaste) {
                        for (var capture : anyCaste) {
                            for (var base : anyCaste) {
                                assertMoveHash(type, side, revocation, promotion, capture, base);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testNullMove() {
        final var hash = 0x0;
        final var moveHash = moveHashFor(hash);
        assertEquals(hash, moveHash.hashCode());
    }

    @Test
    void testEquality() {
        final var hash = 0x0;
        final var first = moveHashFor(hash);
        final var second = moveHashFor(hash);
        assertNotSame(first, second);
        assertEquals(first, second);
        //noinspection ConstantConditions
        if (first.equals(null)) {
            fail("chess.move.hash.equal.on.null");
        }
    }

    @Test
    void testToString() {
        final var moveHash = moveHashFor(0x0);
        assertEquals("MoveHash(0x00000000)", moveHash.toString());
    }

    private void assertMoveHash(MoveType m, Side s, Revocation r, Caste p, Caste c, Caste b) {
        final var f = Square.A1;
        final var t = Square.H8;
        final var hash = hashFor(m, s, r, p, c, b, f, t);
        final var moveHash = moveHashFor(hash);
        assertSame(m, moveHash.type());
        assertSame(s, moveHash.side());
        assertSame(p, moveHash.promotion());
        assertSame(c, moveHash.capture());
        assertSame(b, moveHash.base());
        assertSame(f, moveHash.from());
        assertSame(t, moveHash.to());
    }

    private int hashFor(MoveType m, Side s, Revocation r, Caste p, Caste c, Caste b, Square f, Square t) {
        return new MoveIndexer(m, s, r, p, c, b, f, t).hash;
    }

    private static class MoveIndexer extends MoveHash {

        /** Private constructor. */
        private MoveIndexer(MoveType m, Side s, Revocation r, Caste p, Caste c, Caste b, Square f, Square t) {
            super(m, s, r, p, c, b, f, t);
        }
    }
}
