// src/main/java/com/yourcompany/campaignapp/model/AdChannel.java
package com.yourcompany.campaignapp.model;

public class AdChannel {
    private int channelId;
    private String channelName; // Ví dụ: "Facebook Ads", "Google Ads", "LinkedIn Ads"

    public AdChannel() {}

    public AdChannel(String channelName) {
        this.channelName = channelName;
    }

    // Getters and Setters
    public int getChannelId() { return channelId; }
    public void setChannelId(int channelId) { this.channelId = channelId; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    @Override
    public String toString() {
        return "AdChannel{" +
               "channelId=" + channelId +
               ", channelName='" + channelName + '\'' +
               '}';
    }
}n