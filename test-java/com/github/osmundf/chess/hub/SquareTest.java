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
            for (int rank = 1; rank <= 8; rank++) {
                var square = squareFor(file, rank);
                var index = ((rank - 1) << 3) | (file - 'a');
                assertEquals(file, square.file());
                assertEquals(rank, square.rank());
                assertEquals(index, square.index());
            }
        }
    }

    @Test
    void testIndexRange() {
        for (var index = (byte) 0; index < 64; index++) {
            var square = squareFromIndex(index);
            assertEquals(index, square.index());
        }
    }

    @Test
    void testSame() {
        for (var file = 'a'; file <= 'h'; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                var first = squareFor(file, rank);
                var second = squareFor(file, rank);
                assertSame(first, second);
            }
        }

        for (var index = 0; index < 64; index++) {
            var first = squareFromIndex((byte) index);
            var second = squareFromIndex((byte) index);
            assertSame(first, second);
        }
    }

    @Test
    void testValidTranslation() {
        for (var file = 'a'; file <= 'h'; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                var square = squareFor(file, rank);
                var bottom = 1 - rank;
                var top = 8 - rank;
                var left = 'a' - file;
                var right = 'h' - file;

                for (var deltaFile = left; deltaFile <= right; deltaFile++) {
                    for (var deltaRank = bottom; deltaRank <= top; deltaRank++) {
                        var locate = square.translate(deltaFile, deltaRank);
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
    void testSquareForException() {
        try {
            var square = squareFor('z', -10);
            fail("chess.square.test.failed: " + square.toString());
        }
        catch (RuntimeException e) {
            var className = e.getClass().getName();
            var cause = e.getCause();
            assertEquals(ChessException.class.getName(), className);
            assertEquals("chess.square.invalid.coordinate", e.getMessage());
            assertNotNull(cause);
            assertEquals("file: z rank: -10", cause.getMessage());
        }
    }

    @Test
    void testFromIndexException() {
        try {
            var square = squareFromIndex((byte) -1);
            fail("chess.square.test.failed: " + square.toString());
        }
        catch (RuntimeException e) {
            var className = e.getClass().getName();
            var cause = e.getCause();
            assertEquals(ChessException.class.getName(), className);
            assertEquals("chess.square.invalid.index", e.getMessage());
            assertNotNull(cause);
            assertEquals("index: -1", cause.getMessage());
        }
    }

    @Test
    void testTranslateException() {
        try {
            var square = squareFromIndex((byte) 0).translate(-1, -1);
            fail("chess.square.test.failed: " + square.toString());
        }
        catch (RuntimeException e) {
            var className = e.getClass().getName();
            var cause = e.getCause();
            assertEquals(ChessException.class.getName(), className);
            assertEquals("chess.square.translate.delta.invalid", e.getMessage());
            assertNotNull(cause);
            assertEquals("square: a1 deltaFile: -1 deltaRank: -1", cause.getMessage());
        }
    }
}
