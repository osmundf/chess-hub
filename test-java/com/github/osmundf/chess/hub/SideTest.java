package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
