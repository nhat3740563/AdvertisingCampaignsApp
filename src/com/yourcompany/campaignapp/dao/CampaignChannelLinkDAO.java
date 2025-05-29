// src/main/java/com/yourcompany/campaignapp/dao/CampaignChannelLinkDAO.java
package com.yourcompany.campaignapp.dao;

import com.yourcompany.campaignapp.model.CampaignChannelLink; // Đảm bảo bạn có lớp CampaignChannelLink trong gói model
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CampaignChannelLinkDAO { // KHÔNG implements GenericDAO vì khóa chính là khóa kép

    public CampaignChannelLink create(CampaignChannelLink link) throws SQLException {
        String sql = "INSERT INTO campaign_channel_links (campaign_id, channel_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, link.getCampaignId());
            pstmt.setInt(2, link.getChannelId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return link;
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    public CampaignChannelLink read(int campaignId, int channelId) throws SQLException {
        String sql = "SELECT campaign_id, channel_id FROM campaign_channel_links WHERE campaign_id = ? AND channel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, campaignId);
            pstmt.setInt(2, channelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCampaignChannelLinkFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    public boolean delete(int campaignId, int channelId) throws SQLException {
        String sql = "DELETE FROM campaign_channel_links WHERE campaign_id = ? AND channel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, campaignId);
            pstmt.setInt(2, channelId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<CampaignChannelLink> readAll() throws SQLException {
        List<CampaignChannelLink> links = new ArrayList<>();
        String sql = "SELECT campaign_id, channel_id FROM campaign_channel_links";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                links.add(extractCampaignChannelLinkFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw e;
        }
        return links;
    }

    private CampaignChannelLink extractCampaignChannelLinkFromResultSet(ResultSet rs) throws SQLException {
        CampaignChannelLink link = new CampaignChannelLink();
        link.setCampaignId(rs.getInt("campaign_id"));
        link.setChannelId(rs.getInt("channel_id"));
        return link;
    }

    public static void main(String[] args) {
        CampaignChannelLinkDAO linkDAO = new CampaignChannelLinkDAO();
        try {
            // Đảm bảo campaign_id và channel_id này tồn tại trong DB của bạn
            int existingCampaignId = 1; // Ví dụ
            int existingChannelId = 1;  // Ví dụ

            // --- TEST CREATE ---
            System.out.println("--- Test Create CampaignChannelLink ---");
            CampaignChannelLink newLink = new CampaignChannelLink(existingCampaignId, existingChannelId);
            CampaignChannelLink createdLink = linkDAO.create(newLink);
            if (createdLink != null) {
                System.out.println("Created link: " + createdLink);
            }

            // --- TEST READ ---
            System.out.println("\n--- Test Read CampaignChannelLink ---");
            CampaignChannelLink readLink = linkDAO.read(existingCampaignId, existingChannelId);
            if (readLink != null) {
                System.out.println("Read link: " + readLink);
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All CampaignChannelLinks ---");
            List<CampaignChannelLink> allLinks = linkDAO.readAll();
            System.out.println("All links:");
            for (CampaignChannelLink link : allLinks) {
                System.out.println("- " + link);
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete CampaignChannelLink ---");
            if (createdLink != null) {
                boolean deleted = linkDAO.delete(createdLink.getCampaignId(), createdLink.getChannelId());
                System.out.println("Link deletion successful? " + deleted);
                if (deleted) {
                    CampaignChannelLink deletedLink = linkDAO.read(createdLink.getCampaignId(), createdLink.getChannelId());
                    System.out.println("Link after deletion (should be null): " + deletedLink);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}