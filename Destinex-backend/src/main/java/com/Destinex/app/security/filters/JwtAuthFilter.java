package com.Destinex.app.security.filters;


import com.Destinex.app.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        JwtUtils jwtUtils = new JwtUtils();
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // throws exceptions if token invalid/expired
                jwtUtils.validateToken(token);
                request.setAttribute("userId", jwtUtils.getId(token));

                String email = jwtUtils.getEmail(token);
                List<String> roles = jwtUtils.getRoles(token); // <-- new method
                roles = roles.stream().map(r -> "ROLE_" + r).toList();

                List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

                // create authentication object
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,authorities);

                // tell Spring Security that this request is authenticated
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"fail\",\"message\":\"Token expired\"}");
                return;
            } catch (JwtException | IllegalArgumentException e) {
                // covers SecurityException, MalformedJwtException, UnsupportedJwtException
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"fail\",\"message\":\"Invalid token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
/*
*  Notes:
* *******************
* in case of log in:
* ********************
*
* 1️⃣ Before AuthenticationManager
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        This is called a token, but it already implements Authentication.
        At this point, it’s “unauthenticated” (token.isAuthenticated() == false).
        It just holds credentials you want to check.

  2️⃣ After AuthenticationManager
        Authentication auth = authenticationManager.authenticate(token);
        The AuthenticationManager calls your AuthenticationProvider → checks credentials.
        If valid, it returns a new Authentication object (usually also a UsernamePasswordAuthenticationToken)
        that is authenticated (isAuthenticated() == true) and contains authorities.

* ***************************************
* in case of normal requests after log in
* ****************************************
*
  1️⃣ What this object is
        UsernamePasswordAuthenticationToken auth =  new UsernamePasswordAuthenticationToken(email, null, authorities);
        UsernamePasswordAuthenticationToken implements Authentication.
        In your code, you manually create it — you’re not calling AuthenticationManager.authenticate().
        You pass:
        email → principal (the identity)
        null → credentials (we don’t need password for JWT)
        authorities → roles/permissions

  2️⃣ Is it a “token” or an “auth object”?
        It is technically both: the class is called UsernamePasswordAuthenticationToken, but it implements Authentication.
        Here, you are bypassing the login check (because JWT is already trusted) and creating a pre-authenticated Authentication object.

  3️⃣ Why Spring Security accepts it
        When you do:  SecurityContextHolder.getContext().setAuthentication(auth);
        Spring Security now treats it as the authenticated Authentication for this request.
        auth.isAuthenticated() is true if you passed a non-empty authorities list.
        From Spring Security’s point of view, this is now the authenticated user — even though you never called AuthenticationManager.

        NOTE:
        UsernamePasswordAuthenticationToken has two “modes”:
          Unauthenticated constructor: new UsernamePasswordAuthenticationToken(principal, credentials) → isAuthenticated() == false
          Authenticated constructor: new UsernamePasswordAuthenticationToken(principal, credentials, authorities) → isAuthenticated() == true but only if authorities is not empty
          If you pass new ArrayList<>() (empty list) as authorities, Spring Security treats it as unauthenticated.
*
* */