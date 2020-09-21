package com.github.osmundf.chess.hub;

/**
 * Chess game castle state.
 */
public class CastleState {

    public static CastleState newCastleState(byte index) {
        boolean wck = (index & 0x80) != 0x0;
        boolean wcq = (index & 0x40) != 0x0;
        boolean wkr = (index & 0x10) != 0x0;
        boolean wqr = (index & 0x20) != 0x0;
        boolean bck = (index & 0x8) != 0x0;
        boolean bcq = (index & 0x4) != 0x0;
        boolean bkr = (index & 0x1) != 0x0;
        boolean bqr = (index & 0x2) != 0x0;

        boolean wc = wck | wcq;
        boolean wr = wkr | wqr;

        boolean bc = bck | bcq;
        boolean br = bkr | bqr;

        if (wc && wr && bc && br) {
            var indexString = String.format("0x%02x", index);
            var cause = new ChessException("both.castled.retained.rights: " + indexString);
            throw new ChessException("chess.castle.state..index.invalid", cause);
        }

        if (wc && wr) {
            var indexString = String.format("0x%02x", index);
            var cause = new ChessException("white.castled.retained.rights: " + indexString);
            throw new ChessException("chess.castle.state..index.invalid", cause);
        }

        if (bc && br) {
            var indexString = String.format("0x%02x", index);
            var cause = new ChessException("black.castled.retained.rights: " + indexString);
            throw new ChessException("chess.castle.state..index.invalid", cause);
        }

        if (wck && wcq && bck && bcq) {
            var indexString = String.format("0x%02x", index);
            var cause = new ChessException("both.castled.both.sides: " + indexString);
            throw new ChessException("chess.castle.state..index.invalid", cause);
        }

        if (wck && wcq) {
            var indexString = String.format("0x%02x", index);
            var cause = new ChessException("white.castled.both.sides: " + indexString);
            throw new ChessException("chess.castle.state..index.invalid", cause);
        }

        if (bck && bcq) {
            var indexString = String.format("0x%02x", index);
            var cause = new ChessException("black.castled.both.sides: " + indexString);
            throw new ChessException("chess.castle.state..index.invalid", cause);
        }

        return new CastleState(index);
    }

    private final byte index;

    /**
     * Chess castle state constructor (protected).
     *
     * @param index castle state index
     */
    protected CastleState(byte index) {
        // index: white[cc][kq] black[cc][kq]
        this.index = index;
    }

