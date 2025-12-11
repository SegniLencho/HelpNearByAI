
package com.helpnearby.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.helpnearby.entities.Request;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {
    List<Request> findByUserId(String userId);
}
