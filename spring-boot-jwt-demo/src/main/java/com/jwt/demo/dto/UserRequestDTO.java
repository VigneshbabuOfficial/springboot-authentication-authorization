package com.jwt.demo.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequestDTO {

	 private Long id;
	    private String username;
	    private String password;
	    private Set<String> roles;
}
