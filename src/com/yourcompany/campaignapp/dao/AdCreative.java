// src/main/java/com/yourcompany/campaignapp/model/AdCreative.java
package com.yourcompany.campaignapp.model;

public class AdCreative {
    private int creativeId;
    private String creativeName;
    private String creativeType; // Ví dụ: "image", "video", "text"
    private String filePath;
    private int campaignId; // Khóa ngoại liên kết với Campaign

    public AdCreative() {}

    public AdCreative(String creativeName, String creativeType, String filePath, int campaignId) {
        this.creativeName = creativeName;
        this.creativeType = creativeType;
        this.filePath = filePath;
        this.campaignId = campaignId;
    }

    // Getters and Setters
    public int getCreativeId() { return creativeId; }
    public void setCreativeId(int creativeId) { this.creativeId = creativeId; }
    public String getCreativeName() { return creativeName; }
    public void setCreativeName(String creativeName) { this.creativeName = creativeName; }
    public String getCreativeType() { return creativeType; }
    public void setCreativeType(String creativeType) { this.creativeType = creativeType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }

    @Override
    public String toString() {
        return "AdCreative{" +
               "creativeId=" + creativeId +
               ", creativeName='" + creativeName + '\'' +
               ", creativeType='" + creativeType + '\'' +
               ", filePath='" + filePath + '\'' +
               ", campaignId=" + campaignId +
               '}';
    }
}