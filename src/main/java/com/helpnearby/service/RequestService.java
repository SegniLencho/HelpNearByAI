package com.helpnearby.service;

import com.helpnearby.dto.CreateRequestDto;
import com.helpnearby.dto.PagedResponse;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

@Service
public class RequestService {

	@PersistenceContext
	private EntityManager entityManager;

	private S3UploadService s3UploadService;

	private RequestImageRepository requestImageRepository;

	private RequestRepository requestRepository;

	RequestService(RequestRepository requestRepository, RequestImageRepository requestImageRepository,
			S3UploadService s3UploadService) {
		this.requestRepository = requestRepository;
		this.requestImageRepository = requestImageRepository;
		this.s3UploadService = s3UploadService;
	}

	// Create
	@Caching(evict = { @CacheEvict(value = "openRequests", allEntries = true),
			@CacheEvict(value = "userRequests", key = "#request.userId") })
	public Request createRequest(String userId, CreateRequestDto requestDto) {
		Request request = RequestCreateMapper.toEntity(userId, requestDto);
		System.out.println("Create Request payload " + requestDto.toString());
		return requestRepository.save(request);
	}

	@Cacheable(value = "openRequests", // cache name in Redis
			key = "{#page, #size}" // unique key per page + size
	)
	public PagedResponse<RequestListDTO> getAllRequests(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<RequestListDTO> pageResult = requestRepository.findOpenRequestsWithPrimaryImage(pageable);

		return new PagedResponse<>(pageResult.getContent(), pageResult.getNumber(), pageResult.getSize(),
				pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.isLast());
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

}
