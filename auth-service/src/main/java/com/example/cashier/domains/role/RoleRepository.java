package com.example.cashier.domains.role;

import com.example.cashier.domains.role.entity.Role;

public interface RoleRepository {
    Role getByRoleName(String roleName);

    Role getByRoleId(String roleId);
}
