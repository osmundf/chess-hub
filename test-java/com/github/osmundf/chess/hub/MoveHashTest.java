package com.github.osmundf.chess.hub;

import org.junit.jupiter.api.Test;

import static com.github.osmundf.chess.hub.Caste.BISHOP;
import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.KNIGHT;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
import static com.github.osmundf.chess.hub.MoveHash.moveHashFor;
import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_LONG;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_SHORT;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class MoveHashTest {

    @Test
    void testRange() {
        // null move:
        // type[000] side[s] promotion[000] capture[000] base[000] from[rrr,fff] to[rrr,fff]

        // base/capture (pawn/knight/bishop/queen):
        // type[ttt] side[s] promotion[000] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]
        // type[ttt] side[s] promotion[000] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]

        // base/capture (king/rook):
        // type[ttt] side[s] castle[ppp] capture[ccc] base[110] from[rrr,fff] to[rrr,fff]
        // type[ttt] side[s] castle[ppp] capture[ccc] base[100] from[rrr,fff] to[rrr,fff]

        // castling (type: [castle_short,castle_long]):
        // type[ttt] side[s] promotion[001] castle[ccc] base[110] king[xxx,xxx] rook[xxx,xxx]

        // basic (double-push):
        // type[ttt] side[s] promotion[001] capture[000] base[001] from[rrr,fff] to[rrr,fff]

        // en-passant
        // type[ttt] side[s] promotion[001] capture[001] base[001] from[rrr,fff] to[rrr,fff]

        // promotion/capture-promotion:
        // type[ttt] side[s] promotion[ppp] capture[ccc] base[001] from[rrr,fff] to[rrr,fff]

        var anyRevocation = new Caste[] {ROOK, KING, QUEEN, NONE};
        var kingRevocation = new Caste[] {ROOK, KING, QUEEN};
        var rookRevocation = new Caste[] {KING, QUEEN, NONE};
        var boardSide = new Side[] {WHITE, BLACK};
        var target = new Caste[] {PAWN, KNIGHT, BISHOP, ROOK, QUEEN};

        for (var type : new MoveType[] {BASE}) {
            for (var side : boardSide) {
                // Base king move.
                for (var base : new Caste[] {KING}) {
                    for (var promotion : anyRevocation) {
                        assertMove(type, side, promotion, NONE, base);
                    }
                }

                // Base rook move.
                for (var base : new Caste[] {ROOK}) {
                    for (var promotion : rookRevocation) {
                        assertMove(type, side, promotion, NONE, base);
                    }
                }

                // Base pawn/knight/bishop/queen move.
                for (var base : new Caste[] {PAWN, KNIGHT, BISHOP, QUEEN}) {
                    assertMove(type, side, NONE, NONE, base);
                }
            }
        }

        for (var type : new MoveType[] {DOUBLE_PUSH}) {
            for (var side : boardSide) {
                // Pawn double push.
                for (var base : new Caste[] {PAWN}) {
                    assertMove(type, side, PAWN, NONE, base);
                }
            }
        }

        for (var type : new MoveType[] {EN_PASSANT}) {
            for (var side : boardSide) {
                // Pawn en passant.
                for (var base : new Caste[] {PAWN}) {
                    assertMove(type, side, PAWN, PAWN, base);
                }
            }
        }

        for (var type : new MoveType[] {CAPTURE}) {
            for (var side : boardSide) {
                for (var capture : target) {
                    // Capture with king move.
                    for (var base : new Caste[] {KING}) {
                        for (var promotion : anyRevocation) {
                            assertMove(type, side, promotion, capture, base);
                        }
                    }

                    // Capture with rook move.
                    for (var base : new Caste[] {ROOK}) {
                        for (var promotion : rookRevocation) {
                            assertMove(type, side, promotion, capture, base);
                        }
                    }

                    // Capture with pawn/knight/bishop/queen move.
                    for (var base : new Caste[] {PAWN, KNIGHT, BISHOP, QUEEN}) {
                        assertMove(type, side, NONE, capture, base);
                    }
                }
            }
        }

        for (var type : new MoveType[] {CASTLE_SHORT, CASTLE_LONG}) {
            for (var side : boardSide) {
                for (var capture : kingRevocation) {
                    // Castling.
                    assertMove(type, side, PAWN, capture, KING);
                }
            }
        }
    }

    @Test
    void testInvalidHash() {
        for (int i = 0x1; i < 0x3f; i++) {
            int hash = i << 25;
            try {
                var moveHash = moveHashFor(hash);
                fail("chess.move.hash.test.failed: " + moveHash.toString());
            }
            catch (RuntimeException e) {
                var className = e.getClass().getName();
                var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.hash.hash.invalid", e.getMessage());
                assertNotNull(cause);
                assertEquals(format("hash: 0x%08x", hash), cause.getMessage());
            }
        }
    }

    @Test
    void testInvalidDoublePush() {
        for (var type : MoveType.values()) {
            if (DOUBLE_PUSH == type) {
                continue;
            }
            for (var side : new Side[] {WHITE, BLACK}) {
                for (Square square : Square.values()) {
                    var hash = hashFor(type, side, PAWN, NONE, PAWN, square, square);
                    try {
                        moveHashFor(hash);
                        fail("chess.move.hash.test.invalid.double.push.move");
                    }
                    catch (RuntimeException e) {
                        assertEquals(ChessException.class.getName(), e.getClass().getName());
                        assertEquals("chess.move.hash.invalid.double.push.move", e.getMessage());
                        assertNotNull(e.getCause());
                        var format = "type: %s capture: %s";
                        var causeMessage = format(format, type, NONE);
                        assertEquals(causeMessage, e.getCause().getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testInvalidEnPassant() {
        for (var type : MoveType.values()) {
            if (EN_PASSANT == type) {
                continue;
            }
            for (var side : new Side[] {WHITE, BLACK}) {
                for (Square square : Square.values()) {
                    var hash = hashFor(type, side, PAWN, PAWN, PAWN, square, square);
                    try {
                        moveHashFor(hash);
                        fail("chess.move.hash.test.invalid.double.push.move");
                    }
                    catch (RuntimeException e) {
                        assertEquals(ChessException.class.getName(), e.getClass().getName());
                        assertEquals("chess.move.hash.invalid.en.passant.move", e.getMessage());
                        assertNotNull(e.getCause());
                        var format = "type: %s capture: %s";
                        var causeMessage = format(format, type, PAWN);
                        assertEquals(causeMessage, e.getCause().getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testInvalidPawnMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, PAWN, KING, PAWN, square, square));
                    fail("chess.move.hash.test.invalid.pawn.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.pawn.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, BASE, PAWN, KING);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(BASE, side, KING, NONE, PAWN, square, square));
                    fail("chess.move.hash.test.invalid.pawn.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.promotion.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s";
                    var causeMessage = format(format, BASE, KING);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(BASE, side, NONE, PAWN, PAWN, square, square));
                    fail("chess.move.hash.test.invalid.pawn.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.pawn.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, BASE, NONE, PAWN);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidKnightMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(CAPTURE, side, NONE, NONE, KNIGHT, square, square));
                    fail("chess.move.hash.test.invalid.knight.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.knight.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidBishopMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(CAPTURE, side, NONE, NONE, BISHOP, square, square));
                    fail("chess.move.hash.test.invalid.bishop.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.bishop.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidQueenMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(CAPTURE, side, NONE, NONE, QUEEN, square, square));
                    fail("chess.move.hash.test.invalid.queen.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.queen.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidRookMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, ROOK, NONE, ROOK, square, square));
                    fail("chess.move.hash.test.rook.revocation.invalid");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.rook.revocation.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s";
                    var causeMessage = format(format, BASE, ROOK);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(CAPTURE, side, NONE, NONE, ROOK, square, square));
                    fail("chess.move.hash.test.invalid.rook.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.rook.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidCastling() {
        for (var type : new MoveType[] {CASTLE_SHORT, CASTLE_LONG}) {
            for (var side : new Side[] {WHITE, BLACK}) {
                var kingSquare = WHITE == side ? squareFor('e', 1) : squareFor('e', 8);
                var rookSquare = CASTLE_SHORT == type ? kingSquare.left(4) : kingSquare.right(3);
                var hash = hashFor(type, side, PAWN, NONE, KING, kingSquare, rookSquare);
                try {
                    moveHashFor(hash);
                    fail("chess.move.hash.test.invalid.castle.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.castling.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s capture: %s";
                    var causeMessage = format(format, type, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidKingMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, KNIGHT, NONE, KING, square, square));
                    fail("chess.move.hash.test.king.revocation.invalid");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.king.revocation.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, BASE, KNIGHT, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(BASE, side, BISHOP, NONE, KING, square, square));
                    fail("chess.move.hash.test.king.revocation.invalid");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.king.revocation.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, BASE, BISHOP, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(CAPTURE, side, NONE, NONE, KING, square, square));
                    fail("chess.move.hash.test.invalid.king.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.king.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s";
                    var causeMessage = format(format, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidMove() {
        for (var side : new Side[] {WHITE, BLACK}) {
            for (Square square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, PAWN, NONE, NONE, square, square));
                    fail("chess.move.hash.test.invalid.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.move", e.getMessage());
                    assertNotNull(e.getCause());
                    var format = "type: %s promotion: %s capture: %s base: %s";
                    var causeMessage = format(format, BASE, PAWN, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testHashCode() {
        var hash = 0x0;
        var moveHash = moveHashFor(hash);
        assertEquals(hash, moveHash.hashCode());
    }

    @Test
    void testEquality() {
        var hash = 0x0;
        var first = moveHashFor(hash);
        var second = moveHashFor(hash);
        assertNotSame(first, second);
        assertEquals(first, second);
        if (first.equals(second)) {
            second = null;
        }
        if (first.equals(second)) {
            fail("chess.move.hash.test.equal.on.null");
        }
    }

    @Test
    void testToString() {
        var moveHash = moveHashFor(0x0);
        assertEquals("MoveHash(0x00000000)", moveHash.toString());
    }

    private void assertMove(MoveType type, Side side, Caste promotion, Caste capture, Caste base) {
        for (var square : Square.values()) {
            int hash = hashFor(type, side, promotion, capture, base, square, square);
            var moveHash = MoveHash.moveHashFor(hash);
            assertSame(type, moveHash.type());
            assertSame(side, moveHash.side());
            assertSame(promotion, moveHash.promotion());
            assertSame(capture, moveHash.capture());
            assertSame(base, moveHash.base());
            assertSame(square, moveHash.from());
            assertSame(square, moveHash.to());
        }
    }

    private int hashFor(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
        return new MoveIndexer(m, s, p, c, b, f, t).hash;
    }

    private static class MoveIndexer extends MoveHash {

        /** Private constructor. */
        private MoveIndexer(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
            super(m, s, p, c, b, f, t);
        }
    }
}
