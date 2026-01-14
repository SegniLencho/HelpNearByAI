package com.helpnearby.service;

import com.helpnearby.dto.CreateRequestDto;
import com.helpnearby.dto.RequestFormMetadata;
import com.helpnearby.dto.RequestImageResponseDto;
import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.dto.RequestResponseDto;
import com.helpnearby.dto.UpdateRequestDto;
import com.helpnearby.dto.UpdateRequestImageDto;
import com.helpnearby.entities.Request;
import com.helpnearby.entities.RequestImage;
import com.helpnearby.repository.RequestImageRepository;
import com.helpnearby.repository.RequestRepository;
import com.helpnearby.util.RequestCreateMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService {

	private RequestRepository requestRepository;

	private RequestImageRepository requestImageRepository;

	private final S3UploadService s3UploadService;

	RequestService(RequestRepository requestRepository, RequestImageRepository requestImageRepository,
			S3UploadService s3UploadService) {
		this.requestRepository = requestRepository;
		this.requestImageRepository = requestImageRepository;
		this.s3UploadService = s3UploadService;
	}

	// Create
	public Request createRequest(String userId, CreateRequestDto requestDto) {
		Request request = RequestCreateMapper.toEntity(userId, requestDto);
		System.out.println("Create Request payload " + requestDto.toString());
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
		RequestResponseDto requestResponseDto = new RequestResponseDto();
		Optional<Request> request = requestRepository.findById(id);
		if (!request.isEmpty()) {
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
	@Transactional
	public Request updateRequest(UpdateRequestDto requestUpdate) {

		System.out.println("Attempting to update request with id: " + requestUpdate);
		Request existing = requestRepository.findById(requestUpdate.getRequestId())
				.orElseThrow(() -> new RuntimeException("Request not found with id: " + requestUpdate.getRequestId()));

		// 1. Delete removed images
		if (requestUpdate.getRemovedImages() != null) {
			List<String> idsToDelete = requestUpdate.getRemovedImages().stream().map(UpdateRequestImageDto::getId)
					.collect(Collectors.toList());
			List<RequestImage> imagesToDelete = requestImageRepository.findAllById(idsToDelete);
			imagesToDelete.forEach(img -> s3UploadService.deleteObject(img.getS3Key()));
			requestImageRepository.deleteAll(imagesToDelete);
		}

		// 2. Add new images
		if (requestUpdate.getNewImages() != null) {
			for (UpdateRequestImageDto newImg : requestUpdate.getNewImages()) {
				RequestImage imgEntity = new RequestImage();
				imgEntity.setS3Key(newImg.getS3Key());
				imgEntity.setUrl(newImg.getUrl());
				imgEntity.setPrimaryImage(newImg.isPrimaryImage());
				imgEntity.setRequest(existing);
				requestImageRepository.save(imgEntity);
			}
		}

		Optional.ofNullable(requestUpdate.getTitle()).ifPresent(existing::setTitle);
		Optional.ofNullable(requestUpdate.getDescription()).ifPresent(existing::setDescription);
		Optional.ofNullable(requestUpdate.getCategory()).ifPresent(existing::setCategory);
		Optional.ofNullable(requestUpdate.getReward()).ifPresent(existing::setReward);
		Optional.ofNullable(requestUpdate.getStatus()).ifPresent(existing::setStatus);
		Optional.ofNullable(requestUpdate.getUrgency()).ifPresent(existing::setUrgency);

		return requestRepository.save(existing);
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

		dto.setLocation(request.getLocation() != null ? request.getLocation().toString() : null);

		dto.setImages(request.getImages().stream().map(img -> {
			RequestImageResponseDto imageDto = new RequestImageResponseDto();
			imageDto.setId(img.getId());
			imageDto.setUrl(img.getUrl());
			imageDto.setPrimaryImage(img.isPrimaryImage());
			imageDto.setS3Key(img.getS3Key());
			return imageDto;
		}).toList());

		return dto;
	}
	
	/**
	 * Get form metadata for request creation - helps frontend display proper placeholders and validation
	 */
	public RequestFormMetadata getRequestFormMetadata() {
		Map<String, RequestFormMetadata.FieldMetadata> fields = new HashMap<>();
		
		// Title field
		fields.put("title", new RequestFormMetadata.FieldMetadata(
			"Enter a clear title for your request", 
			"text", 
			true, 
			255, 
			"required|min:5|max:255", 
			"Be specific about what help you need"
		));
		
		// Description field
		fields.put("description", new RequestFormMetadata.FieldMetadata(
			"Describe your request in detail", 
			"textarea", 
			true, 
			2000, 
			"required|min:10|max:2000", 
			"Include important details like location, timing, and specific requirements"
		));
		
		// Category field
		fields.put("category", new RequestFormMetadata.FieldMetadata(
			"Select a category", 
			"select", 
			true, 
			null, 
			"required", 
			"Choose the category that best fits your request"
		));
		
		// Reward field
		fields.put("reward", new RequestFormMetadata.FieldMetadata(
			"Enter reward amount (optional)", 
			"number", 
			false, 
			null, 
			"numeric|min:0", 
			"Offering a reward can help attract more helpers"
		));
		
		// Urgency field
		fields.put("urgency", new RequestFormMetadata.FieldMetadata(
			"Select urgency level", 
			"select", 
			true, 
			null, 
			"required", 
			"How urgent is your request?"
		));
		
		// Location fields
		fields.put("latitude", new RequestFormMetadata.FieldMetadata(
			"Latitude", 
			"number", 
			true, 
			null, 
			"required|numeric|between:-90,90", 
			"Your location latitude"
		));
		
		fields.put("longitude", new RequestFormMetadata.FieldMetadata(
			"Longitude", 
			"number", 
			true, 
			null, 
			"required|numeric|between:-180,180", 
			"Your location longitude"
		));
		
		// Categories
		List<String> categories = Arrays.asList(
			"Home & Garden", 
			"Transportation", 
			"Technology", 
			"Moving & Delivery", 
			"Pet Care", 
			"Childcare", 
			"Tutoring & Education", 
			"Health & Wellness", 
			"Events & Entertainment", 
			"Professional Services", 
			"Emergency", 
			"Other"
		);
		
		// Urgency levels
		List<String> urgencyLevels = Arrays.asList("LOW", "MEDIUM", "URGENT");
		
		// Status options (for reference, usually set automatically)
		List<String> statusOptions = Arrays.asList("OPEN", "INPROGRESS", "CLOSED");
		
		return new RequestFormMetadata(fields, categories, urgencyLevels, statusOptions);
	}

}
