package com.github.osmundf.chess.hub;

import static com.github.osmundf.chess.hub.Side.NO_SIDE;
import static com.github.osmundf.chess.hub.Side.WHITE;

/**
 * Chess game castle state.
 */
public class CastleState {

    public static CastleState castleStateFor(byte hash) {
        boolean wck = (hash & 0x80) != 0x0;
        boolean wcq = (hash & 0x40) != 0x0;
        boolean wkr = (hash & 0x10) != 0x0;
        boolean wqr = (hash & 0x20) != 0x0;
        boolean bck = (hash & 0x8) != 0x0;
        boolean bcq = (hash & 0x4) != 0x0;
        boolean bkr = (hash & 0x1) != 0x0;
        boolean bqr = (hash & 0x2) != 0x0;

        boolean wc = wck | wcq;
        boolean wr = wkr | wqr;

        boolean bc = bck | bcq;
        boolean br = bkr | bqr;

        if (wc && wr && bc && br) {
            var hashString = String.format("0x%02x", hash);
            var cause = new ChessException("both.castled.retained.rights: " + hashString);
            throw new ChessException("chess.castle.state.hash.invalid", cause);
        }

        if (wc && wr) {
            var hashString = String.format("0x%02x", hash);
            var cause = new ChessException("white.castled.retained.rights: " + hashString);
            throw new ChessException("chess.castle.state.hash.invalid", cause);
        }

        if (bc && br) {
            var hashString = String.format("0x%02x", hash);
            var cause = new ChessException("black.castled.retained.rights: " + hashString);
            throw new ChessException("chess.castle.state.hash.invalid", cause);
        }

        if (wck && wcq && bck && bcq) {
            var hashString = String.format("0x%02x", hash);
            var cause = new ChessException("both.castled.both.sides: " + hashString);
            throw new ChessException("chess.castle.state.hash.invalid", cause);
        }

        if (wck && wcq) {
            var hashString = String.format("0x%02x", hash);
            var cause = new ChessException("white.castled.both.sides: " + hashString);
            throw new ChessException("chess.castle.state.hash.invalid", cause);
        }

        if (bck && bcq) {
            var hashString = String.format("0x%02x", hash);
            var cause = new ChessException("black.castled.both.sides: " + hashString);
            throw new ChessException("chess.castle.state.hash.invalid", cause);
        }

        return new CastleState(hash);
    }

    private final byte hash;

    /**
     * Chess castle state constructor (protected).
     *
     * @param hash castle state hash
     */
    protected CastleState(byte hash) {
        // index: white[cc][kq] black[cc][kq]
        this.hash = hash;
    }

