package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Square.newSquare;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class SquareTest {

    @Test
    void testRange() {
        for (var file = 'a'; file <= 'h'; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                var square = newSquare(file, rank);
                var index = ((rank - 1) << 3) | (file - 'a');
                assertEquals(file, square.file());
                assertEquals(rank, square.rank());
                assertEquals(index, square.index());
            }
        }
    }

    @Test
    void testException() {
        try {
            var square = newSquare('z', -10);
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
}
