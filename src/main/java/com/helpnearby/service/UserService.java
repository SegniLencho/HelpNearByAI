package com.helpnearby.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.helpnearby.entities.User;
import com.helpnearby.exception.InvalidOtpException;
import com.helpnearby.repository.UserRepository;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Value("${twilio.verify-sid}")
	private String verifyServiceSid;

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

	public String sendOtp(String phoneNumber) {
		Verification verification = Verification.creator(verifyServiceSid, phoneNumber, "sms").create();
		if (!"pending".equals(verification.getStatus())) {
			throw new RuntimeException("OTP not sent");
		}
		return "OTP SENT";
	}

	public String verifyOtp(String phoneNumber, String code) {
		VerificationCheck check = VerificationCheck.creator(verifyServiceSid).setTo(phoneNumber).setCode(code).create();
		if (!"approved".equals(check.getStatus())) {
			throw new InvalidOtpException();
		}

		int updated = userRepository.markPhoneVerifiedByPhone(phoneNumber);

		if (updated != 1) {
			throw new IllegalStateException("Phone verification update failed");
		}
		return "Phone Verified";

	}
}