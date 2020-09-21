package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

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
        assertEquals(2, Side.WHITE.index());
        assertEquals(1, Side.BLACK.index());
        assertEquals(0, Side.NO_SIDE.index());
    }

    @Test
    void testFlags() {
        for (var side : Side.values()) {
            if (Side.NO_SIDE == side) {
                assertFalse(side.isWhite());
                assertFalse(side.isBlack());
                assertSame(side, side.opposite());
            }
            else if (Side.WHITE == side) {
                assertTrue(side.isWhite());
                assertFalse(side.isBlack());
                assertSame(Side.BLACK, side.opposite());
            }
            else if (Side.BLACK == side) {
                assertFalse(side.isWhite());
                assertTrue(side.isBlack());
                assertSame(Side.WHITE, side.opposite());
            }
        }
    }
}