    /**
     * Returns new instance where side castled king side.
     *
     * @param side side to castle king-side.
     * @return new instance where side castled king-side
     */
    public CastleState castleKingSide(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.castle.king.side.failed", cause);
        }
        if (!hasKingSideRight(side)) {
            var cause = new ChessException("state: " + this);
            throw new ChessException("chess.castle.state.castle.king.side.failed", cause);
        }
        if (WHITE == side) {
            return new CastleState((byte) ((hash & 0x0f) | 0x80));
        }
        else {
            return new CastleState((byte) ((hash & 0xf0) | 0x8));
        }
    }

    /**
     * Returns a new instance where side castled queen side.
     *
     * @param side side to castle queen-side
     * @return new instance where side castled queen-side
     */
    public CastleState castleQueenSide(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.castle.queen.side.failed", cause);
        }
        if (!hasQueenSideRight(side)) {
            var cause = new ChessException("state: " + this);
            throw new ChessException("chess.castle.state.castle.queen.side.failed", cause);
        }
        if (WHITE == side) {
            return new CastleState((byte) ((hash & 0x0f) | 0x40));
        }
        else {
            return new CastleState((byte) ((hash & 0xf0) | 0x4));
        }
    }

    /**
     * Returns if side has castled.
     *
     * @param side side to check if castled
     * @return true if side has castled, false otherwise
     */
    public boolean hasCastled(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.castled.failed", cause);
        }
        return quickHasCastled(side);
    }

    /**
     * Return if side has castled king side.
     *
     * @param side side to check if castled king-side
     * @return true if side has castled king-side (short), false otherwise
     */
    public boolean hasCastledKingSide(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.has.castled.king.side.failed", cause);
        }
        if (side == WHITE) {
            return (hash & 0x80) != 0;
        }
        else {
            return (hash & 0x8) != 0;
        }
    }

    /**
     * Returns if side has castled queen side.
     *
     * @param side side to check if castled queen-side
     * @return true if side has castled queen-side (long), false otherwise
     */
    public boolean hasCastledQueenSide(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.castled.queen.side.failed", cause);
        }
        if (WHITE == side) {
            return (hash & 0x40) != 0;
        }
        else {
            return (hash & 0x4) != 0;
        }
    }

    /**
     * Returns new instance with both castling rights revoked.
     *
     * @param side side to revoke both castling rights
     * @return new instance with both castling rights revoked
     */
    public CastleState revokeBoth(Side side) {
        if (side == null || side == NO_SIDE) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.revoke.both.failed", cause);
        }
        if (quickHasCastled(side)) {
            var cause = new ChessException("side.is.castled: " + side);
            throw new ChessException("chess.castle.state.revoke.both.failed", cause);
        }
        if (WHITE == side) {
            return new CastleState((byte) (hash & 0xf));
        }
        else {
            return new CastleState((byte) (hash & 0xf0));
        }
    }

    /**
     * Returns new instance with king side castling right revoked.
     *
     * @param side side to revoke king-side castling
     * @return new instance with king-side castling right revoked
     */
    public CastleState revokeKingSide(Side side) {
        if (side == null || side == NO_SIDE) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.revoke.king.side.failed", cause);
        }
        if (quickHasCastled(side)) {
            var cause = new ChessException("side.is.castled");
            throw new ChessException("chess.castle.state.revoke.king.side.failed", cause);
        }
        if (WHITE == side) {
            return new CastleState((byte) (hash & 0x1f));
        }
        else {
            return new CastleState((byte) (hash & 0xf1));
        }
    }

    /**
     * Returns new instance with queen side castling right revoked.
     *
     * @param side side to revoke queen-side castling
     * @return new instance with queen-side castling right revoked
     */
    public CastleState revokeQueenSide(Side side) {
        if (side == null || side == NO_SIDE) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.revoke.queen.side.failed", cause);
        }
        if (quickHasCastled(side)) {
            var cause = new ChessException("side.is.castled: " + side);
            throw new ChessException("chess.castle.state.revoke.queen.side.failed", cause);
        }
        if (WHITE == side) {
            return new CastleState((byte) (hash & 0x2f));
        }
        else {
            return new CastleState((byte) (hash & 0xf2));
        }
    }

    /**
     * Returns new instance with both castling rights restored.
     *
     * @param side side to restore both castling rights
     * @return new instance with both castling rights restored
     */
    public CastleState restoreBoth(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.restore.failed", cause);
        }
        int result;
        if (WHITE == side) {
            result = hash & 0x0f | 0x30;
        }
        else {
            result = hash & 0xf0 | 0x03;
        }
        return new CastleState((byte) result);
    }

    /**
     * Returns new instance with king side castling right restored.
     *
     * @param side side to restore king-side castling right
     * @return new instance with kind-side castling right restored
     */
    public CastleState restoreKingSide(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.restore.failed", cause);
        }
        int result;
        if (WHITE == side) {
            result = hash & 0x0f | 0x20;
        }
        else {
            result = hash & 0xf0 | 0x02;
        }
        return new CastleState((byte) result);
    }

    /**
     * Returns new instance with queen side castling right restored.
     *
     * @param side side to restore queen-side castling right
     * @return new instance with queen-side castling rights restored
     */
    public CastleState restoreQueenSide(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.restore.failed", cause);
        }
        int result;
        if (WHITE == side) {
            result = hash & 0x0f | 0x10;
        }
        else {
            result = hash & 0xf0 | 0x01;
        }
        return new CastleState((byte) result);
    }

    /**
     * Returns if side has any right to castle.
     *
     * @param side board side
     * @return true if side has any right to castle, false otherwise
     */
    public boolean hasAnyRight(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.any.right.failed", cause);
        }
        if (WHITE == side) {
            return (hash & 0x30) != 0;
        }
        else {
            return (hash & 0x3) != 0;
        }
    }

    /**
     * Returns if side has right to castle king side.
     *
     * @param side board side
     * @return true if side has right to castle king-side (short), false otherwise
     */
    public boolean hasKingSideRight(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.king.side.right.failed", cause);
        }
        if (WHITE == side) {
            return (hash & 0x20) != 0x0;
        }
        else {
            return (hash & 0x2) != 0;
        }
    }

    /**
     * Returns if side has right to castle queen side.
     *
     * @param side side to check for queen-side castling right
     * @return true if side has right to castle queen-side (long), false otherwise
     */
    public boolean hasQueenSideRight(Side side) {
        if (side == null || NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.queen.side.right.failed", cause);
        }
        if (WHITE == side) {
            return (hash & 0x10) != 0;
        }
        else {
            return (hash & 0x1) != 0;
        }
    }

    private boolean quickHasCastled(final Side side) {
        if (side == WHITE) {
            return (hash & 0xc0) != 0x0;
        }
        else {
            return (hash & 0xc) != 0x0;
        }
    }

    /** Returns castle state hash value. */
    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CastleState)) {
            return false;
        }
        return this == object || this.hash == ((CastleState) object).hash;
    }

    @Override
    public String toString() {
        return String.format("castleState(0x%02x)", hash);
    }
}
