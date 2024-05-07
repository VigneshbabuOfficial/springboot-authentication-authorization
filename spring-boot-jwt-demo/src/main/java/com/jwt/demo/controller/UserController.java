package com.jwt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.demo.dto.UserResponseDTO;
import com.jwt.demo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping
	public ResponseEntity getAllUsers() {
		try {
			List<UserResponseDTO> userResponses = userService.getAllUser();
			return ResponseEntity.ok(userResponses);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/profile")
	public ResponseEntity<UserResponseDTO> getUserProfile() {
		try {
			UserResponseDTO userResponse = userService.getUser();
			return ResponseEntity.ok().body(userResponse);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
