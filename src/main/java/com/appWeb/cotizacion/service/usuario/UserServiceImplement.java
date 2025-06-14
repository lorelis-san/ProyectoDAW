package com.appWeb.cotizacion.service.usuario;


import com.appWeb.cotizacion.model.usuario.User;
import com.appWeb.cotizacion.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplement implements UserDetailsService{

	@Autowired
	private UserRepository dao;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User usuario = dao.findOneByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("El usuario con dicho email: "+email+ "no existe."));
		
		return new UserDetailImplement(usuario);
	}
		
}
