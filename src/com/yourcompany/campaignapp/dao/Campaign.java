// src/main/java/com/yourcompany/campaignapp/model/Campaign.java
package com.yourcompany.campaignapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Campaign {
    private int campaignId;
    private String campaignName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private String status; // Ví dụ: "draft", "active", "completed", "paused"
    private int userId; // Khóa ngoại liên kết với User

    public Campaign() {}

    public Campaign(String campaignName, LocalDate startDate, LocalDate endDate, BigDecimal budget, String status, int userId) {
        this.campaignName = campaignName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.status = status;
        this.userId = userId;
    }

    // Getters and Setters
    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Campaign{" +
               "campaignId=" + campaignId +
               ", campaignName='" + campaignName + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", budget=" + budget +
               ", status='" + status + '\'' +
               ", userId=" + userId +
               '}';
    }
}