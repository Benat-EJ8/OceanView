package com.oceanview.resort.service;

import com.oceanview.resort.domain.User;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.domain.Guest;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.repository.GuestRepositoryImpl;
import com.oceanview.resort.repository.RoleRepository;
import com.oceanview.resort.repository.RoleRepositoryImpl;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.repository.UserRepositoryImpl;
import com.oceanview.resort.security.PasswordHasher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final RoleRepository roleRepository = new RoleRepositoryImpl();
    private final GuestRepository guestRepository = new GuestRepositoryImpl();
    private final ActivityLogService activityLogService = new ActivityLogService();

    public Optional<UserDTO> findById(Integer id) {
        return userRepository.findById(id).map(UserMapper::toDTO);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    public List<UserDTO> findByRole(String roleCode) {
        return userRepository.findByRole(roleCode).stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    public Optional<User> create(User user, String plainPassword, HttpServletRequest request) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) return Optional.empty();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) return Optional.empty();
        user.setPasswordHash(PasswordHasher.hash(plainPassword));
        user.setLocked(false);
        user.setFailedAttempts(0);
        if (user.getActive() == null) user.setActive(true);
        if (userRepository.save(user)) {
            roleRepository.findById(user.getRoleId()).ifPresent(role -> {
                if ("CUSTOMER".equals(role.getCode())) {
                    Guest guest = new Guest();
                    guest.setUserId(user.getId());
                    guest.setFirstName(user.getFirstName());
                    guest.setLastName(user.getLastName());
                    guest.setEmail(user.getEmail());
                    guest.setPhone(user.getPhone());
                    guestRepository.save(guest);
                }
            });
            activityLogService.log(null, "USER_CREATE", "USER", String.valueOf(user.getId()), request.getRemoteAddr());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public boolean update(User user, HttpServletRequest request) {
        User existing = userRepository.findById(user.getId()).orElse(null);
        if (existing == null) return false;
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setRoleId(user.getRoleId());
        existing.setBranchId(user.getBranchId());
        existing.setLocked(user.getLocked());
        existing.setLockReason(user.getLockReason());
        existing.setActive(user.getActive());
        boolean ok = userRepository.update(existing);
        if (ok) activityLogService.log(null, "USER_UPDATE", "USER", String.valueOf(user.getId()), request.getRemoteAddr());
        return ok;
    }

    public boolean resetPassword(Integer userId, String newPassword) {
        User u = userRepository.findById(userId).orElse(null);
        if (u == null) return false;
        u.setPasswordHash(PasswordHasher.hash(newPassword));
        u.setLocked(false);
        u.setFailedAttempts(0);
        return userRepository.update(u);
    }

    public boolean deleteById(Integer id) {
        return userRepository.deleteById(id);
    }
}
