package com.helpnearby.controller;

import com.helpnearby.dto.FileMeta;
import com.helpnearby.dto.PresignedUpload;
import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.entities.Request;
import com.helpnearby.service.RequestService;
import com.helpnearby.service.S3UploadService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
public class RequestsController {

	private RequestService requestService;

	private final S3UploadService s3UploadService;

	public RequestsController(RequestService requestService, S3UploadService s3UploadService) {
		this.requestService = requestService;
		this.s3UploadService = s3UploadService;
	}

	// Create
	@PostMapping
	public ResponseEntity<Request> createRequest(@RequestBody Request request) {
		Request created = requestService.createRequest(request);
		return ResponseEntity.ok(created);
	}

	// Replaced with only top 5
//	@GetMapping
//	public ResponseEntity<List<Request>> getAllRequests() {
//		return ResponseEntity.ok(requestService.getAllRequests());
//	}
	
    @GetMapping
    public ResponseEntity<Page<RequestListDTO>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
		return ResponseEntity.ok(requestService.getAllRequests(page,size));

    }
    
	// Read by ID
	@GetMapping("/{id}")
	public ResponseEntity<Request> getRequestById(@PathVariable String id) {
		return requestService.getRequestById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Read by User
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Request>> getRequestsByUser(@PathVariable String userId) {
		return ResponseEntity.ok(requestService.getRequestsByUserId(userId));
	}

	// Update
	@PutMapping("/{id}")
	public ResponseEntity<Request> updateRequest(@PathVariable String id, @RequestBody Request request) {
		return requestService.getRequestById(id).map(existing -> {
			request.setId(id); // ensure ID remains the same
			return ResponseEntity.ok(requestService.updateRequest(request));
		}).orElse(ResponseEntity.notFound().build());
	}

	// Delete
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
		requestService.deleteRequest(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/presign")
	public List<PresignedUpload> presign(@RequestBody List<FileMeta> files) {
		return files.stream().map(f -> s3UploadService.generatePresignedUrl(f.fileName(), f.contentType())).toList();
	}
}
