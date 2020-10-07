package com.github.osmundf.chess.hub;

import java.util.Objects;
import java.util.Optional;

import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.ROOK;
import static com.github.osmundf.chess.hub.MoveType.BASE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE;
import static com.github.osmundf.chess.hub.MoveType.CAPTURE_PROMOTION;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_LONG;
import static com.github.osmundf.chess.hub.MoveType.CASTLE_SHORT;
import static com.github.osmundf.chess.hub.MoveType.DOUBLE_PUSH;
import static com.github.osmundf.chess.hub.MoveType.EN_PASSANT;
import static com.github.osmundf.chess.hub.MoveType.PROMOTION;
import static com.github.osmundf.chess.hub.Piece.pieceFor;
import static com.github.osmundf.chess.hub.Revocation.REVOKE_NONE;
import static com.github.osmundf.chess.hub.Side.BLACK;
import static com.github.osmundf.chess.hub.Side.WHITE;
import static com.github.osmundf.chess.hub.Square.squareFor;
import static java.lang.String.format;

/**
 * Chess move.
 *
 * @author Osmund
 * @version 1.0.0
 */
public class Move extends MoveHash {

    /**
     * Chess move factory method.
     *
     * @param hash move hash
     * @return new instance of move
     */
    public static Move moveFor(int hash) {
        MoveHash moveHash = new MoveHash(hash);
        MoveType type = moveHash.type();
        Side side = moveHash.side();
        Revocation revocation = moveHash.revocation();
        Caste promotion = moveHash.promotion();
        Caste capture = moveHash.capture();
        Caste base = moveHash.base();
        ChessException error = moveHash.getError(type, revocation, promotion, capture, base);
        if (error != null) {
            throw error;
        }
        Square from = moveHash.from();
        Square to = moveHash.to();
        return new Move(type, side, revocation, promotion, capture, base, from, to).validate();
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

        Side side = piece.side();
        Caste base = piece.caste();
        Square from = piece.square();

        // Delegate to basic move with no revocation specified.
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
        // Castling revocation cannot be deduced for king or rook.
        if (KING == base || ROOK == base) {
            ChessException cause = new ChessException("base: " + base);
            throw new ChessException("chess.move.revocation.missing", cause);
        }

        // Delegate to full basic move factory method.
        return basicMove(side, REVOKE_NONE, base, from, to);
    }

