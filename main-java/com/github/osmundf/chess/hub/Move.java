package com.github.osmundf.chess.hub;

import java.util.Objects;
import java.util.Optional;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_BOTH;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_KING_SIDE;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.CastleRevocation.REVOKE_QUEEN_SIDE;
import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static java.lang.String.format;

/**
 * Chess move.
 */
public class Move extends MoveHash {

    /**
     * Chess move factory method.
     *
     * @param hash move hash
     * @return new instance of move
     */
    public static Move moveFor(int hash) {
        var moveHash = new MoveHash(hash);
        var type = moveHash.type();
        var side = moveHash.side();
        var promotion = moveHash.promotion();
        var capture = moveHash.capture();
        var base = moveHash.base();
        var error = moveHash.getError(type, promotion, capture, base);
        if (error != null) {
            throw error;
        }
        var from = moveHash.from();
        var to = moveHash.to();
        return new Move(type, side, promotion, capture, base, from, to).validate();
    }

    /**
     * Creates a new move instance for base or double-push move.
     *
     * @param piece piece to move
     * @param to    piece destination square
     * @return new move instance for basic move
     */
    public static Move basicMove(Piece piece, Square to) {
        Objects.requireNonNull(piece, "chess.move.piece.null");
        Objects.requireNonNull(to, "chess.move.to.square.null");

        var side = piece.side();
        var base = piece.caste();
        var from = piece.square();
        return basicMove(side, base, from, to);
    }

    /**
     * Creates a new move instance for base or double-push move.
     *
     * @param side piece side
     * @param base piece caste
     * @param from source square
     * @param to   destination square
     * @return new move instance for base or double-push move
     */
    public static Move basicMove(Side side, Caste base, Square from, Square to) {
        Objects.requireNonNull(base, "chess.move.base.null");

        // Castling revocation cannot be deduced for king or rook.
        if (KING == base || ROOK == base) {
            var cause = new ChessException("TODO");
            throw new ChessException("Revocation", cause);
        }

        return basicMove(side, REVOKE_NONE, base, from, to);
    }

    public static Move basicMove(Piece piece, CastleRevocation revocation, Square to) {
        Objects.requireNonNull(piece, "chess.move.piece.null");
        Objects.requireNonNull(revocation, "chess.move.revocation.null");

        var side = piece.side();
        var promotion = revocation.asCaste();
        var base = piece.caste();
        var from = piece.square();

        if (KING != base && ROOK != base) {
            if (REVOKE_NONE != revocation) {
                var template = "revocation: %s base: %s";
                var cause = new ChessException(format(template, revocation, base));
                throw new ChessException("chess.move.revocation.invalid", cause);
            }
        }

        return new Move(BASE, side, promotion, NONE, base, from, to);
    }

    public static Move basicMove(Side side, CastleRevocation revocation, Caste base, Square from, Square to) {
        Objects.requireNonNull(side, "chess.move.side.null");
        Objects.requireNonNull(revocation, "chess.move.revocation.null");
        Objects.requireNonNull(base, "chess.move.base.null");
        Objects.requireNonNull(from, "chess.move.from.square.null");
        Objects.requireNonNull(to, "chess.move.to.square.null");

        if (KING != base && ROOK != base) {
            if (REVOKE_NONE != revocation) {
                var template = "revocation: %s base: %s";
                var cause = new ChessException(format(template, revocation, base));
                throw new ChessException("chess.move.revocation.invalid", cause);
            }
        }

        // Check for pawn double-push.
        if (PAWN == base) {
            var fromRank = from.rank();
            var toRank = to.rank();

            // Check for valid double-push.
            if (WHITE == side) {
                if (fromRank == 2 && toRank == 4) {
                    return new Move(DOUBLE_PUSH, side, PAWN, NONE, base, from, to).validate();
                }
            }
            // Check for valid double-push.
            if (BLACK == side) {
                if (fromRank == 7 && toRank == 5) {
                    return new Move(DOUBLE_PUSH, side, PAWN, NONE, base, from, to).validate();
                }
            }
        }

        return new Move(BASE, side, revocation.asCaste(), NONE, base, from, to).validate();
    }

