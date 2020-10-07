package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Revocation.REVOKE_BOTH;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_KING_SIDE;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_QUEEN_SIDE;
import static com.github.osmundf.chess.hub.Revocation.revocationFromIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RevocationTest {

    @Test
    void testEnumSize() {
        assertEquals(4, Revocation.values().length);
    }

    @Test
    void testIndex() {
        assertEquals(3, REVOKE_BOTH.index());
        assertEquals(2, REVOKE_KING_SIDE.index());
        assertEquals(1, REVOKE_QUEEN_SIDE.index());
        assertEquals(0, REVOKE_NONE.index());
    }

    @Test
    void testFromIndex() {
        assertEquals(REVOKE_BOTH, revocationFromIndex(3));
        assertEquals(REVOKE_KING_SIDE, revocationFromIndex(2));
        assertEquals(REVOKE_QUEEN_SIDE, revocationFromIndex(1));
        assertEquals(REVOKE_NONE, revocationFromIndex(0));
    }

    @Test
    void testInvalidFromIndex() {
        try {
            revocationFromIndex(-1);
            fail("chess.revocation.test.invalid.index.failed");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class, e.getClass());
            assertEquals("chess.revocation.index.invalid", e.getMessage());
            assertEquals(ChessException.class, e.getCause().getClass());
            assertEquals("index: -1", e.getCause().getMessage());
        }
    }

    @Test
    void testKingSide() {
        assertTrue(REVOKE_BOTH.isKingSide());
        assertTrue(REVOKE_KING_SIDE.isKingSide());
        assertFalse(REVOKE_QUEEN_SIDE.isKingSide());
        assertFalse(REVOKE_NONE.isKingSide());
    }

    @Test
    void testQueenSide() {
        assertTrue(REVOKE_BOTH.isQueenSide());
        assertFalse(REVOKE_KING_SIDE.isQueenSide());
        assertTrue(REVOKE_QUEEN_SIDE.isQueenSide());
        assertFalse(REVOKE_NONE.isQueenSide());
    }

}
