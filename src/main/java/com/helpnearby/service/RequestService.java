package com.helpnearby.service;

import com.helpnearby.dto.CreateRequestDto;
import com.helpnearby.dto.RequestImageResponseDto;
import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.dto.RequestResponseDto;
import com.helpnearby.entities.Request;
import com.helpnearby.repository.RequestRepository;
import com.helpnearby.util.RequestCreateMapper;

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
	public Request createRequest(String userId,CreateRequestDto requestDto) {
        Request request = RequestCreateMapper.toEntity(userId,requestDto);
        System.out.println("Create Request payload "+requestDto.toString() );
		return requestRepository.save(request);
	}

	
	public Page<RequestListDTO> getAllRequests(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return requestRepository.findOpenRequestsWithPrimaryImage(pageable);
	}
	
	
	// Read all with status filter
	public Page<Request> getAllRequestsByStatus(String status, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		if (status != null && !status.isEmpty()) {
			return requestRepository.findByStatusWithImages(status, pageable);
		}
		return requestRepository.findAllWithImages(pageable);
	}

	// Read by ID - try standard findById first, then with images
	public RequestResponseDto getRequestById(String id) {
		RequestResponseDto requestResponseDto=new RequestResponseDto();
		Optional<Request> request = requestRepository.findById(id);
		if(!request.isEmpty()) {
			requestResponseDto = convertReqeustToDto(request.get());
		}
		return requestResponseDto;
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
	public Request updateRequest(String id, Request requestUpdate) {
		System.out.println("Attempting to update request with id: " + id);
		
		// Try to find the request - use standard findById first as it's more reliable
		Optional<Request> existingOpt = requestRepository.findById(id);
		System.out.println("Standard findById result: " + (existingOpt.isPresent() ? "FOUND" : "NOT FOUND"));
		
		// If not found with standard method, try with EntityGraph
		if (existingOpt.isEmpty()) {
			existingOpt = requestRepository.findByIdWithImages(id);
			System.out.println("EntityGraph findById result: " + (existingOpt.isPresent() ? "FOUND" : "NOT FOUND"));
		}
		
		return existingOpt.map(existing -> {
			System.out.println("Request found, proceeding with update...");
			// Initialize images collection if null to avoid NPE
			if (existing.getImages() == null) {
				existing.setImages(new java.util.ArrayList<>());
			}
			// Security: userId should not be changed via update
			// If userId is provided and different, ignore it (or throw exception)
			if (requestUpdate.getUserId() != null && !requestUpdate.getUserId().equals(existing.getUserId())) {
				throw new IllegalArgumentException("Cannot change userId of a request");
			}
			
			// Update only the fields that are provided (non-null)
			if (requestUpdate.getTitle() != null) {
				existing.setTitle(requestUpdate.getTitle());
			}
			if (requestUpdate.getDescription() != null) {
				existing.setDescription(requestUpdate.getDescription());
			}
			if (requestUpdate.getCategory() != null) {
				existing.setCategory(requestUpdate.getCategory());
			}
			if (requestUpdate.getReward() != null) {
				existing.setReward(requestUpdate.getReward());
			}
			if (requestUpdate.getStatus() != null) {
				existing.setStatus(requestUpdate.getStatus());
			}
			if (requestUpdate.getUrgency() != null) {
				existing.setUrgency(requestUpdate.getUrgency());
			}
			// Update coordinates (always update, even if 0.0)
			existing.setLatitude(requestUpdate.getLatitude());
			existing.setLongitude(requestUpdate.getLongitude());
			
			// Handle images - update if provided (empty array means clear images)
			// IMPORTANT: With orphanRemoval=true, we must modify the existing collection,
			// not replace it with a new reference
			if (requestUpdate.getImages() != null) {
				// Initialize collection if null
				if (existing.getImages() == null) {
					existing.setImages(new java.util.ArrayList<>());
				}
				
				// Clear existing images (modify existing collection, don't replace)
				existing.getImages().clear();
				
				// Add new images to the existing collection (don't replace the collection reference)
				if (!requestUpdate.getImages().isEmpty()) {
					requestUpdate.getImages().forEach(img -> {
						if (img != null) {
							img.setRequest(existing);
							existing.getImages().add(img);
						}
					});
				}
				// If empty list, we've already cleared, so nothing to add
			}
			
			// updatedAt will be set automatically by @PreUpdate
			// createdAt is preserved because it's marked as updatable = false
			System.out.println("Saving updated request...");
			Request saved = requestRepository.save(existing);
			System.out.println("Request saved successfully with id: " + saved.getId());
			return saved;
		}).orElseThrow(() -> {
			System.err.println("Request not found with id: " + id);
			return new RuntimeException("Request not found with id: " + id);
		});
	}

	// Delete
	public void deleteRequest(String id) {
		requestRepository.deleteById(id);
	}

//	TODO Fetch top 5 request based on user zipcode + 5 miles
	public RequestResponseDto convertReqeustToDto(Request request) {
		RequestResponseDto dto = new RequestResponseDto();

	    dto.setId(request.getId());
	    dto.setUserId(request.getUserId());
	    dto.setTitle(request.getTitle());
	    dto.setDescription(request.getDescription());
	    dto.setCategory(request.getCategory());
	    dto.setReward(request.getReward());

	    dto.setLatitude(request.getLatitude());
	    dto.setLongitude(request.getLongitude());

	    dto.setStatus(request.getStatus());
	    dto.setUrgency(request.getUrgency());

	    dto.setCreatedAt(request.getCreatedAt());
	    dto.setUpdatedAt(request.getUpdatedAt());

	    dto.setLocation(request.getLocation() != null
	            ? request.getLocation().toString()
	            : null);

	    dto.setImages(
	        request.getImages().stream()
	            .map(img -> {
	                RequestImageResponseDto imageDto = new RequestImageResponseDto();
	                imageDto.setId(img.getId());
	                imageDto.setUrl(img.getUrl());
	                imageDto.setPrimaryImage(img.isPrimaryImage());
	                imageDto.setS3Key(img.getS3Key());
	                return imageDto;
	            })
	            .toList()
	    );

	    return dto;
	}
		

}
