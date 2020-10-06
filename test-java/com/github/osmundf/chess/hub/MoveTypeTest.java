package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE_PROMOTION;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_LONG;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_SHORT;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.MoveType.PROMOTION;
import static com.github.osmundf.chess.hub.MoveType.moveTypeFromIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MoveTypeTest {

    @Test
    void testIndex() {
        for (final var expected : MoveType.values()) {
            final var moveType = moveTypeFromIndex(expected.index());
            assertEquals(expected.index(), moveType.index());
        }
    }

    @Test
    void testSame() {
        for (final var expected : MoveType.values()) {
            final var moveType = moveTypeFromIndex(expected.index());
            assertSame(expected, moveType);
        }
    }

    @Test
    void testException() {
        try {
            final var move = moveTypeFromIndex(-1);
            fail("chess.move.type.test.expected.chess.exception: " + move);
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.move.type.index.invalid", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("index: -1", cause.getMessage());
        }
    }

    @Test
    void testBasic() {
        assertTrue(BASE.isBasic());
        assertFalse(BASE.isPromotion());
        assertFalse(BASE.isCapture());
        assertFalse(BASE.isCastling());
    }

    @Test
    void testCapture() {
        assertFalse(CAPTURE.isBasic());
        assertFalse(CAPTURE.isPromotion());
        assertTrue(CAPTURE.isCapture());
        assertFalse(CAPTURE.isCastling());
    }

    @Test
    void testDoublePush() {
        assertTrue(DOUBLE_PUSH.isBasic());
        assertFalse(DOUBLE_PUSH.isPromotion());
        assertFalse(DOUBLE_PUSH.isCapture());
        assertFalse(DOUBLE_PUSH.isCastling());
    }

    @Test
    void testEnPassant() {
        assertFalse(EN_PASSANT.isBasic());
        assertFalse(EN_PASSANT.isPromotion());
        assertTrue(EN_PASSANT.isCapture());
        assertFalse(EN_PASSANT.isCastling());
    }

    @Test
    void testPromotion() {
        assertFalse(PROMOTION.isBasic());
        assertTrue(PROMOTION.isPromotion());
        assertFalse(PROMOTION.isCapture());
        assertFalse(PROMOTION.isCastling());
    }

    @Test
    void testCapturePromotion() {
        assertFalse(CAPTURE_PROMOTION.isBasic());
        assertTrue(CAPTURE_PROMOTION.isPromotion());
        assertTrue(CAPTURE_PROMOTION.isCapture());
        assertFalse(CAPTURE_PROMOTION.isCastling());
    }

    @Test
    void testCastlingShort() {
        assertTrue(CASTLE_SHORT.isBasic());
        assertFalse(CASTLE_SHORT.isPromotion());
        assertFalse(CASTLE_SHORT.isCapture());
        assertTrue(CASTLE_SHORT.isCastling());
    }

    @Test
    void testCastlingLong() {
        assertTrue(CASTLE_LONG.isBasic());
        assertFalse(CASTLE_LONG.isPromotion());
        assertFalse(CASTLE_LONG.isCapture());
        assertTrue(CASTLE_LONG.isCastling());
    }
}
