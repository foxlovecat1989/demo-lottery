package org.example.demolottery.service;

import org.example.demolottery.entity.Prize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

@Service
public class ProbabilityCalculationService {

    private final SecureRandom random = new SecureRandom();

    public Prize calculateWinningPrize(List<Prize> availablePrizes) {
        if (availablePrizes.isEmpty()) {
            return null;
        }

        BigDecimal totalProbability = calculateTotalProbability(availablePrizes);
        BigDecimal noPrizeProbability = BigDecimal.valueOf(100).subtract(totalProbability);

        if (noPrizeProbability.compareTo(BigDecimal.ZERO) < 0) {
            noPrizeProbability = BigDecimal.ZERO;
        }

        BigDecimal randomValue = generateRandomPercentage();
        
        BigDecimal cumulativeProbability = BigDecimal.ZERO;
        
        for (Prize prize : availablePrizes) {
            cumulativeProbability = cumulativeProbability.add(prize.getProbability());
            if (randomValue.compareTo(cumulativeProbability) <= 0) {
                return prize;
            }
        }

        return null;
    }

    private BigDecimal calculateTotalProbability(List<Prize> prizes) {
        return prizes.stream()
                .map(Prize::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal generateRandomPercentage() {
        double randomDouble = random.nextDouble() * 100;
        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean validateProbabilitySum(List<Prize> prizes) {
        BigDecimal totalProbability = calculateTotalProbability(prizes);
        return totalProbability.compareTo(BigDecimal.valueOf(100)) <= 0;
    }

    public BigDecimal calculateNoPrizeProbability(List<Prize> prizes) {
        BigDecimal totalProbability = calculateTotalProbability(prizes);
        BigDecimal noPrizeProbability = BigDecimal.valueOf(100).subtract(totalProbability);
        return noPrizeProbability.max(BigDecimal.ZERO);
    }
} 