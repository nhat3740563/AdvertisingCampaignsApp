// src/main/java/com/yourcompany/campaignapp/model/CampaignChannelLink.java
package com.yourcompany.campaignapp.model;

public class CampaignChannelLink {
    private int campaignId;
    private int channelId;

    public CampaignChannelLink() {}

    public CampaignChannelLink(int campaignId, int channelId) {
        this.campaignId = campaignId;
        this.channelId = channelId;
    }

    // Getters and Setters
    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }
    public int getChannelId() { return channelId; }
    public void setChannelId(int channelId) { this.channelId = channelId; }

    @Override
    public String toString() {
        return "CampaignChannelLink{" +
               "campaignId=" + campaignId +
               ", channelId=" + channelId +
               '}';
    }

    // Để kiểm tra sự bằng nhau của các đối tượng liên kết
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignChannelLink that = (CampaignChannelLink) o;
        return campaignId == that.campaignId && channelId == that.channelId;
    }

    @Override
    public int hashCode() {
        return 31 * campaignId + channelId;
    }
}