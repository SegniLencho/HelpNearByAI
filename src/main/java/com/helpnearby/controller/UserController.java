package com.helpnearby.controller;

import com.helpnearby.dto.FcmTokenDto;
import com.helpnearby.dto.FileMeta;
import com.helpnearby.dto.PresignedUpload;
import com.helpnearby.entities.User;
import com.helpnearby.service.S3UploadService;
import com.helpnearby.service.UserService;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {


	private UserService userService;
	
	private final S3UploadService s3UploadService;
	

	public UserController(S3UploadService s3UploadService,UserService userService) {
		this.s3UploadService = s3UploadService;
		this.userService=userService;
	}


	// Create
	
	@PostMapping
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User created = userService.createUser(user);
		return ResponseEntity.ok(created);
	}

	// Read all
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	// Read by ID
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable String id) {
		return userService.getByUserID(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	

	// Update
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable String id, @Valid @RequestBody User User) {
		return userService.getByUserID(id).map(existing -> {
			User.setId(id); // ensure ID remains the same
			return ResponseEntity.ok(userService.updateUsers(User));
		}).orElse(ResponseEntity.notFound().build());
	}

	// Delete
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/presign")
	public PresignedUpload presign(@RequestBody FileMeta files) {
		return  (s3UploadService.generatePresignedUrlProfilePicture(files.fileName(), files.contentType()));
	}

	// Register or update FCM token
	@PutMapping("/{id}/fcm-token")
	public ResponseEntity<User> updateFcmToken(@PathVariable String id, @Valid @RequestBody FcmTokenDto fcmTokenDto) {
		return userService.getByUserID(id).map(existing -> {
			existing.setFcmToken(fcmTokenDto.getFcmToken());
			return ResponseEntity.ok(userService.updateUsers(existing));
		}).orElse(ResponseEntity.notFound().build());
	}
}
