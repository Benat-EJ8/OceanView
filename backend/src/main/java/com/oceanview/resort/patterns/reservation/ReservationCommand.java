package com.oceanview.resort.patterns.reservation;

/**
 * Command pattern: Modify/cancel reservation operations.
 */
public interface ReservationCommand {
    boolean execute();
    void undo();
}
