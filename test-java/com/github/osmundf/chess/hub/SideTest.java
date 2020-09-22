package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.NO_SIDE;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SideTest {

    @Test
    void testEnumSize() {
        assertEquals(3, Side.values().length);
    }

    @Test
    void testIndex() {
        assertEquals(2, WHITE.index());
        assertEquals(1, BLACK.index());
        assertEquals(0, NO_SIDE.index());
    }

    @Test
    void testFlags() {
        for (var side : Side.values()) {
            if (NO_SIDE == side) {
                assertFalse(side.isWhite());
                assertFalse(side.isBlack());
                assertSame(side, side.opposite());
            }
            else if (WHITE == side) {
                assertTrue(side.isWhite());
                assertFalse(side.isBlack());
                assertSame(BLACK, side.opposite());
            }
            else if (BLACK == side) {
                assertFalse(side.isWhite());
                assertTrue(side.isBlack());
                assertSame(WHITE, side.opposite());
            }
        }
    }
}
