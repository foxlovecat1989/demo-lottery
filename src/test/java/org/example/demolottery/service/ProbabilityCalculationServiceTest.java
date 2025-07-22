package org.example.demolottery.service;

import org.example.demolottery.entity.Prize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProbabilityCalculationServiceTest {

    @InjectMocks
    private ProbabilityCalculationService probabilityService;

    private List<Prize> testPrizes;

    @BeforeEach
    void setUp() {
        testPrizes = new ArrayList<>();
        
        Prize prize1 = new Prize();
        prize1.setId(1L);
        prize1.setName("Prize 1");
        prize1.setProbability(BigDecimal.valueOf(10.0));
        
        Prize prize2 = new Prize();
        prize2.setId(2L);
        prize2.setName("Prize 2");
        prize2.setProbability(BigDecimal.valueOf(5.0));
        
        Prize prize3 = new Prize();
        prize3.setId(3L);
        prize3.setName("Prize 3");
        prize3.setProbability(BigDecimal.valueOf(15.0));
        
        testPrizes.addAll(Arrays.asList(prize1, prize2, prize3));
    }

    @Test
    void testCalculateWinningPrizeWithEmptyList() {
        Prize result = probabilityService.calculateWinningPrize(new ArrayList<>());
        assertNull(result);
    }

    @Test
    void testCalculateWinningPrizeDistribution() {
        int totalTrials = 10000;
        int prize1Wins = 0;
        int prize2Wins = 0;
        int prize3Wins = 0;
        int noPrizeWins = 0;

        for (int i = 0; i < totalTrials; i++) {
            Prize result = probabilityService.calculateWinningPrize(testPrizes);
            if (result == null) {
                noPrizeWins++;
            } else if (result.getId().equals(1L)) {
                prize1Wins++;
            } else if (result.getId().equals(2L)) {
                prize2Wins++;
            } else if (result.getId().equals(3L)) {
                prize3Wins++;
            }
        }

        double prize1Percentage = (double) prize1Wins / totalTrials * 100;
        double prize2Percentage = (double) prize2Wins / totalTrials * 100;
        double prize3Percentage = (double) prize3Wins / totalTrials * 100;
        double noPrizePercentage = (double) noPrizeWins / totalTrials * 100;

        assertTrue(prize1Percentage >= 8.0 && prize1Percentage <= 12.0, 
                "Prize 1 should win around 10%, got " + prize1Percentage + "%");
        assertTrue(prize2Percentage >= 3.0 && prize2Percentage <= 7.0, 
                "Prize 2 should win around 5%, got " + prize2Percentage + "%");
        assertTrue(prize3Percentage >= 13.0 && prize3Percentage <= 17.0, 
                "Prize 3 should win around 15%, got " + prize3Percentage + "%");
        assertTrue(noPrizePercentage >= 68.0 && noPrizePercentage <= 72.0, 
                "No prize should win around 70%, got " + noPrizePercentage + "%");
    }

    @Test
    void testValidateProbabilitySumValid() {
        assertTrue(probabilityService.validateProbabilitySum(testPrizes));
    }

    @Test
    void testValidateProbabilitySumInvalid() {
        Prize prize4 = new Prize();
        prize4.setProbability(BigDecimal.valueOf(80.0));
        testPrizes.add(prize4);

        assertFalse(probabilityService.validateProbabilitySum(testPrizes));
    }

    @Test
    void testValidateProbabilitySumExactly100() {
        testPrizes.clear();
        
        Prize prize1 = new Prize();
        prize1.setProbability(BigDecimal.valueOf(60.0));
        
        Prize prize2 = new Prize();
        prize2.setProbability(BigDecimal.valueOf(40.0));
        
        testPrizes.addAll(Arrays.asList(prize1, prize2));

        assertTrue(probabilityService.validateProbabilitySum(testPrizes));
    }

    @Test
    void testCalculateNoPrizeProbability() {
        BigDecimal noPrizeProbability = probabilityService.calculateNoPrizeProbability(testPrizes);
        assertEquals(0, BigDecimal.valueOf(70.0).compareTo(noPrizeProbability));
    }

    @Test
    void testCalculateNoPrizeProbabilityZero() {
        testPrizes.clear();
        
        Prize prize1 = new Prize();
        prize1.setProbability(BigDecimal.valueOf(100.0));
        testPrizes.add(prize1);

        BigDecimal noPrizeProbability = probabilityService.calculateNoPrizeProbability(testPrizes);
        assertEquals(0, BigDecimal.valueOf(0.0).compareTo(noPrizeProbability));
    }

    @Test
    void testCalculateNoPrizeProbabilityNegativeHandled() {
        testPrizes.clear();
        
        Prize prize1 = new Prize();
        prize1.setProbability(BigDecimal.valueOf(120.0));
        testPrizes.add(prize1);

        BigDecimal noPrizeProbability = probabilityService.calculateNoPrizeProbability(testPrizes);
        assertEquals(0, BigDecimal.ZERO.compareTo(noPrizeProbability));
    }

    @Test
    void testHighProbabilityPrize() {
        testPrizes.clear();
        
        Prize highProbPrize = new Prize();
        highProbPrize.setId(1L);
        highProbPrize.setProbability(BigDecimal.valueOf(90.0));
        testPrizes.add(highProbPrize);

        int totalTrials = 1000;
        int wins = 0;

        for (int i = 0; i < totalTrials; i++) {
            Prize result = probabilityService.calculateWinningPrize(testPrizes);
            if (result != null && result.getId().equals(1L)) {
                wins++;
            }
        }

        double winPercentage = (double) wins / totalTrials * 100;
        assertTrue(winPercentage >= 85.0 && winPercentage <= 95.0, 
                "High probability prize should win around 90%, got " + winPercentage + "%");
    }

    @Test
    void testLowProbabilityPrize() {
        testPrizes.clear();
        
        Prize lowProbPrize = new Prize();
        lowProbPrize.setId(1L);
        lowProbPrize.setProbability(BigDecimal.valueOf(1.0));
        testPrizes.add(lowProbPrize);

        int totalTrials = 10000;
        int wins = 0;

        for (int i = 0; i < totalTrials; i++) {
            Prize result = probabilityService.calculateWinningPrize(testPrizes);
            if (result != null && result.getId().equals(1L)) {
                wins++;
            }
        }

        double winPercentage = (double) wins / totalTrials * 100;
        assertTrue(winPercentage >= 0.5 && winPercentage <= 1.5, 
                "Low probability prize should win around 1%, got " + winPercentage + "%");
    }
} 