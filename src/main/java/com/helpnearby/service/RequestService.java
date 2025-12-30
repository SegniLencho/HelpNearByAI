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

	// Read all - optimized to only fetch OPEN requests by default
	public Page<Request> getAllRequests(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return requestRepository.findOpenRequestsWithImages(pageable);
	}
	
	// Read all with status filter
	public Page<Request> getAllRequestsByStatus(String status, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		if (status != null && !status.isEmpty()) {
			return requestRepository.findByStatusWithImages(status, pageable);
		}
		return requestRepository.findAllWithImages(pageable);
	}

	// Read by ID - cached
	public Optional<Request> getRequestById(String id) {
		return requestRepository.findByIdWithImages(id);
	}

	// Read by User - optimized query
	public List<Request> getRequestsByUserId(String userId) {
		return requestRepository.findByUserIdWithImages(userId);
	}
	
	// Read by User and Status
	public List<Request> getRequestsByUserIdAndStatus(String userId, String status) {
		return requestRepository.findByUserIdAndStatus(userId, status);
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
