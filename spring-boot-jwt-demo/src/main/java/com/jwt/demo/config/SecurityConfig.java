package com.jwt.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwt.demo.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	JwtAuthFilter jwtAuthFilter;

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@SuppressWarnings("deprecation")
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		/*
		 * return http.csrf(csrf -> csrf.disable()) // .authorizeHttpRequests(requests
		 * -> requests // .requestMatchers("/jwt-app/api/v1/user/save",
		 * "/jwt-app/api/v1/app/test", "/jwt-app/api/v1/app/login",
		 * "/jwt-app/api/v1/app/refreshToken").permitAll()) //
		 * .authorizeHttpRequests(requests ->
		 * requests.requestMatchers("/jwt-app/api/v1/**").authenticated())
		 * .sessionManagement(management ->
		 * management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		 * .authenticationProvider(authenticationProvider())
		 * .addFilterBefore(jwtAuthFilter,
		 * UsernamePasswordAuthenticationFilter.class).build();
		 */

		return http.csrf(csrf -> csrf.disable())
				.authorizeRequests(authorize -> authorize.requestMatchers("/app/**").permitAll())
				.authorizeRequests(authorize -> authorize.requestMatchers("/user/**").authenticated())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;

	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