    /**
     * <p>basicMove.</p>
     *
     * @param piece      a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param revocation a {@link com.github.osmundf.chess.hub.Revocation} object.
     * @param to         a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move basicMove(Piece piece, Revocation revocation, Square to) {
        Objects.requireNonNull(piece, "chess.move.piece.null");

        Side side = piece.side();
        Caste base = piece.caste();
        Square from = piece.square();

        // Delegate to full basic move factory method.
        return basicMove(side, revocation, base, from, to);
    }

    /**
     * <p>basicMove.</p>
     *
     * @param side       a {@link com.github.osmundf.chess.hub.Side} object.
     * @param revocation a {@link com.github.osmundf.chess.hub.Revocation} object.
     * @param base       a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from       a {@link com.github.osmundf.chess.hub.Square} object.
     * @param to         a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move basicMove(Side side, Revocation revocation, Caste base, Square from, Square to) {
        Objects.requireNonNull(side, "chess.move.side.null");
        Objects.requireNonNull(revocation, "chess.move.revocation.null");
        Objects.requireNonNull(base, "chess.move.base.null");
        Objects.requireNonNull(from, "chess.move.from.square.null");
        Objects.requireNonNull(to, "chess.move.to.square.null");

        if (KING != base && ROOK != base) {
            if (REVOKE_NONE != revocation) {
                String template = "revocation: %s base: %s";
                ChessException cause = new ChessException(format(template, revocation, base));
                throw new ChessException("chess.move.revocation.invalid", cause);
            }
        }

        // Check for pawn double-push.
        if (PAWN == base) {
            byte fromRank = from.rank();
            byte toRank = to.rank();

            // Check for valid double-push.
            if (WHITE == side) {
                if (fromRank == 2 && toRank == 4) {
                    return new Move(DOUBLE_PUSH, side, REVOKE_NONE, PAWN, NONE, base, from, to).validate();
                }
            }
            // Check for valid double-push.
            if (BLACK == side) {
                if (fromRank == 7 && toRank == 5) {
                    return new Move(DOUBLE_PUSH, side, REVOKE_NONE, PAWN, NONE, base, from, to).validate();
                }
            }
        }

        return new Move(BASE, side, revocation, NONE, NONE, base, from, to).validate();
    }

    /**
     * <p>captureMove.</p>
     *
     * @param source   a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param captured a {@link com.github.osmundf.chess.hub.Piece} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move captureMove(Piece source, Piece captured) {
        Objects.requireNonNull(source, "chess.move.source.null");
        Objects.requireNonNull(captured, "chess.move.captured.null");

        if (source.side() == captured.side()) {
            ChessException cause = new ChessException("captured: " + captured);
            throw new ChessException("chess.move.captured.friendly.piece", cause);
        }

        if (source.caste() == KING || source.caste() == ROOK) {
            ChessException cause = new ChessException("base: " + source.caste());
            throw new ChessException("chess.move.revocation.missing", cause);
        }

        Side side = source.side();
        Caste capture = captured.caste();
        Caste base = source.caste();
        Square from = source.square();
        Square to = captured.square();
        return captureMove(side, REVOKE_NONE, base, capture, from, to);
    }

    /**
     * <p>captureMove.</p>
     *
     * @param side       a {@link com.github.osmundf.chess.hub.Side} object.
     * @param revocation a {@link com.github.osmundf.chess.hub.Revocation} object.
     * @param base       a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from       a {@link com.github.osmundf.chess.hub.Square} object.
     * @param to         a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move captureMove(Side side, Revocation revocation, Caste base, Caste capture, Square from,
        Square to)
    {

        if (KING != base && ROOK != base) {
            if (REVOKE_NONE != revocation) {
                String template = "revocation: %s base: %s";
                ChessException cause = new ChessException(format(template, revocation, base));
                throw new ChessException("chess.move.revocation.invalid", cause);
            }
        }

        return new Move(CAPTURE, side, revocation, NONE, capture, base, from, to).validate();
    }

    /**
     * <p>doublePush.</p>
     *
     * @param piece a {@link com.github.osmundf.chess.hub.Piece} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move doublePush(Piece piece) {
        return doublePush(piece.side(), piece.caste(), piece.square());
    }

    /**
     * <p>doublePush.</p>
     *
     * @param side a {@link com.github.osmundf.chess.hub.Side} object.
     * @param base a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move doublePush(Side side, Caste base, Square from) {
        assert side != null && Side.NO_SIDE != side;
        assert from.rank() == 2 || from.rank() == 4;
        assert PAWN == base;
        Square to = side.isWhite() ? from.up(2) : from.down(2);
        return new Move(DOUBLE_PUSH, side, REVOKE_NONE, PAWN, NONE, base, from, to).validate();
    }

    /**
     * <p>enPassant.</p>
     *
     * @param pawn a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param to   a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move enPassant(Piece pawn, Square to) {
        assert pawn != null;
        assert pawn.caste() == PAWN;
        return enPassant(pawn.side(), pawn.caste(), pawn.square(), to);
    }

    /**
     * <p>enPassant.</p>
     *
     * @param side a {@link com.github.osmundf.chess.hub.Side} object.
     * @param base a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from a {@link com.github.osmundf.chess.hub.Square} object.
     * @param to   a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move enPassant(Side side, Caste base, Square from, Square to) {
        assert side != null && Side.NO_SIDE != side;
        assert base == PAWN;
        assert from != null;
        assert to != null;
        return new Move(EN_PASSANT, side, REVOKE_NONE, NONE, PAWN, PAWN, from, to).validate();
    }

    /**
     * <p>castle.</p>
     *
     * @param castleShort a boolean.
     * @param king        a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param rook        a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param revocation  a {@link com.github.osmundf.chess.hub.Revocation} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move castle(boolean castleShort, Revocation revocation, Piece king, Piece rook) {
        assert king != null;
        assert KING == king.caste();
        assert rook != null;
        assert ROOK == rook.caste();
        assert king.side() == rook.side();

        MoveType type = castleShort ? MoveType.CASTLE_SHORT : MoveType.CASTLE_LONG;
        Side side = king.side();
        Square from = king.square();
        Square to = rook.square();
        return new Move(type, side, revocation, KING, NONE, KING, from, to).validate();
    }

    private final MoveType type;

    private final Side side;

    private final Revocation revocation;

    private final Caste promotion;

    private final Caste capture;

    private final Caste base;

    private final Square from;

    private final Square to;

    private final Square rookFromSquare;

    private final Square rookToSquare;

    /**
     * Move Constructor (protected).
     *
     * @param type       move type
     * @param side       board side
     * @param revocation castling right revocation
     * @param promotion  piece promotion
     * @param capture    captured piece caste
     * @param base       piece caste
     * @param from       from square
     * @param to         to square
     */
    protected Move(MoveType type, Side side, Revocation revocation, Caste promotion, Caste capture, Caste base,
        Square from, Square to)
    {
        super(type, side, revocation, promotion, capture, base, from, to);
        this.type = type;
        this.side = side;
        this.promotion = type.isPromotion() ? promotion : NONE;
        this.capture = type.isCapture() ? capture : NONE;
        this.base = base;
        this.from = from;
        this.revocation = revocation;

        if (type.isCastling()) {
            this.to = getKingToSquare();
            this.rookFromSquare = to;
            this.rookToSquare = getRookToSquare();
        }
        else {
            this.to = to;
            this.rookFromSquare = null;
            this.rookToSquare = null;
        }
    }

