package com.iscfl.secureapi.api;

import com.iscfl.secureapi.model.User;
import com.iscfl.secureapi.dao.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
@RequestMapping(path="/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserRepository userRepository;

	UserController(UserRepository userRepository){
		this.userRepository = userRepository;
	}


	@GetMapping(path="/all")
	@PreAuthorize("hasAuthority('GET_ALL_USERS')")
	public @ResponseBody Iterable<User> getAllUsers() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}
}
