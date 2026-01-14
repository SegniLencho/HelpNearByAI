package com.helpnearby.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.entities.RequestImage;

@Repository
public interface RequestImageRepository extends JpaRepository<RequestImage, String> {
	
	// Delete images by request ID using native query to avoid FK constraint issues
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM request_images WHERE request_id = :requestId", nativeQuery = true)
	void deleteByRequestId(@Param("requestId") String requestId);
	
	
	@Query(value = "Select * FROM request_images WHERE request_id = :requestId", nativeQuery = true)
	List<RequestImage> getAllImageByRequestId(@Param("requestId") String requestId);
}