    /**
     * Return underlying move hash class.
     *
     * @return underlying {@link com.github.osmundf.chess.hub.MoveHash}
     */
    public MoveHash hash() {
        return new MoveHash(hash);
    }

    /**
     * <p>type.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.MoveType} object.
     */
    public MoveType type() {
        return type;
    }

    /**
     * <p>side.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Side} object.
     */
    public Side side() {
        return side;
    }

    /**
     * <p>promotion.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Caste} object.
     */
    public Caste promotion() {
        return promotion;
    }

    /**
     * <p>promotionPiece.</p>
     *
     * @return a {@link java.util.Optional} object.
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
     * <p>capture.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Caste} object.
     */
    public Caste capture() {
        return capture;
    }

    /**
     * <p>capturedPiece.</p>
     *
     * @return a {@link java.util.Optional} object.
     */
    public Optional<Piece> capturedPiece() {
        if (!type.isCapture()) {
            return Optional.empty();
        }
        else {
            Side side = this.side.opposite();
            Square square = EN_PASSANT == type ? squareFor(to.file(), from.rank()) : to;
            return Optional.of(pieceFor(side, capture, square));
        }
    }

    /**
     * <p>base.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Caste} object.
     */
    public Caste base() {
        return base;
    }

    /**
     * <p>from.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square from() {
        return from;
    }

    /**
     * <p>to.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square to() {
        return to;
    }

    /**
     * <p>revocation.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Revocation} object.
     */
    public Revocation revocation() {
        return revocation;
    }

    /**
     * <p>rookFrom.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square rookFrom() {
        return rookFromSquare;
    }

    /**
     * <p>rookTo.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square rookTo() {
        return rookToSquare;
    }

    /**
     * <p>kingFrom.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square kingFrom() {
        return from;
    }

    /**
     * <p>kingTo.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square kingTo() {
        return to;
    }

    /**
     * <p>revokedKingSide.</p>
     *
     * @return a boolean.
     */
    public boolean revokedKingSide() {
        return revocation.isKingSide();
    }

    /**
     * <p>revokedQueenSide.</p>
     *
     * @return a boolean.
     */
    public boolean revokedQueenSide() {
        return revocation.isQueenSide();
    }

    /**
     * <p>isNull.</p>
     *
     * @return a boolean.
     */
    public boolean isNull() {
        return type == BASE && promotion == NONE && capture == NONE && base == NONE;
    }

