package com.spring.ollama.repository;

import com.spring.ollama.entity.DaySummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Day Summaries
 */
@Repository
public interface DaySummaryRepository extends MongoRepository<DaySummary, String> {

    // Find summary for a specific date
    Optional<DaySummary> findByUserIdAndDate(String userId, LocalDate date);

    // Find all summaries for a user
    List<DaySummary> findByUserIdOrderByDateDesc(String userId);

    // Find summaries in date range
    List<DaySummary> findByUserIdAndDateBetweenOrderByDateDesc(
            String userId, LocalDate startDate, LocalDate endDate);

    // Find summaries by performance level
    List<DaySummary> findByUserIdAndOverallPerformanceOrderByDateDesc(
            String userId, String performance);

    // Get last N summaries for AI context
    @Query("{ 'userId': ?0 }")
    List<DaySummary> findRecentSummaries(String userId);

    // Find yesterday's summary
    default Optional<DaySummary> findYesterdaySummary(String userId) {
        return findByUserIdAndDate(userId, LocalDate.now().minusDays(1));
    }
}