package com.appWeb.cotizacion.repository.user;

import com.appWeb.cotizacion.enums.RoleList;
import com.appWeb.cotizacion.model.usuario.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleList name);
}
