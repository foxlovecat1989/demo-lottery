package org.example.demolottery.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreatePrizeRequest {
    
    @NotBlank(message = "Prize name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Probability is required")
    @DecimalMin(value = "0.01", message = "Probability must be greater than 0")
    @DecimalMax(value = "100.00", message = "Probability cannot exceed 100")
    private BigDecimal probability;
    
    @NotNull(message = "Total quantity is required")
    @Min(value = 0, message = "Total quantity cannot be negative")
    private Integer totalQuantity;
    
    private String imageUrl;
    
    private Integer sortOrder;

    public CreatePrizeRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getProbability() {
        return probability;
    }

    public void setProbability(BigDecimal probability) {
        this.probability = probability;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
} 