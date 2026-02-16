package com.oceanview.resort.service;

import com.oceanview.resort.domain.Guest;
import com.oceanview.resort.domain.Reservation;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.mapper.ReservationMapper;
import com.oceanview.resort.patterns.reservation.DateRangeValidationHandler;
import com.oceanview.resort.patterns.reservation.ReservationValidationHandler;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.repository.GuestRepositoryImpl;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.ReservationRepositoryImpl;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.RoomRepositoryImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationService {
    private final ReservationRepository reservationRepository = new ReservationRepositoryImpl();
    private final GuestRepository guestRepository = new GuestRepositoryImpl();
    private final RoomRepository roomRepository = new RoomRepositoryImpl();
    private final ActivityLogService activityLogService = new ActivityLogService();

    public Optional<ReservationDTO> findById(Integer id) {
        Optional<Reservation> r = reservationRepository.findById(id);
        if (r.isEmpty()) return Optional.empty();
        Reservation res = r.get();
        guestRepository.findById(res.getGuestId()).ifPresent(res::setGuest);
        if (res.getRoomId() != null) roomRepository.findById(res.getRoomId()).ifPresent(res::setRoom);
        return Optional.of(ReservationMapper.toDTO(res));
    }

    public List<ReservationDTO> findByGuestId(Integer guestId) {
        return reservationRepository.findByGuestId(guestId).stream()
                .map(this::enrich)
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> findByBranchId(Integer branchId) {
        return reservationRepository.findByBranchId(branchId).stream()
                .map(this::enrich)
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> findPendingApproval() {
        return reservationRepository.findByStatus("PENDING_APPROVAL").stream()
                .map(this::enrich)
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationValidationHandler.ValidationResult validate(Reservation r) {
        ReservationValidationHandler chain = new DateRangeValidationHandler();
        return chain.validate(r);
    }

    public Optional<Reservation> create(Reservation reservation, Integer createdBy, HttpServletRequest request) {
        ReservationValidationHandler.ValidationResult vr = validate(reservation);
        if (!vr.isValid()) return Optional.empty();
        if (reservation.getRoomId() != null) {
            List<Reservation> overlapping = reservationRepository.findByRoomIdAndOverlappingDates(reservation.getRoomId(), reservation.getCheckInDate(), reservation.getCheckOutDate());
            if (!overlapping.isEmpty()) return Optional.empty();
        }
        reservation.setStatus("PENDING_APPROVAL");
        reservation.setCreatedBy(createdBy);
        if (reservationRepository.save(reservation)) {
            activityLogService.log(createdBy, "RESERVATION_CREATE", "RESERVATION", String.valueOf(reservation.getId()), request.getRemoteAddr());
            return Optional.of(reservation);
        }
        return Optional.empty();
    }

    public boolean approve(Integer reservationId, Integer approvedBy) {
        Reservation r = reservationRepository.findById(reservationId).orElse(null);
        if (r == null || !"PENDING_APPROVAL".equals(r.getStatus())) return false;
        r.setStatus("CONFIRMED");
        r.setApprovedBy(approvedBy);
        r.setApprovedAt(Instant.now());
        return reservationRepository.update(r);
    }

    public boolean cancel(Integer reservationId, String reason) {
        com.oceanview.resort.patterns.reservation.CancelReservationCommand cmd = new com.oceanview.resort.patterns.reservation.CancelReservationCommand(reservationRepository, reservationId, reason);
        return cmd.execute();
    }

    private Reservation enrich(Reservation r) {
        guestRepository.findById(r.getGuestId()).ifPresent(r::setGuest);
        if (r.getRoomId() != null) roomRepository.findById(r.getRoomId()).ifPresent(r::setRoom);
        return r;
    }
}
