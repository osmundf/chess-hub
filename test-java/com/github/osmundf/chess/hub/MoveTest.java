package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Move.basicMove;
import static com.github.osmundf.chess.hub.Move.captureMove;
import static com.github.osmundf.chess.hub.Move.doublePush;
import static com.github.osmundf.chess.hub.Move.moveFor;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        final var from = squareFor('a', 1);
        final var to = squareFor('b', 2);
        final var piece = pieceFor(side, KING, from);
        final var actual = basicMove(piece, REVOKE_NONE, to);
        final var move = moveFor(actual.hashCode());
        assertSame(side, move.side());
        assertEquals(actual, move);
    }

    @Test
    void testFromIndexCapture() {
        final var side = BLACK;
        final var from = squareFor('e', 1);
        final var to = squareFor('e', 2);
        final var piece = pieceFor(side, QUEEN, from);
        final var captured = pieceFor(side.opposite(), PAWN, to);
        final var actual = captureMove(piece, captured);
        final var hash = actual.hashCode();
        final var move = moveFor(hash);
        assertSame(side, move.side());
        assertEquals(actual, move);
    }

    @Test
    void testNewBasic() {
//        fail("TODO");
    }

    @Test
    void testNewCapture() {
//        fail("TODO");
    }

    @Test
    void testFromIndexDoublePush() {
        final var side = WHITE;
        final var from = squareFor('a', 2);
        final var to = squareFor('a', 4);
        final var piece = pieceFor(side, PAWN, from);
        final var actual = doublePush(piece);

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
        assertSame(PAWN, moveHash.promotion(), "test.move.hash.promotion");
        assertSame(NONE, moveHash.capture(), "test.move.hash.capture");
        assertSame(from, moveHash.from(), "test.move.hash.from");
        assertSame(to, moveHash.to(), "test.move.hash.to");

        final var revocation = move.revocation();
        assertFalse(revocation.isKingSide());
        assertFalse(revocation.isQueenSide());
    }

    @Test
    void testNewMoveEnPassant() {
//        fail("TODO");
    }

    @Test
    void testFromIndexCastlingKingSide() {
//        fail("TODO");
    }

    @Test
    void testFromIndexCastlingQueenSide() {
//        fail("TODO");
    }

    @Test
    void testFromIndexPawnPromotion() {
//        fail("TODO");
    }

    @Test
    void testFromIndexPawnPromotionCapture() {
//        fail("TODO");
    }

}
