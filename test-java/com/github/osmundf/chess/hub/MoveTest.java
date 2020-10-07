package com.github.osmundf.chess.hub;

import java.util.Objects;

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
import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE_PROMOTION;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_LONG;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_SHORT;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.MoveType.PROMOTION;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_BOTH;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_KING_SIDE;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_QUEEN_SIDE;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testFromIndexBasic() {
        final var side = WHITE;
        final var from = Square.A1;
        final var to = Square.B2;
        final var piece = pieceFor(side, QUEEN, from);
        final var actual = basicMove(piece, REVOKE_NONE, to);
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
    void testFromIndexCapture() {
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
        final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, PAWN, Square.E2, Square.E3);
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
    void testFromIndexDoublePush() {
        final var side = WHITE;
        final var from = squareFor('a', 2);
        final var to = squareFor('a', 4);
        final var piece = pieceFor(side, PAWN, from);
        final var actual = doublePushMove(piece);

        final var hash = actual.hashCode();
        final var moveHash = actual.hash();
        final var move = moveFor(hash);

        assertSame(side, move.side());
        assertSame(NONE, actual.promotion(), "test.move.promotion");
        assertSame(NONE, actual.capture(), "test.move.capture");
        assertSame(from, move.from(), "test.move.from");
        assertSame(to, move.to(), "test.move.to");
        assertSame(piece.caste(), move.base(), "test.move.base");
        assertEquals(actual, move, "test.move.super.hash");

        assertSame(side, moveHash.side(), "test.move.hash.side");
        assertSame(NONE, moveHash.promotion(), "test.move.hash.promotion");
        assertSame(NONE, moveHash.capture(), "test.move.hash.capture");
        assertSame(from, moveHash.from(), "test.move.hash.from");
        assertSame(to, moveHash.to(), "test.move.hash.to");

        final var revocation = move.revocation();
        assertFalse(revocation.isKingSide());
        assertFalse(revocation.isQueenSide());
    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    void testNewMoveEnPassant() {
        final var side = WHITE;
        final var base = PAWN;
        final var from = Square.D5;
        final var piece = pieceFor(side, base, from);
        final var to = Square.E6;
        final var move = enPassantMove(piece, to);
        final var exception = move.validate();
        if (exception != null) {
            throw exception;
        }
    }

    @Test
    void testKnightMove() {
        {
            final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, KNIGHT, Square.B1, Square.C3);
            final var move = moveFor(hash);
            assertNotNull(move);
        }
        {
            final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, KNIGHT, Square.B1, Square.D2);
            final var move = moveFor(hash);
            assertNotNull(move);
        }
    }

    @Test
    void testBishopMove() {
        final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, BISHOP, Square.C1, Square.A3);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testQueenMove() {
        final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, QUEEN, Square.D1, Square.B3);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testFromIndexCastlingKingSide() {
        final var hash = hashFor(CASTLE_SHORT, WHITE, REVOKE_KING_SIDE, NONE, NONE, KING, Square.E1, Square.H1);
        final var move = moveFor(hash);
        assertSame(Square.E1, move.kingFrom());
        assertSame(Square.G1, move.kingTo());
        assertSame(Square.H1, move.rookFrom());
        assertSame(Square.F1, move.rookTo());
        assertTrue(move.revokedKingSide());
        assertFalse(move.revokedQueenSide());
    }

    @Test
    void testFromIndexCastlingQueenSide() {
        final var hash = hashFor(CASTLE_LONG, WHITE, REVOKE_QUEEN_SIDE, NONE, NONE, KING, Square.E1, Square.A1);
        final var move = moveFor(hash);
        assertSame(Square.E1, move.kingFrom());
        assertSame(Square.C1, move.kingTo());
        assertSame(Square.A1, move.rookFrom());
        assertSame(Square.D1, move.rookTo());
        assertFalse(move.revokedKingSide());
        assertTrue(move.revokedQueenSide());
    }

    @Test
    void testCastleMoveLong() {
        final var king = pieceFor(BLACK, KING, Square.E8);
        final var rook = pieceFor(BLACK, ROOK, Square.A8);
        final var move = castleMove(CASTLE_LONG, REVOKE_BOTH, king, rook);
        assertNull(move.validate());
    }

    @Test
    void testCastleMoveShort() {
        final var king = pieceFor(BLACK, KING, Square.E8);
        final var rook = pieceFor(BLACK, ROOK, Square.H8);
        final var move = castleMove(CASTLE_SHORT, REVOKE_BOTH, king, rook);
        assertNull(move.validate());
    }

    @Test
    void testFromIndexPawnPromotion() {
        final var hash = hashFor(PROMOTION, WHITE, REVOKE_NONE, QUEEN, NONE, PAWN, Square.E7, Square.E8);
        final var move = moveFor(hash);
        final var queen = move.promotionPiece();
        assertTrue(queen.isPresent());
    }

    @Test
    void testFromIndexPawnPromotionCapture() {
        final var hash = hashFor(CAPTURE_PROMOTION, WHITE, REVOKE_NONE, QUEEN, ROOK, PAWN, Square.E7, Square.D8);
        final var move = moveFor(hash);
        final var queen = move.promotionPiece();
        assertTrue(queen.isPresent());
    }

    @Test
    void testRookMove() {
        final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, ROOK, Square.F6, Square.F5);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testKingMove() {
        final var hash = hashFor(BASE, WHITE, REVOKE_NONE, NONE, NONE, KING, Square.D6, Square.E5);
        final var move = moveFor(hash);
        assertNotNull(move);
    }

    @Test
    void testInvalidHash() {
        for (var i = 0x1; i < 0x1f; i++) {
            final var hash = i << 27;
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
    void testPawnMoveFromIndexWithRevocation() {
        final var revocations = new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_BOTH};
        for (var revocation : revocations) {
            final var hash = hashFor(BASE, WHITE, revocation, NONE, NONE, PAWN, Square.E2, Square.E3);
            try {
                final var move = moveFor(hash);
                fail("chess.move.test.failed.for.pawn.move.from.index.with.revocation: " + move.toString());
            }
            catch (RuntimeException e) {
                final var className = e.getClass().getName();
                final var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.invalid.pawn.move", e.getMessage());
                assertNotNull(cause);
                final var template = "type: %s revocation: %s";
                final var causeMessage = format(template, BASE, revocation);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testPawnMoveTeleport() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            try {
                final var hash = hashFor(BASE, side, REVOKE_NONE, NONE, NONE, PAWN, Square.E2, Square.E7);
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
                final var hash = hashFor(DOUBLE_PUSH, side, REVOKE_NONE, NONE, NONE, PAWN, Square.E2, Square.E7);
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
                final var hash = hashFor(EN_PASSANT, side, REVOKE_NONE, NONE, PAWN, PAWN, Square.E2, Square.E7);
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
    void testInvalidDoublePush() {
        final var type = DOUBLE_PUSH;
        final var targets = new Caste[] {PAWN, ROOK, KNIGHT, BISHOP, KING, QUEEN};

        for (final var side : new Side[] {WHITE, BLACK}) {
            for (int i = 0; i < 8; i++) {
                final var from = squareFor((char) ('a' + i), WHITE == side ? 2 : 7);
                final var to = squareFor((char) ('a' + i), WHITE == side ? 4 : 5);

                for (var promotion : targets) {
                    for (var capture : targets) {
                        final var hash = hashFor(type, side, REVOKE_NONE, promotion, capture, PAWN, from, to);
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
    void testInvalidEnPassant() {
        // Moving forward.
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = WHITE == side ? Square.C2 : Square.C7;
            final var to = WHITE == side ? Square.C3 : Square.C6;

            final var hash = hashFor(EN_PASSANT, side, REVOKE_NONE, PAWN, PAWN, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.en.passant.move: " + move);
            }
            catch (RuntimeException e) {
                if (Objects.equals(NullPointerException.class, e.getClass())) {
                    e.printStackTrace();
                }

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

            final var hash = hashFor(EN_PASSANT, side, REVOKE_NONE, NONE, PAWN, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.en.passant.move: " + move);
            }
            catch (RuntimeException e) {
                if (Objects.equals(NullPointerException.class, e.getClass())) {
                    e.printStackTrace();
                }

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
            final var hash = hashFor(PROMOTION, side, REVOKE_NONE, QUEEN, NONE, PAWN, Square.A2, Square.A8);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.pawn.promotion.squares: " + move);
            }
            catch (RuntimeException e) {
                if (Objects.equals(NullPointerException.class, e.getClass())) {
                    e.printStackTrace();
                }

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
            final var hash = hashFor(CAPTURE, side, REVOKE_NONE, NONE, ROOK, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.pawn.capture.promotion.squares: " + move);
            }
            catch (RuntimeException e) {
                if (Objects.equals(NullPointerException.class, e.getClass())) {
                    e.printStackTrace();
                }

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
            final var hash = hashFor(CAPTURE_PROMOTION, side, REVOKE_NONE, QUEEN, ROOK, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.pawn.capture.promotion.squares: " + move);
            }
            catch (RuntimeException e) {
                if (Objects.equals(NullPointerException.class, e.getClass())) {
                    e.printStackTrace();
                }

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

            final var hash = hashFor(CAPTURE_PROMOTION, side, REVOKE_NONE, QUEEN, QUEEN, PAWN, from, to);
            try {
                var move = moveFor(hash);
                fail("chess.move.test.invalid.en.passant.move: " + move);
            }
            catch (RuntimeException e) {
                if (Objects.equals(NullPointerException.class, e.getClass())) {
                    e.printStackTrace();
                }

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
                moveFor(hashFor(BASE, side, REVOKE_NONE, PAWN, KING, PAWN, from, to));
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
                moveFor(hashFor(BASE, side, REVOKE_NONE, KING, NONE, PAWN, from, to));
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
                moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, PAWN, PAWN, from, to));
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
                moveFor(hashFor(PROMOTION, side, REVOKE_NONE, NONE, NONE, PAWN, from, to));
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
                moveFor(hashFor(CAPTURE_PROMOTION, side, REVOKE_NONE, NONE, NONE, PAWN, from, to));
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
                moveFor(hashFor(CASTLE_LONG, side, REVOKE_NONE, NONE, PAWN, PAWN, from, to));
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
                moveFor(hashFor(CASTLE_SHORT, side, REVOKE_NONE, NONE, PAWN, PAWN, from, to));
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
    void testRookMoveWithoutRevocation() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = side.isWhite() ? Square.A1 : Square.A8;
            final var to = side.isWhite() ? Square.A8 : Square.A1;
            final var piece = pieceFor(side, ROOK, from);
            try {
                basicMove(piece, to);
                fail("chess.move.test.invalid.rook.move");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.revocation.missing", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "base: %s";
                final var causeMessage = format(template, ROOK);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testPawnMoveWithRevocation() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (var revocation : new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_BOTH}) {
                final var from = side.isWhite() ? Square.E2 : Square.E7;
                final var to = side.isWhite() ? Square.E3 : Square.E6;
                final var piece = pieceFor(side, PAWN, from);
                try {
                    basicMove(piece, revocation, to);
                    fail("chess.move.test.invalid.rook.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.revocation.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "revocation: %s base: %s";
                    final var causeMessage = format(template, revocation, PAWN);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
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
                captureMove(source, REVOKE_NONE, capture);
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
    void testCaptureWithoutRevocation() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            final var from = side.isWhite() ? Square.A1 : Square.A8;
            final var to = side.isWhite() ? Square.H1 : Square.H8;
            final var source = pieceFor(side, ROOK, from);
            final var capture = pieceFor(side.opposite(), ROOK, to);

            try {
                captureMove(source, capture);
                fail("chess.move.test.capture.without.revocation");
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.revocation.missing", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "base: %s";
                final var causeMessage = format(template, ROOK);
                assertEquals(causeMessage, e.getCause().getMessage());
            }
        }
    }

    @Test
    void testQueenCaptureWithRevocation() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (var revocation : new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_BOTH}) {
                final var from = side.isWhite() ? Square.A1 : Square.A8;
                final var to = side.isWhite() ? Square.H1 : Square.H8;
                final var source = pieceFor(side, QUEEN, from);
                final var capture = pieceFor(side.opposite(), QUEEN, to);

                try {
                    captureMove(source, revocation, capture);
                    fail("chess.move.test.friendly.piece.capture");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.revocation.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "revocation: %s base: %s";
                    final var causeMessage = format(template, revocation, QUEEN);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidKnightMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            // Base with revocation.
            for (var revocation : new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_BOTH}) {
                try {
                    var move = moveFor(hashFor(BASE, side, revocation, NONE, NONE, KNIGHT, Square.D5, Square.F6));
                    fail("chess.move.test.invalid.knight.base.with.revocation: " + move);
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.invalid.knight.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s revocation: %s";
                    final var causeMessage = format(template, BASE, revocation);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }

            // Capture without capture piece.
            try {
                moveFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, KNIGHT, Square.D5, Square.F6));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, KNIGHT, Square.A1, Square.H8));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, KNIGHT, Square.A1, Square.A1));
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
            // Base with revocation.
            for (var revocation : new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_BOTH}) {
                try {
                    var move = moveFor(hashFor(BASE, side, revocation, NONE, NONE, BISHOP, Square.D5, Square.F6));
                    fail("chess.move.test.invalid.bishop.base.with.revocation: " + move);
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.invalid.bishop.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s revocation: %s";
                    final var causeMessage = format(template, BASE, revocation);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }

            // Capture without capture piece.
            try {
                var move = moveFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, BISHOP, Square.A8, Square.H1));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, BISHOP, Square.A1, Square.A8));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, BISHOP, Square.A1, Square.A1));
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
            // Base with revocation.
            for (var revocation : new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_BOTH}) {
                try {
                    var move = moveFor(hashFor(BASE, side, revocation, NONE, NONE, QUEEN, Square.D5, Square.E6));
                    fail("chess.move.test.invalid.queen.base.with.revocation: " + move);
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.invalid.queen.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s revocation: %s";
                    final var causeMessage = format(template, BASE, revocation);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }

            // Capture without capture piece.
            try {
                moveFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, QUEEN, Square.C1, Square.C4));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, QUEEN, Square.C1, Square.F6));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, QUEEN, Square.C1, Square.C1));
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
            // Base with invalid revocation.
            try {
                var move = moveFor(hashFor(BASE, side, REVOKE_BOTH, NONE, NONE, ROOK, Square.D5, Square.E6));
                fail("chess.move.test.invalid.rook.base.with.revocation: " + move);
            }
            catch (RuntimeException e) {
                assertEquals(ChessException.class.getName(), e.getClass().getName());
                assertEquals("chess.move.invalid.rook.move", e.getMessage());
                assertNotNull(e.getCause());
                final var template = "type: %s revocation: %s";
                final var causeMessage = format(template, BASE, REVOKE_BOTH);
                assertEquals(causeMessage, e.getCause().getMessage());
            }

            // Capture without capture piece.
            try {
                moveFor(hashFor(BASE, side, REVOKE_NONE, ROOK, NONE, ROOK, Square.C8, Square.C4));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, ROOK, Square.C1, Square.F6));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, ROOK, Square.C1, Square.C1));
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
                moveFor(hashFor(BASE, side, REVOKE_NONE, ROOK, NONE, ROOK, Square.C8, Square.C4));
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

            // King invalid squares.
            try {
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, KING, Square.A1, Square.H8));
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
                var move = moveFor(hashFor(BASE, side, REVOKE_NONE, NONE, NONE, KING, Square.A1, Square.A1));
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

            for (final var revocation : Revocation.values()) {
                // Invalid promotion.
                for (final var promotion : Caste.values()) {
                    if (NONE != promotion && PAWN != promotion) {
                        try {
                            moveFor(hashFor(BASE, side, revocation, promotion, NONE, KING, Square.D4, Square.E5));
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

                try {
                    moveFor(hashFor(CAPTURE, side, revocation, NONE, NONE, KING, Square.D4, Square.E5));
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
            }
        }
    }

    @Test
    void testCastlingEnemyPiece() {
        final var king = pieceFor(WHITE, KING, Square.E1);
        final var rook = pieceFor(BLACK, ROOK, Square.H1);
        try {
            castleMove(CASTLE_SHORT, REVOKE_BOTH, king, rook);
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
            castleMove(BASE, REVOKE_BOTH, king, rook);
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
                // Castle move without any revocation.
                try {
                    final var kingSquare = WHITE == side ? squareFor('e', 1) : squareFor('e', 8);
                    final var rookSquare = CASTLE_SHORT == type ? kingSquare.left(4) : kingSquare.right(3);
                    final var hash = hashFor(type, side, REVOKE_NONE, PAWN, NONE, KING, kingSquare, rookSquare);
                    moveFor(hash);
                    fail("chess.move.test.invalid.castle.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.invalid.castle.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s revocation: %s";
                    final var causeMessage = format(template, type, REVOKE_NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                // Castle move with promotion.
                try {
                    final var kingSquare = WHITE == side ? squareFor('e', 1) : squareFor('e', 8);
                    final var rookSquare = CASTLE_SHORT == type ? kingSquare.left(4) : kingSquare.right(3);
                    final var hash = hashFor(type, side, REVOKE_BOTH, PAWN, NONE, KING, kingSquare, rookSquare);
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
                    final var hash = hashFor(type, side, REVOKE_BOTH, PAWN, NONE, KING, kingSquare, rookSquare);
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
                final var hash = hashFor(CASTLE_SHORT, side, REVOKE_BOTH, NONE, NONE, KING, kingFrom, rookFrom);
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
                final var hash = hashFor(CASTLE_LONG, side, REVOKE_BOTH, NONE, NONE, KING, kingFrom, rookFrom);
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
                moveFor(hashFor(BASE, side, REVOKE_NONE, PAWN, NONE, NONE, Square.A1, Square.H8));
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
        assertNotEquals(move1, null);
        assertNotEquals(move2, null);
        assertEquals(move1, move2);
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

    private int hashFor(MoveType m, Side s, Revocation r, Caste p, Caste c, Caste b, Square f, Square t) {
        return new MoveIndexer(m, s, r, p, c, b, f, t).hash;
    }

    private static class MoveIndexer extends MoveHash {

        /** Private constructor. */
        private MoveIndexer(MoveType m, Side s, Revocation r, Caste p, Caste c, Caste b, Square f, Square t) {
            super(m, s, r, p, c, b, f, t);
        }
    }
}
