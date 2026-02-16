package com.oceanview.resort.patterns.reservation;

import com.oceanview.resort.domain.Reservation;

public abstract class ReservationValidationHandler {
    private ReservationValidationHandler next;

    public void setNext(ReservationValidationHandler next) {
        this.next = next;
    }

    public final ValidationResult validate(Reservation reservation) {
        ValidationResult r = doValidate(reservation);
        if (!r.isValid()) return r;
        return next != null ? next.validate(reservation) : ValidationResult.ok();
    }

    protected abstract ValidationResult doValidate(Reservation reservation);

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult ok() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult fail(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
