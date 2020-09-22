package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class CasteTest {

    @Test
    void testEnumSize() {
        assertEquals(7, Caste.values().length);
    }

    @Test
    void testValue() {
        assertEquals(300, Caste.KING.value());
        assertEquals(9, Caste.QUEEN.value());
        assertEquals(5, Caste.ROOK.value());
        assertEquals(3, Caste.BISHOP.value());
        assertEquals(3, Caste.KNIGHT.value());
        assertEquals(1, Caste.PAWN.value());
        assertEquals(0, Caste.NONE.value());
    }

    @Test
    void testIndex() {
        assertEquals(6, Caste.KING.index());
        assertEquals(5, Caste.QUEEN.index());
        assertEquals(4, Caste.ROOK.index());
        assertEquals(3, Caste.BISHOP.index());
        assertEquals(2, Caste.KNIGHT.index());
        assertEquals(1, Caste.PAWN.index());
        assertEquals(0, Caste.NONE.index());
    }

    @Test
    void testFromIndex() {
        for (var caste : Caste.values()) {
            var index = caste.index();
            var test = Caste.casteFromIndex(index);
            assertEquals(caste, test);
        }
    }

    @Test
    void testException() {
        try {
            var caste = Caste.casteFromIndex(-1);
            fail("chess.caste.test.expected.chess.exception: " + caste);
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.caste.index.invalid", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("index: -1", cause.getMessage());
        }
    }
}
