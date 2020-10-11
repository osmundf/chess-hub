package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Square.squareFor;
import static com.github.osmundf.chess.hub.Square.squareFromIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class SquareTest {

    @Test
    void testCoordinateRange() {
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var square = squareFor(file, rank);
                final var index = ((rank - 1) << 3) | (file - 'a');
                assertEquals(file, square.file());
                assertEquals(rank, square.rank());
                assertEquals(index, square.index());
            }
        }
    }

    @Test
    void testIndexRange() {
        for (var index = (byte) 0; index < 64; index++) {
            final var square = squareFromIndex(index);
            assertEquals(index, square.index());
        }
    }

    @Test
    void testSame() {
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var first = squareFor(file, rank);
                final var second = squareFor(file, rank);
                assertSame(first, second);
            }
        }

        for (var index = 0; index < 64; index++) {
            final var first = squareFromIndex((byte) index);
            final var second = squareFromIndex((byte) index);
            assertSame(first, second);
        }
    }

    @Test
    void testValidTranslation() {
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var square = squareFor(file, rank);
                final var bottom = 1 - rank;
                final var top = 8 - rank;
                final var left = 'a' - file;
                final var right = 'h' - file;

                for (var deltaFile = left; deltaFile <= right; deltaFile++) {
                    for (var deltaRank = bottom; deltaRank <= top; deltaRank++) {
                        final var locate = square.translate(deltaFile, deltaRank);
                        assertNotNull(locate);

                        if (deltaFile == 0 && deltaRank == 0) {
                            assertSame(square, locate);
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSquareCardinal() {
        for (var f1 = 'a'; f1 <= 'h'; f1++) {
            for (var f2 = 'a'; f2 <= 'h'; f2++) {
                for (var r1 = 1; r1 <= 8; r1++) {
                    for (var r2 = 1; r2 <= 8; r2++) {
                        final var s1 = squareFor(f1, r1);
                        final var s2 = squareFor(f2, r2);
                        final var df = f2 - f1;
                        final var dr = r2 - r1;

                        if (df == 0) {
                            assertSame(s1.up(dr), s2);
                            assertSame(s1.down(-dr), s2);
                        }
                        if (dr == 0) {
                            assertSame(s1.left(-df), s2);
                            assertSame(s1.right(df), s2);
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSquareForException() {
        try {
            final var square = squareFor('z', -10);
            fail("chess.square.test.failed: " + square.toString());
        }
        catch (RuntimeException e) {
            final var className = e.getClass().getName();
            final var cause = e.getCause();
            assertEquals(ChessException.class.getName(), className);
            assertEquals("chess.square.coordinate.invalid", e.getMessage());
            assertNotNull(cause);
            assertEquals("file: z rank: -10", cause.getMessage());
        }
    }

    @Test
    void testFromIndexException() {
        try {
            final var square = squareFromIndex((byte) -1);
            fail("chess.square.test.failed: " + square.toString());
        }
        catch (RuntimeException e) {
            final var className = e.getClass().getName();
            final var cause = e.getCause();
            assertEquals(ChessException.class.getName(), className);
            assertEquals("chess.square.index.invalid", e.getMessage());
            assertNotNull(cause);
            assertEquals("index: -1", cause.getMessage());
        }
    }

    @Test
    void testTranslateException() {
        try {
            final var square = squareFromIndex((byte) 0).translate(-1, -1);
            fail("chess.square.test.failed: " + square.toString());
        }
        catch (RuntimeException e) {
            final var className = e.getClass().getName();
            final var cause = e.getCause();
            assertEquals(ChessException.class.getName(), className);
            assertEquals("chess.square.translate.delta.invalid", e.getMessage());
            assertNotNull(cause);
            assertEquals("square: A1 deltaFile: -1 deltaRank: -1", cause.getMessage());
        }
    }
}
