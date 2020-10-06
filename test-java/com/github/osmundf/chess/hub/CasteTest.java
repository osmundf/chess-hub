package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.BISHOP;
import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.KNIGHT;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
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
        assertEquals(300, KING.value());
        assertEquals(9, QUEEN.value());
        assertEquals(5, ROOK.value());
        assertEquals(3, BISHOP.value());
        assertEquals(3, KNIGHT.value());
        assertEquals(1, PAWN.value());
        assertEquals(0, NONE.value());
    }

    @Test
    void testIndex() {
        assertEquals(6, KING.index());
        assertEquals(5, QUEEN.index());
        assertEquals(4, ROOK.index());
        assertEquals(3, BISHOP.index());
        assertEquals(2, KNIGHT.index());
        assertEquals(1, PAWN.index());
        assertEquals(0, NONE.index());
    }

    @Test
    void testFromIndex() {
        for (final var caste : Caste.values()) {
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