    /**
     * A single point for validating a move.
     *
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public Move validate() {
        if (BASE == type) {
            return this;
        }
        if (CAPTURE == type) {
            return this;
        }
        if (DOUBLE_PUSH == type) {
            if (NONE != promotion) {
                ChessException cause = new ChessException("type: " + type + " promotion: " + promotion);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (NONE != capture) {
                ChessException cause = new ChessException("type: " + type + " capture: " + capture);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (PAWN != base) {
                ChessException cause = new ChessException("type: " + type + " base: " + base);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (from.file() != to.file()) {
                ChessException cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (side.isWhite() && from.rank() != 2 || side.isWhite() && to.rank() != 4) {
                ChessException cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }
            if (side.isBlack() && from.rank() != 7 || side.isBlack() && to.rank() != 5) {
                ChessException cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.double.push.move", cause);
            }

            return this;
        }
        if (EN_PASSANT == type) {
            if (PAWN != promotion) {
                ChessException cause = new ChessException("type: " + type + " promotion: " + promotion);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (PAWN != capture) {
                ChessException cause = new ChessException("type: " + type + " capture: " + capture);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (PAWN != base) {
                ChessException cause = new ChessException("type: " + type + " base: " + base);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (to.file() != from.file()) {
                ChessException cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (side.isWhite() && to.rank() != (from.rank() + 1)) {
                ChessException cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }
            if (side.isBlack() && to.rank() != (from.rank() - 1)) {
                ChessException cause = new ChessException("from: " + from + " to: " + to);
                throw new ChessException("chess.move.invalid.en.passant.move", cause);
            }

            return this;
        }

        if (PROMOTION == type) {
            if (NONE == promotion || PAWN == promotion || KING == promotion) {
                ChessException cause = new ChessException("type: " + type + " promotion: " + promotion);
                throw new ChessException("chess.move.invalid.promotion.move", cause);
            }

            if (NONE != capture) {
                ChessException cause = new ChessException("type: " + type + " capture: " + capture);
                throw new ChessException("chess.move.invalid.promotion.move", cause);
            }

            return this;
        }
        if (CAPTURE_PROMOTION == type) {
            if (NONE == promotion || PAWN == promotion || KING == promotion) {
                ChessException cause = new ChessException("type: " + type + " promotion: " + promotion);
                throw new ChessException("chess.move.invalid.capture.promotion.move", cause);
            }

            if (NONE == capture || KING == capture) {
                ChessException cause = new ChessException("type: " + type + " capture: " + capture);
                throw new ChessException("chess.move.invalid.capture.promotion.move", cause);
            }

            return this;
        }

        // (CASTLE_SHORT == type || CASTLE_LONG == type)
        if (NONE != promotion) {
            ChessException cause = new ChessException("type: " + type + " promotion: " + promotion);
            throw new ChessException("chess.move.invalid.castle.move", cause);
        }
        if (NONE != capture) {
            ChessException cause = new ChessException("type: " + type + " capture: " + capture);
            throw new ChessException("chess.move.invalid.castle.move", cause);
        }
        if (REVOKE_NONE == revocation) {
            ChessException cause = new ChessException("type: " + type + " revocation: " + revocation);
            throw new ChessException("chess.move.invalid.castle.move", cause);
        }
        if (CASTLE_SHORT == type) {
            if (!revocation.isKingSide()) {
                ChessException cause = new ChessException("type: " + type + " revocation: " + revocation);
                throw new ChessException("chess.move.invalid.castle.move", cause);
            }
        }
        if (CASTLE_LONG == type) {
            if (!revocation.isQueenSide()) {
                ChessException cause = new ChessException("type: " + type + " revocation: " + revocation);
                throw new ChessException("chess.move.invalid.castle.move", cause);
            }
        }

        return this;
    }

    /**
     * Return king to-square.
     *
     * @return king to-square.
     */
    private Square getKingToSquare() {
        boolean castleShort = MoveType.CASTLE_SHORT == type;
        return squareFor(castleShort ? 'g' : 'c', kingFrom().rank());
    }

    /**
     * Return rook to-square.
     *
     * @return rook to-square
     */
    private Square getRookToSquare() {
        boolean castleShort = MoveType.CASTLE_SHORT == type;
        return squareFor(castleShort ? 'f' : 'd', rookFrom().rank());
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return super.hash;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Move)) {
            return false;
        }
        Move other = (Move) object;
        return this == object || this.hash == other.hash;
    }

    /**
     * Returns the string representation for the {@link com.github.osmundf.chess.hub.Move} by its hash code.
     *
     * @return representational string
     */
    @Override
    public String toString() {
        return format("Move(0x%08x)", hash);
    }
}
