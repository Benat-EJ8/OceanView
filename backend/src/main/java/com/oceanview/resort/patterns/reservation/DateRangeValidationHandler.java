package com.oceanview.resort.patterns.reservation;

import com.oceanview.resort.domain.Reservation;

import java.time.LocalDate;

public class DateRangeValidationHandler extends ReservationValidationHandler {
    @Override
    protected ValidationResult doValidate(Reservation reservation) {
        if (reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null) {
            return ValidationResult.fail("Check-in and check-out dates are required");
        }
        if (!reservation.getCheckOutDate().isAfter(reservation.getCheckInDate())) {
            return ValidationResult.fail("Check-out must be after check-in");
        }
        if (reservation.getCheckInDate().isBefore(LocalDate.now())) {
            return ValidationResult.fail("Check-in cannot be in the past");
        }
        return ValidationResult.ok();
    }
}
