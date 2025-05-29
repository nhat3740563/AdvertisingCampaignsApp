// src/main/java/com/yourcompany/campaignapp/dao/AdChannelDAO.java
package com.yourcompany.campaignapp.dao;

import com.yourcompany.campaignapp.model.AdChannel; // Đảm bảo bạn có lớp AdChannel trong gói model
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdChannelDAO implements GenericDAO<AdChannel> {

    @Override
    public AdChannel create(AdChannel channel) throws SQLException {
        String sql = "INSERT INTO ad_channels (channel_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, channel.getChannelName());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        channel.setChannelId(generatedKeys.getInt(1));
                        return channel;
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public AdChannel read(int id) throws SQLException {
        String sql = "SELECT channel_id, channel_name FROM ad_channels WHERE channel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdChannelFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public boolean update(AdChannel channel) throws SQLException {
        String sql = "UPDATE ad_channels SET channel_name = ? WHERE channel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, channel.getChannelName());
            pstmt.setInt(2, channel.getChannelId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ad_channels WHERE channel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public List<AdChannel> readAll() throws SQLException {
        List<AdChannel> channels = new ArrayList<>();
        String sql = "SELECT channel_id, channel_name FROM ad_channels";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                channels.add(extractAdChannelFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw e;
        }
        return channels;
    }

    private AdChannel extractAdChannelFromResultSet(ResultSet rs) throws SQLException {
        AdChannel channel = new AdChannel();
        channel.setChannelId(rs.getInt("channel_id"));
        channel.setChannelName(rs.getString("channel_name"));
        return channel;
    }

    public static void main(String[] args) {
        AdChannelDAO adChannelDAO = new AdChannelDAO();
        try {
            // --- TEST CREATE ---
            System.out.println("--- Test Create AdChannel ---");
            AdChannel newChannel = new AdChannel("Facebook Ads");
            AdChannel createdChannel = adChannelDAO.create(newChannel);
            if (createdChannel != null) {
                System.out.println("Created ad channel: " + createdChannel);
            }

            // --- TEST READ ---
            System.out.println("\n--- Test Read AdChannel ---");
            if (createdChannel != null) {
                AdChannel readChannel = adChannelDAO.read(createdChannel.getChannelId());
                if (readChannel != null) {
                    System.out.println("Read ad channel: " + readChannel);
                }
            }

            // --- TEST UPDATE ---
            System.out.println("\n--- Test Update AdChannel ---");
            if (createdChannel != null) {
                createdChannel.setChannelName("Facebook Ads (Premium)");
                boolean updated = adChannelDAO.update(createdChannel);
                System.out.println("Ad channel update successful? " + updated);
                if (updated) {
                    AdChannel updatedChannel = adChannelDAO.read(createdChannel.getChannelId());
                    System.out.println("Ad channel after update: " + updatedChannel);
                }
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All AdChannels ---");
            List<AdChannel> allChannels = adChannelDAO.readAll();
            System.out.println("All ad channels:");
            for (AdChannel channel : allChannels) {
                System.out.println("- " + channel);
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete AdChannel ---");
            if (createdChannel != null) {
                boolean deleted = adChannelDAO.delete(createdChannel.getChannelId());
                System.out.println("Ad channel deletion successful? " + deleted);
                if (deleted) {
                    AdChannel deletedChannel = adChannelDAO.read(createdChannel.getChannelId());
                    System.out.println("Ad channel after deletion (should be null): " + deletedChannel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}