    public static Move captureMove(Piece source, Piece captured) {
        assert source.side().opposite() == captured.side();
        var side = source.side();
        var capture = captured.caste();
        var base = source.caste();
        var from = source.square();
        var to = captured.square();
        return new Move(CAPTURE, side, NONE, capture, base, from, to).validate();
    }

    public static Move doublePush(Piece piece) {
        return doublePush(piece.side(), piece.caste(), piece.square());
    }

    public static Move doublePush(Side side, Caste base, Square from) {
        assert side != null && Side.NO_SIDE != side;
        assert from.rank() == 2 || from.rank() == 4;
        assert PAWN == base;
        var to = side.isWhite() ? from.up(2) : from.down(2);
        return new Move(DOUBLE_PUSH, side, PAWN, NONE, base, from, to).validate();
    }

    public static Move enPassant(Piece pawn, Square to) {
        assert pawn != null;
        assert pawn.caste() == PAWN;
        return enPassant(pawn.side(), pawn.caste(), pawn.square(), to);
    }

    public static Move enPassant(Side side, Caste base, Square from, Square to) {
        assert side != null && Side.NO_SIDE != side;
        assert base == PAWN;
        assert from != null;
        assert to != null;
        return new Move(EN_PASSANT, side, PAWN, base, PAWN, from, to).validate();
    }

    public static Move castle(boolean castleShort, Piece king, Piece rook, CastleRevocation revocation) {
        assert king != null;
        assert KING == king.caste();
        assert rook != null;
        assert ROOK == rook.caste();
        assert king.side() == rook.side();
        var type = castleShort ? MoveType.CASTLE_SHORT : MoveType.CASTLE_LONG;
        var side = king.side();
        var capture = revocation.asCaste();
        var from = king.square();
        var to = rook.square();
        return new Move(type, side, KING, capture, KING, from, to).validate();
    }

    private final MoveType type;

    private final Side side;

    private final Caste promotion;

    private final Caste capture;

    private final Caste base;

    private final Square from;

    private final Square to;

    private final Square rookFromSquare;

    private final Square rookToSquare;

    private final CastleRevocation revocation;

    /**
     * Move Constructor (protected).
     *
     * @param type      move type
     * @param side      board side
     * @param promotion piece promotion
     * @param capture   captured piece caste
     * @param base      piece caste
     * @param from      from square
     * @param to        to square
     */
    protected Move(MoveType type, Side side, Caste promotion, Caste capture, Caste base, Square from, Square to) {
        super(type, side, promotion, capture, base, from, to);
        this.type = type;
        this.side = side;
        this.promotion = type.isPromotion() ? promotion : NONE;
        this.capture = type.isCapture() ? capture : NONE;
        this.base = base;
        this.from = from;

        if (type.isCastling()) {
            this.to = getKingToSquare();
            this.rookFromSquare = to;
            this.rookToSquare = getRookToSquare();
            this.revocation = getRevocation(capture, base);
        }
        else {
            this.to = to;
            this.rookFromSquare = null;
            this.rookToSquare = null;
            this.revocation = getRevocation(promotion, base);
        }
    }

    /**
     *
     * @return
     */
    public MoveType type() {
        return type;
    }

    /**
     *
     * @return
     */
    public Side side() {
        return side;
    }

    /**
     *
     * @return
     */
    public Caste promotion() {
        return promotion;
    }

    /**
     *
     * @return
     */
    public Optional<Piece> promotionPiece() {
        if (promotion == NONE) {
            return Optional.empty();
        }
        else {
            return Optional.of(pieceFor(side, promotion, to));
        }
    }

    /**
     *
     * @return
     */
    public Caste capture() {
        return capture;
    }

