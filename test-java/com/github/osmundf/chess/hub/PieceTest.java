package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Side.NO_SIDE;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class PieceTest {

    @Test
    void testRange() {
        final var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var square = squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (final var side : Side.values()) {
            if (NO_SIDE == side) {
                continue;
            }
            for (final var caste : Caste.values()) {
                if (NONE == caste) {
                    continue;
                }
                for (final var square : squareArray) {
                    final var piece = pieceFor(side, caste, square);

                    assertEquals(piece.side(), side);
                    assertEquals(piece.caste(), caste);
                    assertEquals(piece.square(), square);
                }
            }
        }
    }

    @Test
    void testIndex() {
        final var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var square = squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (final var side : Side.values()) {
            if (NO_SIDE == side) {
                continue;
            }
            for (final var caste : Caste.values()) {
                if (NONE == caste) {
                    continue;
                }
                for (final var square : squareArray) {
                    final var piece = pieceFor(side, caste, square);
                    final var expected = (side.index() << 9) + (caste.index() << 6) + square.index();
                    assertEquals(expected, piece.hashCode());
                }
            }
        }
    }

    @Test
    void testNoSideException() {
        final var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var square = squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (final var caste : Caste.values()) {
            if (NONE == caste) {
                continue;
            }
            for (final var square : squareArray) {
                try {
                    pieceFor(null, caste, square);
                    fail("chess.piece.test.expected.chess.exception.null.side");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.null.argument", e.getMessage());
                    final var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.side.null", cause.getMessage());
                }

                try {
                    pieceFor(NO_SIDE, caste, square);
                    fail("chess.piece.test.expected.chess.exception.no.side");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.no.side", e.getMessage());
                    final var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.side.none", cause.getMessage());
                }
            }
        }
    }

    @Test
    void testNoCasteException() {
        final var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                final var square = squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (final var side : Side.values()) {
            if (NO_SIDE == side) {
                continue;
            }
            for (final var square : squareArray) {
                try {
                    pieceFor(side, null, square);
                    fail("chess.piece.test.expected.chess.exception.null.caste");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.null.argument", e.getMessage());
                    final var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.caste.null", cause.getMessage());
                }

                try {
                    pieceFor(side, NONE, square);
                    fail("chess.piece.test.expected.chess.exception.no.caste");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.no.caste", e.getMessage());
                    final var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.caste.none", cause.getMessage());
                }
            }
        }
    }

    @Test
    void testNoSquareException() {
        for (final var side : Side.values()) {
            if (NO_SIDE == side) {
                continue;
            }
            for (final var caste : Caste.values()) {
                if (NONE == caste) {
                    continue;
                }
                try {
                    pieceFor(side, caste, null);
                    fail("chess.piece.test.expected.chess.exception.null.square");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.null.argument", e.getMessage());
                    final var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.square.null", cause.getMessage());
                }
            }
        }
    }

    @Test
    void testEquals() {
        for (final var s1 : Side.values()) {
            for (final var c1 : Caste.values()) {
                for (final var t1 : Square.values()) {
                    final var first = new PiecePiggy(s1, c1, t1).asPiece();

                    for (final var s2 : Side.values()) {
                        for (final var c2 : Caste.values()) {
                            for (final var t2 : Square.values()) {
                                final var second = new PiecePiggy(s2, c2, t2).asPiece();

                                if (first.equals(second)) {
                                    assertSame(s1, s2);
                                    assertSame(c1, c2);
                                    assertSame(t1, t2);
                                }

                                //noinspection ConstantConditions
                                if (first.equals(null)) {
                                    fail("chess.piece.equal.on.null");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testToString() {
        final var side = WHITE;
        final var caste = KING;
        final var square = squareFor('e', 1);
        final var piece = pieceFor(side, caste, square);
        final var expected = side + "." + caste + "." + square;
        assertEquals(expected, piece.toString());
    }

    private static class PiecePiggy extends Piece {
        protected PiecePiggy(Side side, Caste caste, Square square) {
            super(side, caste, square);
        }

        private Piece asPiece() {
            return this;
        }
    }
}
