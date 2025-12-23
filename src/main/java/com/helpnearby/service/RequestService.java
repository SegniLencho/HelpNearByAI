package com.helpnearby.service;

import com.helpnearby.entities.Request;
import com.helpnearby.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

	@Autowired
	private RequestRepository requestRepository;

	// Create
	public Request createRequest(Request request) {
		return requestRepository.save(request);
	}

	// Read all
	public Page<Request> getAllRequests(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return requestRepository.findAll(pageable);
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

//	TODO Fetch top 5 request based on user zipcode + 5 miles

}
