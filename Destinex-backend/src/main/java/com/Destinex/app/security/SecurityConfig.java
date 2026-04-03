package com.Destinex.app.security;

import com.Destinex.app.security.filters.JsonLoginFilterConfig;
import com.Destinex.app.security.filters.JwtAuthFilter;
import com.Destinex.app.service.ClientService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    /*
    * ✅ AuthenticationManager = the engine that performs authentication using providers ( uses only providers already registered in HttpSecurity)
    * AuthenticationManager: Responsible for authenticating users, Takes credentials → returns authenticated user or throws exception
    * AuthenticationConfiguration: Builds an AuthenticationManager, Registers all available AuthenticationProviders, Returns a ready-to-use manager
    *
    * Flow in your app:
    *    Request → /login  ----> Your filter reads JSON ( extracts credentials)  ----> Creates token:UsernamePasswordAuthenticationToken ---->
    *    calls -------> authManager.authenticate(token)  ----> Manager delegates to: DaoAuthenticationProvider   ---->
    *    Provider: loads user, checks password, returns success / throws exception ( AuthenticationException)
    *
    * ********
    * Note:
    * ********
    *   AuthenticationProvider: Does the actual check, If wrong credentials → throws AuthenticationException
    *   AuthenticationManager: calls provider, if provider throws exception ---> it propagates the exception Then Your failureHandler runs
    * */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider(ClientService clientService, BCryptPasswordEncoder bcryptPasswordEncoder) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(clientService);//set the custom user details service
        auth.setPasswordEncoder(bcryptPasswordEncoder); //set the password encoder - bcrypt
        return auth;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager,DaoAuthenticationProvider authenticationProvider) throws Exception {
        JsonLoginFilterConfig loginFilterConfig = new JsonLoginFilterConfig(authManager);
        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/login").permitAll()      // allow everyone to POST login
                                .requestMatchers("/signup").permitAll()      // allow everyone to POST signup
                                .requestMatchers("/**").hasRole("USER") // everything else requires role
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                )
                .authenticationProvider(authenticationProvider)
                .formLogin(form -> form.disable())
                .csrf(csrf->csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> {})
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilterConfig, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(configurer ->
                        configurer
                                // User is authenticated (logged in) but doesn’t have permission/role to access a resource
                                .accessDeniedHandler((request, response, ex) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write("{\"status\":\"fail\",\"message\":\"Access Denied\"}");
                                })
                                //  User is not authenticated (not logged in or no valid token)
                                .authenticationEntryPoint((request, response, ex) -> {
                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                            response.setContentType("application/json");
                                            response.getWriter().write("{\"status\":\"fail\",\"message\":\"Unauthorized\"}");
                                })
                );

        return http.build();
    }

}

