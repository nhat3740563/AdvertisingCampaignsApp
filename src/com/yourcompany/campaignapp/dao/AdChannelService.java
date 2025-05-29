// src/main/java/com/yourcompany/campaignapp/service/AdChannelService.java
package com.yourcompany.campaignapp.service;

import com.yourcompany.campaignapp.dao.AdChannelDAO;
import com.yourcompany.campaignapp.model.AdChannel;
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.SQLException;
import java.util.List;

public class AdChannelService {
    private AdChannelDAO adChannelDAO;

    public AdChannelService() {
        this.adChannelDAO = new AdChannelDAO();
    }

    public AdChannelService(AdChannelDAO adChannelDAO) {
        this.adChannelDAO = adChannelDAO;
    }

    public AdChannel createAdChannel(String channelName) throws SQLException {
        if (channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be empty.");
        }
        // Thêm logic kiểm tra trùng tên kênh nếu cần
        AdChannel channel = new AdChannel(channelName);
        return adChannelDAO.create(channel);
    }

    public AdChannel getAdChannelById(int channelId) throws SQLException {
        return adChannelDAO.read(channelId);
    }

    public boolean updateAdChannel(AdChannel channel) throws SQLException {
        if (channel.getChannelId() <= 0) {
            throw new IllegalArgumentException("Channel ID must be valid for update.");
        }
        if (channel.getChannelName() == null || channel.getChannelName().trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be empty.");
        }
        return adChannelDAO.update(channel);
    }

    public boolean deleteAdChannel(int channelId) throws SQLException {
        // Có thể thêm logic kiểm tra xem kênh có đang được sử dụng trong các chiến dịch không
        return adChannelDAO.delete(channelId);
    }

    public List<AdChannel> getAllAdChannels() throws SQLException {
        return adChannelDAO.readAll();
    }

    public static void main(String[] args) {
        AdChannelService adChannelService = new AdChannelService();
        try {
            // --- TEST CREATE ---
            System.out.println("--- Test Create AdChannel ---");
            AdChannel createdChannel = adChannelService.createAdChannel("Service Test Channel");
            if (createdChannel != null) {
                System.out.println("Created ad channel: " + createdChannel);
            }

            // --- TEST READ ---
            System.out.println("\n--- Test Read AdChannel ---");
            if (createdChannel != null) {
                AdChannel readChannel = adChannelService.getAdChannelById(createdChannel.getChannelId());
                if (readChannel != null) {
                    System.out.println("Read ad channel: " + readChannel);
                }
            }

            // --- TEST UPDATE ---
            System.out.println("\n--- Test Update AdChannel ---");
            if (createdChannel != null) {
                createdChannel.setChannelName("Updated Service Channel");
                boolean updated = adChannelService.updateAdChannel(createdChannel);
                System.out.println("Ad channel update successful? " + updated);
                if (updated) {
                    System.out.println("Ad channel after service update: " + adChannelService.getAdChannelById(createdChannel.getChannelId()));
                }
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All AdChannels ---");
            List<AdChannel> allChannels = adChannelService.getAllAdChannels();
            System.out.println("All ad channels in DB:");
            for (AdChannel ac : allChannels) {
                System.out.println("- " + ac.getChannelName());
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete AdChannel ---");
            if (createdChannel != null) {
                boolean deleted = adChannelService.deleteAdChannel(createdChannel.getChannelId());
                System.out.println("Ad channel deletion successful? " + deleted);
                if (deleted) {
                    System.out.println("Ad channel after deletion (should be null): " + adChannelService.getAdChannelById(createdChannel.getChannelId()));
                }
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Business logic error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error during AdChannelService test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}