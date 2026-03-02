package com.oceanview.resort.patterns.reservation;

import com.oceanview.resort.domain.Reservation;

import java.time.LocalDate;
import java.math.BigDecimal;


 // Memento: Snapshot of reservation state for history/undo.

public class ReservationMemento {
    private final Integer id;
    private final String status;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final Integer roomId;
    private final BigDecimal depositAmount;

    public ReservationMemento(Reservation r) {
        this.id = r.getId();
        this.status = r.getStatus();
        this.checkInDate = r.getCheckInDate();
        this.checkOutDate = r.getCheckOutDate();
        this.roomId = r.getRoomId();
        this.depositAmount = r.getDepositAmount();
    }

    public Integer getId() { return id; }
    public String getStatus() { return status; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public Integer getRoomId() { return roomId; }
    public BigDecimal getDepositAmount() { return depositAmount; }
}
