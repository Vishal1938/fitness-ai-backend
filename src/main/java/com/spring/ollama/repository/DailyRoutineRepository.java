// ==========================================
// 2. Repository
// ==========================================
package com.spring.ollama.repository;

import com.spring.ollama.entity.DailyRoutine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRoutineRepository extends MongoRepository<DailyRoutine, String> {

    // Find all routines for a user, ordered by day number
    List<DailyRoutine> findByUserIdOrderByDayNumberAsc(String userId);

    // Find specific day for a user
    Optional<DailyRoutine> findByUserIdAndDayNumber(String userId, int dayNumber);

    // Count total routines for a user
    long countByUserId(String userId);

    // Find completed routines for a user
    List<DailyRoutine> findByUserIdAndIsCompletedTrue(String userId);

    // Get the latest routine for a user
    Optional<DailyRoutine> findFirstByUserIdOrderByDayNumberDesc(String userId);
}