    /**
     * Returns new instance where side castled king side.
     *
     * @param side side to castle king-side.
     * @return new instance where side castled king-side
     */
    public CastleState castleKingSide(Side side) {
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.castle.king.side.failed", cause);
        }
        if (Side.WHITE == side) {
            return new CastleState((byte) ((index & 0x0f) | 0x80));
        }
        else {
            return new CastleState((byte) ((index & 0xf0) | 0x8));
        }
    }

    /**
     * Returns a new instance where side castled queen side.
     *
     * @param side side to castle queen-side
     * @return new instance where side castled queen-side
     */
    public CastleState castleQueenSide(Side side) {
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.castle.queen.side.failed", cause);
        }
        if (Side.WHITE == side) {
            return new CastleState((byte) ((index & 0x0f) | 0x40));
        }
        else {
            return new CastleState((byte) ((index & 0xf0) | 0x4));
        }
    }

    /**
     * Returns if side has castled.
     *
     * @param side side to check if castled
     * @return true if side has castled, false otherwise
     */
    public boolean hasCastled(Side side) {
        if (side == null || Side.NO_SIDE == side) {
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
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.has.castled.king.side.failed", cause);
        }
        if (side == Side.WHITE) {
            return (index & 0x80) != 0;
        }
        else {
            return (index & 0x8) != 0;
        }
    }

    /**
     * Returns if side has castled queen side.
     *
     * @param side side to check if castled queen-side
     * @return true if side has castled queen-side (long), false otherwise
     */
    public boolean hasCastledQueenSide(Side side) {
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.castled.queen.side.failed", cause);
        }
        if (Side.WHITE == side) {
            return (index & 0x40) != 0;
        }
        else {
            return (index & 0x4) != 0;
        }
    }

    /**
     * Returns new instance with both castling rights revoked.
     *
     * @param side side to revoke both castling rights
     * @return new instance with both castling rights revoked
     */
    public CastleState revokeBoth(Side side) {
        if (side == null || side == Side.NO_SIDE) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.revoke.both.failed", cause);
        }
        if (quickHasCastled(side)) {
            var cause = new ChessException("side.is.castled: " + side);
            throw new ChessException("chess.castle.state.revoke.both.failed", cause);
        }
        if (Side.WHITE == side) {
            return new CastleState((byte) (index & 0xf));
        }
        else {
            return new CastleState((byte) (index & 0xf0));
        }
    }

    /**
     * Returns new instance with king side castling right revoked.
     *
     * @param side side to revoke king-side castling
     * @return new instance with king-side castling right revoked
     */
    public CastleState revokeKingSide(Side side) {
        if (side == null || side == Side.NO_SIDE) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.revoke.king.side.failed", cause);
        }
        if (quickHasCastled(side)) {
            var cause = new ChessException("side.is.castled");
            throw new ChessException("chess.castle.state.revoke.king.side.failed", cause);
        }
        if (Side.WHITE == side) {
            return new CastleState((byte) (index & 0x1f));
        }
        else {
            return new CastleState((byte) (index & 0xf1));
        }
    }

    /**
     * Returns new instance with queen side castling right revoked.
     *
     * @param side side to revoke queen-side castling
     * @return new instance with queen-side castling right revoked
     */
    public CastleState revokeQueenSide(Side side) {
        if (side == null || side == Side.NO_SIDE) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.revoke.queen.side.failed", cause);
        }
        if (quickHasCastled(side)) {
            var cause = new ChessException("side.is.castled: " + side);
            throw new ChessException("chess.castle.state.revoke.queen.side.failed", cause);
        }
        if (Side.WHITE == side) {
            return new CastleState((byte) (index & 0x2f));
        }
        else {
            return new CastleState((byte) (index & 0xf2));
        }
    }

    /**
     * Returns new instance with both castling rights restored.
     *
     * @param side side to restore both castling rights
     * @return new instance with both castling rights restored
     */
    public CastleState restoreBoth(Side side) {
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.restore.failed", cause);
        }
        int result;
        if (Side.WHITE == side) {
            result = index & 0x0f | 0x30;
        }
        else {
            result = index & 0xf0 | 0x03;
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
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.restore.failed", cause);
        }
        int result;
        if (Side.WHITE == side) {
            result = index & 0x0f | 0x20;
        }
        else {
            result = index & 0xf0 | 0x02;
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
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.restore.failed", cause);
        }
        int result;
        if (Side.WHITE == side) {
            result = index & 0x0f | 0x10;
        }
        else {
            result = index & 0xf0 | 0x01;
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
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.any.right.failed", cause);
        }
        if (Side.WHITE == side) {
            return (index & 0x30) != 0;
        }
        else {
            return (index & 0x3) != 0;
        }
    }

    /**
     * Returns if side has right to castle king side.
     *
     * @param side board side
     * @return true if side has right to castle king-side (short), false otherwise
     */
    public boolean hasKingSideRight(Side side) {
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.king.side.right.failed", cause);
        }
        if (Side.WHITE == side) {
            return (index & 0x20) != 0x0;
        }
        else {
            return (index & 0x2) != 0;
        }
    }

    /**
     * Returns if side has right to castle queen side.
     *
     * @param side side to check for queen-side castling right
     * @return true if side has right to castle queen-side (long), false otherwise
     */
    public boolean hasQueenSideRight(Side side) {
        if (side == null || Side.NO_SIDE == side) {
            var cause = new ChessException("side: " + side);
            throw new ChessException("chess.castle.state.has.queen.side.right.failed", cause);
        }
        if (Side.WHITE == side) {
            return (index & 0x10) != 0;
        }
        else {
            return (index & 0x1) != 0;
        }
    }

    /** Returns index value. */
    public byte index() {
        return index;
    }

    private boolean quickHasCastled(final Side side) {
        if (side == Side.WHITE) {
            return (index & 0xc0) != 0x0;
        }
        else {
            return (index & 0xc) != 0x0;
        }
    }

    @Override
    public int hashCode() {
        return index();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
