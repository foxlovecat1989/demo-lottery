package org.example.demolottery.service;

import org.example.demolottery.dto.request.CreatePrizeRequest;
import org.example.demolottery.dto.response.PrizeResponse;
import org.example.demolottery.entity.Prize;
import org.example.demolottery.exception.LotteryException;
import org.example.demolottery.repository.LotteryActivityRepository;
import org.example.demolottery.repository.PrizeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrizeService {

    private final PrizeRepository prizeRepository;
    private final LotteryActivityRepository activityRepository;

    public PrizeService(PrizeRepository prizeRepository, LotteryActivityRepository activityRepository) {
        this.prizeRepository = prizeRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public PrizeResponse addPrizeToActivity(Long activityId, CreatePrizeRequest request) {
        Prize prize = new Prize(
                request.getName(),
                request.getDescription(),
                request.getProbability(),
                request.getTotalQuantity(),
                activityId
        );
        
        prize.setImageUrl(request.getImageUrl());
        prize.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        
        prize = prizeRepository.save(prize);
        
        return convertToPrizeResponse(prize);
    }

    public List<PrizeResponse> getActivityPrizes(Long activityId) {
        // Verify activity exists
        activityRepository.findById(activityId)
                .orElseThrow(() -> new LotteryException("Activity not found"));

        List<Prize> prizes = prizeRepository.findByActivityIdOrderBySortOrderAsc(activityId);
        
        return prizes.stream()
                .map(this::convertToPrizeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrizeResponse updatePrize(Long activityId, Long prizeId, CreatePrizeRequest request) {
        // Verify activity exists
        activityRepository.findById(activityId)
                .orElseThrow(() -> new LotteryException("Activity not found"));

        Prize prize = prizeRepository.findById(prizeId)
                .orElseThrow(() -> new LotteryException("Prize not found"));

        if (!prize.getActivityId().equals(activityId)) {
            throw new LotteryException("Prize does not belong to this activity");
        }

        prize.setName(request.getName());
        prize.setDescription(request.getDescription());
        prize.setProbability(request.getProbability());
        prize.setTotalQuantity(request.getTotalQuantity());
        prize.setImageUrl(request.getImageUrl());
        if (request.getSortOrder() != null) {
            prize.setSortOrder(request.getSortOrder());
        }

        prize = prizeRepository.save(prize);
        
        return convertToPrizeResponse(prize);
    }

    @Transactional
    public void deletePrize(Long activityId, Long prizeId) {
        // Verify activity exists
        activityRepository.findById(activityId)
                .orElseThrow(() -> new LotteryException("Activity not found"));

        Prize prize = prizeRepository.findById(prizeId)
                .orElseThrow(() -> new LotteryException("Prize not found"));

        if (!prize.getActivityId().equals(activityId)) {
            throw new LotteryException("Prize does not belong to this activity");
        }

        prizeRepository.delete(prize);
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