package com.Destinex.app.security.filters;
import com.Destinex.app.security.utils.JwtUtils;
import com.Destinex.app.security.utils.MyUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class JsonLoginFilterConfig  extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonLoginFilterConfig(AuthenticationManager authManager) {
        this.setAuthenticationManager(authManager);
        this.setFilterProcessesUrl("/login");

        // success handler
        this.setAuthenticationSuccessHandler((request, response, auth) -> {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            System.out.println(roles);
             roles = roles.stream()
                    .map(r -> r.replaceFirst("^ROLE_", ""))
                    .toList();

            String token = JwtUtils.generateToken(userDetails.getUsername(), userDetails.getId(), roles);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Login successful\",\"token\":\"" + token + "\"}");
        });

        // failure handler
        this.setAuthenticationFailureHandler((request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"fail\",\"message\":\"Invalid email or password\"}");
        });
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String, String> creds = objectMapper.readValue(request.getInputStream(), Map.class);

            /*
            * why do we create UsernamePasswordAuthenticationToken token ?
            * the token here will be: { principal = email , credentials = password , authenticated = false, Granted Authorities = [] }
            * *************************************************************
            *   It wraps the credentials (email/username + password) into a standard Spring Security object
            *   It is the input for AuthenticationManager.authenticate(token)
            *   Spring Security uses it to know what type of authentication to perform and pass it to the correct AuthenticationProvider
            *   Without it, Spring Security doesn’t know what to authenticate.
            *   Token = standard container for credentials that Spring Security understands.
            * */
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(creds.get("email"), creds.get("password"));

            AuthenticationManager authManager =  this.getAuthenticationManager();

            // after authenticating it will be { principal = UserDetails object , credentials = null (usually cleared) , authenticated = true, Granted Authorities = roles }
            // this is the Authentication auth object that is being passed to success handler
            return authManager.authenticate(token);

        }catch (AuthenticationException e) {
            throw e; // let Spring handle it
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}