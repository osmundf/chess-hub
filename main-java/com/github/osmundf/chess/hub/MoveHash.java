package com.github.osmundf.chess.hub;

import static com.github.osmundf.chess.hub.Caste.BISHOP;
import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.KNIGHT;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
import static com.github.osmundf.chess.hub.Caste.casteFromIndex;
import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_LONG;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_SHORT;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.MoveType.moveTypeFromIndex;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFromIndex;
import static java.lang.String.format;

/**
 * Chess move hash.
 *
 * @author Osmund
 * @version 1.0.0
 * @since 1.0.0
 */
public class MoveHash {

    /**
     * Chess move hash factory method.
     *
     * @param hash move hash
     * @return new move instance
     */
    public static MoveHash moveHashFor(int hash) {
        // type[ttt] side[s] promotion[ppp] capture[ccc] base[bbb] from[rrr,fff] to[rrr,fff]
        if ((hash & 0xfe000000) != 0x0) {
            var cause = new ChessException(format("hash: 0x%08x", hash));
            throw new ChessException("chess.move.hash.hash.invalid", cause);
        }

        var instance = new MoveHash(hash);
        var type = instance.type();
        var promotion = instance.promotion();
        var capture = instance.capture();
        var base = instance.base();
        var error = instance.getError(type, promotion, capture, base);

        if (error != null) {
            throw error;
        }
        return instance;
    }

    protected final int hash;

    /**
     * Constructor for MoveHash (protected).
     *
     * @param hash move hash
     */
    protected MoveHash(int hash) {
        this.hash = hash;
    }

    /**
     * Constructor for MoveHash (protected)
     *
     * @param m move type
     * @param s move side
     * @param p promotion/castle detail
     * @param c capture/castle detail
     * @param b piece caste
     * @param f piece source square
     * @param t piece target square
     */
    protected MoveHash(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
        var hash = m.index() << 22;
        hash |= s.isWhite() ? 1 << 21 : 0;
        hash |= p.index() << 18;
        hash |= c.index() << 15;
        hash |= b.index() << 12;
        hash |= f.index() << 6;
        hash |= t.index();
        this.hash = hash;
    }

    /**
     * Returns move type.
     *
     * @return move type
     */
    protected MoveType type() {
        return moveTypeFromIndex((hash >> 22) & 0x7);
    }

    /**
     * Returns move side.
     *
     * @return move side
     */
    protected Side side() {
        return (hash & 0x200000) != 0x0 ? WHITE : BLACK;
    }

    /**
     * Returns promotion/revocation detail.
     *
     * @return promotion/revocation detail
     */
    protected Caste promotion() {
        return casteFromIndex((hash & 0x1c0000) >> 18);
    }

    /**
     * Returns capture/castle detail.
     *
     * @return capture/castle detail
     */
    protected Caste capture() {
        return casteFromIndex((hash & 0x38000) >> 15);
    }

    /**
     * Returns caste of moving piece.
     *
     * @return caste of moving piece
     */
    protected Caste base() {
        return casteFromIndex((hash & 0x7000) >> 12);
    }

    /**
     * Returns source/king square.
     *
     * @return source/king square
     */
    protected Square from() {
        return squareFromIndex((byte) ((hash & 0xfc0) >> 6));
    }

    /**
     * Returns destination/rook square.
     *
     * @return destination/rook square
     */
    protected Square to() {
        return squareFromIndex((byte) (hash & 0x3f));
    }

