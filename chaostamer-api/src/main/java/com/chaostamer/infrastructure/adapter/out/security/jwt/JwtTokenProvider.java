package com.chaostamer.infrastructure.adapter.out.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

  // We include the values from application.propperties
  @Value("${jwt.secret-key}")
  private String secretKeyString;

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  // Method to generate the token
  public String generateToken(Authentication authentication) {
    // Get the main user (UserDetails)
    UserDetails mainUser = (UserDetails) authentication.getPrincipal();

    String username = mainUser.getUsername();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpiration);

    // Build the token
    return Jwts.builder()
            .setSubject(username) // Token owner (email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Sign the token
            .compact();
  }

  // Private method to obtain the key
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Method to extract the "Subject" from the token
  public String getEmailFromToken(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }
  
  // Method to validate the token
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      // TODO: log the error
      return false;
    }
  }
}
