package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.enums.RoleList;
import com.appWeb.cotizacion.model.usuario.Role;
import com.appWeb.cotizacion.model.usuario.User;
import com.appWeb.cotizacion.repository.user.RoleRepository;
import com.appWeb.cotizacion.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<User> registrarUsuario(@RequestBody User usuario) {
        String roleName = usuario.getRole().getName().name();

        Optional<Role> role = roleRepository.findByName(RoleList.valueOf(roleName));

        if (role.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        usuario.setRole(role.get());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        User savedUser = userRepository.save(usuario);

        return ResponseEntity.ok(savedUser);
    }

}
