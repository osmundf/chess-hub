package com.github.osmundf.chess.hub;

/**
 * Chess exception.
 */
public final class ChessException extends RuntimeException {

    /**
     * Chess exception constructor.
     *
     * @param message exception message
     */
    public ChessException(String message) {
        super(message);
    }

    /**
     * Chess exception constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ChessException(String message, Throwable cause) {
        super(message, cause);
    }
}