    /**
     * Returns if move hash, excluding squares, has any error.
     *
     * @param type      move type
     * @param promotion promotion/revocation detail
     * @param capture   capture/castle detail
     * @param base      moving piece caste
     * @return chess exception of error, null otherwise
     */
    protected ChessException getError(MoveType type, Caste promotion, Caste capture, Caste base) {
        // Check valid pawn moves.
        if (PAWN == base) {
            if (PAWN == promotion) {
                // Valid pawn double-push.
                if (NONE == capture) {
                    if (DOUBLE_PUSH == type) {
                        return null;
                    }
                    var cause = new ChessException("type: " + type + " capture: " + capture);
                    return new ChessException("chess.move.hash.invalid.double.push.move", cause);
                }

                // Valid pawn capture en passant.
                if (PAWN == capture) {
                    if (EN_PASSANT == type) {
                        return null;
                    }
                    var cause = new ChessException("type: " + type + " capture: " + capture);
                    return new ChessException("chess.move.hash.invalid.en.passant.move", cause);
                }

                var format = "type: %s promotion: %s capture: %s";
                var cause = new ChessException(format(format, type, promotion, capture));
                return new ChessException("chess.move.hash.invalid.pawn.move", cause);
            }

            // Invalid pawn promotion.
            if (KING == promotion) {
                var cause = new ChessException("type: " + type + " promotion: " + promotion);
                return new ChessException("chess.move.hash.invalid.promotion.move", cause);
            }

            // Valid promotion and capture.
            if ((NONE != promotion) == (type.isPromotion())) {
                if (KING != capture && (NONE != capture) == type.isCapture()) {
                    return null;
                }
            }

            // Invalid pawn move.
            var format = "type: %s promotion: %s capture: %s";
            var cause = new ChessException(format(format, type, promotion, capture));
            return new ChessException("chess.move.hash.invalid.pawn.move", cause);
        }

        // Check valid king moves.
        if (KNIGHT == base) {
            if (NONE == promotion) {
                if (KING != capture && (NONE != capture) == type.isCapture()) {
                    if (BASE == type || CAPTURE == type) {
                        return null;
                    }
                }
            }

            // Invalid knight move.
            var format = "type: %s promotion: %s capture: %s";
            var cause = new ChessException(format(format, type, promotion, capture));
            return new ChessException("chess.move.hash.invalid.knight.move", cause);
        }

        // Check valid bishop moves.
        if (BISHOP == base) {
            if (NONE == promotion) {
                if (KING != capture && (NONE != capture) == type.isCapture()) {
                    if (BASE == type || CAPTURE == type) {
                        return null;
                    }
                }
            }

            // Invalid bishop move.
            var format = "type: %s promotion: %s capture: %s";
            var cause = new ChessException(format(format, type, promotion, capture));
            return new ChessException("chess.move.hash.invalid.bishop.move", cause);
        }

        // Check valid queen moves.
        if (QUEEN == base) {
            if (NONE == promotion) {
                if (KING != capture && (NONE != capture) == type.isCapture()) {
                    if (BASE == type || CAPTURE == type) {
                        return null;
                    }
                }
            }

            // Invalid queen move.
            var format = "type: %s promotion: %s capture: %s";
            var cause = new ChessException(format(format, type, promotion, capture));
            return new ChessException("chess.move.hash.invalid.queen.move", cause);
        }

        // Check valid rook moves.
        if (ROOK == base) {
            // Promotion field signals castling right revocation.
            if (NONE != promotion && KING != promotion && QUEEN != promotion) {
                var cause = new ChessException("type: " + type + " promotion: " + promotion);
                return new ChessException("chess.move.hash.rook.revocation.invalid", cause);
            }

            if (KING != capture && (NONE != capture) == type.isCapture()) {
                if (BASE == type || CAPTURE == type) {
                    return null;
                }
            }

            // Invalid rook move.
            var format = "type: %s promotion: %s capture: %s";
            var cause = new ChessException(format(format, type, promotion, capture));
            return new ChessException("chess.move.hash.invalid.rook.move", cause);
        }

        // Check valid king moves.
        if (KING == base) {
            // Check valid castling.
            if (PAWN == promotion) {
                // Castling revoked at least one right.
                if (KING == capture || QUEEN == capture || ROOK == capture) {
                    if (type == CASTLE_LONG || type == CASTLE_SHORT) {
                        return null;
                    }
                }

                var cause = new ChessException("type: " + type + " capture: " + capture);
                return new ChessException("chess.move.hash.castling.invalid", cause);
            }

            // Check for invalid right revocation.
            if (NONE != promotion && KING != promotion && QUEEN != promotion && ROOK != promotion) {
                var format = "type: %s promotion: %s capture: %s";
                var cause = new ChessException(format(format, type, promotion, capture));
                return new ChessException("chess.move.hash.king.revocation.invalid", cause);
            }

            // Check valid move.
            if (KING != capture && (NONE != capture) == type.isCapture()) {
                if (BASE == type || CAPTURE == type) {
                    return null;
                }
            }

            // Invalid king move.
            var format = "type: %s promotion: %s capture: %s";
            var cause = new ChessException(format(format, type, promotion, capture));
            return new ChessException("chess.move.hash.invalid.king.move", cause);
        }

        // Check valid null move.
        if (NONE == base) {
            if (NONE == promotion) {
                if (NONE == capture) {
                    if (BASE == type) {
                        return null;
                    }
                }
            }
        }

        // Invalid move.
        var format = "type: %s promotion: %s capture: %s base: %s";
        var cause = new ChessException(format(format, type, promotion, capture, base));
        return new ChessException("chess.move.hash.invalid.move", cause);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return hash;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MoveHash)) {
            return false;
        }
        var other = (MoveHash) object;
        return this == object || this.hash == other.hash;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return format("MoveHash(0x%08x)", hash);
    }
}
