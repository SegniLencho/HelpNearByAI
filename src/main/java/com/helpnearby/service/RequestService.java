package com.helpnearby.service;

import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.entities.Request;
import com.helpnearby.entities.RequestImage;
import com.helpnearby.repository.RequestRepository;
import com.helpnearby.repository.RequestImageRepository;
import com.helpnearby.service.S3UploadService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class RequestService {

	@Autowired
	private RequestRepository requestRepository;
	
	@Autowired
	private RequestImageRepository requestImageRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private S3UploadService s3UploadService;

	// Create
	public Request createRequest(Request request) {
		return requestRepository.save(request);
	}

	// Read all - optimized to only fetch OPEN requests by default
//	public Page<Request> getAllRequests(int page, int size) {
//		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//		return requestRepository.findOpenRequestsWithImages(pageable);
//	}
	
	
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
	public Optional<Request> getRequestById(String id) {
		Optional<Request> request = requestRepository.findById(id);
		if (request.isEmpty()) {
			request = requestRepository.findByIdWithImages(id);
		}
		return request;
	}

	// Read by User - optimized query
	public List<Request> getRequestsByUserId(String userId) {
		return requestRepository.findByUserIdWithImages(userId);
	}
	
	// Read by User and Status
	public List<Request> getRequestsByUserIdAndStatus(String userId, String status) {
		return requestRepository.findByUserIdAndStatus(userId, status);
	}

	// Delete images in a separate transaction to avoid FK constraint issues
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteRequestImages(String requestId) {
		// Use EntityManager native query directly for more control
		entityManager.createNativeQuery("DELETE FROM request_images WHERE request_id = :requestId")
			.setParameter("requestId", requestId)
			.executeUpdate();
	}

	// Update
	@Transactional
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
		
		Request existing = existingOpt.orElseThrow(() -> {
			System.err.println("Request not found with id: " + id);
			return new RuntimeException("Request not found with id: " + id);
		});
		
		System.out.println("Request found, proceeding with update...");
		
		// Initialize images collection if null to avoid NPE
		if (existing.getImages() == null) {
			existing.setImages(new ArrayList<>());
		}
		
		// Security: userId should not be changed via update
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
		
		// Handle images - only update if non-empty array is provided
		// IMPORTANT: Delete images first, then reload entity to avoid FK constraint violation
		// Logic:
		// - If images is null: keep existing images (don't update)
		// - If images is empty array []: keep existing images (don't clear)
		// - If images has items: replace with new images
		if (requestUpdate.getImages() != null && !requestUpdate.getImages().isEmpty()) {
			// Initialize collection if null
			if (existing.getImages() == null) {
				existing.setImages(new ArrayList<>());
			}
			
			// CRITICAL: Delete images from S3 first, then from database
			if (!existing.getImages().isEmpty()) {
				// Store request ID and collect image URLs before deletion
				String requestId = existing.getId();
				List<String> imageUrls = new ArrayList<>();
				
				// Collect all existing image URLs for S3 deletion
				existing.getImages().forEach(img -> {
					if (img.getUrl() != null && !img.getUrl().isEmpty()) {
						imageUrls.add(img.getUrl());
					}
				});
				
				// Delete all old images from S3
				System.out.println("Deleting " + imageUrls.size() + " old images from S3...");
				imageUrls.forEach(url -> s3UploadService.deleteFileFromS3(url));
				
				// Delete all images using native SQL in separate transaction
				// This completely bypasses Hibernate's entity lifecycle
				deleteRequestImages(requestId);
				
				// Reload the entity from database to get fresh state without images
				// This clears Hibernate's internal tracking of the collection
				Request refreshed = requestRepository.findByIdWithImages(requestId)
					.orElse(existing);
				
				// Copy all updated field values to refreshed entity
				refreshed.setTitle(existing.getTitle());
				refreshed.setDescription(existing.getDescription());
				refreshed.setCategory(existing.getCategory());
				refreshed.setReward(existing.getReward());
				refreshed.setStatus(existing.getStatus());
				refreshed.setUrgency(existing.getUrgency());
				refreshed.setLatitude(existing.getLatitude());
				refreshed.setLongitude(existing.getLongitude());
				
				// Use refreshed entity going forward
				existing = refreshed;
			}
			
			// Create and add new images to the existing entity
			final Request finalExisting = existing; // Final reference for lambda
			requestUpdate.getImages().forEach(img -> {
				if (img != null) {
					// Create new RequestImage entity
					RequestImage newImage = new RequestImage();
					newImage.setUrl(img.getUrl());
					newImage.setPrimaryImage(img.isPrimaryImage());
					newImage.setRequest(finalExisting);
					finalExisting.getImages().add(newImage);
				}
			});
		}
		// If images is null or empty array, do nothing - preserve existing images
		
		// updatedAt will be set automatically by @PreUpdate
		// createdAt is preserved because it's marked as updatable = false
		System.out.println("Saving updated request...");
		Request saved = requestRepository.save(existing);
		System.out.println("Request saved successfully with id: " + saved.getId());
		return saved;
	}

	// Delete
	public void deleteRequest(String id) {
		requestRepository.deleteById(id);
	}

//	TODO Fetch top 5 request based on user zipcode + 5 miles

}
