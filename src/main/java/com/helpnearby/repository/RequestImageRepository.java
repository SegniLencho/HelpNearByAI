package com.helpnearby.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.entities.RequestImage;
import java.util.UUID;

@Repository
public interface RequestImageRepository extends JpaRepository<RequestImage, UUID> {
	
	// Delete images by request ID using native query to avoid FK constraint issues
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM request_images WHERE request_id = :requestId", nativeQuery = true)
	void deleteByRequestId(@Param("requestId") String requestId);
}
