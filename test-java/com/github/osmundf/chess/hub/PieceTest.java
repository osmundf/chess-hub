package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class PieceTest {

    @Test
    void testRange() {
        var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                var square = Square.squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (var side : Side.values()) {
            if (Side.NO_SIDE == side) {
                continue;
            }
            for (var caste : Caste.values()) {
                if (Caste.NONE == caste) {
                    continue;
                }
                for (var square : squareArray) {
                    var piece = Piece.newPiece(side, caste, square);

                    assertEquals(piece.side(), side);
                    assertEquals(piece.caste(), caste);
                    assertEquals(piece.square(), square);
                }
            }
        }
    }

    @Test
    void testIndex() {
        var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                var square = Square.squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (var side : Side.values()) {
            if (Side.NO_SIDE == side) {
                continue;
            }
            for (var caste : Caste.values()) {
                if (Caste.NONE == caste) {
                    continue;
                }
                for (var square : squareArray) {
                    var piece = Piece.newPiece(side, caste, square);
                    var expected = (side.index() << 9) + (caste.index() << 6) + square.index();
                    assertEquals(expected, piece.hashCode());
                }
            }
        }
    }

    @Test
    void testNoSideException() {
        var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                var square = Square.squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (var caste : Caste.values()) {
            if (Caste.NONE == caste) {
                continue;
            }
            for (var square : squareArray) {
                try {
                    var piece = Piece.newPiece(null, caste, square);
                    fail("piece.test.expected.runtime.exception.null.side");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.null.argument", e.getMessage());
                    var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.side.null", cause.getMessage());
                }

                try {
                    var piece = Piece.newPiece(Side.NO_SIDE, caste, square);
                    fail("piece.test.expected.runtime.exception.no.side");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.no.side", e.getMessage());
                    var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.side.none", cause.getMessage());
                }
            }
        }
    }

    @Test
    void testNoCasteException() {
        var squareArray = new Square[64];
        for (var file = 'a'; file <= 'h'; file++) {
            for (var rank = 1; rank <= 8; rank++) {
                var square = Square.squareFor(file, rank);
                squareArray[square.index()] = square;
            }
        }

        for (var side : Side.values()) {
            if (Side.NO_SIDE == side) {
                continue;
            }
            for (var square : squareArray) {
                try {
                    var piece = Piece.newPiece(side, null, square);
                    fail("piece.test.expected.runtime.exception.null.caste");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.null.argument", e.getMessage());
                    var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.caste.null", cause.getMessage());
                }

                try {
                    var piece = Piece.newPiece(side, Caste.NONE, square);
                    fail("piece.test.expected.runtime.exception.no.caste");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.no.caste", e.getMessage());
                    var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.caste.none", cause.getMessage());
                }
            }
        }
    }

    @Test
    void testNoSquareException() {
        for (var side : Side.values()) {
            if (Side.NO_SIDE == side) {
                continue;
            }
            for (var caste : Caste.values()) {
                if (Caste.NONE == caste) {
                    continue;
                }
                try {
                    var piece = Piece.newPiece(side, caste, null);
                    fail("piece.test.expected.runtime.exception.null.square");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.piece.new.piece.null.argument", e.getMessage());
                    var cause = e.getCause();
                    assertNotNull(cause);
                    assertEquals("chess.square.null", cause.getMessage());
                }
            }
        }
    }
}
