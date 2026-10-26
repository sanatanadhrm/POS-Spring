package com.example.cashier.infrastructures.database.postgresql;

import org.springframework.stereotype.Component;

import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.domains.role.RoleRepository;
import com.example.cashier.domains.role.entity.Role;
import com.example.cashier.infrastructures.repository.SpringDataRoleJpaRepository;

@Component
public class RoleRepositoryPostgreImpl implements RoleRepository {

    private final SpringDataRoleJpaRepository jpaRepository;

    public RoleRepositoryPostgreImpl(SpringDataRoleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Role getByRoleName(String roleName) {
        return this.jpaRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("role not found"));
    }

    @Override
    public Role getByRoleId(String roleId) {
        return this.jpaRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("role not found"));
    }

}
