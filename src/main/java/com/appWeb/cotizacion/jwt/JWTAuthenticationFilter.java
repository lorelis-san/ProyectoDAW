package com.appWeb.cotizacion.jwt;

import com.appWeb.cotizacion.model.usuario.Auth;
import com.appWeb.cotizacion.service.usuario.UserDetailImplement;
import com.appWeb.cotizacion.util.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		Auth authCredenciales = new Auth();
		
		try {
			authCredenciales = new ObjectMapper().readValue(request.getReader(), Auth.class);
		} catch (Exception e) {
		}
		
		UsernamePasswordAuthenticationToken userPAT = new UsernamePasswordAuthenticationToken(
				authCredenciales.getEmail(),
				authCredenciales.getPassword(),
				Collections.emptyList()
				);
		
		return getAuthenticationManager().authenticate(userPAT);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		UserDetailImplement userDetails = (UserDetailImplement) authResult.getPrincipal();
		String token = Token.crearToken(userDetails.getUser(), userDetails.getUsername(), userDetails.getRole());
		response.addHeader("Authorization", "Bearer "+ token);
		response.getWriter().flush();
		
		super.successfulAuthentication(request, response, chain, authResult);
	}
	
	
	
}
