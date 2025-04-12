package com.iscfl.secureapi.dao;

import com.iscfl.secureapi.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
