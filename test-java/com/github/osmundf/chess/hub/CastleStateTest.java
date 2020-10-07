package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.CastleState.castleStateFromHash;
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

                        final var index = (byte) (wc << 6 | wr << 4 | bc << 2 | br);
                        final var state = castleStateFromHash(index);
                        assertEquals(index, state.hashCode());
                    }
                }
            }
        }
    }

    @Test
    void testCastlingWhite() {
        final var state = castleStateFromHash((byte) 0x30);
        {
            final var tk = state.castleKingSide(WHITE);
            assertTrue(tk.hasCastledKingSide(WHITE));
            assertFalse(tk.hasCastledQueenSide(WHITE));
            assertFalse(tk.hasAnyRight(WHITE));

            assertFalse(tk.hasAnyRight(BLACK));
            assertFalse(tk.hasCastled(BLACK));
        }
        {
            final var tq = state.castleQueenSide(WHITE);
            assertFalse(tq.hasCastledKingSide(WHITE));
            assertTrue(tq.hasCastledQueenSide(WHITE));
            assertFalse(tq.hasAnyRight(WHITE));
            assertFalse(tq.hasAnyRight(BLACK));
            assertFalse(tq.hasCastled(BLACK));
        }
    }

    @Test
    void testCastlingBlack() {
        final var state = castleStateFromHash((byte) 0x3);
        {
            final var tk = state.castleKingSide(BLACK);
            assertTrue(tk.hasCastledKingSide(BLACK));
            assertFalse(tk.hasCastledQueenSide(BLACK));
            assertFalse(tk.hasAnyRight(BLACK));

            assertFalse(tk.hasAnyRight(WHITE));
            assertFalse(tk.hasCastled(WHITE));
        }
        {
            final var tq = state.castleQueenSide(BLACK);
            assertFalse(tq.hasCastledKingSide(BLACK));
            assertTrue(tq.hasCastledQueenSide(BLACK));
            assertFalse(tq.hasAnyRight(BLACK));
            assertFalse(tq.hasAnyRight(WHITE));
            assertFalse(tq.hasCastled(WHITE));
        }
    }

    @Test
    void testCastleKingSideException() {
        final var state = castleStateFromHash((byte) 0x0);

        try {
            state.castleKingSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
        }

        try {
            state.castleKingSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
        }

        try {
            state.castleKingSide(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
        }

        try {
            state.castleKingSide(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
        }
    }

    @Test
    void testCastleQueenSideException() {
        final var state = castleStateFromHash((byte) 0x0);

        try {
            state.castleQueenSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
        }

        try {
            state.castleQueenSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
        }

        try {
            state.castleQueenSide(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
        }

        try {
            state.castleQueenSide(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
        }
    }

    @Test
    void testHasCastledException() {
        final var state = castleStateFromHash((byte) 0x0);

        // hasCastled()
        try {
            state.hasCastled(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.castled.failed", e.getMessage());
        }

        try {
            state.hasCastled(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.castled.failed", e.getMessage());
        }

        // hasCastledKingSide()
        try {
            state.hasCastledKingSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.castled.king.side.failed", e.getMessage());
        }

        try {
            state.hasCastledKingSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.castled.king.side.failed", e.getMessage());
        }

        // hasCastledQueenSide()
        try {
            state.hasCastledQueenSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.castled.queen.side.failed", e.getMessage());
        }

        try {
            state.hasCastledQueenSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.castled.queen.side.failed", e.getMessage());
        }
    }

    @Test
    void testRevokeWhite() {
        final var state = castleStateFromHash((byte) 0x30);
        final var tk = state.revokeKingSide(WHITE);
        assertFalse(tk.hasCastled(WHITE));
        assertFalse(tk.hasCastledKingSide(WHITE));
        assertFalse(tk.hasCastledQueenSide(WHITE));
        assertTrue(tk.hasQueenSideRight(WHITE));
        assertFalse(tk.hasKingSideRight(WHITE));
        assertTrue(tk.hasQueenSideRight(WHITE));

        final var tq = state.revokeQueenSide(WHITE);
        assertFalse(tq.hasCastled(WHITE));
        assertFalse(tq.hasCastledKingSide(WHITE));
        assertFalse(tq.hasCastledQueenSide(WHITE));
        assertTrue(tq.hasAnyRight(WHITE));
        assertTrue(tq.hasKingSideRight(WHITE));
        assertFalse(tq.hasQueenSideRight(WHITE));

        final var tb = state.revokeBoth(WHITE);
        assertFalse(tb.hasCastled(WHITE));
        assertFalse(tb.hasCastledKingSide(WHITE));
        assertFalse(tb.hasCastledQueenSide(WHITE));
        assertFalse(tb.hasAnyRight(WHITE));
        assertFalse(tb.hasKingSideRight(WHITE));
        assertFalse(tb.hasQueenSideRight(WHITE));
    }

    @Test
    void testRevokeBlack() {
        final var state = castleStateFromHash((byte) 0x03);
        final var tk = state.revokeKingSide(BLACK);
        assertFalse(tk.hasCastled(BLACK));
        assertFalse(tk.hasCastledKingSide(BLACK));
        assertFalse(tk.hasCastledQueenSide(BLACK));
        assertTrue(tk.hasQueenSideRight(BLACK));
        assertFalse(tk.hasKingSideRight(BLACK));
        assertTrue(tk.hasQueenSideRight(BLACK));

        final var tq = state.revokeQueenSide(BLACK);
        assertFalse(tq.hasCastled(BLACK));
        assertFalse(tq.hasCastledKingSide(BLACK));
        assertFalse(tq.hasCastledQueenSide(BLACK));
        assertTrue(tq.hasAnyRight(BLACK));
        assertTrue(tq.hasKingSideRight(BLACK));
        assertFalse(tq.hasQueenSideRight(BLACK));

        final var tb = state.revokeBoth(BLACK);
        assertFalse(tb.hasCastled(BLACK));
        assertFalse(tb.hasCastledKingSide(BLACK));
        assertFalse(tb.hasCastledQueenSide(BLACK));
        assertFalse(tb.hasAnyRight(BLACK));
        assertFalse(tb.hasKingSideRight(BLACK));
        assertFalse(tb.hasQueenSideRight(BLACK));
    }

    @Test
    void testRevokeAfterCastlingException() {
        // revokeBoth()
        try {
            castleStateFromHash((byte) 0x80).revokeBoth(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.both.failed", e.getMessage());
        }
        try {
            castleStateFromHash((byte) 0x40).revokeBoth(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.both.failed", e.getMessage());
        }

        // revokeBoth()
        try {
            castleStateFromHash((byte) 0x08).revokeBoth(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.both.failed", e.getMessage());
        }
        try {
            castleStateFromHash((byte) 0x04).revokeBoth(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.both.failed", e.getMessage());
        }

        // revokeKingSide()
        try {
            castleStateFromHash((byte) 0x80).revokeKingSide(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.king.side.failed", e.getMessage());
        }
        try {
            castleStateFromHash((byte) 0x40).revokeKingSide(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.king.side.failed", e.getMessage());
        }

        // revokeKingSide()
        try {
            castleStateFromHash((byte) 0x08).revokeKingSide(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.king.side.failed", e.getMessage());
        }
        try {
            castleStateFromHash((byte) 0x04).revokeKingSide(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.king.side.failed", e.getMessage());
        }

        // revokeQueenSide()
        try {
            castleStateFromHash((byte) 0x80).revokeQueenSide(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.queen.side.failed", e.getMessage());
        }
        try {
            castleStateFromHash((byte) 0x40).revokeQueenSide(WHITE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.queen.side.failed", e.getMessage());
        }

        // revokeQueenSide()
        try {
            castleStateFromHash((byte) 0x08).revokeQueenSide(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.queen.side.failed", e.getMessage());
        }
        try {
            castleStateFromHash((byte) 0x04).revokeQueenSide(BLACK);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.queen.side.failed", e.getMessage());
        }
    }

    @Test
    void testRestoreWhite() {
        {
            final var wck = castleStateFromHash((byte) 0x80);

            final var kkr = wck.restoreKingSide(WHITE);
            assertFalse(kkr.hasCastled(WHITE));
            assertTrue(kkr.hasKingSideRight(WHITE));
            assertFalse(kkr.hasQueenSideRight(WHITE));

            final var kqr = wck.restoreQueenSide(WHITE);
            assertFalse(kqr.hasCastled(WHITE));
            assertFalse(kqr.hasKingSideRight(WHITE));
            assertTrue(kqr.hasQueenSideRight(WHITE));

            final var kbr = wck.restoreBoth(WHITE);
            assertFalse(kbr.hasCastled(WHITE));
            assertTrue(kbr.hasKingSideRight(WHITE));
            assertTrue(kbr.hasQueenSideRight(WHITE));
        }
        {
            final var wcq = castleStateFromHash((byte) 0x40);

            final var qkr = wcq.restoreKingSide(WHITE);
            assertFalse(qkr.hasCastled(WHITE));
            assertTrue(qkr.hasKingSideRight(WHITE));
            assertFalse(qkr.hasQueenSideRight(WHITE));

            final var qqr = wcq.restoreQueenSide(WHITE);
            assertFalse(qqr.hasCastled(WHITE));
            assertFalse(qqr.hasKingSideRight(WHITE));
            assertTrue(qqr.hasQueenSideRight(WHITE));

            final var qbr = wcq.restoreBoth(WHITE);
            assertFalse(qbr.hasCastled(WHITE));
            assertTrue(qbr.hasKingSideRight(WHITE));
            assertTrue(qbr.hasQueenSideRight(WHITE));
        }
    }

    @Test
    void testRestoreBlack() {
        {
            final var bck = castleStateFromHash((byte) 0x8);

            final var kkr = bck.restoreKingSide(BLACK);
            assertFalse(kkr.hasCastled(BLACK));
            assertTrue(kkr.hasKingSideRight(BLACK));
            assertFalse(kkr.hasQueenSideRight(BLACK));

            final var kqr = bck.restoreQueenSide(BLACK);
            assertFalse(kqr.hasCastled(BLACK));
            assertFalse(kqr.hasKingSideRight(BLACK));
            assertTrue(kqr.hasQueenSideRight(BLACK));

            final var kbr = bck.restoreBoth(BLACK);
            assertFalse(kbr.hasCastled(BLACK));
            assertTrue(kbr.hasKingSideRight(BLACK));
            assertTrue(kbr.hasQueenSideRight(BLACK));
        }
        {
            final var bcq = castleStateFromHash((byte) 0x4);

            final var qkr = bcq.restoreKingSide(BLACK);
            assertFalse(qkr.hasCastled(BLACK));
            assertTrue(qkr.hasKingSideRight(BLACK));
            assertFalse(qkr.hasQueenSideRight(BLACK));

            final var qqr = bcq.restoreQueenSide(BLACK);
            assertFalse(qqr.hasCastled(BLACK));
            assertFalse(qqr.hasKingSideRight(BLACK));
            assertTrue(qqr.hasQueenSideRight(BLACK));

            final var qbr = bcq.restoreBoth(BLACK);
            assertFalse(qbr.hasCastled(BLACK));
            assertTrue(qbr.hasKingSideRight(BLACK));
            assertTrue(qbr.hasQueenSideRight(BLACK));
        }
    }

    @Test
    void testEquality() {
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

                        final var index = (byte) (wc << 6 | wr << 4 | bc << 2 | br);
                        final var state = castleStateFromHash(index);
                        //noinspection ConstantConditions
                        if (state.equals(null)) {
                            fail("chess.castle.state.test");
                        }
                    }
                }
            }
        }
    }

    @Test
    void testToString() {
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

                        final var index = (byte) (wc << 6 | wr << 4 | bc << 2 | br);
                        final var state = castleStateFromHash(index);
                        final var expected = String.format("castleState(0x%02x)", index);
                        assertEquals(expected, state.toString());
                    }
                }
            }
        }
    }

    @Test
    void testFromIndexException() {
        for (var wc = 0x0; wc <= 0x3; wc++) {
            for (var wr = 0x0; wr <= 0x3; wr++) {
                for (var bc = 0x0; bc <= 0x3; bc++) {
                    for (var br = 0x0; br <= 0x3; br++) {
                        final var index = (byte) (wc << 6 | wr << 4 | bc << 2 | br);
                        final var hashString = String.format("0x%02x", index);

                        // Too many error (test shouldn't enforce which is cause is thrown.
                        if (wc == 0x3 && wr != 0x00 || bc == 0x3 && br != 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                            }
                            continue;
                        }

                        // Both castled with right(s) retained.
                        if (wc == 0x3 && wr == 0x0 && bc == 0x3 && br == 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("both.castled.both.sides: 0xcc", cause.getMessage());
                            }
                            continue;
                        }

                        // Both castled with rights retained.
                        if (wc != 0x0 && wr != 0x0 && bc != 0x0 && br != 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("both.castled.retained.rights: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // White castled with rights retained.
                        if (wc != 0x0 && wr != 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("white.castled.retained.rights: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // Black castled with rights retained.
                        if (bc != 0x0 && br != 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("black.castled.retained.rights: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // White castled both sides.
                        if (wc == 0x3 && wr == 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("white.castled.both.sides: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        // Black castled both sides.
                        if (bc == 0x3 && br == 0x0) {
                            try {
                                castleStateFromHash(index);
                                fail("chess.castle.state.test");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.castle.state.hash.invalid", e.getMessage());
                                final var cause = e.getCause();
                                assertNotNull(cause);
                                assertEquals("black.castled.both.sides: " + hashString, cause.getMessage());
                            }
                            continue;
                        }

                        castleStateFromHash(index);
                    }
                }
            }
        }
    }

    @Test
    void testCastleNullSideException() {
        try {
            castleStateFromHash((byte) 0x00).castleKingSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        try {
            castleStateFromHash((byte) 0x00).castleQueenSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }
    }

    @Test
    void testCastleNoSideException() {
        try {
            castleStateFromHash((byte) 0x00).castleKingSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.king.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        try {
            castleStateFromHash((byte) 0x00).castleQueenSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.castle.queen.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }
    }

    @Test
    void testHasRightNullSideException() {
        final var state = castleStateFromHash((byte) 0x00);

        // hasAnyRight()
        try {
            state.hasAnyRight(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.any.right.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        // hasKingSideRight()
        try {
            state.hasKingSideRight(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.king.side.right.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        // hasQueenSideRight()
        try {
            state.hasQueenSideRight(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.queen.side.right.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }
    }

    @Test
    void testHasRightNoSideException() {
        final var state = castleStateFromHash((byte) 0x00);

        // hasAnyRight()
        try {
            state.hasAnyRight(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.any.right.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        // hasKingSideRight()
        try {
            state.hasKingSideRight(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.king.side.right.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        // hasQueenSideRight()
        try {
            state.hasQueenSideRight(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.has.queen.side.right.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }
    }

    @Test
    void testRevokeNullSideException() {
        final var state = castleStateFromHash((byte) 0x00);

        // revokeBoth()
        try {
            state.revokeBoth(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.both.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        // revokeKingSide()
        try {
            state.revokeKingSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.king.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        // revokeQueenSide()
        try {
            state.revokeQueenSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.queen.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }
    }

    @Test
    void testRevokeNoSideException() {
        final var state = castleStateFromHash((byte) 0x00);

        // revokeBoth()
        try {
            state.revokeBoth(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.both.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        // revokeKingSide()
        try {
            state.revokeKingSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.king.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        // revokeQueenSide()
        try {
            state.revokeQueenSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.revoke.queen.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }
    }

    @Test
    void testRestoreNullSideException() {
        try {
            castleStateFromHash((byte) 0x00).restoreKingSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.king.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        try {
            castleStateFromHash((byte) 0x00).restoreQueenSide(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.queen.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }

        try {
            castleStateFromHash((byte) 0x00).restoreBoth(null);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.both.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: null", cause.getMessage());
        }
    }

    @Test
    void testRestoreNoSideException() {
        try {
            castleStateFromHash((byte) 0x00).restoreKingSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.king.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        try {
            castleStateFromHash((byte) 0x00).restoreQueenSide(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.queen.side.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }

        try {
            castleStateFromHash((byte) 0x00).restoreBoth(NO_SIDE);
            fail("chess.castle.state.test");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.castle.state.restore.both.failed", e.getMessage());
            final var cause = e.getCause();
            assertNotNull(cause);
            assertEquals("side: NO_SIDE", cause.getMessage());
        }
    }
}
