package com.appWeb.cotizacion.service.usuario;

import com.appWeb.cotizacion.dto.user.NewUserDto;
import com.appWeb.cotizacion.enums.RoleList;
import com.appWeb.cotizacion.jwt.JwtUtil;
import com.appWeb.cotizacion.model.usuario.Role;
import com.appWeb.cotizacion.model.usuario.User;
import com.appWeb.cotizacion.repository.user.RoleRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {

    private final UserService userService;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final CookieService cookieService;

    @Autowired
    public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManagerBuilder authenticationManagerBuilder, CookieService cookieService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.cookieService = cookieService;
    }


    public String authenticate(String username, String password, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authResult);

        String jwt = jwtUtil.generateToken(authResult);
        cookieService.addHttpOnlyCookie("jwt", jwt, 7*24*60*60, response);

        User user = userService.findByUsername(username);

        return user.getRole().getName().toString();
    }

    public void registerUser(NewUserDto newUserDto) {
        if (userService.existsByUserName(newUserDto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Role roleUser = roleRepository.findByName(RoleList.ROLE_USER).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        User user = new User(newUserDto.getNombre(), newUserDto.getApellido(), newUserDto.getUsername(), newUserDto.getEmail(), passwordEncoder.encode(newUserDto.getPassword()), roleUser);
        userService.save(user);
    }

    public void logout(HttpServletResponse response){
        cookieService.deleteCookie("jwt",response);
    }

}
