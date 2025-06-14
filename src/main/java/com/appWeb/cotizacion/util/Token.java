package com.appWeb.cotizacion.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Token {

	private final static String TOKEN_SECRETO = "RaFRLGx9vIL0e9GJUZQ5iKCXgM6SUvwT";
	private final static Long TOKEN_DURACION = 1_000_000L;
	
	public static String crearToken(String user, String email) {
		long expiracionTiempo = TOKEN_DURACION * 1_000;
		Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);
		
		Map<String, Object> map = new HashMap<>();
		map.put("nombre", user);
		
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
			
			return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
		} catch (JwtException e) {
			return null;
		}
	}

	
}
