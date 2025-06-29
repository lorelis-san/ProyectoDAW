package com.appWeb.cotizacion.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Token {

	private final static String TOKEN_SECRETO = "RaFRLGx9vIL0e9GJUZQ5iKCXgM6SUvwT";
	private final static Long TOKEN_DURACION = 60*60L;
	
	public static String crearToken(String user, String email, String role) {
		long expiracionTiempo = TOKEN_DURACION * 1_000;
		Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);
		
		Map<String, Object> map = new HashMap<>();
		map.put("nombre", user);
		map.put("role", role);

		return Jwts.builder()
				.setSubject(email)
				.setExpiration(expiracionFecha)
				.addClaims(map)
				.signWith(Keys.hmacShaKeyFor(TOKEN_SECRETO.getBytes()))
				.compact();
		
	}
	
	public static UsernamePasswordAuthenticationToken getAuth(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(TOKEN_SECRETO.getBytes())
					.build()
					.parseClaimsJws(token)
					.getBody();
			
			String email = claims.getSubject();
			String role = claims.get("role", String.class);
			if (email != null && role != null) {
				return new UsernamePasswordAuthenticationToken(
						email,
						null,
						Collections.singletonList(new SimpleGrantedAuthority(role))
				);
			}
			return null;
		} catch (JwtException e) {
			return null;
		}
	}

	
}
