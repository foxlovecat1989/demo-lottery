package org.example.demolottery.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class LotteryDrawResponse {
    
    private String batchId;
    private Long activityId;
    private String activityName;
    private Integer totalDraws;
    private List<DrawResult> results;
    private LocalDateTime drawTime;

    public LotteryDrawResponse() {}

    public LotteryDrawResponse(String batchId, Long activityId, String activityName, 
                              Integer totalDraws, List<DrawResult> results, LocalDateTime drawTime) {
        this.batchId = batchId;
        this.activityId = activityId;
        this.activityName = activityName;
        this.totalDraws = totalDraws;
        this.results = results;
        this.drawTime = drawTime;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getTotalDraws() {
        return totalDraws;
    }

    public void setTotalDraws(Integer totalDraws) {
        this.totalDraws = totalDraws;
    }

    public List<DrawResult> getResults() {
        return results;
    }

    public void setResults(List<DrawResult> results) {
        this.results = results;
    }

    public LocalDateTime getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(LocalDateTime drawTime) {
        this.drawTime = drawTime;
    }

    public static class DrawResult {
        private Integer drawIndex;
        private Boolean won;
        private Long prizeId;
        private String prizeName;
        private String prizeDescription;
        private String prizeImageUrl;

        public DrawResult() {}

        public DrawResult(Integer drawIndex, Boolean won) {
            this.drawIndex = drawIndex;
            this.won = won;
        }

        public DrawResult(Integer drawIndex, Boolean won, Long prizeId, 
                         String prizeName, String prizeDescription, String prizeImageUrl) {
            this.drawIndex = drawIndex;
            this.won = won;
            this.prizeId = prizeId;
            this.prizeName = prizeName;
            this.prizeDescription = prizeDescription;
            this.prizeImageUrl = prizeImageUrl;
        }

        public Integer getDrawIndex() {
            return drawIndex;
        }

        public void setDrawIndex(Integer drawIndex) {
            this.drawIndex = drawIndex;
        }

        public Boolean getWon() {
            return won;
        }

        public void setWon(Boolean won) {
            this.won = won;
        }

        public Long getPrizeId() {
            return prizeId;
        }

        public void setPrizeId(Long prizeId) {
            this.prizeId = prizeId;
        }

        public String getPrizeName() {
            return prizeName;
        }

        public void setPrizeName(String prizeName) {
            this.prizeName = prizeName;
        }

        public String getPrizeDescription() {
            return prizeDescription;
        }

        public void setPrizeDescription(String prizeDescription) {
            this.prizeDescription = prizeDescription;
        }

        public String getPrizeImageUrl() {
            return prizeImageUrl;
        }

        public void setPrizeImageUrl(String prizeImageUrl) {
            this.prizeImageUrl = prizeImageUrl;
        }
    }
} 