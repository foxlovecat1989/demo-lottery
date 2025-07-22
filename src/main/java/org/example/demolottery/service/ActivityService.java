package org.example.demolottery.service;

import org.example.demolottery.dto.request.CreateActivityRequest;
import org.example.demolottery.dto.request.CreatePrizeRequest;
import org.example.demolottery.dto.request.UpdateActivityRequest;
import org.example.demolottery.dto.response.ActivityResponse;
import org.example.demolottery.dto.response.PrizeResponse;
import org.example.demolottery.entity.LotteryActivity;
import org.example.demolottery.entity.Prize;
import org.example.demolottery.exception.LotteryException;
import org.example.demolottery.repository.LotteryActivityRepository;
import org.example.demolottery.repository.PrizeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final LotteryActivityRepository activityRepository;
    private final PrizeRepository prizeRepository;

    public ActivityService(LotteryActivityRepository activityRepository,
                          PrizeRepository prizeRepository) {
        this.activityRepository = activityRepository;
        this.prizeRepository = prizeRepository;
    }

    @Transactional
    public ActivityResponse createActivity(CreateActivityRequest request) {
        validateActivityRequest(request);
        
        LotteryActivity activity = new LotteryActivity(
                request.getName(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMaxDrawsPerUser(),
                request.getMaxConcurrentDraws()
        );

        activity = activityRepository.save(activity);
        
        List<Prize> prizes = createPrizes(activity.getId(), request.getPrizes());
        
        return convertToActivityResponse(activity, prizes);
    }

    @Transactional
    public ActivityResponse updateActivity(Long activityId, UpdateActivityRequest request) {
        LotteryActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new LotteryException("Activity not found"));

        if (request.getName() != null) {
            activity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getStartTime() != null) {
            activity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            activity.setEndTime(request.getEndTime());
        }
        if (request.getMaxDrawsPerUser() != null) {
            activity.setMaxDrawsPerUser(request.getMaxDrawsPerUser());
        }
        if (request.getMaxConcurrentDraws() != null) {
            activity.setMaxConcurrentDraws(request.getMaxConcurrentDraws());
        }

        validateActivityTimes(activity.getStartTime(), activity.getEndTime());
        
        activity = activityRepository.save(activity);
        List<Prize> prizes = prizeRepository.findByActivityIdOrderBySortOrderAsc(activityId);
        
        return convertToActivityResponse(activity, prizes);
    }

    private void validateActivityRequest(CreateActivityRequest request) {
        validateActivityTimes(request.getStartTime(), request.getEndTime());
        
        if (request.getPrizes() != null && !request.getPrizes().isEmpty()) {
            BigDecimal totalProbability = request.getPrizes().stream()
                    .map(CreatePrizeRequest::getProbability)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (totalProbability.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new LotteryException("Total prize probability cannot exceed 100%");
            }
        }
    }

    private void validateActivityTimes(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new LotteryException("End time must be after start time");
        }
    }

    private List<Prize> createPrizes(Long activityId, List<CreatePrizeRequest> prizeRequests) {
        if (prizeRequests == null || prizeRequests.isEmpty()) {
            return List.of();
        }

        List<Prize> prizes = prizeRequests.stream()
                .map(request -> new Prize(
                        request.getName(),
                        request.getDescription(),
                        request.getProbability(),
                        request.getTotalQuantity(),
                        activityId))
                .collect(Collectors.toList());

        for (int i = 0; i < prizes.size(); i++) {
            Prize prize = prizes.get(i);
            CreatePrizeRequest request = prizeRequests.get(i);
            prize.setImageUrl(request.getImageUrl());
            prize.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : i);
        }

        return prizeRepository.saveAll(prizes);
    }

    public ActivityResponse getActivity(Long activityId) {
        LotteryActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new LotteryException("Activity not found"));
        
        List<Prize> prizes = prizeRepository.findByActivityIdOrderBySortOrderAsc(activityId);
        
        return convertToActivityResponse(activity, prizes);
    }

    public Page<ActivityResponse> getActiveActivities(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        List<LotteryActivity> activities = activityRepository
                .findActiveActivities(LotteryActivity.ActivityStatus.ACTIVE, now);
        
        List<ActivityResponse> responses = activities.stream()
                .map(activity -> {
                    List<Prize> prizes = prizeRepository.findByActivityIdOrderBySortOrderAsc(activity.getId());
                    return convertToActivityResponse(activity, prizes);
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<ActivityResponse> getAllActivities(Pageable pageable) {
        Page<LotteryActivity> activityPage = activityRepository.findAll(pageable);
        
        List<ActivityResponse> responses = activityPage.getContent().stream()
                .map(activity -> {
                    List<Prize> prizes = prizeRepository.findByActivityIdOrderBySortOrderAsc(activity.getId());
                    return convertToActivityResponse(activity, prizes);
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, activityPage.getTotalElements());
    }

    @Transactional
    public ActivityResponse updateActivityStatus(Long activityId, LotteryActivity.ActivityStatus status) {
        LotteryActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new LotteryException("Activity not found"));
        
        activity.setStatus(status);
        activity = activityRepository.save(activity);
        
        List<Prize> prizes = prizeRepository.findByActivityIdOrderBySortOrderAsc(activityId);
        
        return convertToActivityResponse(activity, prizes);
    }

    private ActivityResponse convertToActivityResponse(LotteryActivity activity, List<Prize> prizes) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setName(activity.getName());
        response.setDescription(activity.getDescription());
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setMaxDrawsPerUser(activity.getMaxDrawsPerUser());
        response.setMaxConcurrentDraws(activity.getMaxConcurrentDraws());
        response.setStatus(activity.getStatus());
        response.setCreatedAt(activity.getCreatedAt());
        
        List<PrizeResponse> prizeResponses = prizes.stream()
                .map(this::convertToPrizeResponse)
                .collect(Collectors.toList());
        response.setPrizes(prizeResponses);
        
        return response;
    }

    private PrizeResponse convertToPrizeResponse(Prize prize) {
        PrizeResponse response = new PrizeResponse();
        response.setId(prize.getId());
        response.setName(prize.getName());
        response.setDescription(prize.getDescription());
        response.setProbability(prize.getProbability());
        response.setTotalQuantity(prize.getTotalQuantity());
        response.setRemainingQuantity(prize.getRemainingQuantity());
        response.setImageUrl(prize.getImageUrl());
        response.setSortOrder(prize.getSortOrder());
        return response;
    }
} 