    /**
     *
     * @return
     */
    public Optional<Piece> capturedPiece() {
        if (!type.isCapture()) {
            return Optional.empty();
        }
        else {
            var side = this.side.opposite();
            var square = EN_PASSANT == type ? squareFor(to.file(), from.rank()) : to;
            return Optional.of(pieceFor(side, capture, square));
        }
    }

    /**
     *
     * @return
     */
    public Caste base() {
        return base;
    }

    /**
     *
     * @return
     */
    public Square from() {
        return from;
    }

    /**
     *
     * @return
     */
    public Square to() {
        return to;
    }

    /**
     *
     * @return
     */
    public CastleRevocation revocation() {
        return revocation;
    }

    /**
     *
     * @return
     */
    public Square rookFrom() {
        return rookFromSquare;
    }

    /**
     *
     * @return
     */
    public Square rookTo() {
        return rookToSquare;
    }

    /**
     *
     * @return
     */
    public Square kingFrom() {
        return from;
    }

    /**
     *
     * @return
     */
    public Square kingTo() {
        return to;
    }

    /**
     *
     * @return
     */
    public boolean revokedKingSide() {
        return revocation.isKingSide();
    }

    /**
     *
     * @return
     */
    public boolean revokedQueenSide() {
        return revocation.isQueenSide();
    }

    /**
     *
     * @return
     */
    public boolean isNull() {
        return type == BASE && promotion == NONE && capture == NONE && base == NONE;
    }

    /**
     * TODO create a single point for validation of move.
     * @return
     */
    public Move validate() {
        if (BASE == type) {
            return this;
        }
        if (CAPTURE == type) {
            return this;
        }
        if (DOUBLE_PUSH == type) {
            if (PAWN != promotion) {
                var cause = new ChessException("type: " + type + " promotion: " + promotion);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (NONE != capture) {
                var cause = new ChessException("type: " + type + " capture: " + capture);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (PAWN != base) {
                var cause = new ChessException("type: " + type + " base: " + base);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (from.file() != to.file()) {
                var cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (side.isWhite() && from.rank() != 2 || side.isWhite() && to.rank() != 4) {
                var cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (side.isBlack() && from.rank() != 7 || side.isBlack() && to.rank() != 5) {
                var cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }

            return this;
        }
        if (EN_PASSANT == type) {
            if (PAWN != promotion) {
                var cause = new ChessException("type: " + type + " promotion: " + promotion);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (PAWN != capture) {
                var cause = new ChessException("type: " + type + " capture: " + capture);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (PAWN != base) {
                var cause = new ChessException("type: " + type + " base: " + base);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (to.file() != from.file()) {
                var cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (side.isWhite() && to.rank() != (from.rank() + 1)) {
                var cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (side.isBlack() && to.rank() != (from.rank() - 1)) {
                var cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }

            return this;
        }

        return this;
    }

    /**
     *
     * @return
     */
    private Square getKingToSquare() {
        var castleShort = MoveType.CASTLE_SHORT == type;
        return squareFor(castleShort ? 'g' : 'c', kingFrom().rank());
    }

    /**
     *
     * @return
     */
    private Square getRookToSquare() {
        var castleShort = MoveType.CASTLE_SHORT == type;
        return squareFor(castleShort ? 'f' : 'd', rookFrom().rank());
    }

    /**
     *
     * @param flag
     * @param base
     * @return
     */
    private CastleRevocation getRevocation(Caste flag, Caste base) {
        if (ROOK != base && KING != base) {
            return REVOKE_NONE;
        }
        if (ROOK == flag) {
            return REVOKE_BOTH;
        }
        if (KING == flag) {
            return REVOKE_KING_SIDE;
        }
        if (QUEEN == flag) {
            return REVOKE_QUEEN_SIDE;
        }
        return REVOKE_NONE;
    }

    @Override
    public int hashCode() {
        return super.hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Move)) {
            return false;
        }
        var other = (Move) object;
        return this == object || this.hash == other.hash;
    }

    @Override
    public String toString() {
        return format("Move(0x%08x)", hash);
    }
}
