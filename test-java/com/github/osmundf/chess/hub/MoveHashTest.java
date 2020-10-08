package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.MoveHash.moveHashFor;
import static com.github.osmundf.chess.hub.MoveHelper.hashFor;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class MoveHashTest {

    @Test
    void testRange() {
        final var boardSide = new Side[] {WHITE, BLACK};
        final var anyCaste = Caste.values();

        for (var type : MoveType.values()) {
            for (var side : boardSide) {
                for (var promotion : anyCaste) {
                    for (var capture : anyCaste) {
                        for (var base : anyCaste) {
                            assertMoveHash(type, side, promotion, capture, base);
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
    void testInvalidHash() {
        for (var i = 0x1; i < 0x7f; i++) {
            final var hash = i << 25;
            assertFalse(moveHashFor(hash).valid());
        }
    }

    @Test
    void testEquality() {
        final var hash = 0x0;
        final var moveHash1 = moveHashFor(hash);
        final var moveHash2 = moveHashFor(hash);
        assertNotSame(moveHash1, moveHash2);
        assertEquals(moveHash1, moveHash2);
        assertNotEquals(moveHash1, null);
        assertNotEquals(moveHash2, null);
    }

    @Test
    void testToString() {
        final var moveHash = moveHashFor(0x0);
        assertEquals("MoveHash(0x00000000)", moveHash.toString());
    }

    private void assertMoveHash(MoveType m, Side s, Caste p, Caste c, Caste b) {
        final var f = Square.A1;
        final var t = Square.H8;
        final var hash = hashFor(m, s, p, c, b, f, t);
        final var moveHash = moveHashFor(hash);
        assertEquals(hash, moveHash.hashCode());
        assertSame(m, moveHash.type());
        assertSame(s, moveHash.side());
        assertSame(p, moveHash.promotion());
        assertSame(c, moveHash.capture());
        assertSame(b, moveHash.base());
        assertSame(f, moveHash.from());
        assertSame(t, moveHash.to());
    }
}
