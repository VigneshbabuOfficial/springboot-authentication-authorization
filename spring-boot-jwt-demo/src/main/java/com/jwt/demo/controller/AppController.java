package com.jwt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.demo.dto.AuthRequestDTO;
import com.jwt.demo.dto.JwtResponseDTO;
import com.jwt.demo.dto.RefreshTokenRequestDTO;
import com.jwt.demo.dto.UserRequestDTO;
import com.jwt.demo.dto.UserResponseDTO;
import com.jwt.demo.entities.RefreshToken;
import com.jwt.demo.service.JwtService;
import com.jwt.demo.service.TokenService;
import com.jwt.demo.service.UserService;


@RestController
@RequestMapping("/app")
public class AppController {

	@Autowired
    private JwtService jwtService;

    @Autowired
    TokenService refreshTokenService;


    @Autowired
    private  AuthenticationManager authenticationManager;
    
    
    @Autowired
	UserService userService;
    
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/test")
    public String test() {
        try {
            return "Welcome";
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	

	@PostMapping("/user")
	public ResponseEntity saveUser(@RequestBody UserRequestDTO userRequest) {
		try {
			UserResponseDTO userResponse = userService.saveUser(userRequest);
			return ResponseEntity.ok(userResponse);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    @PostMapping("/login")
    public JwtResponseDTO login(@RequestBody AuthRequestDTO authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
           return JwtResponseDTO.builder()
                   .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                   .token(refreshToken.getToken()).build();

        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }

    }


    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }
}
