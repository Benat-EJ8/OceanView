package com.oceanview.resort.patterns.reservation;

public interface ReservationCommand {
    boolean execute();
    void undo();
}
