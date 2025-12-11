package com.helpnearby.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.helpnearby.entities.User;
import com.helpnearby.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	// Create
	public User createUser(User user) {
		return userRepository.save(user);
	}

	// Read all
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Read by ID
	public Optional<User> getByUserID(String id) {
		return userRepository.findById(id);
	}


	// Update
	public User updateUsers(User user) {
		return userRepository.save(user);
	}

	// Delete
	public void deleteUser(String id) {
		userRepository.deleteById(id);
	}
}