package com.helpnearby.service;

import com.helpnearby.dto.CreateRequestDto;
import com.helpnearby.dto.MultiUserNotificationRequestDto;
import com.helpnearby.dto.RequestImageResponseDto;
import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.dto.RequestResponseDto;
import com.helpnearby.dto.UpdateRequestDto;
import com.helpnearby.dto.UpdateRequestImageDto;
import com.helpnearby.entities.Request;
import com.helpnearby.entities.RequestImage;
import com.helpnearby.entities.User;
import com.helpnearby.repository.RequestImageRepository;
import com.helpnearby.repository.RequestRepository;
import com.helpnearby.repository.UserRepository;
import com.helpnearby.util.RequestCreateMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService {

	@PersistenceContext
	private EntityManager entityManager;

	private S3UploadService s3UploadService;

	private RequestImageRepository requestImageRepository;

	private UserRepository userRepository;

	private RequestRepository requestRepository;

	private NotificationService notificationService;

	RequestService(RequestRepository requestRepository, RequestImageRepository requestImageRepository,
			S3UploadService s3UploadService, UserRepository userRepository,NotificationService notificationService) {
		this.requestRepository = requestRepository;
		this.requestImageRepository = requestImageRepository;
		this.s3UploadService = s3UploadService;
		this.userRepository = userRepository;
		this.notificationService=notificationService;
	}

	// Create
	public Request createRequest(String userId, CreateRequestDto requestDto) {
		Request request = RequestCreateMapper.toEntity(userId, requestDto);

		// search for user with in 10 miles of raduis and notify them
		Request createdRequest = requestRepository.save(request);
		List<User> usersWithIn10Miles = notifyUserWithIn10MileRadius(requestDto.getLongitude(), request.getLatitude(),userId);
		// Notify Nearby Users
		MultiUserNotificationRequestDto multiUserNotification = new MultiUserNotificationRequestDto();
		multiUserNotification.setBody("New Help Requested");
		multiUserNotification.setTitle("Your neighbor nearby needs help");
		multiUserNotification.setUserIds(usersWithIn10Miles);
		notificationService.sendNotificationToUsers(multiUserNotification);

		return createdRequest;
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
	public void deleteRequest(String requestId) {
		// 1. Delete removed images

		List<RequestImage> imagesToDelete = requestImageRepository.getAllImageByRequestId(requestId);
		if (!imagesToDelete.isEmpty()) {
			imagesToDelete.forEach(img -> s3UploadService.deleteObject(img.getS3Key()));
			requestImageRepository.deleteByRequestId(requestId);
		}
		requestRepository.deleteById(requestId);
	}

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

	@Async
	private List<User> notifyUserWithIn10MileRadius(double longitude, double latitude, String requesterUserId){
		double radiusMiles = 10;

		double latDelta = radiusMiles / 69.0;
		double lonDelta = radiusMiles / (69.0 * Math.cos(Math.toRadians(latitude)));
		double latMin = latitude - latDelta;
		double latMax = latitude + latDelta;
		double lonMin = longitude - lonDelta;
		double lonMax = longitude + lonDelta;

		return userRepository.getUsersWithin10MilesOptimized(latitude, longitude, latMin, latMax, lonMin, lonMax,requesterUserId);
	}

}
