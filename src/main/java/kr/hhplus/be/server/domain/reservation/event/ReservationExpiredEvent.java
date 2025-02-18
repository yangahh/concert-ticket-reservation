package kr.hhplus.be.server.domain.reservation.event;

public record ReservationExpiredEvent (
    Long reservationId
) {
    public ReservationExpiredEvent(Long reservationId) {
        this.reservationId = reservationId;
    }
}
