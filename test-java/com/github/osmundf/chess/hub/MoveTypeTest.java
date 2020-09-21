package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.MoveType.BASIC;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveTypeTest {

    @Test
    void testIndex() {
        for (var expected : MoveType.values()) {
            var moveType = moveTypeFromIndex(expected.index());
            assertEquals(expected.index(), moveType.index());
        }
    }

    @Test
    void testSame() {
        for (var expected : MoveType.values()) {
            var moveType = moveTypeFromIndex(expected.index());
            assertSame(expected, moveType);
        }
    }

    @Test
    void testException() {

    }

    @Test
    void testBasic() {
        assertTrue(BASIC.isBasic());
        assertFalse(BASIC.isPromotion());
        assertFalse(BASIC.isCapture());
        assertFalse(BASIC.isCastling());
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
        assertFalse(CASTLE_SHORT.isBasic());
        assertFalse(CASTLE_SHORT.isPromotion());
        assertFalse(CASTLE_SHORT.isCapture());
        assertTrue(CASTLE_SHORT.isCastling());
    }

    @Test
    void testCastlingLong() {
        assertFalse(CASTLE_LONG.isBasic());
        assertFalse(CASTLE_LONG.isPromotion());
        assertFalse(CASTLE_LONG.isCapture());
        assertTrue(CASTLE_LONG.isCastling());
    }
}
