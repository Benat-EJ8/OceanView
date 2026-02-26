package com.oceanview.resort.patterns.reservation;

import com.oceanview.resort.domain.Reservation;

public abstract class ReservationValidationHandler {
    private ReservationValidationHandler next;

    public void setNext(ReservationValidationHandler next) {
        this.next = next;
    }

    public final ReservationValidationHandler.ValidationResult validate(Reservation reservation) {
        ReservationValidationHandler.ValidationResult r = doValidate(reservation);
        if (!r.isValid()) return r;
        return next != null ? next.validate(reservation) : ReservationValidationHandler.ValidationResult.ok();
    }

    protected abstract ReservationValidationHandler.ValidationResult doValidate(Reservation reservation);

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ReservationValidationHandler.ValidationResult ok() {
            return new ReservationValidationHandler.ValidationResult(true, null);
        }

        public static ReservationValidationHandler.ValidationResult fail(String message) {
            return new ReservationValidationHandler.ValidationResult(false, message);
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
