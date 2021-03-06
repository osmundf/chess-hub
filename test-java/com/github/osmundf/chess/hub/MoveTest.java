package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.BISHOP;
import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.KNIGHT;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
import static com.github.osmundf.chess.hub.Move.basicMove;
import static com.github.osmundf.chess.hub.Move.captureMove;
import static com.github.osmundf.chess.hub.Move.castleMove;
import static com.github.osmundf.chess.hub.Move.doublePushMove;
import static com.github.osmundf.chess.hub.Move.enPassantMove;
import static com.github.osmundf.chess.hub.Move.moveFor;
import static com.github.osmundf.chess.hub.MoveHelper.hashFor;
import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE_PROMOTION;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_LONG;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_SHORT;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.MoveType.PROMOTION;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.NO_SIDE;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * MoveTest class.
 *
 * @author Osmund
 * @since 1.0.0
 */
public class MoveTest {

    @Test
    void testBasicFromIndex() {
        final var side = WHITE;
        final var from = Square.A1;
        final var to = Square.B2;
        final var piece = pieceFor(side, QUEEN, from);
        final var actual = basicMove(piece, to);
        final var move = moveFor(actual.hashCode());
        assertSame(side, move.side());
        assertEquals(actual, move);
        assertFalse(move.capturedPiece().isPresent());
        assertFalse(move.promotionPiece().isPresent());
        assertNull(move.kingFrom());
        assertNull(move.kingTo());
        assertNull(move.rookFrom());
        assertNull(move.rookTo());
    }

    @Test
    void testCaptureFromIndex() {
        final var side = BLACK;
        final var from = Square.D2;
        final var to = Square.E3;
        final var piece = pieceFor(side, QUEEN, from);
        final var captured = pieceFor(side.opposite(), PAWN, to);
        final var actual = captureMove(piece, captured);
        final var hash = actual.hashCode();
        final var move = moveFor(hash);
        assertSame(side, move.side());
        assertEquals(actual, move);
        assertTrue(move.capturedPiece().isPresent());
    }

