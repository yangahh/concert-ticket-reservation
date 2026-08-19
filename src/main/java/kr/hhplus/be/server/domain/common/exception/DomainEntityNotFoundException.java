package kr.hhplus.be.server.domain.common.exception;

public class DomainEntityNotFoundException extends RuntimeException {
    public DomainEntityNotFoundException(String message) {
        super(message);
    }
}
