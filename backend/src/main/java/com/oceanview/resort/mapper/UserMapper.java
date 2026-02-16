package com.oceanview.resort.mapper;

import com.oceanview.resort.domain.User;
import com.oceanview.resort.dto.UserDTO;

public final class UserMapper {
    private UserMapper() {}

    public static UserDTO toDTO(User u) {
        if (u == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setRoleId(u.getRoleId());
        dto.setRoleCode(u.getRole() != null ? u.getRole().getCode() : null);
        dto.setBranchId(u.getBranchId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setPhone(u.getPhone());
        dto.setLocked(u.getLocked());
        dto.setActive(u.getActive());
        dto.setBranchName(u.getBranch() != null ? u.getBranch().getName() : null);
        return dto;
    }
}
