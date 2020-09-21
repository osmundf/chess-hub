package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

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
                        var state = CastleState.castleStateFor(index);
                        assertEquals(index, state.hashCode());
                    }
                }
            }
        }
    }

    @Test
    void testCastlingWhite() {
        var state = CastleState.castleStateFor((byte) 0x30);
        {
            var tk = state.castleKingSide(Side.WHITE);
            assertTrue(tk.hasCastledKingSide(Side.WHITE));
            assertFalse(tk.hasCastledQueenSide(Side.WHITE));
            assertFalse(tk.hasAnyRight(Side.WHITE));

            assertFalse(tk.hasAnyRight(Side.BLACK));
            assertFalse(tk.hasCastled(Side.BLACK));
        }
        {
            var tq = state.castleQueenSide(Side.WHITE);
            assertFalse(tq.hasCastledKingSide(Side.WHITE));
            assertTrue(tq.hasCastledQueenSide(Side.WHITE));
            assertFalse(tq.hasAnyRight(Side.WHITE));
            assertFalse(tq.hasAnyRight(Side.BLACK));
            assertFalse(tq.hasCastled(Side.BLACK));
        }
    }

    @Test
    void testCastlingBlack() {
        var state = CastleState.castleStateFor((byte) 0x3);
        {
            var tk = state.castleKingSide(Side.BLACK);
            assertTrue(tk.hasCastledKingSide(Side.BLACK));
            assertFalse(tk.hasCastledQueenSide(Side.BLACK));
            assertFalse(tk.hasAnyRight(Side.BLACK));

            assertFalse(tk.hasAnyRight(Side.WHITE));
            assertFalse(tk.hasCastled(Side.WHITE));
        }
        {
            var tq = state.castleQueenSide(Side.BLACK);
            assertFalse(tq.hasCastledKingSide(Side.BLACK));
            assertTrue(tq.hasCastledQueenSide(Side.BLACK));
            assertFalse(tq.hasAnyRight(Side.BLACK));
            assertFalse(tq.hasAnyRight(Side.WHITE));
            assertFalse(tq.hasCastled(Side.WHITE));
        }
    }

    @Test
    void testRevokeWhite() {
        var state = CastleState.castleStateFor((byte) 0x30);
        var tk = state.revokeKingSide(Side.WHITE);
        assertFalse(tk.hasCastled(Side.WHITE));
        assertFalse(tk.hasCastledKingSide(Side.WHITE));
        assertFalse(tk.hasCastledQueenSide(Side.WHITE));
        assertTrue(tk.hasQueenSideRight(Side.WHITE));
        assertFalse(tk.hasKingSideRight(Side.WHITE));
        assertTrue(tk.hasQueenSideRight(Side.WHITE));

        var tq = state.revokeQueenSide(Side.WHITE);
        assertFalse(tq.hasCastled(Side.WHITE));
        assertFalse(tq.hasCastledKingSide(Side.WHITE));
        assertFalse(tq.hasCastledQueenSide(Side.WHITE));
        assertTrue(tq.hasAnyRight(Side.WHITE));
        assertTrue(tq.hasKingSideRight(Side.WHITE));
        assertFalse(tq.hasQueenSideRight(Side.WHITE));

        var tb = state.revokeBoth(Side.WHITE);
        assertFalse(tb.hasCastled(Side.WHITE));
        assertFalse(tb.hasCastledKingSide(Side.WHITE));
        assertFalse(tb.hasCastledQueenSide(Side.WHITE));
        assertFalse(tb.hasAnyRight(Side.WHITE));
        assertFalse(tb.hasKingSideRight(Side.WHITE));
        assertFalse(tb.hasQueenSideRight(Side.WHITE));
    }

    @Test
    void testRevokeBlack() {
        var state = CastleState.castleStateFor((byte) 0x03);
        var tk = state.revokeKingSide(Side.BLACK);
        assertFalse(tk.hasCastled(Side.BLACK));
        assertFalse(tk.hasCastledKingSide(Side.BLACK));
        assertFalse(tk.hasCastledQueenSide(Side.BLACK));
        assertTrue(tk.hasQueenSideRight(Side.BLACK));
        assertFalse(tk.hasKingSideRight(Side.BLACK));
        assertTrue(tk.hasQueenSideRight(Side.BLACK));

        var tq = state.revokeQueenSide(Side.BLACK);
        assertFalse(tq.hasCastled(Side.BLACK));
        assertFalse(tq.hasCastledKingSide(Side.BLACK));
        assertFalse(tq.hasCastledQueenSide(Side.BLACK));
        assertTrue(tq.hasAnyRight(Side.BLACK));
        assertTrue(tq.hasKingSideRight(Side.BLACK));
        assertFalse(tq.hasQueenSideRight(Side.BLACK));

        var tb = state.revokeBoth(Side.BLACK);
        assertFalse(tb.hasCastled(Side.BLACK));
        assertFalse(tb.hasCastledKingSide(Side.BLACK));
        assertFalse(tb.hasCastledQueenSide(Side.BLACK));
        assertFalse(tb.hasAnyRight(Side.BLACK));
        assertFalse(tb.hasKingSideRight(Side.BLACK));
        assertFalse(tb.hasQueenSideRight(Side.BLACK));
    }

    @Test
    void testRestoreWhite() {
        {
            var wck = CastleState.castleStateFor((byte) 0x80);

            var kkr = wck.restoreKingSide(Side.WHITE);
            assertFalse(kkr.hasCastled(Side.WHITE));
            assertTrue(kkr.hasKingSideRight(Side.WHITE));
            assertFalse(kkr.hasQueenSideRight(Side.WHITE));

            var kqr = wck.restoreQueenSide(Side.WHITE);
            assertFalse(kqr.hasCastled(Side.WHITE));
            assertFalse(kqr.hasKingSideRight(Side.WHITE));
            assertTrue(kqr.hasQueenSideRight(Side.WHITE));

            var kbr = wck.restoreBoth(Side.WHITE);
            assertFalse(kbr.hasCastled(Side.WHITE));
            assertTrue(kbr.hasKingSideRight(Side.WHITE));
            assertTrue(kbr.hasQueenSideRight(Side.WHITE));
        }
        {
            var wcq = CastleState.castleStateFor((byte) 0x40);

            var qkr = wcq.restoreKingSide(Side.WHITE);
            assertFalse(qkr.hasCastled(Side.WHITE));
            assertTrue(qkr.hasKingSideRight(Side.WHITE));
            assertFalse(qkr.hasQueenSideRight(Side.WHITE));

            var qqr = wcq.restoreQueenSide(Side.WHITE);
            assertFalse(qqr.hasCastled(Side.WHITE));
            assertFalse(qqr.hasKingSideRight(Side.WHITE));
            assertTrue(qqr.hasQueenSideRight(Side.WHITE));

            var qbr = wcq.restoreBoth(Side.WHITE);
            assertFalse(qbr.hasCastled(Side.WHITE));
            assertTrue(qbr.hasKingSideRight(Side.WHITE));
            assertTrue(qbr.hasQueenSideRight(Side.WHITE));
        }
    }

    @Test
    void testRestoreBlack() {
        {
            var bck = CastleState.castleStateFor((byte) 0x8);

            var kkr = bck.restoreKingSide(Side.BLACK);
            assertFalse(kkr.hasCastled(Side.BLACK));
            assertTrue(kkr.hasKingSideRight(Side.BLACK));
            assertFalse(kkr.hasQueenSideRight(Side.BLACK));

            var kqr = bck.restoreQueenSide(Side.BLACK);
            assertFalse(kqr.hasCastled(Side.BLACK));
            assertFalse(kqr.hasKingSideRight(Side.BLACK));
            assertTrue(kqr.hasQueenSideRight(Side.BLACK));

            var kbr = bck.restoreBoth(Side.BLACK);
            assertFalse(kbr.hasCastled(Side.BLACK));
            assertTrue(kbr.hasKingSideRight(Side.BLACK));
            assertTrue(kbr.hasQueenSideRight(Side.BLACK));
        }
        {
            var bcq = CastleState.castleStateFor((byte) 0x4);

            var qkr = bcq.restoreKingSide(Side.BLACK);
            assertFalse(qkr.hasCastled(Side.BLACK));
            assertTrue(qkr.hasKingSideRight(Side.BLACK));
            assertFalse(qkr.hasQueenSideRight(Side.BLACK));

            var qqr = bcq.restoreQueenSide(Side.BLACK);
            assertFalse(qqr.hasCastled(Side.BLACK));
            assertFalse(qqr.hasKingSideRight(Side.BLACK));
            assertTrue(qqr.hasQueenSideRight(Side.BLACK));

            var qbr = bcq.restoreBoth(Side.BLACK);
            assertFalse(qbr.hasCastled(Side.BLACK));
            assertTrue(qbr.hasKingSideRight(Side.BLACK));
            assertTrue(qbr.hasQueenSideRight(Side.BLACK));
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
                                CastleState.castleStateFor(index);
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
                                CastleState.castleStateFor(index);
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
                                CastleState.castleStateFor(index);
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
                                CastleState.castleStateFor(index);
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
                                CastleState.castleStateFor(index);
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
                                CastleState.castleStateFor(index);
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
                                CastleState.castleStateFor(index);
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

                        CastleState.castleStateFor(index);
                    }
                }
            }
        }
    }

    @Test
    void testCastleNullSideException() {
        try {
            CastleState.castleStateFor((byte) 0x00).castleKingSide(null);
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
            CastleState.castleStateFor((byte) 0x00).castleQueenSide(null);
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
            CastleState.castleStateFor((byte) 0x00).castleKingSide(Side.NO_SIDE);
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
            CastleState.castleStateFor((byte) 0x00).castleQueenSide(Side.NO_SIDE);
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
            CastleState.castleStateFor((byte) 0x00).restoreKingSide(null);
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
            CastleState.castleStateFor((byte) 0x00).restoreQueenSide(null);
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
            CastleState.castleStateFor((byte) 0x00).restoreBoth(null);
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
            CastleState.castleStateFor((byte) 0x00).restoreKingSide(Side.NO_SIDE);
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
            CastleState.castleStateFor((byte) 0x00).restoreQueenSide(Side.NO_SIDE);
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
            CastleState.castleStateFor((byte) 0x00).restoreBoth(Side.NO_SIDE);
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
