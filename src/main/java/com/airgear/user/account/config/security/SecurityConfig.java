package com.airgear.user.account.config.security;

import com.airgear.model.Role;
import com.airgear.user.account.config.security.filters.JWTAuthorizationFilter;
import com.airgear.user.account.service.impl.UserServiceImpl;
import com.airgear.user.account.utils.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.authorities.key}")
    public String authoritiesKey;

    private final UserServiceImpl userService;

    public SecurityConfig(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers(getPermitAllUrls()).permitAll()
                .antMatchers(Routes.USERS + "/me/**").hasAnyRole(
                        Role.USER.getValue(), Role.ADMIN.getValue(), Role.MODERATOR.getValue())
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.getValue())
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthorizationFilter())
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private String[] getPermitAllUrls() {
        List<String> permitAllUrls = new ArrayList<>();
        permitAllUrls.add("/v3/api-docs/**");
        permitAllUrls.add("/swagger-ui/**");
        permitAllUrls.add("/swagger-ui.html");
        return permitAllUrls.toArray(new String[0]);
    }

    private JWTAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(authenticationManager(), secret, authoritiesKey);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
