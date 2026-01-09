package com.helpnearby.service;

import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.entities.Request;
import com.helpnearby.repository.RequestRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
	@Caching(evict = {
		@CacheEvict(value = "openRequests", allEntries = true),
		@CacheEvict(value = "userRequests", key = "#request.userId")
	})
	public Request createRequest(Request request) {
		return requestRepository.save(request);
	}

	// Read all - optimized to only fetch OPEN requests by default
//	public Page<Request> getAllRequests(int page, int size) {
//		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//		return requestRepository.findOpenRequestsWithImages(pageable);
//	}
	
	
	@Cacheable(value = "openRequests", key = "'content:page:' + #page + ':size:' + #size")
	public List<RequestListDTO> getAllRequestsContent(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<RequestListDTO> pageResult = requestRepository.findOpenRequestsWithPrimaryImage(pageable);
		return pageResult.getContent();
	}
	
	@Cacheable(value = "openRequests", key = "'count'")
	public long getOpenRequestsCount() {
		return requestRepository.countByStatus("OPEN");
	}
	
	public Page<RequestListDTO> getAllRequests(int page, int size) {
		// Try to get cached content and count
		List<RequestListDTO> cachedContent = getAllRequestsContent(page, size);
		long totalElements = getOpenRequestsCount();
		
		// Reconstruct Page object
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return new PageImpl<>(cachedContent, pageable, totalElements);
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
	@Cacheable(value = "requests", key = "#id")
	public Optional<Request> getRequestById(String id) {
		return requestRepository.findByIdWithImages(id);
	}

	// Read by User - optimized query
	@Cacheable(value = "userRequests", key = "#userId")
	public List<Request> getRequestsByUserId(String userId) {
		return requestRepository.findByUserIdWithImages(userId);
	}
	
	// Read by User and Status
	@Cacheable(value = "userRequests", key = "#userId + ':status:' + #status")
	public List<Request> getRequestsByUserIdAndStatus(String userId, String status) {
		return requestRepository.findByUserIdAndStatus(userId, status);
	}

	// Update
	@Caching(evict = {
		@CacheEvict(value = "requests", key = "#request.id"),
		@CacheEvict(value = "openRequests", allEntries = true),
		@CacheEvict(value = "userRequests", key = "#request.userId"),
		@CacheEvict(value = "userRequests", key = "#request.userId + ':status:' + #request.status")
	})
	@CachePut(value = "requests", key = "#request.id")
	public Request updateRequest(Request request) {
		return requestRepository.save(request);
	}

	// Delete
	@Caching(evict = {
		@CacheEvict(value = "requests", key = "#id"),
		@CacheEvict(value = "openRequests", allEntries = true),
		@CacheEvict(value = "userRequests", allEntries = true)
	})
	public void deleteRequest(String id) {
		requestRepository.deleteById(id);
	}

//	TODO Fetch top 5 request based on user zipcode + 5 miles

}
