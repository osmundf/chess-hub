package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_BOTH;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_KING_SIDE;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_QUEEN_SIDE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastleRevocationTest {

    @Test
    void testEnumSize() {
        assertEquals(4, CastleRevocation.values().length);
    }

    @Test
    void testIndex() {
        assertEquals(3, REVOKE_BOTH.index());
        assertEquals(2, REVOKE_KING_SIDE.index());
        assertEquals(1, REVOKE_QUEEN_SIDE.index());
        assertEquals(0, REVOKE_NONE.index());
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

    @Test
    void testRevocationAsCaste() {
        assertSame(ROOK, REVOKE_BOTH.asCaste());
        assertSame(KING, REVOKE_KING_SIDE.asCaste());
        assertSame(QUEEN, REVOKE_QUEEN_SIDE.asCaste());
        assertSame(NONE, REVOKE_NONE.asCaste());
    }
}