    @Test
    void testPawnMoveFromIndex() {
        final var hash = hashFor(BASE, WHITE, NONE, NONE, PAWN, Square.E2, Square.E3);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testDoublePushFromBasic() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 2 : 7);
                final var to = squareFor((char) ('a' + i), WHITE == side ? 4 : 5);
                final var piece = pieceFor(side, PAWN, from);
                final var move = basicMove(piece, to);
                assertEquals(DOUBLE_PUSH, move.type());
            }
        }
    }

    @Test
    void testDoublePush() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 2 : 7);
                final var to = squareFor((char) ('a' + i), WHITE == side ? 4 : 5);
                final var piece = pieceFor(side, PAWN, from);
                final var move = doublePushMove(piece);
                assertEquals(DOUBLE_PUSH, move.type());
                assertEquals(to, move.to());
            }
        }
    }

    @Test
    void testDoublePushFromIndex() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 2 : 7);
                final var to = squareFor((char) ('a' + i), WHITE == side ? 4 : 5);
                final var piece = pieceFor(side, PAWN, from);

                final var actual = doublePushMove(piece);
                final var hash = actual.hashCode();
                final var moveHash = actual.id();
                final var move = moveFor(hash);

                assertSame(side, move.side(), "chess.move.test.side");
                assertSame(NONE, actual.promotion(), "chess.move.test.promotion");
                assertSame(NONE, actual.capture(), "chess.move.test.capture.caste");
                assertSame(from, move.from(), "chess.move.test.from.square");
                assertSame(to, move.to(), "chess.move.test.to.square");
                assertSame(piece.caste(), move.base(), "chess.move.test.base.caste");
                assertEquals(actual, move, "chess.move.test.super.hash");

                assertSame(side, moveHash.side(), "chess.move.test.hash.side");
                assertSame(NONE, moveHash.promotion(), "chess.move.test.hash.promotion");
                assertSame(NONE, moveHash.capture(), "chess.move.test.hash.capture");
                assertSame(from, moveHash.from(), "chess.move.test.hash.from");
                assertSame(to, moveHash.to(), "chess.move.test.hash.to");
            }
        }
    }

    @Test
    void testNewMoveEnPassant() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 5 : 4);
                final var piece = pieceFor(side, PAWN, from);

                for (var delta = -1; delta <= 1; delta += 2) {
                    if ((i != 0 || delta != -1) && (i != 7 || delta != 1)) {
                        final var to = squareFor((char) ('a' + i + delta), WHITE == side ? 6 : 3);
                        final var move = enPassantMove(piece, to);
                        final var exception = move.validate();
                        if (exception != null) {
                            throw exception;
                        }
                    }
                }
            }
        }
    }

    @Test
    void testKnightMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var to : new Square[] {Square.C3, Square.D2}) {
                final var hash = hashFor(BASE, side, NONE, NONE, KNIGHT, Square.B1, to);
                final var move = moveFor(hash);
                assertNotNull(move);
            }
        }
    }

    @Test
    void testBishopMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var hash = hashFor(BASE, side, NONE, NONE, BISHOP, Square.C1, Square.A3);
            final var move = moveFor(hash);
            assertNotNull(move);
        }
    }

    @Test
    void testQueenMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var hash = hashFor(BASE, side, NONE, NONE, QUEEN, Square.D1, Square.B3);
            final var move = moveFor(hash);
            assertNotNull(move);
        }
    }

    @Test
    void testCastleKingSideFromIndex() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var kingFrom = WHITE == side ? Square.E1 : Square.E8;
            final var kingTo = WHITE == side ? Square.G1 : Square.G8;
            final var rookFrom = WHITE == side ? Square.H1 : Square.H8;
            final var rookTo = WHITE == side ? Square.F1 : Square.F8;

            final var hash = hashFor(CASTLE_SHORT, side, NONE, NONE, KING, kingFrom, rookFrom);
            final var move = moveFor(hash);
            assertNull(move.validate());
            assertSame(kingFrom, move.kingFrom());
            assertSame(kingTo, move.kingTo());
            assertSame(rookFrom, move.rookFrom());
            assertSame(rookTo, move.rookTo());
        }
    }

    @Test
    void testCastleQueenSideFromIndex() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var kingFrom = WHITE == side ? Square.E1 : Square.E8;
            final var kingTo = WHITE == side ? Square.C1 : Square.C8;
            final var rookFrom = WHITE == side ? Square.A1 : Square.A8;
            final var rookTo = WHITE == side ? Square.D1 : Square.D8;

            final var hash = hashFor(CASTLE_LONG, side, NONE, NONE, KING, kingFrom, rookFrom);
            final var move = moveFor(hash);
            assertNull(move.validate());
            assertSame(kingFrom, move.kingFrom());
            assertSame(kingTo, move.kingTo());
            assertSame(rookFrom, move.rookFrom());
            assertSame(rookTo, move.rookTo());
        }
    }

    @Test
    void testCastleKingSide() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var kingFrom = WHITE == side ? Square.E1 : Square.E8;
            final var kingTo = WHITE == side ? Square.G1 : Square.G8;
            final var rookFrom = WHITE == side ? Square.H1 : Square.H8;
            final var rookTo = WHITE == side ? Square.F1 : Square.F8;

            final var king = pieceFor(side, KING, kingFrom);
            final var rook = pieceFor(side, ROOK, rookFrom);
            final var move = castleMove(CASTLE_SHORT, king, rook);
            assertNull(move.validate());
            assertSame(kingFrom, move.kingFrom());
            assertSame(kingTo, move.kingTo());
            assertSame(rookFrom, move.rookFrom());
            assertSame(rookTo, move.rookTo());
        }
    }

    @Test
    void testCastleQueenSide() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var kingFrom = WHITE == side ? Square.E1 : Square.E8;
            final var kingTo = WHITE == side ? Square.C1 : Square.C8;
            final var rookFrom = WHITE == side ? Square.A1 : Square.A8;
            final var rookTo = WHITE == side ? Square.D1 : Square.D8;

            final var king = pieceFor(side, KING, kingFrom);
            final var rook = pieceFor(side, ROOK, rookFrom);
            final var move = castleMove(CASTLE_LONG, king, rook);
            assertNull(move.validate());
            assertSame(kingFrom, move.kingFrom());
            assertSame(kingTo, move.kingTo());
            assertSame(rookFrom, move.rookFrom());
            assertSame(rookTo, move.rookTo());
        }
    }

    @Test
    void testPawnPromotionFromIndex() {
        final var hash = hashFor(PROMOTION, WHITE, QUEEN, NONE, PAWN, Square.E7, Square.E8);
        final var move = moveFor(hash);
        final var queen = move.promotionPiece();
        assertTrue(queen.isPresent());
    }

    @Test
    void testPawnPromotionCaptureFromIndex() {
        final var hash = hashFor(CAPTURE_PROMOTION, WHITE, QUEEN, ROOK, PAWN, Square.E7, Square.D8);
        final var move = moveFor(hash);
        final var queen = move.promotionPiece();
        assertTrue(queen.isPresent());
    }

    @Test
    void testRookMove() {
        final var hash = hashFor(BASE, WHITE, NONE, NONE, ROOK, Square.F6, Square.F5);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testKingMove() {
        final var hash = hashFor(BASE, WHITE, NONE, NONE, KING, Square.D6, Square.E5);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testInvalidHash() {
        for (var i = 0x1; i < 0x7f; i++) {
            final var hash = i << 25;
            try {
                final var move = moveFor(hash);
                fail("chess.move.test.failed: " + move.toString());
            }
            catch (RuntimeException e) {
                final var className = e.getClass().getName();
                final var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.input.hash.invalid", e.getMessage());
                assertNotNull(cause);
                assertEquals(format("hash: 0x%08x", hash), cause.getMessage());
            }
        }
    }

    @Test
    void testPawnMoveTeleport() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            try {
                final var hash = hashFor(BASE, side, NONE, NONE, PAWN, Square.E2, Square.E7);
                final var move = moveFor(hash);
                fail("chess.move.test.failed.for.pawn.move.teleport: " + move.toString());
            }
            catch (RuntimeException e) {
                final var className = e.getClass().getName();
                final var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(cause);
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.E2, Square.E7);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            try {
                final var hash = hashFor(DOUBLE_PUSH, side, NONE, NONE, PAWN, Square.E2, Square.E7);
                final var move = moveFor(hash);
                fail("chess.move.test.failed.for.pawn.move.teleport: " + move.toString());
            }
            catch (RuntimeException e) {
                final var className = e.getClass().getName();
                final var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.invalid.double.push.move", e.getMessage());
                assertNotNull(cause);
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.E2, Square.E7);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            try {
                final var hash = hashFor(EN_PASSANT, side, NONE, PAWN, PAWN, Square.E2, Square.E7);
                final var move = moveFor(hash);
                fail("chess.move.test.failed.for.pawn.move.teleport: " + move.toString());
            }
            catch (RuntimeException e) {
                final var className = e.getClass().getName();
                final var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.invalid.en.passant.move", e.getMessage());
                assertNotNull(cause);
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.E2, Square.E7);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidDoublePushFromIndex() {
        final var type = DOUBLE_PUSH;
        final var targets = new Caste[] {PAWN, ROOK, KNIGHT, BISHOP, KING, QUEEN};

        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 2 : 7);
                final var to = squareFor((char) ('a' + i), WHITE == side ? 4 : 5);

                for (var promotion : targets) {
                    for (var capture : targets) {
                        final var hash = hashFor(type, side, promotion, capture, PAWN, from, to);
                        try {
                            final var move = moveFor(hash);
                            fail("chess.move.test.invalid.double.push.move: " + move);
                        }
                        catch (RuntimeException e) {
                            assertEquals(ChessException.class.getName(), e.getClass().getName());
                            assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                            assertNotNull(e.getCause());
                            final var template = "type: %s promotion: %s capture: %s";
                            final var causeMessage = format(template, type, promotion, capture);
                            assertEquals(causeMessage, e.getCause().getMessage());
                        }
                    }
                }
            }
        }
    }

    @Test
    void testInvalidDoublePushPiece() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 2 : 7);
                for (var base : Caste.values()) {
                    if (PAWN != base && NONE != base) {
                        try {
                            var piece = pieceFor(side, base, from);
                            var move = doublePushMove(piece);
                            fail("chess.move.test.invalid.double.push.move: " + move);
                        }
                        catch (RuntimeException e) {
                            assertEquals(ChessException.class, e.getClass());
                            assertEquals("chess.move.invalid.double.push.move", e.getMessage());
                            assertNotNull(e.getCause());
                            final var template = "base: %s";
                            final var causeMessage = format(template, base);
                            assertEquals(causeMessage, e.getCause().getMessage());
                        }
                    }
                }
            }
        }
    }

    @Test
    void testInvalidDoublePushSquare() {
        // Invalid double-push move: invalid side.
        for (final var side : new Side[] {null, NO_SIDE}) {
            try {
                var move = doublePushMove(side, null);
                fail("chess.move.test.invalid.double.push.move: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.double.push.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "side: %s";
                final var causeMessage = format(template, side);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }

        // Invalid double-push move: null from square.
        for (final var side : new Side[] {WHITE, BLACK}) {
            try {
                var move = doublePushMove(side, null);
                fail("chess.move.test.invalid.double.push.move: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.double.push.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s";
                final var causeMessage = format(template, "null");
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }

        // Invalid double-push move: invalid from square rank.
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (char file = 'a'; file <= 'h'; file++) {
                for (int rank = 1; rank <= 8; rank++) {
                    final var from = squareFor(file, rank);

                    if (WHITE == side && rank != 2 || BLACK == side && rank != 7) {
                        try {
                            var move = doublePushMove(side, from);
                            fail("chess.move.test.invalid.double.push.move: " + move);
                        }
                        catch (RuntimeException e) {
                            assertEquals(ChessException.class, e.getClass());
                            assertEquals("chess.move.invalid.double.push.move", e.getMessage());
                            assertNotNull(e.getCause());
                            final var template = "side: %s from: %s";
                            final var causeMessage = format(template, side, from);
                            assertEquals(causeMessage, e.getCause().getMessage());
                        }
                    }
                }
            }
        }
    }

    @Test
    void testInvalidEnPassant() {
        // Moving forward.
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = WHITE == side ? Square.C2 : Square.C7;
            final var to = WHITE == side ? Square.C3 : Square.C6;

            final var hash = hashFor(EN_PASSANT, side, PAWN, PAWN, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.en.passant.move: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, EN_PASSANT, PAWN, PAWN);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidEnPassantRank() {
        // Moving diagonal; invalid rank.
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = WHITE == side ? Square.D4 : Square.E5;
            final var to = WHITE == side ? Square.E5 : Square.D4;

            final var hash = hashFor(EN_PASSANT, side, NONE, PAWN, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.en.passant.move: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.en.passant.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, from, to);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidPawnPromotionSquares() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var hash = hashFor(PROMOTION, side, QUEEN, NONE, PAWN, Square.A2, Square.A8);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.pawn.promotion.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.promotion.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A2, Square.A8);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidPawnCaptureSquares() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = WHITE == side ? Square.A7 : Square.A2;
            final var to = WHITE == side ? Square.A8 : Square.A1;
            final var hash = hashFor(CAPTURE, side, NONE, ROOK, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.pawn.capture.promotion.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, from, to);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidPawnCapturePromotionSquares() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = WHITE == side ? Square.A7 : Square.A2;
            final var to = WHITE == side ? Square.A8 : Square.A1;
            final var hash = hashFor(CAPTURE_PROMOTION, side, QUEEN, ROOK, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.pawn.capture.promotion.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.capture.promotion.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, from, to);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidPawnCapturePromotionRank() {
        // Moving diagonal; invalid rank.
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = WHITE == side ? Square.D4 : Square.E5;
            final var to = WHITE == side ? Square.E5 : Square.D4;

            final var hash = hashFor(CAPTURE_PROMOTION, side, QUEEN, QUEEN, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.en.passant.move: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class, e.getClass());
                assertEquals("chess.move.invalid.capture.promotion.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, from, to);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidPawnMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = side.isWhite() ? Square.E2 : Square.E7;
            final var to = side.isWhite() ? Square.E4 : Square.E5;

            try {
                moveFor(hashFor(BASE, side, PAWN, KING, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, BASE, PAWN, KING);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            try {
                moveFor(hashFor(BASE, side, KING, NONE, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, BASE, KING, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            try {
                moveFor(hashFor(BASE, side, NONE, PAWN, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, BASE, NONE, PAWN);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Pawn cannot promote to none.
            try {
                moveFor(hashFor(PROMOTION, side, NONE, NONE, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, PROMOTION, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Pawn cannot promote to none.
            try {
                moveFor(hashFor(CAPTURE_PROMOTION, side, NONE, NONE, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CAPTURE_PROMOTION, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Pawn cannot castle.
            try {
                moveFor(hashFor(CASTLE_LONG, side, NONE, PAWN, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CASTLE_LONG, NONE, PAWN);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Pawn cannot castle.
            try {
                moveFor(hashFor(CASTLE_SHORT, side, NONE, PAWN, PAWN, from, to));
                fail("chess.move.test.invalid.pawn.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CASTLE_SHORT, NONE, PAWN);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testFriendlyPieceCapture() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = side.isWhite() ? Square.A1 : Square.A8;
            final var to = side.isWhite() ? Square.H1 : Square.H8;
            final var source = pieceFor(side, ROOK, from);
            final var capture = pieceFor(side, ROOK, to);

            try {
                captureMove(source, capture);
                fail("chess.move.test.friendly.piece.capture");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.captured.friendly.piece", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "captured: %s";
                final var causeMessage = format(template, capture);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            try {
                captureMove(source, capture);
                fail("chess.move.test.friendly.piece.capture");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.captured.friendly.piece", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "captured: %s";
                final var causeMessage = format(template, capture);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidKnightMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            // Knight capture without capture piece.
            try {
                moveFor(hashFor(CAPTURE, side, NONE, NONE, KNIGHT, Square.D5, Square.F6));
                fail("chess.move.test.invalid.knight.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.knight.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CAPTURE, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Knight invalid squares.
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, KNIGHT, Square.A1, Square.H8));
                fail("chess.move.test.invalid.knight.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.knight.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A1, Square.H8);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Knight invalid squares (stagnation).
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, KNIGHT, Square.A1, Square.A1));
                fail("chess.move.test.invalid.knight.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.knight.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A1, Square.A1);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidBishopMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            // Bishop capture without capture piece.
            try {
                var move = moveFor(hashFor(CAPTURE, side, NONE, NONE, BISHOP, Square.A8, Square.H1));
                fail("chess.move.test.invalid.bishop.move: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.bishop.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CAPTURE, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Bishop invalid squares.
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, BISHOP, Square.A1, Square.A8));
                fail("chess.move.test.invalid.bishop.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.bishop.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A1, Square.A8);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Bishop invalid squares (stagnation).
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, BISHOP, Square.A1, Square.A1));
                fail("chess.move.test.invalid.bishop.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.bishop.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A1, Square.A1);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidQueenMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            // Queen capture without capture piece.
            try {
                moveFor(hashFor(CAPTURE, side, NONE, NONE, QUEEN, Square.C1, Square.C4));
                fail("chess.move.test.invalid.queen.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.queen.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CAPTURE, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Queen invalid squares.
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, QUEEN, Square.C1, Square.F6));
                fail("chess.move.test.invalid.queen.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.queen.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.C1, Square.F6);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Queen invalid squares (stagnation).
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, QUEEN, Square.C1, Square.C1));
                fail("chess.move.test.invalid.queen.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.queen.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.C1, Square.C1);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidRookMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            // Rook capture without capture piece.
            try {
                moveFor(hashFor(BASE, side, ROOK, NONE, ROOK, Square.C8, Square.C4));
                fail("chess.move.test.rook.revocation.invalid");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.rook.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, BASE, ROOK, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Rook invalid squares.
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, ROOK, Square.C1, Square.F6));
                fail("chess.move.test.invalid.rook.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.rook.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.C1, Square.F6);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Rook invalid squares (stagnation).
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, ROOK, Square.C1, Square.C1));
                fail("chess.move.test.invalid.rook.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.rook.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.C1, Square.C1);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidKingMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            // King capture without capture piece.
            try {
                moveFor(hashFor(CAPTURE, side, NONE, NONE, KING, Square.D4, Square.E5));
                fail("chess.move.test.invalid.king.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.king.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s";
                final var causeMessage = format(template, CAPTURE, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // King invalid squares.
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, KING, Square.A1, Square.H8));
                fail("chess.move.test.invalid.rook.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.king.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A1, Square.H8);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // King invalid squares (stagnation).
            try {
                var move = moveFor(hashFor(BASE, side, NONE, NONE, KING, Square.A1, Square.A1));
                fail("chess.move.test.invalid.rook.base.with.invalid.squares: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.king.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "from: %s to: %s";
                final var causeMessage = format(template, Square.A1, Square.A1);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Invalid promotion.
            for (final var promotion : Caste.values()) {
                if (NONE != promotion && PAWN != promotion) {
                    try {
                        moveFor(hashFor(BASE, side, promotion, NONE, KING, Square.D4, Square.E5));
                        fail("chess.move.test.king.promotion.invalid");
                    }
                    catch (RuntimeException e) {
                        assertEquals(ChessException.class.getName(), e.getClass().getName());
                        assertEquals("chess.move.invalid.king.move", e.getMessage());
                        assertNotNull(e.getCause());
                        final var template = "type: %s promotion: %s capture: %s";
                        final var causeMessage = format(template, BASE, promotion, NONE);
                        assertEquals(causeMessage, e.getCause().getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testCastlingEnemyPiece() {
        final var king = pieceFor(WHITE, KING, Square.E1);
        final var rook = pieceFor(BLACK, ROOK, Square.H1);
        try {
            castleMove(CASTLE_SHORT, king, rook);
            fail("chess.move.test.castle.enemy.piece");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.move.castle.move.invalid", e.getMessage());
            assertNotNull(e.getCause());
            final var template = "king: %s rook: %s";
            final var causeMessage = format(template, king, rook);
            assertEquals(causeMessage, e.getCause().getMessage());
        }
    }

    @Test
    void testCastleInvalidMoveType() {
        final var king = pieceFor(WHITE, KING, Square.E1);
        final var rook = pieceFor(WHITE, ROOK, Square.H1);
        try {
            castleMove(BASE, king, rook);
            fail("chess.move.test.castle.invalid.move.type");
        }
        catch (RuntimeException e) {
            assertEquals(ChessException.class.getName(), e.getClass().getName());
            assertEquals("chess.move.castle.move.invalid", e.getMessage());
            assertNotNull(e.getCause());
            final var template = "type: %s";
            final var causeMessage = format(template, BASE);
            assertEquals(causeMessage, e.getCause().getMessage());
        }
    }

    @Test
    void testInvalidCastling() {
        for (final var type : new MoveType[] {CASTLE_SHORT, CASTLE_LONG}) {
            for (final var side : new Side[] {WHITE, BLACK}) {
                // Castle move with promotion.
                try {
                    final var kingSquare = WHITE == side ? squareFor('e', 1) : squareFor('e', 8);
                    final var rookSquare = CASTLE_SHORT == type ? kingSquare.left(4) : kingSquare.right(3);
                    final var hash = hashFor(type, side, PAWN, NONE, KING, kingSquare, rookSquare);
                    moveFor(hash);
                    fail("chess.move.test.invalid.castle.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.invalid.castle.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, type, PAWN, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                // Castle move with stagnation.
                try {
                    final var kingSquare = WHITE == side ? Square.E1 : Square.E8;
                    @SuppressWarnings("UnnecessaryLocalVariable") final var rookSquare = kingSquare;
                    final var hash = hashFor(type, side, PAWN, NONE, KING, kingSquare, rookSquare);
                    moveFor(hash);
                    fail("chess.move.test.invalid.castle.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.invalid.castle.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, type, PAWN, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testCastleShortMoveTeleport() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var kingFrom = WHITE == side ? Square.E2 : Square.E7;
            final var rookFrom = WHITE == side ? Square.A1 : Square.A8;
            final var kingTo = WHITE == side ? Square.G1 : Square.G8;
            final var rookTo = WHITE == side ? Square.F1 : Square.F8;
            // Castle move teleport.
            try {
                final var hash = hashFor(CASTLE_SHORT, side, NONE, NONE, KING, kingFrom, rookFrom);
                moveFor(hash);
                fail("chess.move.test.invalid.castle.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.castle.move", e.getMessage());
                assertNotNull(e.getCause());
                String template = "kingFrom: %s kingTo: %s rookFrom: %s rookTo: %s";
                final var causeMessage = format(template, kingFrom, kingTo, rookFrom, rookTo);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testCastleLongMoveTeleport() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var kingFrom = WHITE == side ? Square.E1 : Square.E8;
            final var rookFrom = WHITE == side ? Square.A2 : Square.A7;
            final var kingTo = WHITE == side ? Square.C1 : Square.C8;
            final var rookTo = WHITE == side ? Square.D1 : Square.D8;
            // Castle move teleport.
            try {
                final var hash = hashFor(CASTLE_LONG, side, NONE, NONE, KING, kingFrom, rookFrom);
                moveFor(hash);
                fail("chess.move.test.invalid.castle.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.castle.move", e.getMessage());
                assertNotNull(e.getCause());
                String template = "kingFrom: %s kingTo: %s rookFrom: %s rookTo: %s";
                final var causeMessage = format(template, kingFrom, kingTo, rookFrom, rookTo);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testInvalidMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            try {
                moveFor(hashFor(BASE, side, PAWN, NONE, NONE, Square.A1, Square.H8));
                fail("chess.move.test.invalid.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s promotion: %s capture: %s base: %s";
                final var causeMessage = format(template, BASE, PAWN, NONE, NONE);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testEquality() {
        final var move1 = moveFor(0x0);
        final var move2 = moveFor(0x0);
        assertNotSame(move1, move2);
        assertEquals(move1, move2);
        assertNotEquals(move1, null);
        assertNotEquals(move2, null);
    }

    @Test
    void testNullMove() {
        final var move = Move.moveFor(0x0);
        assertTrue(move.isNull());
    }

    @Test
    void testToString() {
        final var move = Move.moveFor(0x0);
        assertEquals("Move(0x00000000)", move.toString());
    }
}
