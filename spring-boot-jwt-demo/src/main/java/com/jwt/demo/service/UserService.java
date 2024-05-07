package com.jwt.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwt.demo.dto.UserRequestDTO;
import com.jwt.demo.dto.UserResponseDTO;
import com.jwt.demo.entities.UserInfo;
import com.jwt.demo.repositories.UserRepository;

@Service
public class UserService{

	@Autowired
	private UserRepository userRepository;

	ModelMapper modelMapper = new ModelMapper();

	public UserResponseDTO saveUser(UserRequestDTO userRequest) {

		if (userRequest.getUsername() == null) {
			throw new RuntimeException("Parameter username is not found in request..!!");
		} else if (userRequest.getPassword() == null) {
			throw new RuntimeException("Parameter password is not found in request..!!");
		}

		UserInfo savedUser = null;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = userRequest.getPassword();
		String encodedPassword = encoder.encode(rawPassword);

		UserInfo user = modelMapper.map(userRequest, UserInfo.class);
		user.setPassword(encodedPassword);
		if (userRequest.getId() != null) {
			UserInfo oldUser = userRepository.findFirstById(userRequest.getId());
			if (oldUser != null) {
				oldUser.setId(user.getId());
				oldUser.setPassword(user.getPassword());
				oldUser.setUsername(user.getUsername());
				oldUser.setRoles(user.getRoles());

				savedUser = userRepository.save(oldUser);
			} else {
				throw new RuntimeException("Can't find record with identifier: " + userRequest.getId());
			}
		} else {
			savedUser = userRepository.save(user);
		}
		UserResponseDTO userResponse = modelMapper.map(savedUser, UserResponseDTO.class);
		return userResponse;
	}

	public UserResponseDTO getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetail = (UserDetails) authentication.getPrincipal();
		String usernameFromAccessToken = userDetail.getUsername();
		UserInfo user = userRepository.findByUsername(usernameFromAccessToken);
		UserResponseDTO userResponse = modelMapper.map(user, UserResponseDTO.class);
		return userResponse;
	}

	public List<UserResponseDTO> getAllUser() {
		List<UserInfo> users = (List<UserInfo>) userRepository.findAll();
		List<UserResponseDTO> userResponses = new ArrayList<>();
		for(UserInfo user : users ) {
			userResponses.add(modelMapper.map(user, UserResponseDTO.class));
		}
		return userResponses;
	}

}
