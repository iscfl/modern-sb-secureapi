package com.iscfl.secureapi.dao;

import com.iscfl.secureapi.model.Role;
import com.iscfl.secureapi.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Optional<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findAllByRoleId(Long roleId);

    @Query("SELECT rp FROM RolePermission rp JOIN rp.role r JOIN rp.permission p WHERE r.id = :roleId AND p.id = :permissionId")
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") long roleId, @Param("permissionId") long permissionId);

}
