package com.oceanview.resort.patterns.reservation;

import com.oceanview.resort.domain.Reservation;
import com.oceanview.resort.repository.ReservationRepository;

import java.time.Instant;

public class CancelReservationCommand implements ReservationCommand {
    private final ReservationRepository repository;
    private final int reservationId;
    private final String reason;
    private String previousStatus;
    private Instant previousCancelledAt;
    private String previousCancelReason;

    public CancelReservationCommand(ReservationRepository repository, int reservationId, String reason) {
        this.repository = repository;
        this.reservationId = reservationId;
        this.reason = reason;
    }

    @Override
    public boolean execute() {
        Reservation r = repository.findById(reservationId).orElse(null);
        if (r == null) return false;
        previousStatus = r.getStatus();
        previousCancelledAt = r.getCancelledAt();
        previousCancelReason = r.getCancelReason();
        r.setStatus("CANCELLED");
        r.setCancelledAt(Instant.now());
        r.setCancelReason(reason);
        return repository.update(r);
    }

    @Override
    public void undo() {
        Reservation r = repository.findById(reservationId).orElse(null);
        if (r != null) {
            r.setStatus(previousStatus);
            r.setCancelledAt(previousCancelledAt);
            r.setCancelReason(previousCancelReason);
            repository.update(r);
        }
    }
}
