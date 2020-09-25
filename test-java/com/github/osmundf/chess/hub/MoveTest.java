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

public class MoveTest {

    @Test
    void testFromIndexBasic() {
        var side = WHITE;
        var from = squareFor('a', 1);
        var to = squareFor('b', 2);
        var piece = pieceFor(side, KING, from);
        var actual = basicMove(piece, REVOKE_NONE, to);
        var move = moveFor(actual.hashCode());
        assertSame(side, move.side());
        assertEquals(actual, move);
    }

    @Test
    void testFromIndexCapture() {
        var side = BLACK;
        var from = squareFor('e', 1);
        var to = squareFor('e', 2);
        var piece = pieceFor(side, KING, from);
        var captured = pieceFor(side.opposite(), PAWN, to);
        var actual = captureMove(piece, captured);
        var hash = actual.hashCode();
        var move = moveFor(hash);
        assertSame(side, move.side());
        assertEquals(actual, move);
    }

    @Test
    @Disabled
    void testFromIndexDoublePush() {
        var side = WHITE;
        var from = squareFor('a', 2);
        var to = squareFor('a', 4);
        var piece = pieceFor(side, PAWN, from);
        var actual = doublePush(piece);
        var hash = actual.hashCode();
        var move = moveFor(hash);
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
