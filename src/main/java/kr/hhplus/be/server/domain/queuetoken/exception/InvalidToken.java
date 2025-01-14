package kr.hhplus.be.server.domain.queuetoken.exception;

public class InvalidToken extends RuntimeException {
    public InvalidToken(String message) {
        super(message);
    }
}
