package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.CastleState.castleStateFor;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.NO_SIDE;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class CastleStateTest {

    @Test
    void testIndex() {
        for (var wc = 0x0; wc <= 0x2; wc++) {
            for (var wr = 0x0; wr <= 0x3; wr++) {
                if (wc != 0x0 && wr != 0x0) {
                    continue;
                }

                for (var bc = 0x0; bc <= 0x2; bc++) {
                    for (var br = 0x0; br <= 0x3; br++) {
                        if (bc != 0x0 && br != 0x0) {
                            continue;
                        }

                        var index = (byte) (wc << 6 | wr << 4 | bc << 2 | br);
                        var state = castleStateFor(index);
                        assertEquals(index, state.hashCode());
                    }
                }
            }
        }
    }

    @Test
    void testCastlingWhite() {
        var state = castleStateFor((byte) 0x30);
        {
            var tk = state.castleKingSide(WHITE);
            assertTrue(tk.hasCastledKingSide(WHITE));
            assertFalse(tk.hasCastledQueenSide(WHITE));
            assertFalse(tk.hasAnyRight(WHITE));

            assertFalse(tk.hasAnyRight(BLACK));
            assertFalse(tk.hasCastled(BLACK));
        }
        {
            var tq = state.castleQueenSide(WHITE);
            assertFalse(tq.hasCastledKingSide(WHITE));
            assertTrue(tq.hasCastledQueenSide(WHITE));
            assertFalse(tq.hasAnyRight(WHITE));
            assertFalse(tq.hasAnyRight(BLACK));
            assertFalse(tq.hasCastled(BLACK));
        }
    }

    @Test
    void testCastlingBlack() {
        var state = castleStateFor((byte) 0x3);
        {
            var tk = state.castleKingSide(BLACK);
            assertTrue(tk.hasCastledKingSide(BLACK));
            assertFalse(tk.hasCastledQueenSide(BLACK));
            assertFalse(tk.hasAnyRight(BLACK));

            assertFalse(tk.hasAnyRight(WHITE));
            assertFalse(tk.hasCastled(WHITE));
        }
        {
            var tq = state.castleQueenSide(BLACK);
            assertFalse(tq.hasCastledKingSide(BLACK));
            assertTrue(tq.hasCastledQueenSide(BLACK));
            assertFalse(tq.hasAnyRight(BLACK));
            assertFalse(tq.hasAnyRight(WHITE));
            assertFalse(tq.hasCastled(WHITE));
        }
    }

    @Test
    void testRevokeWhite() {
        var state = castleStateFor((byte) 0x30);
        var tk = state.revokeKingSide(WHITE);
        assertFalse(tk.hasCastled(WHITE));
        assertFalse(tk.hasCastledKingSide(WHITE));
        assertFalse(tk.hasCastledQueenSide(WHITE));
        assertTrue(tk.hasQueenSideRight(WHITE));
        assertFalse(tk.hasKingSideRight(WHITE));
        assertTrue(tk.hasQueenSideRight(WHITE));

        var tq = state.revokeQueenSide(WHITE);
        assertFalse(tq.hasCastled(WHITE));
        assertFalse(tq.hasCastledKingSide(WHITE));
        assertFalse(tq.hasCastledQueenSide(WHITE));
        assertTrue(tq.hasAnyRight(WHITE));
        assertTrue(tq.hasKingSideRight(WHITE));
        assertFalse(tq.hasQueenSideRight(WHITE));

        var tb = state.revokeBoth(WHITE);
        assertFalse(tb.hasCastled(WHITE));
        assertFalse(tb.hasCastledKingSide(WHITE));
        assertFalse(tb.hasCastledQueenSide(WHITE));
        assertFalse(tb.hasAnyRight(WHITE));
        assertFalse(tb.hasKingSideRight(WHITE));
        assertFalse(tb.hasQueenSideRight(WHITE));
    }

    @Test
    void testRevokeBlack() {
        var state = castleStateFor((byte) 0x03);
        var tk = state.revokeKingSide(BLACK);
        assertFalse(tk.hasCastled(BLACK));
        assertFalse(tk.hasCastledKingSide(BLACK));
        assertFalse(tk.hasCastledQueenSide(BLACK));
        assertTrue(tk.hasQueenSideRight(BLACK));
        assertFalse(tk.hasKingSideRight(BLACK));
        assertTrue(tk.hasQueenSideRight(BLACK));

        var tq = state.revokeQueenSide(BLACK);
        assertFalse(tq.hasCastled(BLACK));
        assertFalse(tq.hasCastledKingSide(BLACK));
        assertFalse(tq.hasCastledQueenSide(BLACK));
        assertTrue(tq.hasAnyRight(BLACK));
        assertTrue(tq.hasKingSideRight(BLACK));
        assertFalse(tq.hasQueenSideRight(BLACK));

        var tb = state.revokeBoth(BLACK);
        assertFalse(tb.hasCastled(BLACK));
        assertFalse(tb.hasCastledKingSide(BLACK));
        assertFalse(tb.hasCastledQueenSide(BLACK));
        assertFalse(tb.hasAnyRight(BLACK));
        assertFalse(tb.hasKingSideRight(BLACK));
        assertFalse(tb.hasQueenSideRight(BLACK));
    }

    @Test
    void testRestoreWhite() {
        {
            var wck = castleStateFor((byte) 0x80);

            var kkr = wck.restoreKingSide(WHITE);
            assertFalse(kkr.hasCastled(WHITE));
            assertTrue(kkr.hasKingSideRight(WHITE));
            assertFalse(kkr.hasQueenSideRight(WHITE));

            var kqr = wck.restoreQueenSide(WHITE);
            assertFalse(kqr.hasCastled(WHITE));
            assertFalse(kqr.hasKingSideRight(WHITE));
            assertTrue(kqr.hasQueenSideRight(WHITE));

            var kbr = wck.restoreBoth(WHITE);
            assertFalse(kbr.hasCastled(WHITE));
            assertTrue(kbr.hasKingSideRight(WHITE));
            assertTrue(kbr.hasQueenSideRight(WHITE));
        }
        {
            var wcq = castleStateFor((byte) 0x40);

            var qkr = wcq.restoreKingSide(WHITE);
            assertFalse(qkr.hasCastled(WHITE));
            assertTrue(qkr.hasKingSideRight(WHITE));
            assertFalse(qkr.hasQueenSideRight(WHITE));

            var qqr = wcq.restoreQueenSide(WHITE);
            assertFalse(qqr.hasCastled(WHITE));
            assertFalse(qqr.hasKingSideRight(WHITE));
            assertTrue(qqr.hasQueenSideRight(WHITE));

            var qbr = wcq.restoreBoth(WHITE);
            assertFalse(qbr.hasCastled(WHITE));
            assertTrue(qbr.hasKingSideRight(WHITE));
            assertTrue(qbr.hasQueenSideRight(WHITE));
        }
    }

    @Test
    void testRestoreBlack() {
        {
            var bck = castleStateFor((byte) 0x8);

            var kkr = bck.restoreKingSide(BLACK);
            assertFalse(kkr.hasCastled(BLACK));
            assertTrue(kkr.hasKingSideRight(BLACK));
            assertFalse(kkr.hasQueenSideRight(BLACK));

            var kqr = bck.restoreQueenSide(BLACK);
            assertFalse(kqr.hasCastled(BLACK));
            assertFalse(kqr.hasKingSideRight(BLACK));
            assertTrue(kqr.hasQueenSideRight(BLACK));

            var kbr = bck.restoreBoth(BLACK);
            assertFalse(kbr.hasCastled(BLACK));
            assertTrue(kbr.hasKingSideRight(BLACK));
            assertTrue(kbr.hasQueenSideRight(BLACK));
        }
        {
            var bcq = castleStateFor((byte) 0x4);

            var qkr = bcq.restoreKingSide(BLACK);
            assertFalse(qkr.hasCastled(BLACK));
            assertTrue(qkr.hasKingSideRight(BLACK));
            assertFalse(qkr.hasQueenSideRight(BLACK));

            var qqr = bcq.restoreQueenSide(BLACK);
            assertFalse(qqr.hasCastled(BLACK));
            assertFalse(qqr.hasKingSideRight(BLACK));
            assertTrue(qqr.hasQueenSideRight(BLACK));

            var qbr = bcq.restoreBoth(BLACK);
            assertFalse(qbr.hasCastled(BLACK));
            assertTrue(qbr.hasKingSideRight(BLACK));
            assertTrue(qbr.hasQueenSideRight(BLACK));
        }
    }

    @Test
    void testFromIndexException() {
        for (var wc = 0x0; wc <= 0x3; wc++) {
            for (var wr = 0x0; wr <= 0x3; wr++) {
                for (var bc = 0x0; bc <= 0x3; bc++) {
                    for (var br = 0x0; br <= 0x3; br++) {
                        var index = (byte) (wc << 6 | wr << 4 | bc << 2 | br);
                        var hashString = String.format("0x%02x", index);

                        // Too many error (test shouldn't enforce which is cause is thrown.
                        if (wc == 0x3 && wr != 0x00 || bc == 0x3 && br != 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                            }
                            continue;
                        }

                        // Both castled with right(s) retained.
                        if (wc == 0x3 && wr == 0x0 && bc == 0x3 && br == 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("both.castled.both.sides: 0xcc", cause.getMessage());
                            }
                            continue;
                        }

                        // Both castled with rights retained.
                        if (wc != 0x0 && wr != 0x0 && bc != 0x0 && br != 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("both.castled.retained.rights: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // White castled with rights retained.
                        if (wc != 0x0 && wr != 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("white.castled.retained.rights: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // Black castled with rights retained.
                        if (bc != 0x0 && br != 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("black.castled.retained.rights: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // White castled both sides.
                        if (wc == 0x3 && wr == 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("white.castled.both.sides: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // Black castled both sides.
                        if (bc == 0x3 && br == 0x0) {
                            try {
                                castleStateFor(index);
                                fail("chess.castle.state.test.expected.exception");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("black.castled.both.sides: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        castleStateFor(index);
                    }
                }
            }
        }
    }

    @Test
    void testCastleNullSideException() {
        try {
            castleStateFor((byte) 0x00).castleKingSide(null);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        try {
            castleStateFor((byte) 0x00).castleQueenSide(null);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }
    }

    @Test
    void testCastleNoSideException() {
        try {
            castleStateFor((byte) 0x00).castleKingSide(NO_SIDE);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        try {
            castleStateFor((byte) 0x00).castleQueenSide(NO_SIDE);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }
    }

    @Test
    void testRestoreNullSideException() {
        try {
            castleStateFor((byte) 0x00).restoreKingSide(null);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        try {
            castleStateFor((byte) 0x00).restoreQueenSide(null);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        try {
            castleStateFor((byte) 0x00).restoreBoth(null);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }
    }

    @Test
    void testRestoreNoSideException() {
        try {
            castleStateFor((byte) 0x00).restoreKingSide(NO_SIDE);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        try {
            castleStateFor((byte) 0x00).restoreQueenSide(NO_SIDE);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        try {
            castleStateFor((byte) 0x00).restoreBoth(NO_SIDE);
            fail("chess.castle.state.test.expected.exception");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.failed", e.getMessage());
            var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }
    }
}
