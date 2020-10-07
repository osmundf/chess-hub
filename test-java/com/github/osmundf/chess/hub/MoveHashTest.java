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
import static com.github.osmundf.chess.hub.Revocation.REVOKE_BOTH;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_KING_SIDE;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_QUEEN_SIDE;
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
        // type[000] side[s] revocation[00] promotion[000] capture[000] base[000] from[rrr,fff] to[rrr,fff]

        // base/capture (pawn/knight/bishop/queen):
        // type[ttt] side[s] revocation[00] promotion[000] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]
        // type[ttt] side[s] revocation[00] promotion[000] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]

        // base/capture (king/rook):
        // type[ttt] side[s] revocation[rr] promotion[000] capture[ccc] base[110] from[rrr,fff] to[rrr,fff]
        // type[ttt] side[s] revocation[rr] promotion[000] capture[ccc] base[100] from[rrr,fff] to[rrr,fff]

        // castling (type: [castle_short,castle_long]):
        // type[ttt] side[s] revocation[rr] promotion[001] castle[ccc] base[110] king[xxx,xxx] rook[xxx,xxx]

        // basic (double-push):
        // type[ttt] side[s] revocation[00] promotion[001] capture[000] base[001] from[rrr,fff] to[rrr,fff]

        // en-passant
        // type[ttt] side[s] revocation[00] promotion[001] capture[001] base[001] from[rrr,fff] to[rrr,fff]

        // promotion/capture-promotion:
        // type[ttt] side[s] revocation[00] promotion[ppp] capture[ccc] base[001] from[rrr,fff] to[rrr,fff]

        final var anyRevocation = Revocation.values();
        final var castlingRevocation = new Revocation[] {REVOKE_BOTH, REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE};
        final var rookRevocation = new Revocation[] {REVOKE_KING_SIDE, REVOKE_QUEEN_SIDE, REVOKE_NONE};
        final var boardSide = new Side[] {WHITE, BLACK};
        final var target = new Caste[] {PAWN, KNIGHT, BISHOP, ROOK, QUEEN};

        for (final var type : new MoveType[] {BASE}) {
            for (final var side : boardSide) {
                // Base king move.
                for (final var base : new Caste[] {KING}) {
                    for (final var revocation : anyRevocation) {
                        assertMove(type, side, revocation, NONE, NONE, base);
                    }
                }

                // Base rook move.
                for (final var base : new Caste[] {ROOK}) {
                    for (final var revocation : rookRevocation) {
                        assertMove(type, side, revocation, NONE, NONE, base);
                    }
                }

                // Base pawn/knight/bishop/queen move.
                for (final var base : new Caste[] {PAWN, KNIGHT, BISHOP, QUEEN}) {
                    assertMove(type, side, REVOKE_NONE, NONE, NONE, base);
                }
            }
        }

        for (final var type : new MoveType[] {DOUBLE_PUSH}) {
            for (final var side : boardSide) {
                // Pawn double push.
                for (final var base : new Caste[] {PAWN}) {
                    assertMove(type, side, REVOKE_NONE, PAWN, NONE, base);
                }
            }
        }

        for (final var type : new MoveType[] {EN_PASSANT}) {
            for (final var side : boardSide) {
                // Pawn en passant.
                for (final var base : new Caste[] {PAWN}) {
                    assertMove(type, side, REVOKE_NONE, PAWN, PAWN, base);
                }
            }
        }

        for (final var type : new MoveType[] {CAPTURE}) {
            for (final var side : boardSide) {
                for (final var capture : target) {
                    // Capture with king move.
                    for (final var base : new Caste[] {KING}) {
                        for (final var revocation : anyRevocation) {
                            assertMove(type, side, revocation, NONE, capture, base);
                        }
                    }

                    // Capture with rook move.
                    for (final var base : new Caste[] {ROOK}) {
                        for (final var revocation : anyRevocation) {
                            assertMove(type, side, revocation, NONE, capture, base);
                        }
                    }

                    // Capture with pawn/knight/bishop/queen move.
                    for (final var base : new Caste[] {PAWN, KNIGHT, BISHOP, QUEEN}) {
                        assertMove(type, side, REVOKE_NONE, NONE, capture, base);
                    }
                }
            }
        }

        for (final var type : new MoveType[] {CASTLE_SHORT, CASTLE_LONG}) {
            for (final var side : boardSide) {
                for (final var revocation : castlingRevocation) {
                    // Castling.
                    assertMove(type, side, revocation, PAWN, NONE, KING);
                }
            }
        }
    }

    @Test
    void testInvalidHash() {
        for (var i = 0x1; i < 0x1f; i++) {
            final var hash = i << 27;
            try {
                final var moveHash = moveHashFor(hash);
                fail("chess.move.hash.test.failed: " + moveHash.toString());
            }
            catch (RuntimeException e) {
                final var className = e.getClass().getName();
                final var cause = e.getCause();
                assertEquals(ChessException.class.getName(), className);
                assertEquals("chess.move.hash.input.invalid", e.getMessage());
                assertNotNull(cause);
                assertEquals(format("hash: 0x%08x", hash), cause.getMessage());
            }
        }
    }

    @Test
    void testInvalidDoublePush() {
        for (final var type : MoveType.values()) {
            if (DOUBLE_PUSH == type) {
                continue;
            }
            for (final var side : new Side[] {WHITE, BLACK}) {
                for (final var square : Square.values()) {
                    final var hash = hashFor(type, side, REVOKE_NONE, PAWN, NONE, PAWN, square, square);
                    try {
                        moveHashFor(hash);
                        fail("chess.move.hash.test.invalid.double.push.move");
                    }
                    catch (RuntimeException e) {
                        assertEquals(ChessException.class.getName(), e.getClass().getName());
                        assertEquals("chess.move.hash.invalid.double.push.move", e.getMessage());
                        assertNotNull(e.getCause());
                        final var template = "type: %s capture: %s";
                        final var causeMessage = format(template, type, NONE);
                        assertEquals(causeMessage, e.getCause().getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testInvalidEnPassant() {
        for (final var type : MoveType.values()) {
            if (EN_PASSANT == type) {
                continue;
            }
            for (final var side : new Side[] {WHITE, BLACK}) {
                for (final var square : Square.values()) {
                    final var hash = hashFor(type, side, REVOKE_NONE, PAWN, PAWN, PAWN, square, square);
                    try {
                        moveHashFor(hash);
                        fail("chess.move.hash.test.invalid.double.push.move");
                    }
                    catch (RuntimeException e) {
                        assertEquals(ChessException.class.getName(), e.getClass().getName());
                        assertEquals("chess.move.hash.invalid.en.passant.move", e.getMessage());
                        assertNotNull(e.getCause());
                        final var template = "type: %s capture: %s";
                        final var causeMessage = format(template, type, PAWN);
                        assertEquals(causeMessage, e.getCause().getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testInvalidPawnMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, REVOKE_NONE, PAWN, KING, PAWN, square, square));
                    fail("chess.move.hash.test.invalid.pawn.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.pawn.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, BASE, PAWN, KING);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(BASE, side, REVOKE_NONE, KING, NONE, PAWN, square, square));
                    fail("chess.move.hash.test.invalid.pawn.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.promotion.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s";
                    final var causeMessage = format(template, BASE, KING);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(BASE, side, REVOKE_NONE, NONE, PAWN, PAWN, square, square));
                    fail("chess.move.hash.test.invalid.pawn.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.pawn.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, BASE, NONE, PAWN);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidKnightMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var square : Square.values()) {
                try {
                    moveHashFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, KNIGHT, square, square));
                    fail("chess.move.hash.test.invalid.knight.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.knight.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidBishopMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var square : Square.values()) {
                try {
                    moveHashFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, BISHOP, square, square));
                    fail("chess.move.hash.test.invalid.bishop.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.bishop.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidQueenMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var square : Square.values()) {
                try {
                    moveHashFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, QUEEN, square, square));
                    fail("chess.move.hash.test.invalid.queen.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.queen.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidRookMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, REVOKE_NONE, ROOK, NONE, ROOK, square, square));
                    fail("chess.move.hash.test.rook.revocation.invalid");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.rook.revocation.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s";
                    final var causeMessage = format(template, BASE, ROOK);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }

                try {
                    moveHashFor(hashFor(CAPTURE, side, REVOKE_NONE, NONE, NONE, ROOK, square, square));
                    fail("chess.move.hash.test.invalid.rook.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.rook.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s";
                    final var causeMessage = format(template, CAPTURE, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidCastling() {
        for (final var type : new MoveType[] {CASTLE_SHORT, CASTLE_LONG}) {
            for (final var side : new Side[] {WHITE, BLACK}) {
                final var kingSquare = WHITE == side ? squareFor('e', 1) : squareFor('e', 8);
                final var rookSquare = CASTLE_SHORT == type ? kingSquare.left(4) : kingSquare.right(3);
                final var hash = hashFor(type, side, REVOKE_NONE, PAWN, NONE, KING, kingSquare, rookSquare);
                try {
                    moveHashFor(hash);
                    fail("chess.move.hash.test.invalid.castle.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.castling.invalid", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s revocation: %s capture: %s";
                    final var causeMessage = format(template, type, REVOKE_NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testInvalidKingMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var revocation : Revocation.values()) {
                for (final var square : Square.values()) {
                    // Invalid promotion.
                    for (final var promotion : Caste.values()) {
                        if (NONE != promotion && PAWN != promotion) {
                            try {
                                moveHashFor(hashFor(BASE, side, revocation, promotion, NONE, KING, square, square));
                                fail("chess.move.hash.test.king.promotion.invalid");
                            }
                            catch (RuntimeException e) {
                                assertEquals(ChessException.class.getName(), e.getClass().getName());
                                assertEquals("chess.move.hash.king.promotion.invalid", e.getMessage());
                                assertNotNull(e.getCause());
                                final var template = "type: %s promotion: %s capture: %s";
                                final var causeMessage = format(template, BASE, promotion, NONE);
                                assertEquals(causeMessage, e.getCause().getMessage());
                            }
                        }
                    }

                    try {
                        moveHashFor(hashFor(CAPTURE, side, revocation, NONE, NONE, KING, square, square));
                        fail("chess.move.hash.test.invalid.king.move");
                    }
                    catch (RuntimeException e) {
                        assertEquals(ChessException.class.getName(), e.getClass().getName());
                        assertEquals("chess.move.hash.invalid.king.move", e.getMessage());
                        assertNotNull(e.getCause());
                        final var template = "type: %s promotion: %s capture: %s";
                        final var causeMessage = format(template, CAPTURE, NONE, NONE);
                        assertEquals(causeMessage, e.getCause().getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testInvalidMove() {
        for (final var side : new Side[] {WHITE, BLACK}) {
            for (final var square : Square.values()) {
                try {
                    moveHashFor(hashFor(BASE, side, REVOKE_NONE, PAWN, NONE, NONE, square, square));
                    fail("chess.move.hash.test.invalid.move");
                }
                catch (RuntimeException e) {
                    assertEquals(ChessException.class.getName(), e.getClass().getName());
                    assertEquals("chess.move.hash.invalid.move", e.getMessage());
                    assertNotNull(e.getCause());
                    final var template = "type: %s promotion: %s capture: %s base: %s";
                    final var causeMessage = format(template, BASE, PAWN, NONE, NONE);
                    assertEquals(causeMessage, e.getCause().getMessage());
                }
            }
        }
    }

    @Test
    void testNullMove() {
        final var hash = 0x0;
        final var moveHash = moveHashFor(hash);
        assertEquals(hash, moveHash.hashCode());
    }

    @Test
    void testEquality() {
        final var hash = 0x0;
        final var first = moveHashFor(hash);
        final var second = moveHashFor(hash);
        assertNotSame(first, second);
        assertEquals(first, second);
        //noinspection ConstantConditions
        if (first.equals(null)) {
            fail("chess.move.hash.equal.on.null");
        }
    }

    @Test
    void testToString() {
        final var moveHash = moveHashFor(0x0);
        assertEquals("MoveHash(0x00000000)", moveHash.toString());
    }

    private void assertMove(MoveType type, Side side, Revocation revocation, Caste promotion, Caste capture,
        Caste base)
    {
        for (final var square : Square.values()) {
            final var hash = hashFor(type, side, revocation, promotion, capture, base, square, square);
            final var moveHash = MoveHash.moveHashFor(hash);
            assertSame(type, moveHash.type());
            assertSame(side, moveHash.side());
            assertSame(promotion, moveHash.promotion());
            assertSame(capture, moveHash.capture());
            assertSame(base, moveHash.base());
            assertSame(square, moveHash.from());
            assertSame(square, moveHash.to());
        }
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
