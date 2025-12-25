package com.spring.ollama.repository;

import com.spring.ollama.entity.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Daily Routines
 */
@Repository
public interface DailyRoutineRepository extends MongoRepository<DailyRoutine, String> {

    // Find routine for a specific user and date
    Optional<DailyRoutine> findByUserIdAndDate(String userId, LocalDate date);

    // Find all routines for a user
    List<DailyRoutine> findByUserIdOrderByDateDesc(String userId);

    // Find routines for a user in a date range
    List<DailyRoutine> findByUserIdAndDateBetweenOrderByDateDesc(
            String userId, LocalDate startDate, LocalDate endDate);

    // Find completed routines for a user
    List<DailyRoutine> findByUserIdAndStatusOrderByDateDesc(String userId, String status);

    // Find today's routine for a user
    @Query("{ 'userId': ?0, 'date': ?1 }")
    Optional<DailyRoutine> findTodayRoutine(String userId, LocalDate today);

    // Count completed routines for a user
    long countByUserIdAndStatus(String userId, String status);

    // Find routines by workout completion status
    List<DailyRoutine> findByUserIdAndWorkoutCompletedOrderByDateDesc(
            String userId, boolean completed);

    // Check if routine exists for date
    boolean existsByUserIdAndDate(String userId, LocalDate date);
}

