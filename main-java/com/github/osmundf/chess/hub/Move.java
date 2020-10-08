package com.github.osmundf.chess.hub;

import java.util.Objects;
import java.util.Optional;

import static com.github.osmundf.chess.hub.Caste.BISHOP;
import static com.github.osmundf.chess.hub.Caste.KING;
import static com.github.osmundf.chess.hub.Caste.KNIGHT;
import static com.github.osmundf.chess.hub.Caste.NONE;
import static com.github.osmundf.chess.hub.Caste.PAWN;
import static com.github.osmundf.chess.hub.Caste.QUEEN;
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
        MoveHash moveHash = MoveHash.moveHashFor(hash);
        MoveType type = moveHash.type();
        Side side = moveHash.side();
        Caste promotion = moveHash.promotion();
        Caste capture = moveHash.capture();
        Caste base = moveHash.base();
        Square from = moveHash.from();
        Square to = moveHash.to();

        Move move = new Move(type, side, promotion, capture, base, from, to);

        ChessException error = move.validate();
        if (error != null) {
            throw error;
        }
        return move;
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

        return basicMove(side, base, from, to);
    }

    /**
     * <p>basicMove.</p>
     *
     * @param side       a {@link com.github.osmundf.chess.hub.Side} object.
     * @param base       a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from       a {@link com.github.osmundf.chess.hub.Square} object.
     * @param to         a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move basicMove(Side side, Caste base, Square from, Square to) {
        Objects.requireNonNull(side, "chess.move.side.null");
        Objects.requireNonNull(base, "chess.move.base.null");
        Objects.requireNonNull(from, "chess.move.from.square.null");
        Objects.requireNonNull(to, "chess.move.to.square.null");

        // Check for pawn double-push.
        if (PAWN == base) {
            byte fromRank = from.rank();
            byte toRank = to.rank();

            // Check for valid double-push.
            if (WHITE == side) {
                if (fromRank == 2 && toRank == 4) {
                    return new Move(DOUBLE_PUSH, side, PAWN, NONE, base, from, to);
                }
            }
            // Check for valid double-push.
            if (BLACK == side) {
                if (fromRank == 7 && toRank == 5) {
                    return new Move(DOUBLE_PUSH, side, PAWN, NONE, base, from, to);
                }
            }
        }

        return new Move(BASE, side, NONE, NONE, base, from, to);
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

        Side side = source.side();
        Caste capture = captured.caste();
        Caste base = source.caste();
        Square from = source.square();
        Square to = captured.square();
        return captureMove(side, capture, base, from, to);
    }

    /**
     * <p>captureMove.</p>
     *
     * @param side    a {@link com.github.osmundf.chess.hub.Side} object.
     * @param capture a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param base    a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from    a {@link com.github.osmundf.chess.hub.Square} object.
     * @param to      a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move captureMove(Side side, Caste capture, Caste base, Square from, Square to) {
        return new Move(CAPTURE, side, NONE, capture, base, from, to);
    }

    /**
     * <p>doublePush.</p>
     *
     * @param piece a {@link com.github.osmundf.chess.hub.Piece} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move doublePushMove(Piece piece) {
        Objects.requireNonNull(piece, "move.piece.null");

        return doublePushMove(piece.side(), piece.caste(), piece.square());
    }

    /**
     * <p>doublePush.</p>
     *
     * @param side a {@link com.github.osmundf.chess.hub.Side} object.
     * @param base a {@link com.github.osmundf.chess.hub.Caste} object.
     * @param from a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move doublePushMove(Side side, Caste base, Square from) {
        assert side != null && Side.NO_SIDE != side;
        assert from.rank() == 2 || from.rank() == 7;
        assert PAWN == base;
        Square to = side.isWhite() ? from.up(2) : from.down(2);
        return new Move(DOUBLE_PUSH, side, NONE, NONE, base, from, to);
    }

    /**
     * <p>enPassant.</p>
     *
     * @param pawn a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param to   a {@link com.github.osmundf.chess.hub.Square} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move enPassantMove(Piece pawn, Square to) {
        assert pawn != null;
        assert PAWN == pawn.caste();
        return enPassantMove(pawn.side(), pawn.caste(), pawn.square(), to);
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
    public static Move enPassantMove(Side side, Caste base, Square from, Square to) {
        assert side != null && Side.NO_SIDE != side;
        assert base == PAWN;
        assert from != null;
        assert to != null;
        return new Move(EN_PASSANT, side, NONE, PAWN, PAWN, from, to);
    }

    /**
     * <p>castle.</p>
     *
     * @param type       move type
     * @param king       a {@link com.github.osmundf.chess.hub.Piece} object.
     * @param rook       a {@link com.github.osmundf.chess.hub.Piece} object.
     * @return a {@link com.github.osmundf.chess.hub.Move} object.
     */
    public static Move castleMove(MoveType type, Piece king, Piece rook) {
        Objects.requireNonNull(type, "move.type.null");
        Objects.requireNonNull(king, "move.king.piece.null");
        Objects.requireNonNull(rook, "move.rook.piece.null");

        if (king.side() != rook.side()) {
            String template = "king: %s rook: %s";
            ChessException cause = new ChessException(format(template, king, rook));
            throw new ChessException("chess.move.castle.move.invalid", cause);
        }

        Side side = king.side();
        Square kingSquare = king.square();
        Square rookSquare = rook.square();
        return castleMove(type, side, kingSquare, rookSquare);
    }

    /**
     * <p>castle.</p>
     *
     * @param type       move type
     * @param side       board side
     * @param from       king from square
     * @param to         rook from square
     * @return a castling move
     */
    public static Move castleMove(MoveType type, Side side, Square from, Square to) {
        Objects.requireNonNull(type, "move.type.null");
        Objects.requireNonNull(side, "move.side.null");
        Objects.requireNonNull(from, "move.from.square.null");
        Objects.requireNonNull(to, "move.to.square.null");

        if (CASTLE_LONG != type && CASTLE_SHORT != type) {
            String template = "type: %s";
            ChessException cause = new ChessException(format(template, type));
            throw new ChessException("chess.move.castle.move.invalid", cause);
        }

        return new Move(type, side, NONE, NONE, KING, from, to);
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

    /**
     * Move Constructor (protected).
     *
     * @param m move type
     * @param s board side
     * @param p piece promotion
     * @param c captured piece caste
     * @param b piece caste
     * @param f from square
     * @param t to square
     */
    protected Move(MoveType m, Side s, Caste p, Caste c, Caste b, Square f, Square t) {
        super(m, s, p, c, b, f, t);
        this.type = m;
        this.side = s;
        this.promotion = p;
        this.capture = c;
        this.base = b;
        this.from = f;

        if (m.isCastling()) {
            this.to = getKingToSquare();
            this.rookFromSquare = t;
            this.rookToSquare = getRookToSquare();
        }
        else {
            this.to = t;
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
        if (NONE == promotion) {
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
        return KING != base ? null : from;
    }

    /**
     * <p>kingTo.</p>
     *
     * @return a {@link com.github.osmundf.chess.hub.Square} object.
     */
    public Square kingTo() {
        return KING != base ? null : to;
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
     * Returns if move, including squares, has any error.
     *
     * @return chess exception of error, null otherwise
     */
    final public ChessException validate() {
        // Check valid pawn moves.
        if (PAWN == base) {
            // Valid pawn base/capture move.
            if (BASE == type || CAPTURE == type) {
                if (NONE == promotion) {
                    if (KING != capture && (NONE != capture) == type.isCapture()) {
                        if (validPawnSquares(type, side, from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.pawn.move", cause);
                    }
                }

                String template = "type: %s promotion: %s capture: %s";
                ChessException cause = new ChessException(format(template, type, promotion, capture));
                return new ChessException("chess.move.invalid.pawn.move", cause);
            }

            // Valid pawn double-push.
            if (DOUBLE_PUSH == type) {
                if (NONE == promotion) {
                    if (NONE == capture) {
                        if (validDoublePushSquares(side, from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.double.push.move", cause);
                    }
                }

                String template = "type: %s promotion: %s capture: %s";
                ChessException cause = new ChessException(format(template, type, promotion, capture));
                return new ChessException("chess.move.invalid.pawn.move", cause);
            }

            // Valid pawn capture en passant.
            if (EN_PASSANT == type) {
                if (NONE == promotion) {
                    if (PAWN == capture) {
                        if (validEnPassantSquares(side, from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.en.passant.move", cause);
                    }
                }

                String template = "type: %s promotion: %s capture: %s";
                ChessException cause = new ChessException(format(template, type, promotion, capture));
                return new ChessException("chess.move.invalid.pawn.move", cause);
            }

            // Valid pawn promotion move.
            if (PROMOTION == type) {
                if (NONE != promotion && PAWN != promotion && KING != promotion) {
                    if (NONE == capture) {
                        if (validPromotionSquares(side, from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.promotion.move", cause);
                    }
                }

                String template = "type: %s promotion: %s capture: %s";
                ChessException cause = new ChessException(format(template, type, promotion, capture));
                return new ChessException("chess.move.invalid.pawn.move", cause);
            }

            // Valid pawn capture-promotion move.
            if (CAPTURE_PROMOTION == type) {
                if (NONE != promotion && PAWN != promotion && KING != promotion) {
                    if (NONE != capture && KING != capture) {
                        if (validCapturePromotionSquares(side, from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.capture.promotion.move", cause);
                    }
                }

                String template = "type: %s promotion: %s capture: %s";
                ChessException cause = new ChessException(format(template, type, promotion, capture));
                return new ChessException("chess.move.invalid.pawn.move", cause);
            }

            // Invalid pawn move.
            String template = "type: %s promotion: %s capture: %s";
            ChessException cause = new ChessException(format(template, type, promotion, capture));
            return new ChessException("chess.move.invalid.pawn.move", cause);
        }

        // Check valid knight moves.
        if (KNIGHT == base) {
            // Valid knight base/capture move.
            if (BASE == type || CAPTURE == type) {
                if (NONE == promotion) {
                    if (KING != capture && (NONE != capture) == type.isCapture()) {
                        if (validKnightMove(from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.knight.move", cause);
                    }
                }
            }

            // Invalid knight move.
            String template = "type: %s promotion: %s capture: %s";
            ChessException cause = new ChessException(format(template, type, promotion, capture));
            return new ChessException("chess.move.invalid.knight.move", cause);
        }

        // Check valid bishop moves.
        if (BISHOP == base) {
            // Valid bishop base/capture move.
            if (BASE == type || CAPTURE == type) {
                if (NONE == promotion) {
                    if (KING != capture && (NONE != capture) == type.isCapture()) {
                        if (validBishopMove(from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.bishop.move", cause);
                    }
                }
            }

            // Invalid bishop move.
            String template = "type: %s promotion: %s capture: %s";
            ChessException cause = new ChessException(format(template, type, promotion, capture));
            return new ChessException("chess.move.invalid.bishop.move", cause);
        }

        // Check valid queen moves.
        if (QUEEN == base) {
            // Valid queen base/capture move.
            if (BASE == type || CAPTURE == type) {
                if (NONE == promotion) {
                    if (KING != capture && (NONE != capture) == type.isCapture()) {
                        if (validQueenMove(from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.queen.move", cause);
                    }
                }
            }

            // Invalid queen move.
            String template = "type: %s promotion: %s capture: %s";
            ChessException cause = new ChessException(format(template, type, promotion, capture));
            return new ChessException("chess.move.invalid.queen.move", cause);
        }

        // Check valid rook moves.
        if (ROOK == base) {
            // Valid rook base/capture move.
            if (BASE == type || CAPTURE == type) {
                if (NONE == promotion) {
                    if (KING != capture && (NONE != capture) == type.isCapture()) {
                        if (validRookMove(from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.rook.move", cause);
                    }
                }
            }

            // Invalid rook move.
            String template = "type: %s promotion: %s capture: %s";
            ChessException cause = new ChessException(format(template, type, promotion, capture));
            return new ChessException("chess.move.invalid.rook.move", cause);
        }

        // Check valid king moves.
        if (KING == base) {
            // Valid king base/capture move.
            if (BASE == type || CAPTURE == type) {
                if (NONE == promotion) {
                    if (KING != capture && (NONE != capture) == type.isCapture()) {
                        if (validKingMove(from, to)) {
                            return null;
                        }
                        ChessException cause = new ChessException("from: " + from + " to: " + to);
                        return new ChessException("chess.move.invalid.king.move", cause);
                    }
                }
            }

            // Check valid castling.
            if (CASTLE_LONG == type || CASTLE_SHORT == type) {
                // Valid castle move.
                if (NONE == promotion) {
                    if (NONE == capture) {
                        if (validCastleMove(side, from, to, rookFromSquare, rookToSquare)) {
                            return null;
                        }
                        String template = "kingFrom: %s kingTo: %s rookFrom: %s rookTo: %s";
                        String causeMessage = format(template, from, to, rookFromSquare, rookToSquare);
                        ChessException cause = new ChessException(causeMessage);
                        return new ChessException("chess.move.invalid.castle.move", cause);
                    }
                }

                // Invalid castle move.
                String template = "type: %s promotion: %s capture: %s";
                ChessException cause = new ChessException(format(template, type, promotion, capture));
                return new ChessException("chess.move.invalid.castle.move", cause);
            }

            // Invalid king move.
            String template = "type: %s promotion: %s capture: %s";
            ChessException cause = new ChessException(format(template, type, promotion, capture));
            return new ChessException("chess.move.invalid.king.move", cause);
        }

        // Check valid null move.
        if (NONE == base) {
            if (BASE == type) {
                if (NONE == promotion) {
                    if (NONE == capture) {
                        return null;
                    }
                }
            }
        }

        // Invalid move.
        String template = "type: %s promotion: %s capture: %s base: %s";
        ChessException cause = new ChessException(format(template, type, promotion, capture, base));
        return new ChessException("chess.move.invalid.move", cause);
    }

    /**
     * Chess960 compatible check for valid castling squares.
     *
     * @param side     side
     * @param kingFrom king from square
     * @param kingTo   king to square
     * @param rookFrom rook from square
     * @param rookTo   rook to square
     * @return true if valid, false otherwise
     */
    private boolean validCastleMove(Side side, Square kingFrom, Square kingTo, Square rookFrom, Square rookTo) {
        if (side.isWhite()) {
            if (kingFrom.rank() != 1 || kingTo.rank() != 1) {
                return false;
            }
            if (rookFrom.rank() != 1 || rookTo.rank() != 1) {
                return false;
            }
        }

        if (side.isBlack()) {
            if (kingFrom.rank() != 8 || kingTo.rank() != 8) {
                return false;
            }
            if (rookFrom.rank() != 8 || rookTo.rank() != 8) {
                return false;
            }
        }

        return kingFrom != kingTo;
    }

    private boolean validKingMove(Square from, Square to) {
        if (from == to) {
            return false;
        }

        int deltaFile = from.file() - to.file();
        int deltaRank = from.rank() - to.rank();

        return deltaFile == 1 || deltaFile == -1 || deltaRank == 1 || deltaRank == -1;
    }

    private boolean validRookMove(Square from, Square to) {
        if (from == to) {
            return false;
        }

        int deltaFile = from.file() - to.file();
        int deltaRank = from.rank() - to.rank();

        return deltaFile == 0 || deltaRank == 0;
    }

    private boolean validQueenMove(Square from, Square to) {
        if (from == to) {
            return false;
        }

        int deltaFile = from.file() - to.file();
        int deltaRank = from.rank() - to.rank();

        // Move along file, along rank, or along diagonal.
        return deltaFile == 0 || deltaRank == 0 || deltaFile == deltaRank || deltaFile == -deltaRank;
    }

    private boolean validBishopMove(Square from, Square to) {
        if (from == to) {
            return false;
        }

        int deltaFile = from.file() - to.file();
        int deltaRank = from.rank() - to.rank();

        return deltaFile == deltaRank || deltaFile == -deltaRank;
    }

    private boolean validKnightMove(Square from, Square to) {
        if (from == to) {
            return false;
        }

        int deltaFile = from.file() - to.file();
        int deltaRank = from.rank() - to.rank();

        if ((deltaFile == 2 || deltaFile == -2) && (deltaRank == 1 || deltaRank == -1)) {
            return true;
        }
        return (deltaFile == 1 || deltaFile == -1) && (deltaRank == 2 || deltaRank == -2);
    }

    private boolean validPawnSquares(MoveType type, Side side, Square from, Square to) {
        if (side.isWhite()) {
            if (from.rank() + 1 != to.rank()) {
                return false;
            }
        }
        if (side.isBlack()) {
            if (from.rank() - 1 != to.rank()) {
                return false;
            }
        }

        // Base move.
        if (!type.isCapture()) {
            return from.file() == to.file();
        }

        // Capture move.
        int delta = from.file() - to.file();
        return delta == 1 || delta == -1;
    }

    private boolean validDoublePushSquares(Side side, Square from, Square to) {
        if (side.isWhite()) {
            if (from.rank() != 2 || to.rank() != 4) {
                return false;
            }
        }
        if (side.isBlack()) {
            if (from.rank() != 7 || to.rank() != 5) {
                return false;
            }
        }

        return from.file() == to.file();
    }

    private boolean validEnPassantSquares(Side side, Square from, Square to) {
        if (side.isWhite()) {
            if (to.rank() != from.rank() + 1) {
                return false;
            }
            if (to.rank() != 6) {
                return false;
            }
        }
        if (side.isBlack()) {
            if (to.rank() != from.rank() - 1) {
                return false;
            }
            if (to.rank() != 3) {
                return false;
            }
        }

        int delta = from.file() - to.file();
        return delta == 1 || delta == -1;
    }

    private boolean validPromotionSquares(Side side, Square from, Square to) {
        if (side.isWhite()) {
            if (from.rank() != 7 || to.rank() != 8) {
                return false;
            }
        }
        if (side.isBlack()) {
            if (from.rank() != 2 || to.rank() != 1) {
                return false;
            }
        }

        return from.file() == to.file();
    }

    private boolean validCapturePromotionSquares(Side side, Square from, Square to) {
        if (side.isWhite()) {
            if (from.rank() != 7 || to.rank() != 8) {
                return false;
            }
        }
        if (side.isBlack()) {
            if (from.rank() != 2 || to.rank() != 1) {
                return false;
            }
        }

        // Capture move.
        int delta = from.file() - to.file();
        return delta == 1 || delta == -1;
    }

    /**
     * Return king to-square.
     *
     * @return king to-square
     */
    private Square getKingToSquare() {
        boolean castleShort = MoveType.CASTLE_SHORT == type;
        return squareFor(castleShort ? 'g' : 'c', side.isWhite() ? 1 : 8);
    }

    /**
     * Return rook to-square.
     *
     * @return rook to-square
     */
    private Square getRookToSquare() {
        boolean castleShort = MoveType.CASTLE_SHORT == type;
        return squareFor(castleShort ? 'f' : 'd', side.isWhite() ? 1 : 8);
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
