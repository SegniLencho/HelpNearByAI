
package com.helpnearby.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.fcmToken = null WHERE u.fcmToken IN :tokens")
	int clearFcmTokens(List<String> tokens);

//	add a bounding box
	@Query(value = """
			SELECT *
			FROM users u
			WHERE u.id <> :requesterUserId
			  AND u.latitude BETWEEN :latMin AND :latMax
			  AND u.longitude BETWEEN :lonMin AND :lonMax
			  AND (
			      3959 * acos(
			          cos(radians(:latitude)) *
			          cos(radians(u.latitude)) *
			          cos(radians(u.longitude) - radians(:longitude)) +
			          sin(radians(:latitude)) *
			          sin(radians(u.latitude))
			      )
			  ) <= 10
			""", nativeQuery = true)
	List<User> getUsersWithin10MilesOptimized(@Param("latitude") double latitude, @Param("longitude") double longitude,
			@Param("latMin") double latMin, @Param("latMax") double latMax, @Param("lonMin") double lonMin,
			@Param("lonMax") double lonMax, @Param("requesterUserId") String requesterUserId);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.phone_verified = true WHERE u.phoneNumber = :phoneNumber")
	int markPhoneVerifiedByPhone(String phoneNumber);
	
	
	

}
