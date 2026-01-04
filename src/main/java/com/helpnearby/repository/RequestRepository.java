package com.helpnearby.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.helpnearby.dto.RequestListDTO;
import com.helpnearby.entities.Request;
import jakarta.persistence.QueryHint;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {

	// Optimized query with index usage
	@Query("SELECT r FROM Request r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
	List<Request> findByUserId(String userId);

	// Single request with images - use EntityGraph
	@EntityGraph(attributePaths = { "images" })
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("SELECT r FROM Request r WHERE r.id = :id")
	Optional<Request> findByIdWithImages(String id);

	// User requests with images
	@EntityGraph(attributePaths = { "images" })
	@Query("SELECT r FROM Request r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
	List<Request> findByUserIdWithImages(String userId);

	// Paginated requests with images - optimized
	@EntityGraph(attributePaths = { "images" })
	@Query("SELECT r FROM Request r WHERE r.status = 'OPEN' ORDER BY r.createdAt DESC")
	Page<Request> findOpenRequestsWithImages(Pageable pageable);

	// All requests with images - for admin or when status filter not needed
	@EntityGraph(attributePaths = { "images" })
	@Query("SELECT r FROM Request r ORDER BY r.createdAt DESC")
	Page<Request> findAllWithImages(Pageable pageable);

	// Filter by status
	@EntityGraph(attributePaths = { "images" })
	@Query("SELECT r FROM Request r WHERE r.status = :status ORDER BY r.createdAt DESC")
	Page<Request> findByStatusWithImages(String status, Pageable pageable);

	// Filter by status and user
	@Query("SELECT r FROM Request r WHERE r.userId = :userId AND r.status = :status ORDER BY r.createdAt DESC")
	List<Request> findByUserIdAndStatus(String userId, String status);

	@Query("""
			    SELECT new com.helpnearby.dto.RequestListDTO(
			        r.id,
			        r.title,
			        r.description,
			        r.category,
			        r.reward,
			        r.urgency,
			        r.latitude,
			        r.longitude,
			        r.createdAt,
			        ri.url
			    )
			    FROM Request r
			    LEFT JOIN r.images ri
			        ON ri.primaryImage = true
			    WHERE r.status = 'OPEN'
			    ORDER BY r.createdAt DESC
			""")
	Page<RequestListDTO> findOpenRequestsWithPrimaryImage(Pageable pageable);

	@Query(value = """
			SELECT r.id, r.title,r.description,r.category, r.reward, r.urgency, r.latitude, r.longitude,r.created_at,
			       ri.url AS image_url
			FROM requests r
			LEFT JOIN request_images ri
			    ON ri.request_id = r.id AND ri.is_primary = true
			WHERE r.status = 'OPEN'
			  AND ST_DWithin(
			        r.location,
			        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
			        :radiusMeters
			  )
			ORDER BY r.created_at DESC
			""", countQuery = """
			SELECT COUNT(*)
			FROM requests r
			WHERE r.status = 'OPEN'
			  AND ST_DWithin(
			        r.location,
			        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
			        :radiusMeters
			  )
			""", nativeQuery = true)
	Page<RequestListDTO> findOpenRequestsWithinRadius(@Param("latitude") double latitude,
			@Param("longitude") double longitude, @Param("radiusMeters") double radiusMeters, Pageable pageable);

}
