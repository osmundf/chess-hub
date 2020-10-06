package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.Move.basicMove;
import static com.github.osmundf.chess.hub.Move.captureMove;
import static com.github.osmundf.chess.hub.Move.doublePush;
import static com.github.osmundf.chess.hub.Move.moveFor;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * MoveTest class.
 *
 * @author Osmund
 * @version $Id: $Id
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
        final var piece = pieceFor(side, KING, from);
        final var captured = pieceFor(side.opposite(), PAWN, to);
        final var actual = captureMove(piece, captured);
        final var hash = actual.hashCode();
        final var move = moveFor(hash);
        assertSame(side, move.side());
        assertEquals(actual, move);
    }

    @Test
    @Disabled
    void testFromIndexDoublePush() {
        final var side = WHITE;
        final var from = squareFor('a', 2);
        final var to = squareFor('a', 4);
        final var piece = pieceFor(side, PAWN, from);
        final var actual = doublePush(piece);
        final var hash = actual.hashCode();
        final var move = moveFor(hash);
        assertSame(side, move.side());
        assertEquals(actual, move);
    }

    @Test
    @Disabled
    void testFromIndexCastling() {
        fail("TODO");
    }

    @Test
    @Disabled
    void testNewBasic() {
        fail("TODO");
    }

    @Test
    @Disabled
    void testNewCapture() {
        fail("TODO");
    }

    @Test
    @Disabled
    void testNewMoveEnPassant() {
        fail("TODO");
    }
}
