package com.helpnearby.service;

import com.helpnearby.entities.Request;
import com.helpnearby.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

	@Autowired
	private RequestRepository requestRepository;

	// Create
	public Request createRequest(Request request) {

		String[] sampleImages = { "https://picsum.photos/id/129/800/600", "https://picsum.photos/id/520/800/600",
				"https://picsum.photos/id/367/800/600", "https://picsum.photos/id/19/800/600" };

		request.setImageUrls(Arrays.asList(sampleImages));

		return requestRepository.save(request);
	}

	// Read all
	public List<Request> getAllRequests() {
		return requestRepository.findAll(
		        Sort.by(Sort.Direction.DESC, "createdAt")
		);
	}

	// Read by ID
	public Optional<Request> getRequestById(String id) {
		return requestRepository.findById(id);
	}

	// Read by User
	public List<Request> getRequestsByUserId(String userId) {
		return requestRepository.findByUserId(userId);
	}

	// Update
	public Request updateRequest(Request request) {
		return requestRepository.save(request);
	}

	// Delete
	public void deleteRequest(String id) {
		requestRepository.deleteById(id);
	}
}
