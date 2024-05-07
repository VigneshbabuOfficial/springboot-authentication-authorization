package com.jwt.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jwt.demo.entities.RefreshToken;
import com.jwt.demo.entities.UserInfo;
import com.jwt.demo.repositories.RefreshTokenRepository;
import com.jwt.demo.repositories.UserRepository;

@Service
public class TokenService {

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	UserRepository userRepository;

	public RefreshToken createRefreshToken(String username) {
		UserInfo userInfo = userRepository.findByUsername(username);
		RefreshToken refreshToken = refreshTokenRepository.findByUserInfoId(userInfo.getId()).orElse(null);
		if(refreshToken != null ) {
			refreshToken.setToken(UUID.randomUUID().toString());
			refreshToken.setExpiryDate(Instant.now().plusMillis(60000));
		}else {
			refreshToken = RefreshToken.builder().userInfo(userInfo).token(UUID.randomUUID().toString())
					.expiryDate(Instant.now().plusMillis(600000)).build();
			
		}
		return refreshTokenRepository.save(refreshToken);
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
		}
		return token;
	}
}
