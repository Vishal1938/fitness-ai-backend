
package com.spring.ollama.service;

import com.spring.ollama.entity.DailyRoutine;
import com.spring.ollama.entity.User;
import com.spring.ollama.repository.DailyRoutineRepository;
import com.spring.ollama.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DailyRoutineService {

    private static final Logger logger = LoggerFactory.getLogger(DailyRoutineService.class);

    @Autowired
    private DailyRoutineRepository dailyRoutineRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Save a new daily routine for a user
     */
    public DailyRoutine saveDailyRoutine(String userId, String structuredPlanJson) {
        logger.info("Saving daily routine for user: {}", userId);

        // Get the next day number for this user
        int nextDayNumber = getNextDayNumber(userId);

        DailyRoutine routine = new DailyRoutine();
        routine.setUserId(userId);
        routine.setDayNumber(nextDayNumber);
        routine.setStructuredPlanJson(structuredPlanJson);

        DailyRoutine savedRoutine = dailyRoutineRepository.save(routine);

        // Update user's current daily routine reference
        updateUserCurrentRoutine(userId, savedRoutine.getId());

        logger.info("Saved daily routine with id: {} for user: {}, day: {}",
                savedRoutine.getId(), userId, nextDayNumber);

        return savedRoutine;
    }

    /**
     * Get the next day number for a user
     */
    private int getNextDayNumber(String userId) {
        Optional<DailyRoutine> latestRoutine =
                dailyRoutineRepository.findFirstByUserIdOrderByDayNumberDesc(userId);

        return latestRoutine.map(routine -> routine.getDayNumber() + 1).orElse(1);
    }

    /**
     * Update user's current routine reference
     */
    private void updateUserCurrentRoutine(String userId, String routineId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCurrentDailyRoutineId(routineId);
            user.setLastActiveDate(java.time.LocalDate.now());
            userRepository.save(user);
        }
    }

    /**
     * Get all routines for a user
     */
    public List<DailyRoutine> getAllRoutinesByUser(String userId) {
        logger.info("Fetching all routines for user: {}", userId);
        return dailyRoutineRepository.findByUserIdOrderByDayNumberAsc(userId);
    }

    /**
     * Get a specific day's routine
     */
    public Optional<DailyRoutine> getRoutineByDay(String userId, int dayNumber) {
        logger.info("Fetching routine for user: {}, day: {}", userId, dayNumber);
        return dailyRoutineRepository.findByUserIdAndDayNumber(userId, dayNumber);
    }

    /**
     * Get current (latest) routine for a user
     */
    public Optional<DailyRoutine> getCurrentRoutine(String userId) {
        logger.info("Fetching current routine for user: {}", userId);
        return dailyRoutineRepository.findFirstByUserIdOrderByDayNumberDesc(userId);
    }

    /**
     * Mark a routine as completed
     */
    public DailyRoutine markRoutineAsCompleted(String routineId) {
        logger.info("Marking routine as completed: {}", routineId);

        Optional<DailyRoutine> routineOpt = dailyRoutineRepository.findById(routineId);
        if (routineOpt.isPresent()) {
            DailyRoutine routine = routineOpt.get();
            routine.setCompleted(true);
            routine.setCompletedAt(LocalDateTime.now());
            routine.setUpdatedAt(LocalDateTime.now());

            return dailyRoutineRepository.save(routine);
        }

        throw new RuntimeException("Routine not found with id: " + routineId);
    }

    /**
     * Get total completed routines count for a user
     */
    public long getCompletedRoutinesCount(String userId) {
        return dailyRoutineRepository.findByUserIdAndIsCompletedTrue(userId).size();
    }

    /**
     * Delete all routines for a user (for reset)
     */
    public void deleteAllRoutinesForUser(String userId) {
        logger.info("Deleting all routines for user: {}", userId);
        List<DailyRoutine> routines = dailyRoutineRepository.findByUserIdOrderByDayNumberAsc(userId);
        dailyRoutineRepository.deleteAll(routines);

        // Clear user's current routine reference
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCurrentDailyRoutineId(null);
            userRepository.save(user);
        }
    }
}